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

import com.hitorro.util.core.hash.FPHash64;
import com.hitorro.util.io.largedata.CompressedStreamIO;
import com.hitorro.util.io.largedata.compressedstreams.CInputStream;
import com.hitorro.util.io.largedata.compressedstreams.COutputStream;

import java.io.IOException;

/**
 * Copyright (c) 2003 - present HiTorro All rights reserved. User: chris Date: Dec 12, 2005 Time: 1:17:16 PM basic methods
 * of a log line that provide numeric based representations of fields we care about.
 */
public abstract class LogLineBase implements Comparable<LogLineBase>, CompressedStreamIO {
    public static final int BC = 0;
    public static final int POSTID = 1;
    public static final int HiTorroID = 2;
    public static final int REQUESTURI = 3;
    public static final int ACTIONTYPE = 4;
    public static final int MEDIATYPE = 5;
    public static final int CATEGORY = 5;
    // bc     postid   HiTorro Id    request uri  actiontype    mediatype  category
    public static final boolean options[][] = {{false, false, false, false, false, false, false},      //Beacon
            {false, false, false, false, false, false, false},      //Resource
            {true, true, true, false, false, true, false},       //Download
            {true, false, false, true, false, false, false},      //hostingsite
            {false, false, false, false, false, false, false},      //comment
            {true, false, true, false, true, false, false},      //action
            {false, false, false, false, false, false, false},      //player
            {true, false, false, false, false, false, false},      //playlist
            {true, false, false, false, false, false, false},      //provisionchild
            {false, false, true, false, false, false, false},      //postpage
            {false, true, false, false, false, false, true}};     //feed


    protected int _timeStamp;
    protected int _ipAddress;
    protected byte _requestType;


    protected int _bytesSent;

    protected byte _httpMethod;
    protected short _statusCode;

    protected byte _application;
    protected int _applicationVersion;
    protected byte _platform;
    protected int _platformVersion;
    protected long _combinedPlatformVersion;
    protected long _combinedApplicationVersion;

    // starting in PID_
    protected int _HiTorroId;
    protected int _postId;
    protected byte _mediaType;
    protected byte _actionType;

    protected int _siteId;

    protected String _bc;

    protected String _requestURI;

    protected String _category;


    public void reset() {
        _timeStamp = 0;
        _ipAddress = 0;
        _requestType = 0;
        _bytesSent = 0;
        _httpMethod = 0;
        _statusCode = 0;
        _HiTorroId = 0;
        _postId = 0;
        _mediaType = 0;
        _actionType = 0;

        _application = 0;
        _applicationVersion = 0;
        _platform = 0;
        _platformVersion = 0;
        _combinedPlatformVersion = 0;
        _combinedApplicationVersion = 0;

        _bc = null;
        _requestURI = null;
        _category = null;
    }

    public String getCategory() {
        return _category;
    }

    public void setCategory(String cat) {
        _category = cat;
    }

    public int getSiteId() {
        return _siteId;
    }

    public void setSiteId(int siteId) {
        _siteId = siteId;
    }

    public int compareTo(final LogLineBase logLineBase) {
        if (_timeStamp > logLineBase._timeStamp) {
            return 1;
        }
        if (_timeStamp < logLineBase._timeStamp) {
            return -1;
        }
        return 0;
    }

    public String getRequestURI() {
        return _requestURI;
    }

    public void setRequestURI(String requestURI) {
        _requestURI = requestURI;
    }

    public byte getActionType() {
        return _actionType;
    }

    public void setActionType(byte type) {
        _actionType = type;
    }

    public long getUserHash() {
        long fpHash64;
        long ipAddressLong = (long) getIpAddress();

        fpHash64 = FPHash64.combineFingerPrints(_combinedApplicationVersion, _combinedPlatformVersion);
        fpHash64 = FPHash64.combineFingerPrints(ipAddressLong, fpHash64);

        return fpHash64;
    }

    public boolean close(COutputStream os) throws IOException {
        os.writeInt(0);
        return true;
    }

