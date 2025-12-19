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
package com.hitorro.logdigest;

import com.hitorro.util.basefile.fs.BaseFile;

import java.io.DataOutputStream;
import java.io.IOException;

public class FilterLogFile {

    public static final String FILTER_FILE_PREFIX = "filter_";
    public static final String FILTER_FILE_SUFFIX = ".txt";

    private BaseFile _filterFile;
    private DataOutputStream _filterOS;

    public FilterLogFile(BaseFile srcDirectory, String logFileName) {
        _filterFile = srcDirectory.getChild(FILTER_FILE_PREFIX + logFileName + FILTER_FILE_SUFFIX);
    }

    private void open() throws IOException {
        _filterOS = _filterFile.getDataOutputStream();
    }

    public void close() throws IOException {
        if (_filterOS != null) {
            _filterOS.close();
        }
    }

    public void logFilterLine(String line) throws IOException {
        if (_filterOS == null) {
            open();
        }
        _filterOS.writeBytes(line + "\r\n");
    }
}
