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


import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Summary {

    public static final int ID_NONE = -1;
    public static final int STATUS_COMPLETE = 0;              //  log file inserted successfully
    public static final int STATUS_ERROR = -1;          //  Error occurred during import
    public static final int STATUS_IMPORTING = 1;      //  log file is importing
    //public static final int STATUS_ERROR = 2;          //  Error occurred during import

    //  Log File base filename
    public static final String LOG_FILE_AUDIO = "audio_";
    public static final String LOG_FILE_WWW = "www_";
    public static final String LOG_FILE_GETV = "getv_access_";
    public static final String LOG_FILE_MEDIA = "media_";
    public static final String LOG_FILE_ORIGIN = "access_";
    public static final String LOG_FILE_LIMELIGHT = "HiTorro.ht.";

    //  Domain
    //public static final String DOMAIN_SCOBLEIZER = "scobleizer";
    public static final String DOMAIN_AUDIO = "HiTorro Audio";
    public static final String DOMAIN_WWW = "HiTorro WWW";
    public static final String DOMAIN_GETV = "GETV";
    public static final String DOMAIN_MEDIA = "HiTorro Media";
    public static final String DOMAIN_ORIGIN = "HiTorro Origin";
    public static final String DOMAIN_LIMELIGHT = "Limelight";


    private int _id = ID_NONE;                    //  Summary Id
    private String _fileName;           //  Log File Name (without directory)
    private String _domain;             //  Domain the log file is coming from
    private String _logMachine;         //  IP/HostName of machine generating log file
    private long _timeImportStart;      //  Time Import started
    private long _timeImportEnd;        //  Time Import ended
    private long _timeFirstEntry;       //  Timestamp of first log entry line
    private long _timeLastEntry;        //  Timestamp of last log entry line
    private int _countImported = 0;     //  Number of lines successfully parsed & inserted into database
    private int _countFiltered = 0;     //  Number of lines filtered out by filters listFiles
    private int _countFailed = 0;       //  Number of lines which failed parsing
    private int _countTotal = 0;        //  Total number of lines in log file
    private int _status = 0;            //  Status code indicating log file import status - STATUS_COMPLETE or 1-based index of line

    /**
     * @param filename File name
     * @return Domain Name for specified file (look for prefix in filename)
     */
    public static final String getDomainByFileName(String filename) {
        String result = null;
        if (filename.startsWith(LOG_FILE_AUDIO)) {
            result = DOMAIN_AUDIO;
        } else if (filename.startsWith(LOG_FILE_WWW)) {
            result = DOMAIN_WWW;
        } else if (filename.startsWith(LOG_FILE_GETV)) {
            result = DOMAIN_GETV;
        } else if (filename.startsWith(LOG_FILE_MEDIA)) {
            result = DOMAIN_MEDIA;
        } else if (filename.startsWith(LOG_FILE_ORIGIN)) {
            result = DOMAIN_ORIGIN;
        } else if (filename.startsWith(LOG_FILE_LIMELIGHT)) {
            result = DOMAIN_LIMELIGHT;
        }
        return result;
    }

    /**
     * @param file Log File
     * @return True if the Log File is supported (based on file name)
     */
    public static boolean isValidLogFile(File file) {
        return file.getName().startsWith(LOG_FILE_AUDIO) ||
                file.getName().startsWith(LOG_FILE_WWW) ||
                file.getName().startsWith(LOG_FILE_MEDIA) ||
                file.getName().startsWith(LOG_FILE_GETV) ||
                file.getName().startsWith(LOG_FILE_ORIGIN) ||
                file.getName().startsWith(LOG_FILE_LIMELIGHT);
    }

    //  If this summary does not have an ID, it hasn't been created in database
    public boolean exists() {
        return (getId() != Summary.ID_NONE);
    }

    public boolean isImporting() {
        return (_status == Summary.STATUS_IMPORTING);
    }

    public boolean isComplete() {
        return (_status == Summary.STATUS_COMPLETE);
    }

    public String getInsertSQL() {
        StringBuffer query = new StringBuffer("insert into import_summary (file_name, domain, log_machine, ");
        query.append("time_import_start, time_import_end, time_first_entry, time_last_entry, ");
        query.append("count_imported, count_filtered, count_failed, count_total ");
        query.append(") values ( ");
        query.append("'");
        query.append(_fileName);
        query.append("', '");
        query.append(_domain);
        query.append("', '");
        query.append(_logMachine);
        query.append("', ");
        query.append(_timeImportStart);
        query.append(", ");
        query.append(_timeImportEnd);
        query.append(", ");
        query.append(_timeFirstEntry);
        query.append(", ");
        query.append(_timeLastEntry);
        query.append(", ");
        query.append(_countImported);
        query.append(", ");
        query.append(_countFiltered);
        query.append(", ");
        query.append(_countFailed);
        query.append(", ");
        query.append(_countTotal);
        query.append(") ");
        return query.toString();
    }

    //  Update import_summary's status (indicating the line of the log file we are processing
    public void updateStatusLine(Statement stmt, int status) throws SQLException {
        _status = status;
        stmt.execute("UPDATE import_summary SET status = " + status + " WHERE file_name = '" + _fileName + "'");
    }

    public String getUpdateStatusSQL(int status) {
        _status = status;
        return "UPDATE import_summary SET status = " + status + " WHERE file_name = '" + _fileName + "'";
    }

    public int getDigestLineCount(Statement stmt) throws SQLException {
        int result = 0;
        ResultSet rs = stmt.executeQuery("SELECT count(summary_id) FROM access_log_digest where summary_id = " + getId());
        if (rs != null && rs.next()) {
            result = rs.getInt(1);
        }
        return result;
    }

    //  Update Status to "OK" & set import end time
    public void updateComplete(Statement stmt) throws SQLException {
        _status = STATUS_COMPLETE;
        stmt.execute("UPDATE import_summary SET status = " + STATUS_COMPLETE + ", time_import_end = " + System.currentTimeMillis() / 1000 + " WHERE file_name = '" + _fileName + "'");
    }

    //  Attempt to load import_summary.id for specified log file.
    //  If log exists, return true; otherwise, return false
    public boolean load(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();

        boolean result = false;
        ResultSet rsExists = stmt.executeQuery("SELECT id, status FROM import_summary WHERE file_name = '" + _fileName + "'");
        if (rsExists != null && rsExists.next()) {
            //_status = rsExists.getInt(1);
            setId(rsExists.getInt(1));
            setStatus(rsExists.getInt(2));
            result = true;
        }
        return result;
    }

    /**
     * Retrieve Id of Most Recently Inserted Summary entry
     *
     * @param stmt
     * @throws SQLException
     */
    public void insert(Statement stmt) throws SQLException {
        stmt.execute(getInsertSQL());
        ResultSet rs = stmt.executeQuery("SELECT LAST_INSERT_ID()");
        if (rs != null && rs.next()) {
            setId(rs.getInt(1));
        }
    }

    public int getId() {
        return _id;
    }

    public void setId(int id) {
        _id = id;
    }

    public int getCountFailed() {
        return _countFailed;
    }

    public void setCountFailed(int countFailed) {
        _countFailed = countFailed;
    }

    public int getCountFiltered() {
        return _countFiltered;
    }

    public void setCountFiltered(int countFiltered) {
        _countFiltered = countFiltered;
    }

    public int getCountImported() {
        return _countImported;
    }

    public void setCountImported(int countImported) {
        _countImported = countImported;
    }

    public int getCountTotal() {
        return _countTotal;
    }

    public void setCountTotal(int countTotal) {
        _countTotal = countTotal;
    }

    public String getDomain() {
        return _domain;
    }

    public void setDomain(String domain) {
        _domain = domain;
    }

    public String getFileName() {
        return _fileName;
    }

    /**
     * Set the File Name & Domain of this Summary entry
     *
     * @param fileName Log File Name
     */
    public void setFileName(String fileName) {
        _fileName = fileName;
        if (_fileName != null) {
            setDomain(getDomainByFileName(_fileName));
        }
    }

    public String getLogMachine() {
        return _logMachine;
    }

    public void setLogMachine(String logMachine) {
        _logMachine = logMachine;
    }

    public long getTimeFirstEntry() {
        return _timeFirstEntry;
    }

    public void setTimeFirstEntry(long timeFirstEntry) {
        _timeFirstEntry = timeFirstEntry;
    }

    public long getTimeImportEnd() {
        return _timeImportEnd;
    }

    public void setTimeImportEnd(long timeImportEnd) {
        _timeImportEnd = timeImportEnd;
    }

    public long getTimeImportStart() {
        return _timeImportStart;
    }

    public void setTimeImportStart(long timeImportStart) {
        _timeImportStart = timeImportStart;
    }

    public long getTimeLastEntry() {
        return _timeLastEntry;
    }

    public void setTimeLastEntry(long timeLastEntry) {
        _timeLastEntry = timeLastEntry;
    }

    public int getStatus() {
        return _status;
    }

    public void setStatus(int status) {
        _status = status;
    }
}
