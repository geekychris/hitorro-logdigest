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
package com.hitorro.logdigest.old;

import com.hitorro.logdigest.NewLogLine;
import com.hitorro.util.basefile.fs.BaseFile;
import com.hitorro.util.core.iterator.CloseableIterator;
import com.hitorro.util.io.largedata.BaseFileAccessingObjectFactory;
import com.hitorro.util.io.largedata.compressedstreams.COutputStream;

import java.io.IOException;

/**
 * <p/>
 * Copyright (c) 2003 - present HiTorro All rights reserved. User: chris Date: Dec 27, 2005 Time: 2:29:52 PM
 */
public class LogLineWriter {
    private COutputStream fsOutputStream;
    private BaseFileAccessingObjectFactory factory;

    public LogLineWriter(BaseFile f, BaseFileAccessingObjectFactory fac) throws IOException {
        factory = fac;
        setFile(f);
    }

    public void setFile(BaseFile f) throws IOException {
        fsOutputStream = f.getCOutputStream();
    }

    public boolean write(CloseableIterator<NewLogLine> iter) throws IOException {
        NewLogLine ll;
        while (iter.hasNext()) {
            ll = iter.next();
            if (ll != null) {
                ll.write(fsOutputStream);
            }
        }
        // ensure we close it off.
        close();
        return true;
    }

    public boolean write(NewLogLine ll) throws IOException {
        ll.write(fsOutputStream);
        return true;
    }

    public boolean close() throws IOException {
        new NewLogLine().close(fsOutputStream);
        fsOutputStream.close();
        return true;
    }
}
