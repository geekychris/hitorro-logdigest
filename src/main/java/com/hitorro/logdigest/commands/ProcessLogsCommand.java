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
import com.hitorro.logdigest.LogProcessor;
import com.hitorro.logdigest.NewLogDigesterService;
import com.hitorro.util.basefile.fs.BaseFile;
import com.hitorro.util.commandandcontrol.Command;
import com.hitorro.util.commandandcontrol.CommandSession;
import com.hitorro.util.commandandcontrol.Response;
import com.hitorro.util.commandandcontrol.ano.CommandDef;

import java.io.IOException;

/**
 * Copyright (c) 2003 - present HiTorro All rights reserved. User: chris Date: Jan 14, 2008 Time: 7:53:33 AM
 */
@CommandDef(command = "logdigester.processlogs", description = "Process any outstanding log files and turn them into serlog format")
public class ProcessLogsCommand extends Command {
    public boolean execute(String rawValue, JVS args, Response response, CommandSession session) throws Exception {
        NewLogDigesterService ser = NewLogDigesterService.getService();
        if (ser == null) {
            this.writeSimpleError(response, "DigesterService not initialized.");
            return false;
        }
        BaseFile dbDir = ser.getDatabaseDir();
        dbDir.mkdir();
        LogProcessor lfp = ser.getLogProcessor();
        BaseFile errors = ser.getErrorsFile();
        BaseFile processed = ser.getProcessedDir();
        lfp.processLogFiles(getList(), processed, errors, dbDir);
        this.writeSuccess(response, "Processed log entries");
        return true;
    }

    private BaseFile[] getList() throws IOException {
        BaseFile dir = NewLogDigesterService.LogDigesterLogfileDir.apply();
        return dir.listFiles();
    }
}
