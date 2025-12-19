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

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

//  Generates Log Files (for loading test data)
public class LogGenerator {

    //  Apache Log Date Format
    private static SimpleDateFormat LogDateFormat = new SimpleDateFormat("dd/MMM/yyyy HH:mm:ss");

    public static void generateLogFile(String sourceFileName, String newFileName) {
        try {
            String line = "";
            FileWriter fw = new FileWriter(newFileName);
            BufferedWriter w = new BufferedWriter(fw);
            //ModelReader fr = new ModelReader(sourceFileName);
            //BufferedReader r = new BufferedReader(fr);
            int i = 0;
            int LINES = 1000000;
            String newHost = "";
//            int LINES = 1000;
            while (i < LINES) {
                FileReader fr = new FileReader(sourceFileName);
                BufferedReader r = new BufferedReader(fr);
                while ((line = r.readLine()) != null) {
                    int bytes[] = intToByteInts(i);
                    newHost = bytes[0] + "." + bytes[1] + "." + bytes[2] + "." + bytes[3];
                    i++;
                    processLogLine(w, line, newHost);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static byte[] intToBytes(int i) {
        byte[] dword = new byte[4];
        dword[0] = (byte) ((i >>> 24) & 0x000000FF);
        dword[1] = (byte) ((i >> 16) & 0x000000FF);
        dword[2] = (byte) ((i >> 8) & 0x000000FF);
        dword[3] = (byte) (i & 0x00FF);
        return dword;
    }

    private static int[] intToByteInts(int i) {
        int[] dword = new int[4];
        dword[0] = ((i >>> 24) & 0x000000FF);
        dword[1] = ((i >> 16) & 0x000000FF);
        dword[2] = ((i >> 8) & 0x000000FF);
        dword[3] = (i & 0x00FF);
        return dword;
    }


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
    public static void processLogLine(BufferedWriter fw, String line, String newHost) {
        try {

            //  IP
            int indexIP = line.indexOf(' ');
            String host = line.substring(0, indexIP);

            //  Get Time Stamp from Date
            int indexDateStart = line.indexOf('[');
            int indexDateEnd = line.indexOf(']');
            String wholeDate = line.substring(indexDateStart + 1, indexDateEnd);
            wholeDate = wholeDate.replaceFirst(":", " ");
            int timeStamp = 0;
            try {
                long millis = LogDateFormat.parse(wholeDate).getTime();
                timeStamp = (int) (millis / 1000);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            //  Hash Host Name (IP Address)

            //  Tokenize the remaining content in the line ..
            StringReader reader = new StringReader(line.substring(indexDateEnd + 2));
            StreamTokenizer tok = new StreamTokenizer(reader);

            //  First token is Request Line
            tok.nextToken();

            //  Hash Request Line
            String requestLine = tok.sval;

            int indexMethod = requestLine.indexOf(' ');
            int indexProtocol = requestLine.lastIndexOf(' ');

            //  Hash Request Method
            String requestMethod = requestLine.substring(0, indexMethod);

            //  Hash URI
            String uri = requestLine.substring(indexMethod + 1, indexProtocol);

            //  Hash Request Protocol
            String requestProtocol = requestLine.substring(indexProtocol, requestLine.length());

            tok.nextToken();    //  Second Token is Request Status Code
            int statusCode = (int) tok.nval;

            tok.nextToken();    //  Third Token is Request Result Size
            int sizeResult = (int) tok.nval;

            tok.nextToken();    //  Fourth Token is Referrer
            String referrer = tok.sval;

            tok.nextToken();    //  Fifth Token is User Agent
            String agent = tok.sval;

            String toWrite = "";
            toWrite += newHost + " - - ";
            String newDate = LogDateFormat.format(new Date(timeStamp));
            newDate = newDate.replaceFirst(" ", ":");
            toWrite += "[" + newDate + " +0000] ";
            toWrite += "\"" + requestLine + "\" ";
            toWrite += Integer.toString(statusCode) + " ";
            toWrite += Integer.toString(sizeResult) + " ";
            toWrite += "\"" + referrer + "\" ";
            toWrite += "\"" + agent + "\" \r\n";
            fw.write(toWrite);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
