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
public class IntTuple extends AggregatorTuple {
    public static final int EndPattern = 310687;
    protected int i;
    protected int f;

    public void writeCSVRow(CSVFormattedWriter formatter) throws UnsupportedFormatterException, ArrayIndexOutOfBoundsException {
        formatter.formatInt(0, i);
        formatter.formatInt(1, f);
    }

    public void consume(final AggregatorTuple aggregatorTuple) {
        IntTuple t = (IntTuple) aggregatorTuple;
        f += t.f;
    }

    public int compareTo(final AggregatorTuple aggregatorTuple) {
        IntTuple t = (IntTuple) aggregatorTuple;
        if (i > t.i) {
            return 1;
        }
        if (i < t.i) {
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

    public int getFrequency() {
        return f;
    }

    public void setFrequency(int f) {
        this.f = f;
    }

    public void write(final COutputStream os) throws IOException {
        os.writeVInt(i);
        os.writeVInt(f);
    }

    public boolean read(final CInputStream is) throws IOException {
        i = is.readVInt();
        f = is.readVInt();
        //Console.println("%s %s", i, f);
        return i != EndPattern || f != EndPattern;
    }

    public boolean close(final COutputStream os) throws IOException {
        os.writeVInt(EndPattern);
        os.writeVInt(EndPattern);
        return false;
    }

    @Override
    public long getSize() {
        return 0;
    }
}
/*
runaggregate aggregate=agr_requesttype year=2007 month=12 day=1;
runaggregate aggregate=agr_requesttype year=2007 month=12 day=2;
runaggregate aggregate=agr_requesttype year=2007 month=12 day=3;
runaggregate aggregate=agr_requesttype year=2007 month=12 day=4;
runaggregate aggregate=agr_requesttype year=2007 month=12 day=5;
runaggregate aggregate=agr_requesttype year=2007 month=12 day=6;
runaggregate aggregate=agr_requesttype year=2007 month=12 day=7;
runaggregate aggregate=agr_requesttype year=2007 month=12 day=8;
runaggregate aggregate=agr_requesttype year=2007 month=12 day=9;
runaggregate aggregate=agr_requesttype year=2007 month=12 day=10;
runaggregate aggregate=agr_requesttype year=2007 month=12 day=11;
runaggregate aggregate=agr_requesttype year=2007 month=12 day=12;
runaggregate aggregate=agr_requesttype year=2007 month=12 day=13;
runaggregate aggregate=agr_requesttype year=2007 month=12 day=14;
runaggregate aggregate=agr_requesttype year=2007 month=12 day=15;
runaggregate aggregate=agr_requesttype year=2007 month=12 day=16;
runaggregate aggregate=agr_requesttype year=2007 month=12 day=17;
runaggregate aggregate=agr_requesttype year=2007 month=12 day=18;
runaggregate aggregate=agr_requesttype year=2007 month=12 day=19;
runaggregate aggregate=agr_requesttype year=2007 month=12 day=20;
runaggregate aggregate=agr_requesttype year=2007 month=12 day=21;
runaggregate aggregate=agr_requesttype year=2007 month=12 day=22;
runaggregate aggregate=agr_requesttype year=2007 month=12 day=23;
runaggregate aggregate=agr_requesttype year=2007 month=12 day=24;
runaggregate aggregate=agr_requesttype year=2007 month=12 day=25;
runaggregate aggregate=agr_requesttype year=2007 month=12 day=26;
runaggregate aggregate=agr_requesttype year=2007 month=12 day=27;
runaggregate aggregate=agr_requesttype year=2007 month=12 day=28;
runaggregate aggregate=agr_requesttype year=2007 month=12 day=29;
runaggregate aggregate=agr_requesttype year=2007 month=12 day=30;
runaggregate aggregate=agr_requesttype year=2007 month=12 day=31;
*/



