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

import com.hitorro.logdigest.aggregates.MediaIdAggregator;
import com.hitorro.logdigest.aggregates.MediaIdMediaTypeAggregator;
import com.hitorro.logdigest.aggregates.PostAggregator;
import com.hitorro.logdigest.aggregates.RequestTypeAggregator;
import com.hitorro.logdigest.commands.ProcessLogsCommand;
import com.hitorro.logdigest.commands.ReadTestCommand;
import com.hitorro.logdigest.commands.RemoveSummaryCommand;
import com.hitorro.logdigest.commands.RunAggregate;
import com.hitorro.network.rpc.cluster.ClusterService;
import com.hitorro.util.basefile.fs.BaseFile;
import com.hitorro.util.io.largedata.compressedstreams.aggregator.AggregatorContext;
import com.hitorro.util.json.keys.BasefileProperty;
import com.hitorro.util.startupframework.phases.ServiceDefinition;

/**
 */
@ServiceDefinition(dependentService = {},
        shortName = "newlogdigest",
        description = "new log digest service",
        debugCommands = {ProcessLogsCommand.class, RemoveSummaryCommand.class, ReadTestCommand.class, RunAggregate.class},
        typeManagedClasses = {},
        uiDirectories = {})
public class NewLogDigesterService {

    public static BasefileProperty LogDigesterLogfileDir = new BasefileProperty("logfiledir",
            "default log file location",
            "file://${HT_BIN}/testinput/ht/logdigest/LogDigesterTestService");
    public static BasefileProperty LogDigesterDBDir = new BasefileProperty("logdbdir",
            "default log file location",
            "file://${HT_HOME}/logdb");

    public static BasefileProperty LogDigesterProcessed = new BasefileProperty("logprocessed",
            "default log file location",
            "file://${HT_HOME}/logprocessed");

    public static BasefileProperty ErrorsFileDir = new BasefileProperty("errors",
            "location for errors",
            "file://${HT_HOME}/logprocessorerrors/errorFile");
    public static final String LogDigestKey = "logdigest";
    private static NewLogDigesterService service;
    private BaseFile dbd;
    private BaseFile errorsFile;
    private LogProcessor logProcessor;
    private BaseFile databaseDir;
    private BaseFile processedDir;
    private AggregatorContext aggregators = new AggregatorContext();

    public static NewLogDigesterService getService() {
        return service;
    }


    public AggregatorContext getAggregators() {
        return aggregators;
    }


    public String init(boolean dbInit, final boolean upgrading, final long currentVersion, final long targetVersion) {
        ClusterService.getThisInstanceDefinition().addInstanceCapability(LogDigestKey, "", "", "", true);
        service = this;
        initAggregatorContext();

        return null;
    }

    private void initAggregatorContext() {
        aggregators.add(new PostAggregator());
        aggregators.add(new MediaIdAggregator());
        aggregators.add(new MediaIdMediaTypeAggregator());
        aggregators.add(new RequestTypeAggregator());
    }


    public String start(boolean dbInit) {
        BaseFile p = null;

        p = LogDigesterProcessed.apply();
        if (p != null) {
            setProcessedDir(p);
            getProcessedDir().mkdir();
        }
        setErrorsFile(ErrorsFileDir.apply());
        setLogProcessor(new LogProcessor());
        dbd = LogDigesterDBDir.apply();
        setDatabaseDir(dbd);

        return null;
    }

    public String deInit() {
        return null;
    }

    public BaseFile getErrorsFile() {
        return errorsFile;
    }

    public void setErrorsFile(final BaseFile errorsFile) {
        this.errorsFile = errorsFile;
    }

    public LogProcessor getLogProcessor() {
        return logProcessor;
    }

    public void setLogProcessor(final LogProcessor logProcessor) {
        this.logProcessor = logProcessor;
    }

    public BaseFile getDatabaseDir() {
        return databaseDir;
    }

    public void setDatabaseDir(final BaseFile databaseDir) {
        this.databaseDir = databaseDir;
    }

    public BaseFile getProcessedDir() {
        return processedDir;
    }

    public void setProcessedDir(final BaseFile processedDir) {
        this.processedDir = processedDir;
    }
}
