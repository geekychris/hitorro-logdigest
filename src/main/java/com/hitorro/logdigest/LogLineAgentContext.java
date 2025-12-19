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

import com.hitorro.util.core.Constants;
import com.hitorro.util.core.IntegerUtil;
import com.hitorro.util.core.string.StringUtil;

import java.util.HashMap;
import java.util.Map;

public class LogLineAgentContext {
    static final String TokenCompatible = "compatible";
    static final String TokenMozilla = "Mozilla";
    static final char TokenOpenParenthesis = '(';
    static final char TokenCloseParenthesis = ')';
    static final Map<Object, Object> AgentApplicationLookup = new HashMap<Object, Object>() {
        {
            byte ordinal = 0;
            String[] applications = {"Firefox", "Googlebot", "MSIE", "Miro", "Mozilla", "NaverBot", "NetNewsWire", "Opera", "Safari",
                    "SimplePie", "TVTonic", "Windows-RSS-Platform", "Yahoo Pipes", "Yahoo! Slurp", "YahooFeedSeeker Testing",
                    "YahooFeedSeeker", "iTunes", "popurls spider", "Wget", TokenCompatible};

            for (String application : applications) {
                bidirectionalPut(application, ordinal++);
            }
        }


        void bidirectionalPut(Object obj1, Object obj2) {
            put(obj1, obj2);
            put(obj2, obj1);
        }

    };
    static final Map<Object, Object> AgentPlatformLookup = new HashMap<Object, Object>() {
        {
            String[] platforms = {"Linux i686", "Windows NT 5.2", "Windows NT 5.1", "Windows NT 6.0", "Windows NT 5.0", "Intel Mac OS X",
                    "Macintosh", "Windows", "CPU like Mac OS X"};
            byte ordinal = 0;

            for (String platform : platforms) {
                bidirectionalPut(platform, ordinal++);
            }
        }


        void bidirectionalPut(Object obj1, Object obj2) {
            put(obj1, obj2);
            put(obj2, obj1);
        }

    };
    static final Map<Object, Object> AgentApplicationPlatformMap = new HashMap<Object, Object>() {
        {
            put("Firefox", 2);
            put("MSIE", 2);
            put("Mozilla", 2);
            put("NetNewsWire", 0);
            put("Opera", 0);
            put("Safari", 2);
            put("SimplePie", -1);
            put("Windows-RSS-Platform", 1);
            put("Yahoo! Slurp", -1);
            put("Yahoo Pipes", -1);
            put("YahooFeedSeeker", -1);
            put("YahooFeedSeeker Testing", -1);
            put("iTunes", 0);
            put("popurls spider", -1);
            put("Googlebot", -1);
            put("msnbot", -1);
            put("NaverBot", -1);
            put(TokenCompatible, 2);
        }
    };
    private final Map<Object, Object> m_agentUnknownTokens = new HashMap<Object, Object>();
    String m_agentInner = Constants.EmptyString;
    String m_agentOuterBegin = Constants.EmptyString;
    String m_agentOuterEnd = Constants.EmptyString;
    String[] m_agentInnerTokens = null;
    int m_agentInnerTokenCnt = -1;
    private String m_agent = null;
    private String m_application = Constants.EmptyString;
    private String m_applicationName = Constants.EmptyString;
    private int m_applicationVersion = -1;
    private String m_platform = Constants.EmptyString;
    private String m_platformName = Constants.EmptyString;
    private int m_platformVersion = -1;

    public LogLineAgentContext() {
    }

    /**
     * agent - agent token extracted from Apache 'combined' log record for example:  Mozilla/5.0 (X11; U; Linux i686;
     * en-US; rv:1.8.1.6) Gecko/20061201 Firefox/2.0.0.6 (Ubuntu-feisty)
     *
     * @param agent
     */
    public void setAgent(String agent) {
        clearAgent();

        m_agent = agent;
    }

