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
 * grab the site and the breadcrumb
 * <p/>
 * store the host uri in the request uri field.
 */
public class HostingsiteClassifier extends Classifier {
    public static final String HostingString = "hostingsite";
    public final UrlCursorParameter breadcrumb = new UrlCursorParameter("breadcrumb");
    public final UrlCursorParameter hosturl = new UrlCursorParameter("hosturl");
    public UrlCursorParameters params = new UrlCursorParameters(breadcrumb, hosturl);

    public String[] exampleUri() {
        return new String[]{"origin-services.HiTorro.net/ping/hostingsite?breadcrumb=925ba603a5804f7b98562231b629a2b5&hosturl=http%3A%2F%2Fwww%2Ejaiku%2Ecom%2Fblog%2F"};
    }

    public byte classify(UrlMemoryCursor curs, NewLogLine ll) {
        if (curs.resetToLastPart()) {
            if (curs.isTokenSameIgnoreCase(HostingString)) {
                if (params.collect(curs)) {
                    ll.setBreadcrumb(breadcrumb.getValue());
                    String hs = hosturl.getValue();
                    ll.setRequestURI(hs);
                }
                return Classifier.Hostingsite;
            }
        }
        return 0;
    }

    public boolean getShouldWrite() {
        return true;
    }
}
