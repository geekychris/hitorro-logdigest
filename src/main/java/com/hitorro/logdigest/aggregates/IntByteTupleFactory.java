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

import com.hitorro.util.core.iterator.LikeRowMerger;
import com.hitorro.util.io.largedata.BaseFileAccessingObjectFactory;

import java.util.Comparator;

/**
 */
public class IntByteTupleFactory extends BaseFileAccessingObjectFactory<IntByteTuple> {
    public IntByteTupleFactory() {
        setComparitor(new DefaultIntByteTupleComparator());
        setMerger(new IntByteTupleMerger());
        setNewFunction(IntByteTupleFactory::getNewValue);
    }

    public static IntByteTuple getNewValue(Object o) {
        return new IntByteTuple();
    }
}

/**
 * Simply apply the frequencies of like rows.
 */
class IntByteTupleMerger implements LikeRowMerger<IntByteTuple> {
    public IntByteTuple apply(final IntByteTuple old, final IntByteTuple newval) {
        newval.f += old.f;
        return newval;
    }
}

class DefaultIntByteTupleComparator implements Comparator<IntByteTuple> {
    public int compare(final IntByteTuple intByteTuple, final IntByteTuple t) {
        if (intByteTuple.i > t.i) {
            return 1;
        }
        if (intByteTuple.i < t.i) {
            return -1;
        }
        if (intByteTuple.b > t.b) {
            return 1;
        }
        if (intByteTuple.b < t.b) {
            return -1;
        }
        return 0;
    }
}