    /**
     * reset member variables in preparation for setting a new agent string
     */
    private void clearAgent() {
        m_application = Constants.EmptyString;
        m_applicationName = Constants.EmptyString;
        m_applicationVersion = -1;

        m_platform = Constants.EmptyString;
        m_platformName = Constants.EmptyString;
        m_platformVersion = -1;

        m_agentInner = Constants.EmptyString;
        m_agentOuterBegin = Constants.EmptyString;
        m_agentOuterEnd = Constants.EmptyString;
        m_agentInnerTokens = null;
        m_agentInnerTokenCnt = -1;
    }

    /**
     * given an agent string, split it into three: 1. text up to the '('; 2. tokenizeFromSingleChar the text inside the ()'s; 3. the
     * text after the ')' as seen in a string like: Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.8.1.1)
     * Gecko/20061204 Firefox/2.0.0.1 1. Mozilla/5.0 2. Windows; U; Windows NT 5.1; en-US; rv:1.8.1.1 3. Gecko/20061204
     * Firefox/2.0.0.1
     */
    private void splitAgentString() {
        int openParenIndex = -1;
        int closeParenIndex = -1;

        if (StringUtil.nullOrEmptyString(m_agentOuterBegin)) {
            openParenIndex = m_agent.indexOf(TokenOpenParenthesis);
            closeParenIndex = m_agent.indexOf(TokenCloseParenthesis);

            if (openParenIndex != -1 &&
                    closeParenIndex != -1 &&
                    closeParenIndex >= openParenIndex + 1) {
                int begin = 0;
                int end = openParenIndex - 1;
                if (end >= begin) {
                    m_agentOuterBegin = m_agent.substring(begin, end);
                }

                begin = closeParenIndex + 1;
                end = m_agent.length();
                if (end >= begin) {
                    m_agentOuterEnd = m_agent.substring(begin, end);
                }

                if (closeParenIndex > openParenIndex & openParenIndex > -1) {
                    m_agentInner = m_agent.substring(openParenIndex + 1, closeParenIndex);
                }

                if (!StringUtil.nullOrEmptyOrBlankString(m_agentInner)) {
                    m_agentInnerTokens = StringUtil.tokenizeFromSingleChar(m_agentInner, ";", true);
                    m_agentInnerTokenCnt = m_agentInnerTokens.length;
                }
            } else {
                m_agentOuterBegin = m_agent;
            }
        }
    }

    /**
     * given an agent string return the relevant application, ie Epiphany/1.4.4 or Firefox/2.0.0.9
     * <p/>
     * for detailed explanation, see: http://msdn2.microsoft.com/en-us/library/ms537503.aspx#UnderstandUA
     * http://www.texsoft.it/index.php?c=software&m=sw.php.useragent&l=it
     * <p/>
     * for a comprehensive listFiles, see: http://www.zytrax.com/tech/web/browser_ids.htm
     *
     * @return application from agent string
     */
    public String getApplication() {
        if (StringUtil.nullOrEmptyString(m_agentOuterBegin)) {
            splitAgentString();
        }

        if (!StringUtil.nullOrEmptyString(m_agentOuterBegin)) {
            if (m_agentOuterBegin.startsWith(TokenMozilla)) {
                if (m_agentInner.startsWith(TokenCompatible) && m_agentInnerTokenCnt > 1) {
                    m_application = m_agentInnerTokens[1];
                } else if (!StringUtil.nullOrEmptyString(m_agentOuterEnd)) {
                    int lastSpaceIndex = m_agentOuterEnd.lastIndexOf(' ');

                    if (m_agentOuterEnd.charAt(m_agentOuterEnd.length() - 1) == ')') {
                        int firstSpaceIndex = m_agentOuterEnd.lastIndexOf(' ', lastSpaceIndex - 1);

                        if (firstSpaceIndex >= 0 && lastSpaceIndex >= 0 && lastSpaceIndex >= firstSpaceIndex) {
                            m_application = m_agentOuterEnd.substring(firstSpaceIndex + 1, lastSpaceIndex);
                        }
                    } else {
                        m_application = m_agentOuterEnd.substring(lastSpaceIndex + 1);
                    }

                } else {
                    if (m_agentInnerTokenCnt > 1) {
                        m_application = m_agentInnerTokens[1];
                    }
                }
            } else {
                int firstSpaceIndex = m_agentOuterBegin.indexOf(' ');

                if (firstSpaceIndex > 0) {
                    m_application = m_agentOuterBegin.substring(0, firstSpaceIndex + 1);
                } else {
                    m_application = m_agentOuterBegin;
                }
            }
        }

        return m_application;
    }

