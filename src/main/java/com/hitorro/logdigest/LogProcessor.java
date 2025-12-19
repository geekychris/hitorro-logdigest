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

import com.hitorro.util.basefile.fs.BaseFile;
import com.hitorro.util.json.keys.BasefileProperty;
import com.hitorro.util.json.keys.BooleanProperty;
import com.hitorro.util.json.keys.StringProperty;
import com.hitorro.util.mail.RawSMTPMailer;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * LogDigest processes raw log data & inserts relational data into database
 */
public class LogProcessor {

    public static final BasefileProperty SourceDirectory =
            new BasefileProperty("logdigest.sourcedir", "directory of log files to be read", "");

    public static final BasefileProperty DBDirectory =
            new BasefileProperty("logdigest.dbdir", "directory of database files", "");
    public static final BasefileProperty DestinationDirectory =
            new BasefileProperty("logdigest.destdir", "directory where processed log files are written", "");

    public static final BasefileProperty GeoIpFile =
            new BasefileProperty("logdigest.geoip.path", "path of geoip file", "");


    public static final StringProperty AdminEmail =
            new StringProperty("logdigest.admin.email", "admin email address", "steve@HiTorro.net");
    public static final StringProperty MetricsHost =
            new StringProperty("logdigest.db.metrics.host", "hostname of metrics database", "localhost");
    public static final StringProperty MetricsDb =
            new StringProperty("logdigest.db.metrics.dbname", "database name of metrics database", "HiTorrometrics");
    public static final StringProperty MetricsDbUser =
            new StringProperty("logdigest.db.metrics.user", "user of metrics database", "root");
    public static final StringProperty MetricsDbPassword =
            new StringProperty("logdigest.db.metrics.pass", "password of metrics database", "root");
    public static final BooleanProperty MetricsDoGeoip =
            new BooleanProperty("logdigest.dogeoip", "whether to run the geoip work", false);
    //  Date Format to appear in error email
    private static final SimpleDateFormat _dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private static final String LOG_DIGEST_MAIL = "logs@HiTorro.net";
    private static FilterExpressions _filters;      //  filters used to ignore entries in log file
    private boolean _initialized = false;
    //  This Thread's listFiles of Errors
    private List<String> _listErrors = new ArrayList<String>();

    private BaseFile dbDir;


    public LogProcessor() {
    }

    public boolean isInitialized() {
        return _initialized;
    }

    public void processLogFiles() throws IOException {
        processLogFiles(getLogFiles(), getDestinationDirectory(), getErrorDirectory(), getCacheDirectory());
    }

    /**
     * Process the Log Files in the Source Directory
     */
    public void processLogFiles(BaseFile files[], BaseFile processedDirectory, BaseFile errorDirectory, BaseFile dbDir) {
        this.dbDir = dbDir;
        if (_filters == null) {
            _filters = new FilterExpressions();
            _filters.init();
        }
        long startMain = System.currentTimeMillis();
        boolean processedFiles = false;

        try {
            //  Get files to be processed

            //  Process Files (if no files, exit)
            if (files != null && files.length > 0) {
                // process all the files
                String nodeID = "tempNodeID";
                LogFileProcessor p = new LogFileProcessor(_filters);
                for (int i = files.length - 1; i >= 0; i--) {
                    BaseFile f = files[i];
                    if (f.isFile()) {
                        p.processLogFile(f, processedDirectory, errorDirectory, nodeID, _listErrors, dbDir);
                        processedFiles = true;
                        //Log.logdigest.info("processed %s", files[i].toString());
                    }
                }
            } else {
                //System.out.println("No Log Files found in directory:" + getSourceDirectory());
            }
        } catch (Exception e) {
            //  Catch IO exceptions e.g. File Not Found;
            Log.logdigest.error("Exception processing log file %s %e", e, e);
            _listErrors.add(e.getMessage());
        }

        //  If Errors occur, mail to administrator
        //mailErrors();
    }

    private String getAdminEmail() {
        return AdminEmail.apply();
    }

    /**
     * @return Array of Log Files in Source Directory
     */
    private BaseFile[] getLogFiles() throws IOException {
        BaseFile sourceDirectory = getSourceDirectory();
        return sourceDirectory.listFiles();
    }

    /**
     * Mail Log Import Errors (if any) to Administrator
     */
    private void mailErrors() {
        if (_listErrors.size() > 0) {
            StringBuffer errors = new StringBuffer();
            for (String err : _listErrors) {
                errors.append(err);
                errors.append("\r\n");
            }

            String subject = "Errors During Import [" + _dateFormat.format(new Date(System.currentTimeMillis())) + "]";
            RawSMTPMailer.sendMessageRawSMTP(getAdminEmail(), subject, errors.toString(), LOG_DIGEST_MAIL);
            _listErrors.clear();    //  clear Errors
        }
    }

    // origin-media.HiTorro.net
    // origin-media.HiTorro.net/redirects/lsi/
    // download.php
    // www.johnfurrier.com
    // www.marketingvoices.com
    // www.nextgearshow.com

    //  Return the Destination Directory for Log Files after they have been successfully parsed & imported

    private BaseFile getDestinationDirectory() {
        BaseFile destDir = DestinationDirectory.apply();
        if (!destDir.exists()) {
            destDir.mkdir();
        }
        return destDir;
    }

    //  Return the Source Directory for Log Files to be parsed & imported
    private BaseFile getSourceDirectory() {
        BaseFile srcDir = SourceDirectory.apply();
        if (!srcDir.exists()) {
            srcDir.mkdir();
        }
        return srcDir;
    }

    private BaseFile getCacheDirectory() {
        BaseFile srcDir = DBDirectory.apply();
        if (!srcDir.exists()) {
            srcDir.mkdir();
        }
        return srcDir;
    }

    //  Return the Error Directory for Log Files that have failed parsing or importing
    private BaseFile getErrorDirectory() {
        BaseFile errorDir = getDestinationDirectory().getChild(LogFileProcessor.ERROR_LOG_PATH);
        if (!errorDir.exists()) {
            errorDir.mkdir();
        }
        return errorDir;
    }
}
