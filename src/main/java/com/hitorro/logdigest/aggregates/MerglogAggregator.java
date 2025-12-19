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
package com.hitorro.logdigest.aggregates;

import com.hitorro.logdigest.LogFileProcessor;
import com.hitorro.logdigest.NewLogLine;
import com.hitorro.util.basefile.fs.BaseFile;
import com.hitorro.util.core.opers.HTPredicate;
import com.hitorro.util.io.csv.CSVFormattedWriter;
import com.hitorro.util.io.largedata.BaseFileAccessingObjectFactory;
import com.hitorro.util.io.largedata.compressedstreams.aggregator.Aggregator;

import java.util.Queue;

/**
 * Copyright (c) 2003 - present HiTorro All rights reserved. User: chris Date: Jan 2, 2008 Time: 1:08:38 PM Somewhat of a
 * dummy aggregator as currently this aggregator does not create mserlog's.  Simply its a flyweight for others to depend
 * on.
 */
public class MerglogAggregator implements Aggregator<NewLogLine, NewLogLine> {
    public static final String MserLog = "mserlog";


    public HTPredicate<NewLogLine> getDefaultOperator() {
        // no default operator
        return null;
    }

    public boolean setOutput(final BaseFile targetFile) {
        return false;
    }

    public boolean aggregate(final NewLogLine nll) {
        return false;
    }

    public boolean close() {
        return false;
    }

    public boolean merge(final Queue<BaseFile> sourceFiles) {
        return false;
    }

    public void reset() {
    }

    public String getFileExtension() {
        return MserLog;
    }

    public Aggregator[] getDependcies() {
        return new Aggregator[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Aggregator getInputFileType() {
        return null;
    }

    public BaseFileAccessingObjectFactory<NewLogLine> getTargetObjectFactory() {
        return LogFileProcessor.getLogLineFactory();
    }

    public CSVFormattedWriter getCSVFormattedWriter() {
        // nothing to report
        return null;
    }

    public String getDescription() {
        return "Not Applicable";
    }
}
