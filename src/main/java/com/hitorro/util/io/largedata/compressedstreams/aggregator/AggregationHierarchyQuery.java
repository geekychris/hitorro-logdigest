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
package com.hitorro.util.io.largedata.compressedstreams.aggregator;


import com.hitorro.util.basefile.fs.BaseFile;
import com.hitorro.util.basefile.tools.direnum.DateEnumAction;
import com.hitorro.util.basefile.tools.direnum.DirDateEnumerator;
import com.hitorro.util.basefile.tools.direnum.DirEnumUtil;
import com.hitorro.util.core.Log;
import com.hitorro.util.core.iterator.FilteringIterator;
import com.hitorro.util.core.opers.HTPredicate;
import com.hitorro.util.core.string.Fmt;
import com.hitorro.util.io.largedata.BaseFileAccessingObjectFactory;
import com.hitorro.util.io.largedata.CompressedStreamIO;
import com.hitorro.util.io.largedata.CompressedStreamIOIterator;
import com.hitorro.util.io.largedata.DayAggregationAction;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

/**
 */
public class AggregationHierarchyQuery<T extends CompressedStreamIO> {
    public static final String Merged = "merged.";
    private BaseFile dbDir;
    private BaseFileAccessingObjectFactory<T> baseFactory;
    private com.hitorro.util.io.largedata.compressedstreams.aggregator.Aggregator baseAggregator;

    public AggregationHierarchyQuery(BaseFile dbDir, com.hitorro.util.io.largedata.compressedstreams.aggregator.Aggregator baseAggregator, BaseFileAccessingObjectFactory<T> baseFactory) {
        this.dbDir = dbDir;
        this.baseFactory = baseFactory;
        this.baseAggregator = baseAggregator;
    }

    private static final BaseFile summary(BaseFile f) {
        BaseFile r = f.getChild("summary");
        r.mkdir();
        return r;
    }

    private static final boolean aggregate(CompressedStreamIOIterator iter, com.hitorro.util.io.largedata.compressedstreams.aggregator.Aggregator aggregator, HTPredicate<CompressedStreamIO> oper) {
        return aggregate(new FilteringIterator<CompressedStreamIO>(iter, oper), aggregator);
    }

    private static final boolean aggregate(Iterator<CompressedStreamIO> iter, com.hitorro.util.io.largedata.compressedstreams.aggregator.Aggregator aggregator) {
        CompressedStreamIO row;
        while (iter.hasNext()) {
            row = iter.next();
            aggregator.aggregate(row);
        }
        try {
            aggregator.close();
        } catch (IOException e) {
            Log.util.error("Exception writing aggregate %s %e", e, e);
            return false;
        }
        return true;
    }

    private static final BaseFile aggregateFile(BaseFile dir, com.hitorro.util.io.largedata.compressedstreams.aggregator.Aggregator aggr) {
        return dir.getChild(Fmt.S("%s%s", Merged, aggr.getFileExtension()));
    }

    public BaseFile getAggregateFileForYear(com.hitorro.util.io.largedata.compressedstreams.aggregator.Aggregator aggregator, int year) throws Exception {
        BaseFile quarterDir = summary(DirEnumUtil.getDirForYear(dbDir, year));
        BaseFile aggr = aggregateFile(quarterDir, aggregator);
        if (aggr.exists()) {
            return aggr;
        }
        Queue<BaseFile> files = new LinkedList<BaseFile>();
        for (int i = 0; i < 4; i++) {
            BaseFile f = getAggregateFileForQuarter(aggregator, year, i);
            if (BaseFile.notNullAndExists(f)) {
                files.add(f);
            } else {
                Log.util.warn("Unable to generate aggregate for year:%s for year: %s", aggregator.getFileExtension(), year);
            }
        }
        aggregator.setOutput(aggr);
        if (aggregator.merge(files)) {
            Log.util.info("Merged Year %s", aggregator.getFileExtension(), aggr);
            return aggr;
        }
        return null;
    }

    public BaseFile getAggregateFileForQuarter(com.hitorro.util.io.largedata.compressedstreams.aggregator.Aggregator aggregator, int year, int quarter) throws Exception {
        BaseFile quarterDir = summary(DirEnumUtil.getDirForQuarter(dbDir, year, quarter));
        BaseFile aggr = aggregateFile(quarterDir, aggregator);
        if (aggr.exists()) {
            return aggr;
        }
        Queue<BaseFile> files = new LinkedList<BaseFile>();
        for (int i = 0; i < 3; i++) {
            BaseFile f = getAggregateFileForMonth(aggregator, year, i + (quarter * 3) + 1);
            if (BaseFile.notNullAndExists(f)) {
                files.add(f);
            } else {
                // dont want to stop, there maybe something we are querying for that is only specific to a quarter
                Log.util.warn("Unable to generate aggregate:%s for year: %s quarter: %s", aggregator.getFileExtension(), year, quarter);
            }
        }
        aggregator.setOutput(aggr);
        if (aggregator.merge(files)) {
            Log.util.info("Merged Quarter %s", aggregator.getFileExtension(), aggr);
            return aggr;
        }
        return null;
    }

    public BaseFile getAggregateFileForMonth(com.hitorro.util.io.largedata.compressedstreams.aggregator.Aggregator aggregator, int year, int month) throws Exception {
        BaseFile monthDir = summary(DirEnumUtil.getDirForMonth(dbDir, year, month));
        BaseFile aggr = aggregateFile(monthDir, aggregator);
        if (aggr.exists()) {
            return aggr;
        }
        // get file for each day!
        Queue<BaseFile> files = new LinkedList<BaseFile>();
        DirDateEnumerator enumerator = new DirDateEnumerator(dbDir);
        DateEnumAction action = new DayAggregationAction(files, aggregator, this);
        HTPredicate<Integer> constraint = DirEnumUtil.getMonthof(year, month);
        enumerator.executeYears(action, constraint);
        aggregator.setOutput(aggr);
        if (aggregator.merge(files)) {
            Log.util.info("Merged LongMonth %s", aggregator.getFileExtension(), aggr);
            return aggr;
        }
        return null;
    }

    public BaseFile getAggregateFileForDay(com.hitorro.util.io.largedata.compressedstreams.aggregator.Aggregator aggregator, int year, int month, int day) throws Exception {
        BaseFile monthDir = summary(DirEnumUtil.getDirForDay(dbDir, year, month, day));
        BaseFile aggr = aggregateFile(monthDir, aggregator);
        if (aggr.exists()) {
            return aggr;
        }
        com.hitorro.util.io.largedata.compressedstreams.aggregator.Aggregator inputAggr = aggregator.getInputFileType();
        if (inputAggr.getFileExtension().equals(baseAggregator.getFileExtension())) {
            // special case to deal with the "Base" file that we are going to use to derive from.  In this case we
            // dont create the source we mearly ensure its in the right place merged from stuff in the "log" directory.
            BaseFile f = AggregationUtil.ensureBaseAggregateMerged(dbDir, year, month, day, baseFactory);
            if (BaseFile.notNullAndExists(f)) {
                // Special flyweight case where we want the mserlog
                CompressedStreamIOIterator input = new CompressedStreamIOIterator(f, baseFactory);
                aggregator.setOutput(aggr);
                HTPredicate<CompressedStreamIO> oper = aggregator.getDefaultOperator();
                if (oper != null) {
                    aggregate(input, aggregator, oper);
                } else {
                    aggregate(input, aggregator);
                }

                return aggr;
            }
        }
        return null;
    }
}

