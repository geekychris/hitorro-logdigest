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
import com.hitorro.util.urlparser.ExtensionMatcher;
import com.hitorro.util.urlparser.TokenMatch;
import com.hitorro.util.urlparser.UrlMemoryCursor;

/**
 * Copyright (c) 2003 - present HiTorro All rights reserved. User: chris Date: Dec 21, 2005 Time: 7:07:04 PM
 * <p/>
 * Resources generally are anything that is downloaded as part of a pate request, but we care not to track.  Such as
 * images.
 */
public class ResourceClassifier extends Classifier {
    public static final String BeaconString = "HiTorro_beacon.gif";
    private ExtensionMatcher extensionmatcher;
    private TokenMatch tokenMatcher = new TokenMatch("robots.txt", "crossdomain.xml", "themes", "plugins", "news.php", "rd", "show_news.php");

    public ResourceClassifier() {
        extensionmatcher = new ExtensionMatcher("gif", "jpg", "html", "css", "png", "swf", "ico", "jsp", "js");
    }

    public String[] exampleUri() {
        return new String[]{"/origin-iphone.HiTorro.net/", "origin-www.HiTorro.net/home/technology/2149/%5C%22http:/media1.HiTorro.net/graphics/HiTorro_beacon.gif?p=/home/technology/2149/netgear-introduces-gigabit-speed-to-their-stackable-smart-switches%5C%22",
                "origin-www.HiTorro.net/home/technology/2149/%5C%22http:/media1.HiTorro.net/graphics/crossdomain.xml?p=/home/technology/2149/netgear-introduces-gigabit-speed-to-their-stackable-smart-switches%5C%22"};
    }

    public byte classify(UrlMemoryCursor curs, NewLogLine ll) {
        if (curs.resetToLastPart()) {
            if (extensionmatcher.match(curs)) {
                return Classifier.Resource;
            }
            if (tokenMatcher.match(curs)) {
                return Classifier.Resource;
            }
        }
        return 0;
    }

    public boolean getShouldWrite() {
        return false;
    }
}


