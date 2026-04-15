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

import gnu.trove.map.hash.TLongIntHashMap;
import com.hitorro.logdigest.old.LogLineWriter;
import com.hitorro.logdigest.requestclassifier.ClassifierContext;
import com.hitorro.util.basefile.fs.BaseFile;
import com.hitorro.util.core.hash.FPHash64;
import com.hitorro.util.core.string.Fmt;
import com.hitorro.util.io.ResetableStringReader;
import com.hitorro.util.io.largedata.BaseFileAccessingObjectFactory;
import com.hitorro.util.io.net.InetUtil;
import com.hitorro.util.json.keys.IntegerProperty;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.List;
import java.util.zip.GZIPInputStream;

/**
 * <p/>
 */
public class LogFileProcessor {
    public static IntegerProperty BufferSize =
            new IntegerProperty("logdigest.buffersize", "digester buffer size", 8192 * 256);
    //  Zip File suffix
    public static final String ZIP_SUFFIX = ".gz";
    // enable to compare the type codes output from the old vs new isQuestionClassifier
    private static final boolean assertIt = false;
    //  Error Log directory
    public static final String ERROR_LOG_PATH = "error";
    public static int counter = 0;
    private static SimpleDateFormat LogDateFormat = new SimpleDateFormat("dd/MMM/yyyy HH:mm:ss Z");
    private static FilterExpressions _filters;
    private ClassifierContext context = new ClassifierContext();
    private BaseFile dbDir;
    private BaseFileAccessingObjectFactory factory;
    //return new NewLogLine();
    private LogLineAgentContext _agentContext = new LogLineAgentContext();
    private ResetableStringReader reader = new ResetableStringReader(null);
    private TLongIntHashMap t = new TLongIntHashMap();

    public LogFileProcessor(FilterExpressions filter) {
        _filters = filter;
        factory = getLogLineFactory();
    }

    public static BaseFileAccessingObjectFactory getLogLineFactory() {
        BaseFileAccessingObjectFactory factory = new BaseFileAccessingObjectFactory();
        factory.setComparitor(new DefaultNLLComparator())
                .setNewFunction(LogFileProcessor::getNewLogLine);
        return factory;

    }


    //  Parse the given line from the current Log File,
    //  Hash its fields, and
    //  Add a LogEntry to this File's listFiles of Log Entries

    public static NewLogLine getNewLogLine(Object o) {
        return new NewLogLine();
    }

