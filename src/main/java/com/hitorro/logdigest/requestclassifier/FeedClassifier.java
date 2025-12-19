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

/**
 * Copyright (c) 2003 - present HiTorro All rights reserved. User: chris Date: Dec 28, 2005 Time: 4:34:43 PM
 */
public class FeedClassifier extends Classifier {
    public static final String FeedString = "feed";
    public final com.hitorro.util.urlparser.UrlCursorParameter feed = new com.hitorro.util.urlparser.UrlCursorParameter("feed");
    public final com.hitorro.util.urlparser.UrlCursorParameter category = new com.hitorro.util.urlparser.UrlCursorParameter("category_name");
    public com.hitorro.util.urlparser.UrlCursorParameters params = new com.hitorro.util.urlparser.UrlCursorParameters(feed, category);
    private com.hitorro.util.urlparser.TokenMatch tokenMatcher = new com.hitorro.util.urlparser.TokenMatch("feed", "wp-rss2.php", "wp-feed.php");
    private com.hitorro.util.urlparser.PathMatch pathMatcher = new com.hitorro.util.urlparser.PathMatch("home", "feeds");

    public String[] exampleUri() {
        return new String[]{"origin-www.HiTorro.net/wp-rss2.php?category_name=InfoTalk&feed=rss2",
                "/origin-www.nextgearshow.com/1783/gms-lansing-delta-township-plant/feed/",
                "/origin-www.nextgearshow.com/1737/driving-the-camaro-convertible-concept/feed/",
                "/origin-www.nextgearshow.com/feed",
                "/origin-www.nextgearshow.com/feed/atom/",
                "/origin-www.HiTorro.net/home/wp-feed.php/?cat=0&submit.x=7&submit.y=8",
                "/origin-www.HiTorro.net/home/feeds/symantec"// interesting category markers?
        };
    }

    public byte classify(com.hitorro.util.urlparser.UrlMemoryCursor curs, NewLogLine ll) {
        // XXX TODO I would of thought there would be some kind of data to extract from the feed, not sure what that is?
        // Post ID?  Category??
        if (curs.resetToFirstPart()) {
            if (tokenMatcher.match(curs)) {
                // feed at the start
                if (params.collect(curs)) {
                    if (category.getValue() != null) {
                        String cat = category.getValue();
                        ll.setCategory(cat);
                    }
                }
                return Classifier.Feed;
            }
        }
        if (curs.resetToLastPart()) {
            if (tokenMatcher.match(curs)) {
                // feed at the end
                if (curs.resetToFirstPart()) {
                    String tok = curs.getToken();
                    int id = ClassifierUtil.getHiTorroOrPostIdFromString(tok);
                    if (id != 0) {
                        ll.setPostID(id);
                    }
                }
                return Classifier.Feed;
            }
        }
        if (params.collect(curs)) {
            if (feed.getValue() != null) {
                // something of the form: "origin-www.HiTorro.net/wp-rss2.php?category_name=InfoTalk&feed=rss2"
                return Classifier.Feed;
            }
        }
        if (curs.resetToFirstPart()) {
            if (pathMatcher.match(curs)) {
                if (curs.nextToken()) {
                    String cat = curs.getToken();
                    ll.setCategory(cat);
                    return Classifier.Feed;
                }
            }
        }
        return 0;
    }

    public boolean getShouldWrite() {
        return false;
    }
}
