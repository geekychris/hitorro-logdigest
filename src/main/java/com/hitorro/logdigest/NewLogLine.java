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

import com.hitorro.util.io.csv.CSVFormattedWriter;

/**
 * Copyright (c) 2003 - present HiTorro All rights reserved. User: chris Date: Dec 12, 2005 Time: 10:58:08 AM
 */
public class NewLogLine extends LogLineBase {
    //  Apache Log Date Format

    private String _agent;

    private String _referrer;
    private String _requestLine;
    private String _requestProtocol;
    private boolean _shouldWrite;

    public NewLogLine() {

    }

    public void reset() {
        _agent = null;
        _referrer = null;
        _requestLine = null;
        _requestProtocol = null;
        _shouldWrite = false;
        super.reset();
    }

    public boolean getShouldWrite() {
        return _shouldWrite;
    }

    public void setShouldWrite(boolean flag) {
        _shouldWrite = flag;
    }

    public String getAgentAsString() {
        return _agent;
    }


    public String getReferrer() {
        return _referrer;
    }

    protected void setReferrer(String referrer) {
        _referrer = referrer;
    }

    public String getRequestProtocol() {
        return _requestProtocol;
    }

    protected void setRequestProtocol(String requestProtocol) {
        _requestProtocol = requestProtocol;
    }

    public int getStatusCode() {
        return _statusCode;
    }

    // ************* Honor base

    // ******************************************* PRIVATE SETTERS **************************************************

    public String getRequestLine() {
        return _requestLine;
    }

    public void setRequestLine(String requestLine) {
        _requestLine = requestLine;
    }

    protected void setAgent(String agent) {
        _agent = agent;
    }

    protected void setTimeStamp(int timeStamp) {
        _timeStamp = timeStamp;
    }


    public void writeCSVRow(final CSVFormattedWriter formatter) throws ArrayIndexOutOfBoundsException {
        //XXX TODO add writing code
    }

    @Override
    public long getSize() {
        return 0;
    }
}