    public void write(COutputStream os) throws IOException {
        os.writeInt(_timeStamp);
        os.writeInt(_ipAddress);
        os.writeByte(_application);
        os.writeInt(_applicationVersion);
        os.writeByte(_platform);
        os.writeInt(_platformVersion);
        os.writeByte(_requestType);
        os.writeVInt(_siteId);
        boolean row[] = options[_requestType];

        if (row[BC]) {
            writeString(os, _bc);
        }
        if (row[POSTID]) {
            os.writeVInt(_postId);
        }
        if (row[HiTorroID]) {
            os.writeVInt(_HiTorroId);
        }
        if (row[REQUESTURI]) {
            writeString(os, _requestURI);
        }
        if (row[ACTIONTYPE]) {
            os.writeByte(_actionType);
        }
        if (row[MEDIATYPE]) {
            os.writeByte(_mediaType);
        }
        if (row[CATEGORY]) {
            writeString(os, _category);
        }
    }

    private void writeString(final COutputStream os, String s) throws IOException {
        if (s == null) {
            os.writeByte((byte) 0);
        } else {
            os.writeByte((byte) 1);
            os.writeString(s);
        }
    }

    private String readString(final CInputStream os) throws IOException {
        if (os.readByte() != 0) {
            return os.readString();
        }
        return null;
    }


    public boolean read(CInputStream is) throws IOException {
        _timeStamp = is.readInt();
        if (_timeStamp == 0) {
            return false;
        }
        _ipAddress = is.readInt();
        _application = is.readByte();
        _applicationVersion = is.readInt();
        _platform = is.readByte();
        _platformVersion = is.readInt();
        _requestType = is.readByte();
        _siteId = is.readVInt();
        boolean row[] = options[_requestType];
        if (row[BC]) {
            _bc = readString(is);
        }
        if (row[POSTID]) {
            _postId = is.readVInt();
        }
        if (row[HiTorroID]) {
            _HiTorroId = is.readVInt();
        }
        if (row[REQUESTURI]) {
            _requestURI = readString(is);
        }
        if (row[ACTIONTYPE]) {
            _actionType = is.readByte();
        }
        if (row[MEDIATYPE]) {
            _mediaType = is.readByte();
        }
        if (row[CATEGORY]) {
            _category = readString(is);
        }
        return true;
    }

    public String getBreadcrumb() {
        return _bc;
    }

    public void setBreadcrumb(String bc) {
        _bc = bc;
    }

    public byte getRequestType() {
        return _requestType;
    }

    public void setRequestType(byte rt) {
        _requestType = rt;
    }

    public int getTimeStamp() {
        return _timeStamp;
    }

    public int getIpAddress() {
        return _ipAddress;
    }

    protected void setIpAddress(int ip) {
        _ipAddress = ip;
    }

    public byte getHTTPMethod() {
        return _httpMethod;
    }

    // *********** Media related items.
    public byte getMediaType() {
        return _mediaType;
    }

    public void setMediaType(byte b) {
        _mediaType = b;
    }

    public int getHiTorroID() {
        return _HiTorroId;
    }

    public void setHiTorroID(int pid) {
        _HiTorroId = pid;
    }

    public int getPostID() {
        return _postId;
    }

    public void setPostID(int mid) {
        _postId = mid;
    }

    public int getBytesSent() {
        return _bytesSent;
    }

    protected void setBytesSent(int bytesSent) {
        _bytesSent = bytesSent;
    }

    protected void setHttpMethod(byte b) {
        _httpMethod = b;
    }

    protected void setStatusCode(short statusCode) {
        _statusCode = statusCode;
    }

    protected void setPlatform(byte platform) {
        _platform = platform;
    }

    protected void setPlatformVersion(int platformVersion) {
        _platformVersion = platformVersion;
    }

    protected void setCombinedPlatformVersion(long application) {
        _combinedPlatformVersion = application;
    }

    protected void setApplication(byte application) {
        _application = application;
    }

    protected void setApplicationVersion(int applicationVersion) {
        _applicationVersion = applicationVersion;
    }

    protected void setCombinedApplicationVersion(long application) {
        _combinedApplicationVersion = application;
    }

}