    public long getApplicationAsLong() {
        byte nameOrdinal = getApplicationOrdinal();
        int version = getApplicationVersionAsInt();
        return getLongFromOrdinalAndVersion(nameOrdinal, version);
    }

    /**
     * given a application, return just the name. thus, given Firefox/2.0.0.1, return Firefox
     *
     * @return string - application name
     */
    public String getApplicationName() {

        if (StringUtil.nullOrEmptyString(m_application)) {
            getApplication();
        }

        if (StringUtil.nullOrEmptyString(m_applicationName)) {
            int begin = -1;
            char[] beginDelimiters = {' ', '/'};
            int end = getCharLocation(m_application, 0, beginDelimiters, Constants.ascii_numeric);


            if (end > 0 && end < m_application.length()) {
                begin = 0;
                m_applicationName = m_application.substring(begin, end - 1);
            } else {
                m_applicationName = m_application;
            }

        }

        return m_applicationName;
    }

    /**
     * given a application, return just the name. thus, given Firefox/2.0.0.1, return 2.0.0.1
     *
     * @return string - application version
     */
    public String getApplicationVersion() {

        if (StringUtil.nullOrEmptyString(m_platformName)) {
            getApplicationName();
        }


        String applicationVersion = Constants.EmptyString;

        if (!StringUtil.nullOrEmptyString(m_applicationName)) {

            int begin = m_applicationName.length();
            int end = m_application.length();


            if (begin < end) {
                applicationVersion = m_application.substring(++begin, end);
            }

        }

        return applicationVersion;
    }

    /**
     * given a application, return the major, minor version numbers as a unique integer thus, given Firefox/2.0.0.1,
     * return 2.0 compressed into an integer
     *
     * @return int - unique representation of version major + minor numbers
     */
    public int getApplicationVersionAsInt() {

        int intVersion = -1;
        String version = getApplicationVersion();

        if (!StringUtil.nullOrEmptyString(version)) {
            intVersion = getIntFromVersionString(version, '.');
        }

        return intVersion;
    }

    /**
     * given an application that is derived from an agent string return the internal, arbitrarily assigned ordinal.
     *
     * @return byte - ordinal
     */
    public byte getApplicationOrdinal() {
        String applicationName = getApplicationName();

        byte ordinal = -1;
        Byte ordinalByte = (Byte) AgentApplicationLookup.get(applicationName);

        if (ordinalByte != null) {
            ordinal = ordinalByte.byteValue();
        } else {
            if (!StringUtil.nullOrEmptyString(applicationName)) {
                m_agentUnknownTokens.put(applicationName, AgentTokenType.Application);
            }
        }

        return ordinal;
    }

    /**
     * given an arbitrarily assigned ordinal, return associated application.
     *
     * @param ordinal - arbitrarily assigned ordinal
     * @return String - application string
     */
    public String getApplicationFromOrdinal(byte ordinal) {
        String application = null;
        Object applicationObj = null;

        if (ordinal != -1) {
            applicationObj = AgentApplicationLookup.get(ordinal);

            if (applicationObj != null) {
                application = (String) applicationObj;
            }
        }

        return application;
    }

