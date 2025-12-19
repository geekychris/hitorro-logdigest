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
 * Copyright (c) 2003 - present HiTorro All rights reserved. User: chris Date: Dec 25, 2005 Time: 11:20:03 AM We care about
 * the referrer and we care about the parent BC, which gets assigned to the bc field.
 */
public class ProvisionChild extends Classifier {
    public static final String ProvisionChild = "provisionChild";

    public final UrlCursorParameter parent = new UrlCursorParameter("parent");
    public UrlCursorParameters params = new UrlCursorParameters(parent);

    public String[] exampleUri() {
        return new String[]{"origin-services.HiTorro.net/breadcrumb/provisionChild?parent=bc9d1e39c494440e8d963774c3bee8fc"};
    }

    public byte classify(UrlMemoryCursor curs, NewLogLine ll) {
        if (curs.resetToLastPart()) {
            if (curs.isTokenSameIgnoreCase(ProvisionChild)) {
                if (params.collect(curs)) {
                    String parentBC = parent.getValue();
                    ll.setBreadcrumb(parentBC);
                }
                return Classifier.ProvisionChild;
            }
        }
        return 0;
    }

    public boolean getShouldWrite() {
        return true;
    }

    public boolean requestorNeeded() {
        return true;
    }
}
