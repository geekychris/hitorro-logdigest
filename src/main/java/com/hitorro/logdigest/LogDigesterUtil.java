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

import com.hitorro.logdigest.old.LogLineWriter;
import com.hitorro.util.basefile.fs.BaseFile;
import com.hitorro.util.io.FileUtil;
import com.hitorro.util.io.largedata.BaseFileAccessingObjectFactory;
import com.hitorro.util.io.largedata.compressedstreams.aggregator.AggregationUtil;

import java.io.IOException;
import java.util.Calendar;

/**
 * <p/>
 */
public class LogDigesterUtil {

    public static final LogLineWriter getWriter(NewLogLine ll,
                                                BaseFile inputFile,
                                                BaseFile dbDir,
                                                BaseFileAccessingObjectFactory factory) throws IOException {
        String name = inputFile.getName();
        long t = ll.getTimeStamp();
        t = t * 1000;
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(t);
        String newFileName = FileUtil.getFileNameFromPeer(name, "serlog");
        BaseFile f = AggregationUtil.getPath(true, dbDir, newFileName, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH));
        f.mkParentDir();
        return new LogLineWriter(f, factory);
    }
}