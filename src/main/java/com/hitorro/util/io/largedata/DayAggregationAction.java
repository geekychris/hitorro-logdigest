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
import com.hitorro.util.io.largedata.compressedstreams.aggregator.AggregationHierarchyQuery;
import com.hitorro.util.io.largedata.compressedstreams.aggregator.Aggregator;

import java.util.Queue;

/**
 * Copyright (c) 2003 - present HiTorro All rights reserved. User: chris Date: Jan 3, 2008 Time: 3:13:30 PM
 */
public class DayAggregationAction implements DateEnumAction {
    private Queue<BaseFile> queue;
    private Aggregator aggr;
    private AggregationHierarchyQuery context;

    public DayAggregationAction(Queue<BaseFile> queue, Aggregator aggr, AggregationHierarchyQuery context) {
        this.queue = queue;
        this.context = context;
        this.aggr = aggr;
    }

    public boolean executeYear(int year, BaseFile dayDir) {
        return false;
    }

    public boolean executeMonth(int year, int month, BaseFile dayDir) {
        return false;
    }

    public boolean executeDay(int year, int month, int day, BaseFile dayDir) {
        BaseFile f = null;
        try {
            f = context.getAggregateFileForDay(aggr, year, month, day);
        } catch (Exception e) {
            return false;
        }
        if (BaseFile.notNullAndExists(f)) {
            queue.add(f);
            return true;
        }
        Log.logdigest.error("Unable to create aggregate for year: %s month: %s day: %s, aggr: %s", year, month, day, this.aggr.getFileExtension());
        return false;
    }
}
