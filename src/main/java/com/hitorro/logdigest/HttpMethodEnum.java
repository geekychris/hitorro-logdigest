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


import gnu.trove.map.hash.TByteObjectHashMap;

import java.util.HashMap;

/**
 * Copyright (c) 2003 - present HiTorro All rights reserved. User: chris Date: Dec 12, 2005 Time: 11:36:47 AM
 */
public enum HttpMethodEnum {
    GET("GET", 0),
    PUT("PUT", 1),
    HEAD("HEAD", 2),
    POST("POST", 3),
    DELETE("DELETE", 4),
    TRACE("TRACE", 5);
    private static HashMap<String, HttpMethodEnum> s_byShortName;
    private static TByteObjectHashMap m_ords;
    private String m_name;
    private byte m_ord;

    HttpMethodEnum(String name, int ord) {
        m_name = name.toLowerCase();
        m_ord = (byte) ord;
        setMapEntry(this);
    }

    public static HttpMethodEnum getFilterByName(String name) {
        return s_byShortName.get(name.toLowerCase());
    }

    public static HttpMethodEnum getFilterByOrdinal(byte ord) {
        return (HttpMethodEnum) m_ords.get(ord);
    }

    public static int size() {
        return s_byShortName.size();
    }

    private static void setMapEntry(HttpMethodEnum filter) {
        if (s_byShortName == null) {
            s_byShortName = new HashMap<String, HttpMethodEnum>();
        }
        if (m_ords == null) {
            m_ords = new TByteObjectHashMap();
        }
        s_byShortName.put(filter.getName(), filter);
        m_ords.put(filter.getOrdinal(), filter);
    }

    public byte getOrdinal() {
        return m_ord;
    }

    public String getName() {
        return m_name;
    }
}


