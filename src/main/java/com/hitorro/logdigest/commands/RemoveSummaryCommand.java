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
import com.hitorro.logdigest.NewLogDigesterService;
import com.hitorro.util.basefile.fs.BaseFile;
import com.hitorro.util.basefile.tools.direnum.DirDateEnumerator;
import com.hitorro.util.commandandcontrol.Command;
import com.hitorro.util.commandandcontrol.CommandSession;
import com.hitorro.util.commandandcontrol.Response;
import com.hitorro.util.commandandcontrol.ano.CommandDef;
import com.hitorro.util.io.largedata.RemoveSummary;

import java.io.IOException;

/**
 */
@CommandDef(command = "logdigester.removesummary", description = "Remove all log summary files.")
public class RemoveSummaryCommand extends Command {
    public boolean execute(String rawValue, JVS args, Response response, CommandSession session) {
        NewLogDigesterService ser = NewLogDigesterService.getService();
        if (ser == null) {
            this.writeSimpleError(response, "DigesterService not initialized.");
            return false;
        }
        BaseFile dbDir = ser.getDatabaseDir();


        try {
            RemoveSummary rm = new RemoveSummary(dbDir);
            DirDateEnumerator dde = new DirDateEnumerator(dbDir);
            boolean result = dde.executeYears(rm, null);

        } catch (IOException e) {
            this.writeSimpleError(response, "Unable to remove summary %s %e", e, e);
            return false;
        }
        this.writeSuccess(response, "Processed log entries");
        return true;
    }
}