    /**
     * given an agent string return the relevant platform, ie Linux i686
     * <p/>
     * for detailed explanation, see: http://msdn2.microsoft.com/en-us/library/ms537503.aspx#UnderstandUA
     * http://www.texsoft.it/index.php?c=software&m=sw.php.useragent&l=it
     * <p/>
     * for a comprehensive listFiles, see: http://www.zytrax.com/tech/web/browser_ids.htm
     *
     * @return application from agent string
     */
    public String getPlatform() {
        if (StringUtil.nullOrEmptyString(m_agentInner)) {
            splitAgentString();
        }

        if (m_agentInnerTokenCnt > -1) {
            if (StringUtil.nullOrEmptyString(m_applicationName)) {
                getApplicationName();
            }


            byte lookupIndex = -1;
            Object lookupIndexObj = null;

            if (m_agentInnerTokens[0].equals(TokenCompatible)) {
                lookupIndexObj = AgentApplicationPlatformMap.get(TokenCompatible);
            }

            Object lookupIndexObjTmp = AgentApplicationPlatformMap.get(m_applicationName);
            if (lookupIndexObjTmp != null) {
                lookupIndexObj = lookupIndexObjTmp;
            }


            if (lookupIndexObj != null) {
                lookupIndex = ((Integer) lookupIndexObj).byteValue();
            }

            if (lookupIndex > -1 && lookupIndex < m_agentInnerTokenCnt) {
                m_platform = m_agentInnerTokens[lookupIndex];
            }

        }

        return m_platform;
    }

    public long getPlatformAsLong() {
        byte nameOrdinal = getPlatformOrdinal();
        int version = getPlatformVersionAsInt();
        return getLongFromOrdinalAndVersion(nameOrdinal, version);
    }

    /**
     * given a platform, return just the name. thus, given Windows NT 6.0 return Windows NT
     *
     * @return string - platform name
     */
    public String getPlatformName() {

        if (StringUtil.nullOrEmptyString(m_platform)) {
            getPlatform();
        }

        if (StringUtil.nullOrEmptyString(m_platformName)) {
            int begin = -1;
            char[] beginDelimiters = {' ', '/'};
            int end = getCharLocation(m_platform, 0, beginDelimiters, Constants.ascii_numeric);


            if (end > 0 && end < m_platform.length()) {
                begin = 0;
                m_platformName = m_platform.substring(begin, end - 1);
            } else {
                m_platformName = m_platform;
            }

        }

        return m_platformName;
    }

    /**
     * given a platform, return just the version as a string. thus, given Windows NT 6.0 return 6.0
     *
     * @return string - platform version
     */
    public String getPlatformVersion() {
        String platformVersion = Constants.EmptyString;

        if (StringUtil.nullOrEmptyString(m_platformName)) {
            getPlatformName();
        }


        if (!StringUtil.nullOrEmptyString(m_platformName)) {

            int begin = m_platformName.length();
            int end = m_platform.length();


            if (begin < end) {
                platformVersion = m_platform.substring(++begin, end);
            }
        }

        return platformVersion;

    }

    public int getPlatformVersionAsInt() {
        int intVersion = -1;
        String version = getPlatformVersion();

        if (!StringUtil.nullOrEmptyString(version)) {
            intVersion = getIntFromVersionString(version, '.');
        }

        return intVersion;
    }

    /**
     * given a platform that is derived from an agent string return the internal, arbitrarily assigned ordinal.
     *
     * @return byte - ordinal
     */
    public byte getPlatformOrdinal() {
        String platform = getPlatform();

        byte ordinal = -1;
        Byte ordinalInt = (Byte) AgentPlatformLookup.get(platform);

        if (ordinalInt != null) {
            ordinal = ordinalInt.byteValue();
        } else {
            if (!StringUtil.nullOrEmptyString(platform)) {
                m_agentUnknownTokens.put(platform, AgentTokenType.Platform);
            }
        }

        return ordinal;
    }

