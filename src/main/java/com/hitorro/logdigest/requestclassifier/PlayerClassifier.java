/*
 * Copyright (c) 2006-2025 Chris Collins
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.hitorro.logdigest.requestclassifier;

import com.hitorro.logdigest.NewLogLine;
import com.hitorro.util.urlparser.TokenMatch;
import com.hitorro.util.urlparser.UrlCursorParameter;
import com.hitorro.util.urlparser.UrlCursorParameters;
import com.hitorro.util.urlparser.UrlMemoryCursor;

/**
 * <p/>
 * Player will have a breadcrumb
 * <p/>
 * // must come before resource due to the swf test!!!!
 */
public class PlayerClassifier extends Classifier {
    // not to be static as they contain computed values for the current thread.
    public final UrlCursorParameter breadcrumb = new UrlCursorParameter("bc");
    public UrlCursorParameters params = new UrlCursorParameters(breadcrumb);
    private TokenMatch tokenMatcher = new TokenMatch("HiTorro-player.swf");

    public String[] exampleUri() {
        return new String[]{"origin-www.HiTorro.net/player/HiTorro-player.swf?bc=3F34K2L1",
                "/origin-www.nextgearshow.com/1697/HiTorro-player.swf"};
    }

    public byte classify(UrlMemoryCursor curs, NewLogLine ll) {
        if (curs.resetToLastPart()) {
            if (tokenMatcher.match(curs)) {

                if (params.collect(curs)) {
                    //got values
                    String bc = breadcrumb.getValue();
                    ll.setBreadcrumb(bc);
                    return Classifier.Player;
                }
                // could be a post id at the first position
                if (curs.resetToFirstPart()) {
                    String postId = curs.getToken();
                    int id = ClassifierUtil.getHiTorroOrPostIdFromString(postId);
                    if (id > 0) {
                        ll.setPostID(id);
                        return Classifier.Player;
                    }
                }
            }
        }
        return 0;
    }

    public boolean getShouldWrite() {
        return true;
    }
}




