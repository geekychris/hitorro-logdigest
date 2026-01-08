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
import com.hitorro.util.urlparser.UrlMemoryCursor;

/**
 * request to a specific request type and optionally extract out the appropriate meta parts.
 */
public abstract class Classifier {
    public static final byte Beacon = 0;
    public static final byte Resource = 1;
    public static final byte Download = 2;
    public static final byte Hostingsite = 3;
    public static final byte Comment = 4;
    public static final byte Action = 5;
    public static final byte Player = 6;
    public static final byte Playlist = 7;
    public static final byte ProvisionChild = 8;
    public static final byte Postpage = 9;
    public static final byte Feed = 10;
    public static final String[] RequestTypes = new String[]{"Beacon",
            "Resource",
            "Download",
            "Hostingsite",
            "Comment",
            "Action",
            "Player",
            "Playlist",
            "ProvisionChild",
            "Postpage",
            "Feed"
    };
    public static final byte NA = 127;

    /**
     * classify a line
     *
     * @param curs
     * @param ll
     * @return 0 if not classified, not 0 if classified.  Check the getShouldWrite to determine if it should be thrown
     * away or not.
     */
    public abstract byte classify(UrlMemoryCursor curs, NewLogLine ll);

    public abstract boolean getShouldWrite();

    public abstract String[] exampleUri();

    public String[] negativeExampleUri() {
        // negative test first one
        return new String[]{};
    }

    public boolean requestorNeeded() {
        return false;
    }
}
