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
import com.hitorro.util.urlparser.PathMatch;
import com.hitorro.util.urlparser.UrlCursorParameter;
import com.hitorro.util.urlparser.UrlCursorParameters;
import com.hitorro.util.urlparser.UrlMemoryCursor;

/**
 * Copyright (c) 2003 - present HiTorro All rights reserved. User: chris Date: Dec 24, 2005 Time: 6:26:07 PM expects a
 * breadcrumb and playlist!!!
 */
public class PlaylistClassifier extends Classifier {
    // not to be static as they contain computed values for the current thread.
    public final UrlCursorParameter breadcrumb = new UrlCursorParameter("bc");
    public UrlCursorParameters params = new UrlCursorParameters(breadcrumb);
    private PathMatch pathMatcher = new PathMatch("breadcrumb", "playlist");

    public String[] exampleUri() {
        return new String[]{"http://origin-services.HiTorro.net/breadcrumb/playlist/925ba603a5804f7b98562231b629a2b5/PID_011808",
                "origin-services.HiTorro.net/breadcrumb/playlist/925ba603a5804f7b98562231b629a2b5/PID_011808"};
    }

    public byte classify(UrlMemoryCursor curs, NewLogLine ll) {
        if (curs.resetToFirstPart()) {
            if (pathMatcher.match(curs)) {
                if (curs.nextToken()) {
                    ll.setBreadcrumb(curs.getToken());
                    if (curs.nextToken()) {
                        ll.setHiTorroID(ClassifierUtil.getHiTorroOrPostIdFromString(curs.getToken()));
                        //has bc and playlist
                        return Classifier.Playlist;
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




