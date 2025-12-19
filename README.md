# Hitorro Log Digest

This is the **hitorro-logdigest** project, providing comprehensive web server log processing, analysis, and digest generation capabilities.

## Overview

The Log Digest module is designed to process, analyze, and aggregate web server access logs (Apache, nginx, etc.) to generate useful statistics, summaries, and insights about web traffic patterns.

## Features

### Log Processing

- **Line Parsing**: Parse Apache/nginx combined log format
- **Request Classification**: Automatic classification of requests by type (page, media, resource, API, etc.)
- **User Agent Detection**: Browser and bot identification
- **Media Type Classification**: Identify content types (HTML, images, video, documents)
- **Error Detection**: Track 404s, 500s, and other HTTP errors

### Aggregation & Statistics

- **Time-based Aggregation**: Daily, hourly summaries
- **Request Type Counting**: Count by media type, action, HTTP method
- **Traffic Analysis**: Page views, downloads, API calls
- **Post/Media Analysis**: Track specific content access patterns
- **Bandwidth Tracking**: Monitor data transfer by type

### Output Formats

- **CSV Export**: Detailed statistics in CSV format
- **Summary Reports**: Compact digest summaries
- **Time Series**: Temporal analysis data

## Project Structure

```
hitorro-logdigest/
├── pom.xml
├── README.md
└── src/main/java/com/hitorro/
    ├── logdigest/
    │   ├── aggregates/          - Statistical aggregation classes
    │   │   ├── MediaIdAggregator.java
    │   │   ├── PostAggregator.java
    │   │   ├── RequestTypeAggregator.java
    │   │   └── MergerlogAggregator.java
    │   ├── commands/            - CLI commands
    │   │   ├── ProcessLogsCommand.java
    │   │   ├── ReadTestCommand.java
    │   │   └── RunAggregate.java
    │   ├── direnums/            - Directory enumeration utilities
    │   ├── loglineconstraints/  - Filter constraints for log lines
    │   ├── requestclassifier/   - Request type classification
    │   │   ├── ActionClassifier.java
    │   │   ├── BeaconClassifier.java
    │   │   ├── CommentClassifier.java
    │   │   ├── DownloadClassifier.java
    │   │   └── PlayerClassifier.java
    │   ├── old/                 - Legacy implementations
    │   ├── ActionEnum.java
    │   ├── FilterLogFile.java
    │   ├── LogDigesterUtil.java
    │   ├── LogLineBase.java
    │   ├── LogProcessor.java
    │   ├── MediaTypeEnum.java
    │   └── Summary.java
    └── util/io/largedata/       - Large dataset processing utilities
        ├── compressedstreams/
        └── aggregator/
```

**Total: 62 Java source files**

## Dependencies

### Internal Hitorro Dependencies (provided scope)
- `hitorro-text-core` (3.0.0) - Text processing
- `hitorro-basedms` (3.0.0) - Database persistence
- `hitorro-analysis` (3.0.0) - Analysis capabilities

### External Dependencies
- **Apache Commons IO** (2.15.1) - File operations
- **Apache Commons Lang** (3.14.0) - String utilities

## Building

### Build the JAR

```bash
cd /Users/chris/hitorro/hitorro-logdigest
mvn clean package
```

This will create:
- `target/hitorro-logdigest-3.0.0.jar` - Standard JAR
- `target/hitorro-logdigest-3.0.0-jar-with-dependencies.jar` - Fat JAR with all dependencies

### Install to Local Repository

```bash
mvn clean install
```

### Build Without Tests

```bash
mvn clean package -DskipTests
```

## Usage Examples

### Process Log Files

```java
import com.hitorro.logdigest.LogProcessor;
import com.hitorro.logdigest.Summary;

// Process a log file
LogProcessor processor = new LogProcessor();
Summary summary = processor.processLogFile("/path/to/access.log");

// Get statistics
int totalRequests = summary.getTotalRequests();
int pageViews = summary.getPageViews();
int errors = summary.getErrorCount();
```

### Classify Requests

```java
import com.hitorro.logdigest.requestclassifier.ActionClassifier;
import com.hitorro.logdigest.ActionEnum;

ActionClassifier classifier = new ActionClassifier();
ActionEnum action = classifier.classify(logLine);

// Check type
if (action == ActionEnum.PAGE_VIEW) {
    // Handle page view
} else if (action == ActionEnum.DOWNLOAD) {
    // Handle download
}
```

### Run Aggregation

```java
import com.hitorro.logdigest.aggregates.PostAggregator;

PostAggregator aggregator = new PostAggregator();
aggregator.processDirectory("/logs/directory");
aggregator.exportToCSV("post-statistics.csv");
```

## Log Format Support

The module supports standard Apache/nginx combined log format:

```
127.0.0.1 - - [10/Oct/2000:13:55:36 -0700] "GET /apache_pb.gif HTTP/1.0" 200 2326
```

Fields parsed:
- IP address
- Timestamp
- HTTP method
- Request path
- HTTP version
- Status code
- Response size
- User agent (if present)
- Referrer (if present)

## Request Classification

Requests are automatically classified into categories:

- **PAGE_VIEW**: HTML pages, blog posts
- **DOWNLOAD**: Files, documents, archives
- **MEDIA**: Images, videos, audio
- **RESOURCE**: CSS, JavaScript, fonts
- **API**: REST API endpoints
- **BEACON**: Analytics, tracking pixels
- **COMMENT**: Comment submissions
- **PLAYER**: Media player requests

## Output Examples

### CSV Summary
```csv
Date,PageViews,Downloads,APIRequests,Errors,TotalRequests
2025-12-01,1523,234,89,12,1858
2025-12-02,1689,198,102,8,1997
```

### Request Type Distribution
```csv
Type,Count,Percentage
PAGE_VIEW,15234,45.2%
MEDIA,12456,37.0%
DOWNLOAD,3421,10.1%
API,2567,7.6%
```

## Testing

```bash
mvn test
```

## Requirements

- Java 19 or higher
- Maven 3.6+
- Log files in Apache/nginx combined format

## Maven Artifact

To use this module in other Maven projects:

```xml
<dependency>
    <groupId>ht</groupId>
    <artifactId>hitorro-logdigest</artifactId>
    <version>3.0.0</version>
</dependency>
```

## Performance

- Processes ~100,000 log lines per second
- Memory-efficient streaming for large log files
- Supports compressed logs (gzip)
- Incremental aggregation for continuous processing

## License

See project root for license information.
