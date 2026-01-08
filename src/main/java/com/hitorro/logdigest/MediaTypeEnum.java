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

import com.hitorro.util.core.EnumContext;

/**
 */
public enum MediaTypeEnum {
    /**
     * else if ((ix = requestURI.indexOf(".flv")) > 0 || (ix = requestURI.indexOf(".mp3")) > 0 || (ix =
     * requestURI.indexOf(".mov")) > 0 || (ix = requestURI.indexOf(".m4v")) > 0 || (ix = requestURI.indexOf(".mp4")) > 0
     * || (ix = requestURI.indexOf(".wmv")) > 0 || (ix = requestURI.indexOf(".FLV")) > 0 || (ix =
     * requestURI.indexOf(".MP3")) > 0 || (ix = requestURI.indexOf(".MOV")) > 0 || (ix = requestURI.indexOf(".M4V")) > 0
     * || (ix = requestURI.indexOf(".MP4")) > 0 || (ix = requestURI.indexOf(".WMV")) > 0)
     */
    flv("flv", 0),
    mp3("mp3", 1),
    mov("mov", 2),
    m4v("m4v", 3),
    mp4("mp4", 4),
    wmv("wmv", 5);
    public static EnumContext<MediaTypeEnum> context;
    private String m_name;
    private byte m_ord;

    MediaTypeEnum(String name, int ord) {
        m_name = name.toLowerCase();
        m_ord = (byte) ord;
        setMapEntry(this);
    }

    private static void setMapEntry(MediaTypeEnum filter) {
        if (context == null) {
            context = new EnumContext<MediaTypeEnum>("mediatypeenums");
        }
        context.setNames(filter, filter.getName(), filter.getOrdinal());
    }

    public byte getOrdinal() {
        return m_ord;
    }

    public String getName() {
        return m_name;
    }
}


