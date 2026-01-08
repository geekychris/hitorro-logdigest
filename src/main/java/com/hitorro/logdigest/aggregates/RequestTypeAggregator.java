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

import com.hitorro.logdigest.NewLogLine;
import com.hitorro.logdigest.requestclassifier.Classifier;
import com.hitorro.util.core.opers.HTPredicate;
import com.hitorro.util.io.csv.CSVFormattedWriter;
import com.hitorro.util.io.csv.formatters.CSVFormatter;
import com.hitorro.util.io.csv.formatters.CSVIntFormatter;

/**
 */
public class RequestTypeAggregator extends SingleIntAggregator {
    private static String[] Header = new String[]{"RequestId", "Frequency"};

    public int getInt(NewLogLine nll) {
        return nll.getRequestType();
    }

    public String getFileExtension() {
        return "agr_requesttype";
    }

    public HTPredicate<NewLogLine> getDefaultOperator() {
        // no default operator
        return null;
    }

    public CSVFormattedWriter getCSVFormattedWriter() {
        CSVFormatter formatters[] = new CSVFormatter[]{new RequestTypeFormatter(), new CSVIntFormatter()};
        return new CSVFormattedWriter(Header, formatters);
    }

    public String getDescription() {
        return "Request id count.";
    }
}

class RequestTypeFormatter extends CSVIntFormatter {
    public String format(int value) {
        if (Classifier.RequestTypes.length < value) {
            return "unknown request type";
        }
        return Classifier.RequestTypes[value];
    }
}
