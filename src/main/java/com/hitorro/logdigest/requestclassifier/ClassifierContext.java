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

import gnu.trove.map.hash.TObjectIntHashMap;
import com.hitorro.logdigest.Log;
import com.hitorro.logdigest.NewLogLine;
import com.hitorro.util.core.Console;
import com.hitorro.util.urlparser.TokenKey;
import com.hitorro.util.urlparser.UrlMemoryCursor;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class ClassifierContext {
    private static final boolean log = false;
    private Classifier classifiers[];
    private List<Classifier> clTemp = new ArrayList<Classifier>();
    private UrlMemoryCursor curs = new UrlMemoryCursor();
    private TObjectIntHashMap map = new TObjectIntHashMap();

    public ClassifierContext() {
        init();
    }

    public void init() {
        add(new BeaconClassifier());
        add(new PlayerClassifier());
        add(new ResourceClassifier());
        add(new DownloadClassifier());
        add(new HostingsiteClassifier());
        add(new CommentClassifier());
        add(new ActionClassifier());

        add(new PlaylistClassifier());
        add(new ProvisionChild());
        add(new PostpageClassifier());
        add(new FeedClassifier());
        finalizeForClassification();
    }

    public boolean selfTest() {
        boolean success = true;
        for (Classifier cl : classifiers) {
            success = testUrls(cl.exampleUri(), cl, success, true);

            success = testUrls(cl.negativeExampleUri(), cl, success, false);
        }
        return success;
    }

    private boolean testUrls(final String[] uris, final Classifier cl, boolean success, boolean positive) {
        for (String uri : uris) {
            NewLogLine nll = new NewLogLine();
            curs.setUrl(uri);
            byte result = cl.classify(curs, nll);
            if (result == 0) {
                if (positive) {
                    success = false;
                    String name = cl.getClass().getCanonicalName();
                    reportError("Failure: %s, uri: %s", name, uri);
                } else {
                    reportDebug("Success: %s", cl.getClass().getCanonicalName());
                }
                //Console.println("Failure: %s, uri: %s", name, uri);
            } else {
                if (positive) {
                    reportDebug("Success: %s", cl.getClass().getCanonicalName());
                } else {
                    success = false;
                    String name = cl.getClass().getCanonicalName();
                    reportError("Failure: %s, uri: %s", name, uri);
                }
                //Console.println("Success: %s", cl.getClassification().getCanonicalName());

            }
        }
        return success;
    }

    private void reportDebug(String msg, Object... args) {
        if (log) {
            Log.logdigest.debug(msg, args);
        } else {
            Console.println(msg, args);
        }
    }

    private void reportError(String msg, Object... args) {
        if (log) {
            Log.logdigest.error(msg, args);
        } else {
            Console.println(msg, args);
        }
    }

    public void add(Classifier c) {
        clTemp.add(c);
    }

    public void finalizeForClassification() {
        int size = clTemp.size();
        classifiers = new Classifier[size];
        for (int i = 0; i < size; i++) {
            classifiers[i] = clTemp.get(i);
        }
    }

    public boolean classify(NewLogLine nll, String requestor) {
        nll.setRequestType(Classifier.NA);
        nll.setShouldWrite(false);
        curs.setUrl(nll.getRequestURI());
        TokenKey key = ClassifierUtil.TLDMatcher.match(curs);
        nll.setSiteId(key.getId());
        byte result;
        for (Classifier cl : classifiers) {
            try {
                result = cl.classify(curs, nll);
                if (result != 0) {
                    if (!map.containsKey(cl)) {
                        map.put(cl, 1);
                    }
                    nll.setRequestType(result);
                    if (cl.requestorNeeded()) {
                        nll.setRequestLine(requestor);
                    } else {
                        nll.setRequestLine(null);
                    }
                    if (cl.getShouldWrite()) {
                        nll.setShouldWrite(true);
                        return true;
                    }
                    return false;
                }
            } catch (Exception e) {
                Log.logdigest.error("Exception %s %e for url: %s", e, e, nll.getRequestURI());
            }
        }
        // if we get here, maybe we should be reporting something we couldnt identify...then again its just trash!
        return false;
    }
}
