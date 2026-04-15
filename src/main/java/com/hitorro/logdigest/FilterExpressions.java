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

import com.hitorro.util.json.keys.StringProperty;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * FilterExpressions contains a listFiles of Regular Expressions used to remove entries from log files.
 */
public class FilterExpressions {

    public static StringProperty FilterFile =
            new StringProperty("logdigest.filterfile", "location of filters file", "filters.txt");
    private List<Pattern> _regularExpressions;

    public FilterExpressions() {

    }

    public void init() {
        File filterFile = new File(FilterFile.apply());
        if (filterFile.exists()) {
            try {
                FileReader fr = new FileReader(filterFile);
                BufferedReader reader = new BufferedReader(fr);
                String line = reader.readLine();
                _regularExpressions = new ArrayList<Pattern>();
                while (line != null) {
                    if (line.length() > 0) {
                        Pattern p = Pattern.compile(line);
                        _regularExpressions.add(p);
                    }
                    line = reader.readLine();
                }
                reader.close();
            } catch (IOException e) {
                Log.logdigest.error("Error in initializing Filters %s %e", e, e);
            }
        }
    }

    public List getExpressions() {
        return _regularExpressions;
    }

    public boolean matches(String line) {
        boolean result = false;
        if (_regularExpressions != null) {
            for (Pattern regex : _regularExpressions) {
                Matcher m = regex.matcher(line);
                if (m.matches()) {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }

}
