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
import com.hitorro.util.core.Log;
import com.hitorro.util.core.string.Fmt;
import com.hitorro.util.io.largedata.BaseFileAccessingObjectFactory;
import com.hitorro.util.io.largedata.LargeDataSetUtil;

import java.io.IOException;
import java.util.Queue;

/**
 */
public class AggregationUtil {
    public static final String SerLogExt = "serlog";
    // merged server log file
    public static final String MSerLogExt = "mserlog";
    public static final String SerLog = AggregationHierarchyQuery.Merged + MSerLogExt;

    public static final boolean removeSummaryDir(BaseFile dbDir, int y, int m, int d) {
        BaseFile sumDir = getSummaryDir(y, m, d, dbDir);
        try {
            sumDir.deleteContentOfDir(true);
        } catch (IOException e) {
            Log.util.error("Unable to delete directory: %s %s %e", sumDir, e, e);
            return false;
        }
        return true;
    }

    public static final boolean removeSummaryDir(BaseFile dbDir, int y, int m) {
        BaseFile sumDir = getSummaryDir(y, m, dbDir);
        try {
            sumDir.deleteContentOfDir(true);
        } catch (IOException e) {
            Log.util.error("Unable to delete directory: %s %s %e", sumDir, e, e);
            return false;
        }
        return true;
    }

    public static final boolean removeSummaryDir(BaseFile dbDir, int y) {
        BaseFile sumDir = getSummaryDir(y, dbDir);
        try {
            sumDir.deleteContentOfDir(true);
        } catch (IOException e) {
            Log.util.error("Unable to delete directory: %s %s %e", sumDir, e, e);
            return false;
        }
        return true;
    }

    private static BaseFile getSummaryDir(final int y, int m, int d, final BaseFile dbDir) {
        return dbDir.getChild(Fmt.S("%s/%s/%s/summary", y, m, d));
    }

    private static BaseFile getSummaryDir(final int y, int m, final BaseFile dbDir) {
        return dbDir.getChild(Fmt.S("%%s/%s/summary", y, m));
    }

    private static BaseFile getSummaryDir(final int y, final BaseFile dbDir) {
        return dbDir.getChild(Fmt.S("%s/summary", y));
    }

    private static BaseFile getLogDir(final int y, int m, int d, final BaseFile dbDir) {
        return dbDir.getChild(Fmt.S("%s/%s/%s/logs", y, m, d));
    }

    public static final BaseFile getPath(boolean logDir, BaseFile dbDir, String newFileName, int y, int m, int d) {
        if (logDir) {
            return dbDir.getChild(Fmt.S("%s/%s/%s/logs/%s", y, m, d, newFileName));
        }

        return dbDir.getChild(Fmt.S("%s/%s/%s/summary/%s", y, m, d, newFileName));
    }

    /**
     * Whatever is the basis for all derived aggregats, we need to be able to take the raw input aggregates and generate
     * a single file.
     *
     * @param dbDir
     * @param y
     * @param m
     * @param d
     * @return
     * @throws IOException
     */
    public static final BaseFile ensureBaseAggregateMerged(BaseFile dbDir, int y, int m, int d, BaseFileAccessingObjectFactory factory) throws Exception {
        BaseFile log = getPath(false, dbDir, SerLog, y, m, d);
        if (BaseFile.notNullAndExists(log)) {
            return log;
        }
        BaseFile logDir = getLogDir(y, m, d, dbDir);
        BaseFile summaryDir = getSummaryDir(y, m, d, dbDir);

        BaseFile tempFile = LargeDataSetUtil.mergeCompressedStreamIOFiles(logDir, summaryDir, SerLogExt, MSerLogExt, true, null, factory);
        if (BaseFile.notNullAndExists(tempFile)) {
            BaseFile real = summaryDir.getChild(SerLog);
            tempFile.renameTo(real);
            return real;
        }
        return null;
    }

    public static boolean mergeCompressedStreamFromAggregator(final Queue<BaseFile> sourceFiles, com.hitorro.util.io.largedata.compressedstreams.aggregator.Aggregator aggr, BaseFile outputFile) throws Exception {
        if (sourceFiles.size() == 0) {
            return false;
        }
        if (sourceFiles.size() == 1) {
            // we only want to copy the file, no need to apply.
            BaseFile f = sourceFiles.remove();
            outputFile.copy(f);
            return true;
        }
        BaseFileAccessingObjectFactory factory = aggr.getTargetObjectFactory();
        /*
        (BaseFile files[],
       BaseFile outputDir,
       String extension,
       String outputExtension,
       boolean preRemoveOutputFiles,
       Comparator comp,
       CompressedStreamIOObjectFactory factory) throws IOException
         */
        BaseFile outFile = LargeDataSetUtil.mergeCompressedStreamIOFiles(sourceFiles.toArray(new BaseFile[sourceFiles.size()]),
                outputFile.getParent(),
                aggr.getFileExtension(),
                aggr.getFileExtension(),
                true,
                factory.getDefaultComparitor(),
                factory);
        if (BaseFile.notNullAndExists(outFile)) {
            return outFile.renameTo(outputFile);
        }
        return false;
    }

}
