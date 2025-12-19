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

import gnu.trove.iterator.TLongIntIterator;
import gnu.trove.map.hash.TLongIntHashMap;
import com.hitorro.logdigest.NewLogLine;
import com.hitorro.util.basefile.fs.BaseFile;
import com.hitorro.util.core.ByteManipulationUtil;
import com.hitorro.util.io.largedata.BaseFileAccessingObjectFactory;
import com.hitorro.util.io.largedata.CompressedStreamIO;
import com.hitorro.util.io.largedata.compressedstreams.COutputStream;
import com.hitorro.util.io.largedata.compressedstreams.aggregator.AggregationUtil;
import com.hitorro.util.io.largedata.compressedstreams.aggregator.Aggregator;

import java.io.IOException;
import java.util.Arrays;
import java.util.Queue;

/**
 * Copyright (c) 2003 - present HiTorro All rights reserved. User: chris Date: Dec 27, 2005 Time: 6:31:50 PM ordinal and a
 * byte type aggregator
 */
public abstract class IntByteComboAggregator implements Aggregator<NewLogLine, IntByteTuple> {
    private TLongIntHashMap m_map = new TLongIntHashMap();

    private BaseFile outputFile;
    private int counter = 0;

    public boolean aggregate(NewLogLine nll) {
        counter++;
        int i = getInt(nll);
        byte b = getByte(nll);
        long l = ByteManipulationUtil.combineIntByte(i, b);
        int f = m_map.get(l);
        if (f == 0) {
            m_map.put(l, 1);
        } else {
            m_map.increment(i);
        }
        return true;
    }

    public abstract int getInt(NewLogLine nll);

    public abstract byte getByte(NewLogLine nll);

    public void reset() {
        m_map.clear();
    }

    public boolean setOutput(final BaseFile targetFile) {
        outputFile = targetFile;
        return true;
    }

    public boolean close() throws IOException {
        if (m_map.size() == 0) {
            return false;
        }
        IntByteTuple arr[] = new IntByteTuple[m_map.size()];
        int i = 0;
        IntByteTuple curr;
        long l;
        for (TLongIntIterator it = m_map.iterator(); it.hasNext(); ) {
            curr = new IntByteTuple();
            it.advance();
            curr.setFrequency(it.value());
            l = it.key();
            curr.setInt(ByteManipulationUtil.intByteIntPart(l));
            curr.setByte(ByteManipulationUtil.intByteBytePart(l));
            arr[i] = curr;
            i++;
        }
        Arrays.sort(arr);

        writeTable(arr);

        // we do the sort and save here.
        return false;
    }

    private void writeTable(final IntByteTuple[] arr) throws IOException {
        COutputStream fs = outputFile.getCOutputStream();
        for (CompressedStreamIO out : arr) {
            out.write(fs);
        }
        arr[0].close(fs);
        fs.close();
    }

    public BaseFileAccessingObjectFactory<IntByteTuple> getTargetObjectFactory() {
        return new IntByteTupleFactory();
    }

    public Aggregator[] getDependcies() {
        return new Aggregator[]{new MerglogAggregator()};
    }

    public Aggregator getInputFileType() {
        return new MerglogAggregator();
    }

    public boolean merge(final Queue<BaseFile> sourceFiles) throws Exception {
        if (sourceFiles.size() == 0) {
            return false;
        }
        return AggregationUtil.mergeCompressedStreamFromAggregator(sourceFiles, this, outputFile);
    }


}
