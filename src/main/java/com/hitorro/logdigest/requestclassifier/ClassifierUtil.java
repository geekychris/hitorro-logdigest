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

import com.hitorro.util.core.IntegerUtil;
import com.hitorro.util.core.string.StringUtil;
import com.hitorro.util.urlparser.TLDMatcher;
import com.hitorro.util.urlparser.TokenKey;

/**
 */
public class ClassifierUtil {

    public static final String PID = "PID_";
    public static TokenKey flyweight = new TokenKey("", -1);
    public static TokenKey HiTorro = new TokenKey("HiTorro.net", 1);
    public static TokenKey nextgearshow = new TokenKey("nextgearshow.com", 2);
    public static TokenKey rebootshow = new TokenKey("rebootshow.com", 3);
    public static TokenKey scobleshow = new TokenKey("scobleshow.com", 4);
    public static TokenKey marketingvoices = new TokenKey("marketingvoices.com", 5);
    public static TokenKey cesbloghaus = new TokenKey("cesbloghaus.com", 6);
    public static TokenKey applevoicesshow = new TokenKey("applevoicesshow.com", 7);
    public static TLDMatcher TLDMatcher = new TLDMatcher(flyweight, HiTorro, nextgearshow,
            rebootshow, scobleshow, marketingvoices,
            cesbloghaus, applevoicesshow);

    /**
     * If the number starts with the PID_ then its a HiTorro id (media id, not post id).
     *
     * @param token
     * @return 0 if not parsable, else a post or HiTorro id.
     */
    public static final int getHiTorroOrPostIdFromString(String token) {
        if (StringUtil.nullOrEmptyOrBlankString(token)) {
            return 0;
        }
        if (token.startsWith(PID)) {
            token = token.substring(PID.length());
            if (StringUtil.nullOrEmptyOrBlankString(token)) {
                return 0;
            }
        }

        if (IntegerUtil.isNumber(token)) {
            return Integer.parseInt(token);
        } else {
            return 0;
        }
    }

}
