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
import com.hitorro.util.urlparser.UrlCursor;
import com.hitorro.util.urlparser.UrlCursorParameter;
import com.hitorro.util.urlparser.UrlMemoryCursor;

/**
 * Copyright (c) 2003 - present HiTorro All rights reserved. User: chris Date: Dec 26, 2005 Time: 11:25:29 AM Looking for
 * postpage
 * <p/>
 * POST ID (not HiTorro id)
 */
public class PostpageClassifier extends Classifier {
    public static final String HomeString = "home";

    public final UrlCursorParameter p = new UrlCursorParameter("p");

    public String[] exampleUri() {
        // negative test first one
        return new String[]{"http://www.HiTorro.net/home/4769/talking-with-dopplrs-ceo-about-business-travel-tool",
                "www.HiTorro.net/home/?p=4769",
                "http://rebootshow.com/34/the-reboot-top-ten-games-to-buy-for-xmas"};
        // XXX TODO rebootshow, is that legal?
    }

    public String[] negativeExampleUri() {
        // negative test first one
        return new String[]{"/origin-www.rebootshow.com/home/wp-login",
                "/origin-www.HiTorro.net/home/?2098711937"};
        // XXX TODO rebootshow, is that legal?
    }

    public byte classify(UrlMemoryCursor curs, NewLogLine ll) {
        if (curs.resetToFirstPart()) {
            if (curs.isTokenSameIgnoreCase(HomeString)) {
                // home string case

                if (curs.nextToken()) {
                    // we need either p= or
                    if (p.isKey(curs)) {
                        ll.setPostID(ClassifierUtil.getHiTorroOrPostIdFromString(curs.getValuePartOfArgToken()));
                        return Classifier.Postpage;
                    } else {
                        if (curs.getUrlPartType() == UrlCursor.Part.Path) {
                            int id = ClassifierUtil.getHiTorroOrPostIdFromString(curs.getToken());
                            if (id > 0) {
                                ll.setPostID(id);
                                return Classifier.Postpage;
                            }
                        }
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
