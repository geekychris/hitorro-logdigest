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
package com.hitorro.logdigest.aggregates;


import gnu.trove.iterator.TIntIntIterator;
import gnu.trove.map.hash.TIntIntHashMap;
import com.hitorro.logdigest.NewLogLine;
import com.hitorro.util.basefile.fs.BaseFile;
import com.hitorro.util.io.largedata.BaseFileAccessingObjectFactory;
import com.hitorro.util.io.largedata.CompressedStreamIO;
import com.hitorro.util.io.largedata.compressedstreams.COutputStream;
import com.hitorro.util.io.largedata.compressedstreams.aggregator.AggregationUtil;
import com.hitorro.util.io.largedata.compressedstreams.aggregator.Aggregator;

import java.io.IOException;
import java.util.Arrays;
import java.util.Queue;

/**
 * <p/>
 * Copyright (c) 2003 - present HiTorro All rights reserved. User: chris Date: Dec 27, 2005 Time: 4:58:41 PM
 */
public abstract class SingleIntAggregator implements Aggregator<NewLogLine, IntTuple> {
    private TIntIntHashMap m_map = new TIntIntHashMap();
    private BaseFile outputFile;

    public boolean aggregate(NewLogLine nll) {
        int i = getInt(nll);
        int f = m_map.get(i);
        if (f == 0) {
            m_map.put(i, 1);
        } else {
            m_map.increment(i);
        }
        return true;
    }

    public abstract int getInt(NewLogLine nll);

    public void reset() {
        m_map.clear();
    }

    public boolean setOutput(final BaseFile targetFile) {
        outputFile = targetFile;
        return true;
    }

    public boolean merge(final Queue<BaseFile> sourceFiles) throws Exception {
        return AggregationUtil.mergeCompressedStreamFromAggregator(sourceFiles, this, outputFile);
    }


    public boolean close() throws IOException {
        if (m_map.size() == 0) {
            return false;
        }
        IntTuple arr[] = new IntTuple[m_map.size()];
        int i = 0;
        IntTuple curr;
        int l;
        for (TIntIntIterator it = m_map.iterator(); it.hasNext(); ) {
            curr = new IntTuple();
            it.advance();
            curr.setFrequency(it.value());
            l = it.key();
            curr.setInt(l);
            arr[i] = curr;
            i++;
        }
        Arrays.sort(arr);

        writeTable(arr);
        // we do the sort and save here.
        return true;
    }

    private void writeTable(final CompressedStreamIO[] arr) throws IOException {
        COutputStream fs = outputFile.getCOutputStream();
        for (CompressedStreamIO out : arr) {
            out.write(fs);
        }
        arr[0].close(fs);
        fs.close();
    }

    public BaseFileAccessingObjectFactory<IntTuple> getTargetObjectFactory() {
        return new IntTupleFactory();
    }


    public Aggregator[] getDependcies() {
        return new Aggregator[]{new MerglogAggregator()};
    }

    public Aggregator getInputFileType() {
        return new MerglogAggregator();
    }
}