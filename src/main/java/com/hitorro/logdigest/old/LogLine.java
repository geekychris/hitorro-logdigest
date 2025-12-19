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

import com.hitorro.logdigest.Log;

import java.io.StreamTokenizer;
import java.io.StringReader;
import java.text.SimpleDateFormat;

public class LogLine {

    //  Apache Log Date Format
    private static SimpleDateFormat LogDateFormat = new SimpleDateFormat("dd/MMM/yyyy HH:mm:ss Z");
    private static SimpleDateFormat LogDateFormatExt = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss Z");

    private String _agent;
    private int _bytesSent;
    private String _referrer;
    private String _host;
    private String _requestLine;
    private String _requestMethod;
    private String _requestProtocol;
    private String _requestURI;
    private int _statusCode;
    private int _timeStamp;

    private int _timeTransfer;
    private String _cookie;
    private long _bytesSizeTotal;
    private long _byteRange;
    private boolean _lastByteServed;

    private String _browser;
    private String _os;


    //  Example Data:
    //  70.250.153.114 - - [21/Jun/2006:18:07:15 +0000] "GET /origin-www.HiTorro.net/wp-content/themes/HiTorro-2/images/bg-mediabox.png HTTP/1.1" 200 4364 "http://www.HiTorro.net" "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.0.3705)" "__utma=225066395.1102106375.1150913206.1150913206.1150913206.1; __utmb=225066395; __utmc=225066395; __utmz=225066395.1150913206.1.1.utmccn=(direct)|utmcsr=(direct)|utmcmd=(none)"
    //  218.1.188.195 - - [21/Jun/2006:18:12:09 +0000] "GET /origin-www.HiTorro.net/audio/podsummit/053006_PodSummit_with_Sunil_Paul_Part_1.mp3 HTTP/1.1" 302 656 "-" "iTunes/6.0.1 (Windows; N)" "-"
    //
    //  Fields:     <IP Address>
    //              <Time Stamp>
    //              <REQUEST METHOD>
    //              <URI>
    //              <PROTOCOL>
    //              <REQUEST STATUS CODE>
    //              <REQUEST RESULT SIZE>
    //              <REFERRER>
    //              <USER AGENT>
    //              <Urchin Tracking Monitor Cookie Info>  (supposed to help identify uniquity of user)
    public static LogLine processLogLine(String line) {

        LogLine logLine = new LogLine();

        try {

            //  IP
            int indexIP = line.indexOf(' ');
            logLine.setHost(line.substring(0, indexIP));

            //  Get Time Stamp from Date
            int indexDateStart = line.indexOf('[');
            int indexDateEnd = line.indexOf(']');
            String wholeDate = null;
            wholeDate = line.substring(indexDateStart + 1, indexDateEnd);
            wholeDate = wholeDate.replaceFirst(":", " ");

            int timeStamp = 0;
            java.util.Date d = LogDateFormat.parse(wholeDate);
            long millis = d.getTime();
            timeStamp = (int) (millis / 1000);
            logLine.setTimeStamp(timeStamp);

            //  Tokenize the remaining content in the line ..
            String remaining = line.substring(indexDateEnd + 2);
            StringReader reader = new StringReader(remaining);
            StreamTokenizer tok = new StreamTokenizer(reader);

            //  First token is Request Line
            tok.nextToken();

            //  Request Line
            String requestLine = tok.sval;
            logLine.setRequestLine(requestLine);

            int indexMethod = requestLine.indexOf(' ');
            int indexProtocol = requestLine.lastIndexOf(' ');

            //  Request Method
            logLine.setRequestMethod(requestLine.substring(0, indexMethod));

            //  URI

            if (indexMethod < indexProtocol) {
                logLine.setRequestURI(requestLine.substring(indexMethod + 1, indexProtocol));
            }

            //  Request Protocol

            logLine.setRequestProtocol(requestLine.substring(indexProtocol, requestLine.length()));

            //  Second Token is Request Status Code
            tok.nextToken();

            logLine.setStatusCode((int) tok.nval);

            //  Third Token is Request Result Size
            tok.nextToken();
            logLine.setBytesSent((int) tok.nval);

            //  Fourth Token is Referrer
            tok.nextToken();
            logLine.setReferrer(tok.sval);

            //  Fifth Token is User Agent
            tok.nextToken();
            logLine.setAgent(tok.sval);

        } catch (Exception e) {
            e.printStackTrace();
            logLine = null;
        }

        return logLine;
    }

