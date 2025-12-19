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
package com.hitorro.util.io.largedata;

import com.hitorro.logdigest.Log;
import com.hitorro.util.basefile.fs.BaseFile;
import com.hitorro.util.basefile.tools.direnum.DateEnumAction;
import com.hitorro.util.io.largedata.compressedstreams.aggregator.AggregationUtil;

/**
 * Copyright (c) 2003 - present HiTorro All rights reserved. User: chris Date: Jan 3, 2008 Time: 8:53:16 AM
 */
public class RemoveSummary implements DateEnumAction {
    private BaseFile dbDir;

    public RemoveSummary(BaseFile dbDir) {
        this.dbDir = dbDir;
    }

    public boolean executeYear(int year, BaseFile dayDir) {
        return true;
    }

    public boolean executeMonth(int year, int month, BaseFile dayDir) {
        AggregationUtil.removeSummaryDir(dbDir, year, month);
        Log.logdigest.info("Removed summary: %s %s", year, month);

        return true;
    }

    public boolean executeDay(int year, int month, int day, BaseFile dayDir) {
        AggregationUtil.removeSummaryDir(dbDir, year, month, day);
        Log.logdigest.info("Removed summary: %s %s %s", year, month, day);

        return true;
    }
}
