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

import com.hitorro.util.basefile.filters.FileExtension;
import com.hitorro.util.basefile.fs.BaseFile;
import com.hitorro.util.core.ArrayUtil;
import com.hitorro.util.core.Log;
import com.hitorro.util.io.largedata.iterator.BaseFileSelectTreeController;
import com.hitorro.logdigest.NewLogLine;

import java.io.IOException;
import java.util.Comparator;

/**
 */
public class LargeDataSetUtil {

    /**
     * Merge all input files found in the input directory into one file
     *
     * @param inputDir
     * @param outputDir
     * @param extension
     * @param outputExtension
     * @param preRemoveOutputFiles
     * @param comp
     * @param factory
     * @return
     * @throws java.io.IOException
     */
    public static final BaseFile mergeCompressedStreamIOFiles(BaseFile inputDir,
                                                              BaseFile outputDir,
                                                              String extension,
                                                              String outputExtension,
                                                              boolean preRemoveOutputFiles,
                                                              Comparator comp,
                                                              com.hitorro.util.io.largedata.BaseFileAccessingObjectFactory factory) throws Exception {
        return mergeCompressedStreamIOFiles(inputDir.listFiles(new FileExtension(extension, true)),
                outputDir, extension, outputExtension, preRemoveOutputFiles, comp, factory);
    }

    /**
     * Merge
     *
     * @param files
     * @param outputDir
     * @param extension
     * @param outputExtension
     * @param preRemoveOutputFiles
     * @param comp                 can be null, in which case it will use the factories default comparator.
     * @return
     * @throws IOException
     */
    public static final BaseFile mergeCompressedStreamIOFiles(BaseFile files[],
                                                              BaseFile outputDir,
                                                              String extension,
                                                              String outputExtension,
                                                              boolean preRemoveOutputFiles,
                                                              Comparator comp,
                                                              com.hitorro.util.io.largedata.BaseFileAccessingObjectFactory factory) throws Exception {
        if (ArrayUtil.nullOrEmpty(files)) {
            return null;
        }
        outputDir.mkdir();
        com.hitorro.util.io.largedata.CompressedStreamIteratorSink<NewLogLine> sink = new com.hitorro.util.io.largedata.CompressedStreamIteratorSink();
        // we assume input and output are the same and we can recycle the elements
        sink.setFactory(factory);
        if (comp == null) {
            comp = factory.getDefaultComparitor();
        }
        if (preRemoveOutputFiles) {
            if (extension.equalsIgnoreCase(outputExtension)) {
                Log.util.error("Cant preremove files with extension %s as the input extension is same", outputExtension);
            } else {
                BaseFile remove[] = outputDir.listFiles(new FileExtension(outputExtension, true));
                if (!ArrayUtil.nullOrEmpty(remove)) {
                    for (BaseFile f : remove) {
                        f.delete();
                    }
                }
            }
        }
        BaseFileSelectTreeController stc = new com.hitorro.util.io.largedata.CompressedStreamBaseFileSelectTreeController(outputDir,
                files,
                20,
                true,
                outputExtension,
                factory, true);
        return stc.merge();
    }
}
