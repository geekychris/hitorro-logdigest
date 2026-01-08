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

import com.hitorro.util.io.csv.CSVFormattedWriter;
import com.hitorro.util.io.csv.UnsupportedFormatterException;
import com.hitorro.util.io.largedata.compressedstreams.CInputStream;
import com.hitorro.util.io.largedata.compressedstreams.COutputStream;
import com.hitorro.util.io.largedata.compressedstreams.aggregator.AggregatorTuple;

import java.io.IOException;

/**
 */
public class IntByteTuple extends AggregatorTuple {
    /**
     * EndPattern is used to determine if we have hit the end of the stream
     */
    public static final int EndPattern = 310687;
    protected int i;
    protected byte b;
    protected int f;

    public void writeCSVRow(CSVFormattedWriter formatter) throws UnsupportedFormatterException, ArrayIndexOutOfBoundsException {
        formatter.formatInt(0, i);
        formatter.formatByte(1, b);
        formatter.formatInt(2, f);
    }

    public void consume(final AggregatorTuple aggregatorTuple) {
        IntByteTuple t = (IntByteTuple) aggregatorTuple;
        f += t.f;
    }

    public int compareTo(final AggregatorTuple aggregatorTuple) {
        IntByteTuple t = (IntByteTuple) aggregatorTuple;
        if (i > t.i) {
            return 1;
        }
        if (i < t.i) {
            return -1;
        }
        if (b > t.b) {
            return 1;
        }
        if (b < t.b) {
            return -1;
        }
        return 0;
    }

    public int getInt() {
        return i;
    }

    public void setInt(int i) {
        this.i = i;
    }

    public byte getByte() {
        return b;
    }

    public void setByte(byte b) {
        this.b = b;
    }

    public int getFrequency() {
        return f;
    }

    public void setFrequency(int f) {
        this.f = f;
    }

    public void write(final COutputStream os) throws IOException {
        os.writeVInt(i);
        os.writeByte(b);
        os.writeVInt(f);
    }

    public boolean read(final CInputStream fis) throws IOException {
        i = fis.readVInt();
        b = fis.readByte();
        f = fis.readVInt();
        return i != EndPattern || f != EndPattern;
    }

    public boolean close(final COutputStream os) throws IOException {
        os.writeVInt(EndPattern);
        os.writeByte((byte) 0);
        os.writeVInt(EndPattern);
        return true;
    }

    @Override
    public long getSize() {
        return 0;
    }
}
