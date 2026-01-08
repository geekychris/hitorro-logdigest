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
public enum ActionEnum {
    PLAY("PLAY", 0),
    STOP("STOP", 1),
    PLAYLIST("PLAYLIST", 2),
    CLICKTHROUGH("CLICKTHROUGH", 3),
    OTHER("OTHER", 4);

    public static EnumContext<ActionEnum> actionContext;
    private String m_name;
    private byte m_ord;

    ActionEnum(String name, int ord) {
        m_name = name.toLowerCase();
        m_ord = (byte) ord;
        setMapEntry(this);
    }

    private static void setMapEntry(ActionEnum filter) {
        if (actionContext == null) {
            actionContext = new EnumContext<ActionEnum>("actionenum");
        }

        actionContext.setNames(filter, filter.getName(), filter.getOrdinal());
    }

    public byte getOrdinal() {
        return m_ord;
    }

    public String getName() {
        return m_name;
    }
}


