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
import com.hitorro.logdigest.Log;
import com.hitorro.logdigest.LogFileProcessor;
import com.hitorro.logdigest.NewLogDigesterService;
import com.hitorro.util.basefile.fs.BaseFile;
import com.hitorro.util.basefile.tools.direnum.DateEnumAction;
import com.hitorro.util.basefile.tools.direnum.DirDateEnumerator;
import com.hitorro.util.commandandcontrol.Command;
import com.hitorro.util.commandandcontrol.CommandSession;
import com.hitorro.util.commandandcontrol.Response;
import com.hitorro.util.commandandcontrol.ano.CommandDef;
import com.hitorro.util.io.largedata.BaseFileAccessingObjectFactory;
import com.hitorro.util.io.largedata.CompressedStreamIOIterator;
import com.hitorro.util.io.largedata.compressedstreams.aggregator.AggregationUtil;

import java.io.IOException;

/**
 * Copyright (c) 2003 - present HiTorro All rights reserved. User: chris Date: Jan 14, 2008 Time: 8:15:45 AM
 */
@CommandDef(command = "logdigester.readallserlog", description = "test command to ensure that all serlogs can be read")
public class ReadTestCommand extends Command {
    public boolean execute(String rawValue, JVS args, Response response, CommandSession session) {
        NewLogDigesterService ser = NewLogDigesterService.getService();
        if (ser == null) {
            this.writeSimpleError(response, "DigesterService not initialized.");
            return false;
        }
        BaseFile dbDir = ser.getDatabaseDir();

        try {
            ReadTestAction pa = new ReadTestAction(dbDir);
            Log.util.info("Started merging");

            DirDateEnumerator dde = new DirDateEnumerator(dbDir);
            boolean result = dde.executeYears(pa, null);
        } catch (IOException e) {
            this.writeSimpleError(response, "Unable to read summary %s %e", e, e);
            return false;
        }
        this.writeSuccess(response, "Read log entries");
        return true;
    }
}

class ReadTestAction implements DateEnumAction {
    BaseFile dbDir;

    public ReadTestAction(BaseFile dir) {
        dbDir = dir;
    }

    public boolean executeDay(int year, int month, int day, BaseFile dayDir) {
        BaseFile f = null;
        try {
            Log.logdigest.info("Creating summary: %s %s %s", year, month, day);
            f = AggregationUtil.ensureBaseAggregateMerged(dbDir, year, month, day, LogFileProcessor.getLogLineFactory());

        } catch (IOException e) {
            Log.util.error("Exception %s %e for date: %s %s %s", e, e, year, month, day);
            return false;
        } catch (Exception e) {
            Log.util.error("Exception  %s %e for date: %s %s %s, file: %s", e, e, year, month, day, f);
        }
        try {

            if (BaseFile.notNullAndExists(f)) {
                BaseFileAccessingObjectFactory factory = LogFileProcessor.getLogLineFactory();
                CompressedStreamIOIterator iter = new CompressedStreamIOIterator(f, factory);
                while (iter.hasNext()) {
                    iter.next();
                }
                iter.close();
            }
        } catch (IOException e) {
            Log.util.error("Exception %s %e for date: %s %s %s", e, e, year, month, day);
            return false;
        } catch (Exception e) {
            Log.util.error("Exception  %s %e for date: %s %s %s, file: %s", e, e, year, month, day, f);
        }
        return true;
    }

    public boolean executeMonth(int year, int month, BaseFile dayDir) {
        return true;
    }
}

