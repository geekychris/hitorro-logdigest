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
import com.hitorro.util.urlparser.UrlCursorParameter;
import com.hitorro.util.urlparser.UrlCursorParameters;
import com.hitorro.util.urlparser.UrlMemoryCursor;

/**
 * <p/>
 * //Beacon (uri=*HiTorro_beacon.gif)
 * <p/>
 * //example: origin-www.HiTorro.net/home/technology/2149/%5C%22http:/media1.HiTorro.net/graphics/HiTorro_beacon.gif?p=/home/technology/2149/netgear-introduces-gigabit-speed-to-their-stackable-smart-switches%5C%22
 * <p/>
 * Uses Last Part of path: No
 */
public class BeaconClassifier extends Classifier {
    public static final String BeaconString = "HiTorro_beacon.gif";

    public final UrlCursorParameter url = new UrlCursorParameter("p");
    public final UrlCursorParameter id = new UrlCursorParameter("id");
    public UrlCursorParameters params = new UrlCursorParameters(url, id);

    public String[] exampleUri() {
        return new String[]{"http://origin-www.HiTorro.net/home/technology/2149/%5C%22http:/media1.HiTorro.net/graphics/HiTorro_beacon.gif?p=/home/technology/2149/netgear-introduces-gigabit-speed-to-their-stackable-smart-switches%5C%22",
                "origin-www.HiTorro.net/home/technology/2149/%5C%22http:/media1.HiTorro.net/graphics/HiTorro_beacon.gif?p=/home/technology/2149/netgear-introduces-gigabit-speed-to-their-stackable-smart-switches%5C%22"};
    }

    public byte classify(UrlMemoryCursor curs, NewLogLine ll) {
        if (curs.resetToLastPart()) {
            if (curs.isTokenSameIgnoreCase(BeaconString)) {
                if (params.collect(curs)) {
                    String pageUrl = url.getValue();
                    String userCookie = id.getValue();
                    // XXX TODO note I dont have a good example of this AND I have no idea what we want todo with it!
                }
                return Classifier.Beacon;
            }
        }
        return 0;
    }

    public boolean getShouldWrite() {
        return false;
    }
}
