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

import com.hitorro.logdigest.ActionEnum;
import com.hitorro.logdigest.NewLogLine;
import com.hitorro.util.urlparser.UrlCursorParameter;
import com.hitorro.util.urlparser.UrlCursorParameters;
import com.hitorro.util.urlparser.UrlMemoryCursor;


/**
 * Copyright (c) 2003 - present HiTorro All rights reserved. User: chris Date: Dec 24, 2005 Time: 11:19:26 AM Determine
 * user actions. If it is an action we need to extract: - breadcrumb - mediaId - time - action
 */
public class ActionClassifier extends Classifier {
    public static final String ActionString = "useraction";
    private static final String HiTorroIDStart = "PID_";
    // not to be static as they contain computed values for the current thread.
    public final UrlCursorParameter breadcrumb = new UrlCursorParameter("breadcrumb");
    public final UrlCursorParameter mediaId = new UrlCursorParameter("mediaId");
    public final UrlCursorParameter action = new UrlCursorParameter("action");
    public UrlCursorParameters params = new UrlCursorParameters(breadcrumb, mediaId, action);

    public String[] exampleUri() {
        return new String[]{"origin-services.HiTorro.net/ping/useraction?action=play&breadcrumb=f007c9881bed4c4dbc9983fd37815b03&mediaId=PID_013171&time=0"};
    }

    public byte classify(UrlMemoryCursor curs, NewLogLine ll) {
        if (curs.resetToLastPart()) {
            if (curs.isTokenSameIgnoreCase(ActionString)) {
                if (params.collect(curs)) {
                    //got values
                    String bc = breadcrumb.getValue();
                    ll.setBreadcrumb(bc);
                    String mid = mediaId.getValue();
                    String act = action.getValue();
                    ActionEnum e = ActionEnum.actionContext.getByShortName(act);
                    ll.setActionType(e.getOrdinal());
                    // XXX action????
                    ll.setHiTorroID(ClassifierUtil.getHiTorroOrPostIdFromString(mid));
                }
                return Classifier.Action;
            }
        }
        return 0;
    }

    public boolean getShouldWrite() {
        return true;
    }
}



