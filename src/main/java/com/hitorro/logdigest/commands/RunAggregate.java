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
package com.hitorro.logdigest.commands;

import com.hitorro.jsontypesystem.JVS;
import com.hitorro.logdigest.LogFileProcessor;
import com.hitorro.logdigest.NewLogDigesterService;
import com.hitorro.logdigest.aggregates.MerglogAggregator;
import com.hitorro.util.basefile.fs.BaseFile;
import com.hitorro.util.commandandcontrol.ano.CommandArgument;
import com.hitorro.util.commandandcontrol.ano.CommandDef;
import com.hitorro.util.core.Log;
import com.hitorro.util.core.string.Fmt;
import com.hitorro.util.core.string.StringUtil;
import com.hitorro.util.io.csv.CSVFormattedWriter;
import com.hitorro.util.io.csv.UnsupportedFormatterException;
import com.hitorro.util.io.largedata.compressedstreams.aggregator.AggregationHierarchyQuery;
import com.hitorro.util.io.largedata.compressedstreams.aggregator.Aggregator;
import com.hitorro.util.io.largedata.compressedstreams.aggregator.AggregatorContext;
import com.hitorro.util.json.keys.IntegerProperty;
import com.hitorro.util.json.keys.StringProperty;

/**
 * Copyright (c) 2003 - present HiTorro All rights reserved. User: chris Date: Jan 16, 2008 Time: 10:56:49 AM
 */
@CommandDef(command = "logdigester.runaggregate", description = "Run an aggregator")
public class RunAggregate extends com.hitorro.util.commandandcontrol.Command {
    @CommandArgument(required = false)
    private StringProperty aggregate = new StringProperty("aggregate", "Name of aggregate to chunkParser", null);
    @CommandArgument(required = false)
    private IntegerProperty year = new IntegerProperty("year", "year to chunkParser", -1);
    @CommandArgument(required = false)
    private IntegerProperty month = new IntegerProperty("month", "month", -1);
    @CommandArgument(required = false)
    private IntegerProperty day = new IntegerProperty("day", "day", -1);
    @CommandArgument(required = false)
    private IntegerProperty quarter = new IntegerProperty("quarter", "day", -1);

    public boolean execute(String rawValue, JVS args, com.hitorro.util.commandandcontrol.Response response, com.hitorro.util.commandandcontrol.CommandSession session) throws Exception {
        NewLogDigesterService ser = NewLogDigesterService.getService();

        if (ser == null) {
            this.writeSimpleError(response, "DigesterService not initialized.");
            return false;
        }
        BaseFile dbDir = ser.getDatabaseDir();
        AggregatorContext context = ser.getAggregators();

        String agr = aggregate.apply(args);
        if (StringUtil.nullOrEmptyOrBlankString(agr)) {
            response.addInfo(com.hitorro.util.commandandcontrol.InfoLevel.Error, "Aggregate name is required");
            context.list(response);
            response.end();
            return false;
        }
        Aggregator aggr = context.getAggregator(agr);
        if (aggr == null) {
            response.addInfo(com.hitorro.util.commandandcontrol.InfoLevel.Error, Fmt.S("Aggregate does not exist: %s", agr));
            context.list(response);
            response.end();
            return false;
        }
        int y = year.apply(args);
        int m = month.apply(args);
        int d = day.apply(args);
        int q = quarter.apply(args);
        if (y < 2006 || y > 2020) {
            response.addInfo(com.hitorro.util.commandandcontrol.InfoLevel.Error, "Year not within expected range");
            return false;
        }

        com.hitorro.util.io.largedata.BaseFileAccessingObjectFactory logFactory =
                new com.hitorro.util.io.largedata.BaseFileAccessingObjectFactory();
        logFactory.setBaseFileSinkMapper(new com.hitorro.util.io.largedata.CompressedIOSinkMapper(logFactory))
                .setBaseFileToChainingMapper(new com.hitorro.util.io.largedata.CompressedIOIteratorMapper(logFactory));

        AggregationHierarchyQuery query = new AggregationHierarchyQuery(dbDir, new MerglogAggregator(), LogFileProcessor.getLogLineFactory());
        BaseFile f = get(query, aggr, y, m, d, q);

        if (!BaseFile.notNullAndExists(f)) {
            response.addInfo(com.hitorro.util.commandandcontrol.InfoLevel.Error, "Aggregation file did not get created, check log.");
            context.list(response);
            response.end();
            return false;
        }
        CSVFormattedWriter writer = aggr.getCSVFormattedWriter();
        com.hitorro.util.commandandcontrol.CSVWriterResponse responseWrapper = new com.hitorro.util.commandandcontrol.CSVWriterResponse(response, writer, "Aggregation", "AggregateRow");

        com.hitorro.util.io.largedata.BaseFileAccessingObjectFactory fact =
                new com.hitorro.util.io.largedata.BaseFileAccessingObjectFactory();
        fact.setBaseFileSinkMapper(new com.hitorro.util.io.largedata.CompressedIOSinkMapper(fact))
                .setBaseFileToChainingMapper(new com.hitorro.util.io.largedata.CompressedIOIteratorMapper(fact));

        com.hitorro.util.io.largedata.CompressedStreamIOIterator iter = new com.hitorro.util.io.largedata.CompressedStreamIOIterator(f, fact);
        com.hitorro.util.io.largedata.CompressedStreamIO row;

        // Looks a tad funky.  We pull out of the tuple the data and mapp it into thw formatted writer
        // then we call the writer to write that row with the csvwriter which in this case is mapped to a response object
        writer.writeHeader(responseWrapper);
        while (iter.hasNext()) {
            row = iter.next();
            try {
                row.writeCSVRow(writer);
                writer.write(responseWrapper);
            } catch (UnsupportedFormatterException e) {
                Log.util.error("%s %e", e, e);
            }
        }
        this.writeSuccess(response, "Read log entries");
        return true;
    }

    private BaseFile get(AggregationHierarchyQuery query, Aggregator aggr, int year, int month, int day, int quarter) throws Exception {
        if (quarter != -1) {
            return query.getAggregateFileForQuarter(aggr, year, quarter);
        }

        if (month != -1) {
            if (day != -1) {
                return query.getAggregateFileForDay(aggr, year, month, day);
            }
            return query.getAggregateFileForMonth(aggr, year, month);
        }
        return query.getAggregateFileForYear(aggr, year);
    }
}