    /**
     * #Fields: date time cs-ip cs-method cs-uri sc-status sc-bytes time-taken cs(referer) cs(user-agent) cs(cookie)
     * object_size byte_range lastbyte Log record format - date_YYYY-MM-DD \t time_HH:MM:SS \t client_ip \t http_method
     * \t arl_stem \t status_code \t total_bytes \t transfer_time \t "referrer" \t "user_agent" \t "cookie" \t
     * total_object_size \t byte_range \t last_byte_served_flag
     *
     * @param line
     * @return new instance of LogLine containing fields parsed from Line
     */
    public LogLine processLogLineExtended(String line) {
        LogLine logLine = new LogLine();

        try {

            //  Get Time Stamp from Date
            //  date_YYYY-MM-DD \t
            //  time_HH:MM:SS \t
            int indexDateStart = line.indexOf('[');
            int indexDateEnd = line.indexOf(']');
            String wholeDate = null;
            wholeDate = line.substring(indexDateStart + 1, indexDateEnd);
            wholeDate = wholeDate.replaceFirst(":", " ");

            int timeStamp = 0;
            long millis = LogDateFormatExt.parse(wholeDate).getTime();
            timeStamp = (int) (millis / 1000);
            logLine.setTimeStamp(timeStamp);

            //  IP
            //  client_ip \t
            int indexIP = line.indexOf(' ');
            logLine.setHost(line.substring(0, indexIP));

            //  Tokenize the remaining content in the line ..
            StringReader reader = new StringReader(line.substring(indexDateEnd + 2));
            StreamTokenizer tok = new StreamTokenizer(reader);

            //  Request Line
            tok.nextToken();
            String requestLine = tok.sval;
            logLine.setRequestLine(requestLine);

            int indexMethod = requestLine.indexOf(' ');
            int indexProtocol = requestLine.lastIndexOf(' ');

            //  http_method \t
            logLine.setRequestMethod(requestLine.substring(0, indexMethod));

            //  URI
            //  arl_stem \t   (Akamai Resource Locator)
            if (indexMethod < indexProtocol) {
                logLine.setRequestURI(requestLine.substring(indexMethod + 1, indexProtocol));
            }

            //  Request Protocol
            logLine.setRequestProtocol(requestLine.substring(indexProtocol, requestLine.length()));

            //  status_code \t
            tok.nextToken();
            logLine.setStatusCode((int) tok.nval);

            //  total_bytes \t
            tok.nextToken();
            logLine.setBytesSent((int) tok.nval);

            //  transfer_time \t
            //private int     _timeTransfer;

            //  "referrer" \t
            tok.nextToken();
            logLine.setReferrer(tok.sval);

            //  "user_agent" \t
            tok.nextToken();
            logLine.setAgent(tok.sval);

            //  "cookie" \t
            //private String  _cookie;

            //  total_object_size \t
            //private long    _bytesSizeTotal;

            //  byte_range \t
            //private long    _byteRange;

            //  last_byte_served_flag                                    
            //private boolean _lastByteServed;

        } catch (Exception e) {
            Log.logdigest.error("Error in processing log line %s %e", e, e);
            logLine = null;
        }

        return logLine;
    }

    //  Use Browser & OS constants
    private String extractBrowser(String userAgent) {
        return "";
        //if (userAgent.indexOf("")
    }

    private String extractOperatingSystem(String userAgent) {
        return "";
    }

    public String getAgent() {
        return _agent;
    }

    public void setAgent(String agent) {
        _agent = agent;
    }

    public String getBrowser() {
        return _browser;
    }

    public void setBrowser(String browser) {
        _browser = browser;
    }

    public int getBytesSent() {
        return _bytesSent;
    }

    public void setBytesSent(int bytesSent) {
        _bytesSent = bytesSent;
    }

    public String getHost() {
        return _host;
    }

    public void setHost(String host) {
        _host = host;
    }

    public String getOs() {
        return _os;
    }

    public void setOs(String os) {
        _os = os;
    }

    public String getReferrer() {
        return _referrer;
    }

    public void setReferrer(String referrer) {
        _referrer = referrer;
    }

    public String getRequestLine() {
        return _requestLine;
    }

    public void setRequestLine(String requestLine) {
        _requestLine = requestLine;
    }

    public String getRequestMethod() {
        return _requestMethod;
    }

    public void setRequestMethod(String requestMethod) {
        _requestMethod = requestMethod;
    }

    public String getRequestProtocol() {
        return _requestProtocol;
    }

    public void setRequestProtocol(String requestProtocol) {
        _requestProtocol = requestProtocol;
    }

    public String getRequestURI() {
        return _requestURI;
    }

    public void setRequestURI(String requestURI) {
        _requestURI = requestURI;
    }

    public int getStatusCode() {
        return _statusCode;
    }

    public void setStatusCode(int statusCode) {
        _statusCode = statusCode;
    }

    public int getTimeStamp() {
        return _timeStamp;
    }

    public void setTimeStamp(int timeStamp) {
        _timeStamp = timeStamp;
    }


}