    /**
     * Process a single Log File
     *
     * @param file Log File
     */
    public void processLogFile(BaseFile file, BaseFile processedDirectory, BaseFile errorDirectory, String nodeId, List<String> listErrors, BaseFile dbDir) {
        this.dbDir = dbDir;
        String line;
        LogLineWriter writer = null;
        try {
            //Log.logdigest.info("Importing %s", file.getName());

            //  Prepare Summary
            Summary logSummary = new Summary();
            logSummary.setFileName(file.getName());
            logSummary.setLogMachine(nodeId);
            logSummary.setTimeImportStart(System.currentTimeMillis() / 1000);  //  end time is set immediately before commit

            boolean doProcessFile = true;   //  Don't process the file if a completed Summary exists for the file


            if (doProcessFile) {

                //  If File is Compressed, used compressed reader; else use uncompressed
                BufferedReader reader = null;
                InputStream fis = file.getDataInputStream();
                if (file.getName().endsWith(ZIP_SUFFIX)) {
                    try {
                        GZIPInputStream gzipIn = new GZIPInputStream(fis);
                        InputStreamReader isr = new InputStreamReader(gzipIn);
                        reader = new BufferedReader(isr, getBufferSize());
                    } catch (IOException e) {
                        //  if gzip file is zero-length or not in gzip format, Move file to Error directory
                        Log.logdigest.error("Exception processing unzip %s %e", e, e);
                        listErrors.add("Invalid Gzip Format for file: " + file.getName());
                        fis.close();
                        //TODO: Note: Don't move file in error condition.  Leave it in place to be resolved.
                        //file.renameTo(new File(errorDirectory, file.getName()));
                    }
                } else {
                    InputStreamReader isr = new InputStreamReader(fis);
                    reader = new BufferedReader(isr, getBufferSize());
                }

                if (reader != null) {

                    //  Prepare Error & HTPredicate files
                    ErrorLogFile fileError = new ErrorLogFile(errorDirectory, file.getName());
                    FilterLogFile fileFilter = new FilterLogFile(errorDirectory, file.getName());

                    //  Read lines from Log file & Process

                    line = reader.readLine();
                    NewLogLine ll = null;
                    while (line != null) {

                        ll = processLogLine(line, logSummary, fileError, fileFilter, ll);
                        if (ll != null) {
                            // XXX TODO write entry
                            if (ll.getShouldWrite()) {
                                logSummary.setCountImported(logSummary.getCountImported() + 1);

                                if (writer == null) {
                                    writer = LogDigesterUtil.getWriter(ll, file, dbDir, factory);
                                }
                                writer.write(ll);
                            }
                        } else {
                            logSummary.setCountFailed(logSummary.getCountFailed() + 1);
                        }
                        logSummary.setCountTotal(logSummary.getCountTotal() + 1);
                        line = reader.readLine();
                    }
                    if (writer != null) {
                        counter++;
                        writer.close();
                    }
                    //  Update Summary with time of first & last entries
                    /*if (_logEntries.size() > 0)
                    {
                        LogEntry firstEntry = _logEntries.get(0);
                        logSummary.setTimeFirstEntry(firstEntry.getTimeStamp());
                        LogEntry lastEntry = _logEntries.get(_logEntries.size() - 1);
                        logSummary.setTimeLastEntry(lastEntry.getTimeStamp());
                    } */

                    //  Manually Garbage Collect
                    //System.gc();

                    //  Close Log File
                    reader.close();

                    //  If there were error lines in the file, ...
                    Log.logdigest.info("Processed file %s, processed %s, imported %s, failed %s, filtered %s, logs written %s",
                            file.getName(),
                            logSummary.getCountTotal(),
                            logSummary.getCountImported(),
                            logSummary.getCountFailed(),
                            logSummary.getCountFiltered(),
                            counter);
                    if (logSummary.getCountFailed() > 0) {
                        listErrors.add("Errors occurred in file: " + file.getName());
                        Log.logdigest.error("Errors occured in file (%s) %s",
                                logSummary.getCountFailed(), file.getName());
                    }

                    //  Move file to Processed directory


                    if (processedDirectory != null) {
                        BaseFile newFilename = null;
                        if (writer == null) {
                            BaseFile notUsedDir = processedDirectory.getChild("notused");
                            notUsedDir.mkdir();
                            newFilename = notUsedDir.getChild(file.getName());
                        } else {
                            newFilename = processedDirectory.getChild(file.getName());
                        }
                        if (!file.renameTo(newFilename)) {
                            String errorMessage = Fmt.S("Unable to rename file: %s", file.getName());
                            Log.logdigest.error("Unable to rename file %s", file.getName());
                            BaseFile to = errorDirectory.getChild(file.getName());
                            file.renameTo(to);
                            listErrors.add(errorMessage);
                        }
                    }
                }
            }
        } catch (IOException e) {
            Log.logdigest.error("Exception processing log file (2) %s %e", e, e);
            listErrors.add("Error during Import: " + file.getName() + "\r\n" + e.getMessage());
        }
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

    /**
     * Parse the given line from the current Log File, Hash its fields, and Add a LogEntry to this File's listFiles of
     * Log Entries
     *
     * @param line       String Line from Log File
     * @param logSummary Summary Entry (record in 'access_log_digest' table)
     * @param fileError
     * @param fileFilter
     * @throws IOException
     */


    private NewLogLine processLogLine(String line, Summary logSummary, ErrorLogFile fileError, FilterLogFile fileFilter, NewLogLine logLine) throws IOException {

        //  If line matches the regular expression filters, log the line to the filter file
        if (_filters == null) {
            Log.logdigest.error("Filters are null");
            return null;
        }
        if (line == null) {
            Log.logdigest.error("Line is null");
            return null;
        }
        if (_filters.matches(line)) {
            fileFilter.logFilterLine(line);
            logSummary.setCountFiltered(logSummary.getCountFiltered() + 1);
        } else {
            logLine = processLogLine(line, logLine);

            if (logLine != null) {
                int timeStamp = logLine.getTimeStamp();

                String referer = logLine.getReferrer();
                String requestURI = logLine.getRequestURI();
                if (requestURI == null) {
                    // we have a problem where the request uri doesnt get processed correctly due to an extra quote
                    // following the uri.
                    return null;

                }

                // new classification
                context.classify(logLine, referer);
                if (assertIt) {
                    assertTypes(requestURI, timeStamp, logLine);
                }


            } else {

                //  if log line is null, there was an error in parsing... log the error line to a file
                fileError.logError(line);
                logSummary.setCountFailed(logSummary.getCountFailed() + 1);

            }
        }

        return logLine;
    }

    private final NewLogLine processLogLine(String line, NewLogLine logLine) {
        if (line.startsWith("#")) {
            return null;
        }
        if (logLine == null) {
            logLine = new NewLogLine();
        } else {
            logLine.reset();
        }
        try {

            //  IP
            int indexIP = line.indexOf(' ');
            if (indexIP == -1) {
                // not looking like a log line!
                return null;
            }
            String ip = line.substring(0, indexIP);
            logLine.setIpAddress(InetUtil.inetAddressToInt(ip));

            //  Get Time Stamp from Date
            int indexDateStart = line.indexOf('[');
            int indexDateEnd = line.indexOf(']');
            if (indexDateStart == -1) {
                // not looking like a log line!
                return null;
            }
            if (indexDateEnd == -1) {
                return null;
            }
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
            reader.set(remaining);
            StreamTokenizer tok = new StreamTokenizer(reader);

            //  First token is Request Line
            tok.nextToken();

            //  Request Line
            String requestLine = tok.sval;
            logLine.setRequestLine(requestLine);

            int indexMethod = requestLine.indexOf(' ');
            int indexProtocol = requestLine.lastIndexOf(' ');

            //  Request Method
            String m = requestLine.substring(0, indexMethod);
            HttpMethodEnum meth = HttpMethodEnum.getFilterByName(m);
            if (meth != null) {
                logLine.setHttpMethod(meth.getOrdinal());
            } else {
                Log.logdigest.error("Cant find method test: %s", m);
            }

            //  URI

            if (indexMethod < indexProtocol) {
                logLine.setRequestURI(requestLine.substring(indexMethod + 1, indexProtocol));
            }

            //  Request Protocol

            logLine.setRequestProtocol(requestLine.substring(indexProtocol, requestLine.length()));

            //  LongSecond Token is Request Status Code
            tok.nextToken();

            logLine.setStatusCode((short) tok.nval);

            //  Third Token is Request Result Size
            tok.nextToken();
            logLine.setBytesSent((int) tok.nval);

            //  Fourth Token is Referrer
            tok.nextToken();
            logLine.setReferrer(tok.sval);

            //  Fifth Token is User Agent
            tok.nextToken();
            _agentContext.setAgent(tok.sval);
            logLine.setApplication(_agentContext.getApplicationOrdinal());
            logLine.setApplicationVersion(_agentContext.getApplicationVersionAsInt());
            logLine.setCombinedApplicationVersion(_agentContext.getApplicationAsLong());
            logLine.setPlatform(_agentContext.getPlatformOrdinal());
            logLine.setPlatformVersion(_agentContext.getApplicationVersionAsInt());
            logLine.setCombinedPlatformVersion(_agentContext.getPlatformAsLong());
        } catch (Exception e) {
            Log.logdigest.error("Error in processing log line %s %e", e, e);
            logLine = null;
        }

        return logLine;
    }

    private int getBufferSize() {
        return BufferSize.apply();
    }

    private void assertTypes(final String requestURI, final int timeStamp, final NewLogLine logLine) {
        byte type = OldClassifier.handleRequestAndMediaTypeByte(requestURI, timeStamp, logLine);
        byte ourType = logLine.getRequestType();
        if (ourType != type) {
            if ((ourType == 1 && type == 127) || (ourType == 127 && type == 1) || (ourType == 9)) {
                // ignore them for now!
            } else {
                long hash = FPHash64.getFP(logLine.getRequestURI());
                if (!t.contains(hash)) {
                    t.put(hash, 1);
                    Log.logdigest.error("Inconsistent types %s, %s, %s", ourType, type, logLine.getRequestURI());
                }
            }
        }
    }

}


class DefaultNLLComparator implements Comparator<NewLogLine> {
    public int compare(final NewLogLine newLogLine, final NewLogLine newLogLine1) {
        return newLogLine.compareTo(newLogLine1);
    }
}