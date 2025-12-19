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

import com.hitorro.logdigest.MediaTypeEnum;
import com.hitorro.logdigest.NewLogLine;
import com.hitorro.util.urlparser.UrlCursorParameter;
import com.hitorro.util.urlparser.UrlCursorParameters;
import com.hitorro.util.urlparser.UrlMemoryCursor;

/**
 * Copyright (c) 2003 - present HiTorro All rights reserved. User: chris Date: Dec 23, 2005 Time: 10:02:34 AM Anything that
 * is considered a download, must further be refined to know the pid if it has one.
 * <p/>
 * <p/>
 * breadcrumb content : has pid Thats a Post ID and a HiTorro ID.
 */
public class DownloadClassifier extends Classifier {
    public static final String PopupString = "popup.php";
    public final UrlCursorParameter breadcrumb = new UrlCursorParameter("breadcrumb");
    public final UrlCursorParameter content = new UrlCursorParameter("content");
    public final UrlCursorParameter postUrl = new UrlCursorParameter("postUrl");
    public UrlCursorParameters params = new UrlCursorParameters(breadcrumb, content, postUrl);
    //private ExtensionMatcher matcher;
    private UrlMemoryCursor c2 = new UrlMemoryCursor();


    public DownloadClassifier() {
        //matcher = new ExtensionMatcher("flv", "mp3", "mov", "wmv", "mp4", "m4v");
    }

    public byte classify(UrlMemoryCursor curs, NewLogLine ll) {
        if (curs.resetToLastPart()) {
            if (curs.isTokenSameIgnoreCase(PopupString)) {
                // popup
                //XXX TODO this is not sufficient
                if (params.collect(curs)) {
                    String cont = content.getValue();
                    if (cont != null) {
                        c2.setUrl(cont);
                        // goto the last -1 position
                        if (c2.resetToLastPart(1)) {
                            ll.setHiTorroID(ClassifierUtil.getHiTorroOrPostIdFromString(c2.getToken()));
                            c2.resetToLastPart();
                            if (c2.hasExtension()) {
                                String ext = c2.getValuePartOfArgToken();

                                MediaTypeEnum v = MediaTypeEnum.context.getByShortName(ext);
                                if (v != null) {
                                    ll.setMediaType(v.getOrdinal());
                                }
                            }
                            String bc = breadcrumb.getValue();
                            ll.setBreadcrumb(bc);
                        }
                    }
                    String purl = postUrl.getValue();
                    if (purl != null) {
                        c2.setUrl(purl);
                        c2.resetToFirstPart();
                        if (c2.nextToken()) {
                            ll.setPostID(ClassifierUtil.getHiTorroOrPostIdFromString(c2.getToken()));
                        }
                    }
                }
                return Classifier.Download;
            }
        }
        return 0;
    }

    public String[] exampleUri() {
        return new String[]{"/origin-www.applevoicesshow.com/search/popup.php?content=http://media1.HiTorro.net/media/2007/06/PID_011588/HiTorro_Apple_WWDC_Steve_Jobs_Keynote_.flv&totalTime=3122000&postURL=http://www.HiTorro.net/home/3294/apple-wwdc-2007-steve-jobs-keynote-part-2-of-2%20%20%20%20%20%20%20%20%20%20%20%20&breadcrumb=b448bc43bda94d53bcba5140dff3c326&mp3ImageSrc=http://media1.HiTorro.net/media/2007/06/PID_011588/HiTorro_Apple_WWDC_Steve_Jobs_Keynote_.jpg&permalink=http://www.HiTorro.net&commentsLink=undefined&ispopup=true",
                "/origin-www.HiTorro.net/player/popup.php?content=undefined&totalTime=229000&postURL=http://www.HiTorro.net/home/3160/sands-of-passion-episode-4%20%20%20%20%20%20%20%20%20%20%20%20&breadcrumb=2f413ca4cba1466398ac83e47d0cddc2&mp3ImageSrcundefined&permalinkhttp://www.HiTorro.net/home/3225/a-cialis-ad-with-cuba-gooding-jr&commentsLinkundefined"};
    }

    public boolean getShouldWrite() {
        return true;
    }
}