    /**
     * given an arbitrarily assigned ordinal, return associated platform.
     *
     * @param ordinal - arbitrarily assigned ordinal
     * @return String - platform string
     */
    public String getPlatformFromOrdinal(byte ordinal) {
        String platform = null;
        Object platformObj = null;

        if (ordinal != -1) {
            platformObj = AgentPlatformLookup.get(ordinal);

            if (platformObj != null) {
                platform = (String) platformObj;
            }
        }
        return platform;
    }

    /**
     * return hash-apply of unidentified agents and platform.
     *
     * @return apply of platform and application tokens.
     */
    public Map getUknownTokens() {
        return m_agentUnknownTokens;
    }

    /**
     * forward search a string for the first index of a character, as specified in validDelimiters, provided that the
     * found character is immediately succeeded by a character specified in requiredCharAfterDelimiter
     *
     * @param target                     - String to be searched
     * @param beginIndex                 - int character position to begin searching
     * @param validDelimiters            - char[] valid characters to find
     * @param requiredCharAfterDelimiter - char[] valid character, required to exist after any one of the valid
     *                                   delimiter characters
     * @return int - index of first valid delimiter character
     */
    private int getCharLocation(String target, int beginIndex, char[] validDelimiters, char[] requiredCharAfterDelimiter) {
        boolean delimiterFound = false;

        if (beginIndex < 0) {
            return -1;
        }

        if ((validDelimiters == null) || validDelimiters.length <= 0) {
            return -1;
        }

        if (requiredCharAfterDelimiter == null || requiredCharAfterDelimiter.length <= 0) {
            return -1;
        }


        for (int i = beginIndex; i < target.length(); i++) {
            char current = target.charAt(i);

            if (!delimiterFound) {
                for (char validDelimiter : validDelimiters) {
                    if (current == validDelimiter) {
                        delimiterFound = true;
                    }
                }
            } else {
                for (char requiredChar : requiredCharAfterDelimiter) {
                    if (current == requiredChar) {
                        return i;
                    }
                }

                delimiterFound = false;

            }

        }

        return -1;
    }

    /**
     * convertToPdf the first 2 positions in a version string to a unique integer.  Thus, '2.0.0.1', convertToPdf '2.0'
     * to an integer.
     *
     * @param version - string representation of a version number
     * @return int representation of major minor
     */
    private int getIntFromVersionString(String version, char delimiter) {
        // todo: abstract this to return x positions in a version string as an arbitrarily long number of bytes?

        int byteAccum = 0;
        int intAccum = 0;
        int delimiterCnt = 0;
        boolean nextByte = true;


        if (!StringUtil.nullOrEmptyOrBlankString(version)) {
            for (int i = 0; delimiterCnt < 2 && i < version.length(); i++) {
                char c = version.charAt(i);

                if (IntegerUtil.isNumber(c)) {
                    if (nextByte) {
                        nextByte = false;
                        byteAccum = 0;
                    }

                    byteAccum = byteAccum * 10;
                    byteAccum = byteAccum + IntegerUtil.charNumbertoInt(c);
                }

                if (c == delimiter || i == version.length() - 1 || !IntegerUtil.isNumber(c)) {
                    intAccum = intAccum << 16;
                    intAccum = intAccum | 0;
                    intAccum = intAccum | byteAccum;
                    delimiterCnt++;
                    nextByte = true;
                }
            }
        }

        return intAccum;

    }

    private long getLongFromOrdinalAndVersion(byte nameOrdinal, int version) {
        long nameOrdinalLong = (long) nameOrdinal;
        long versionLong = (long) version;
        long combined = -1;

        combined = nameOrdinalLong;
        combined = (combined << 32);
        combined = combined | 0;
        combined = combined | versionLong;

        return combined;

    }


    private enum AgentTokenType {
        Platform, Application
    }
}
