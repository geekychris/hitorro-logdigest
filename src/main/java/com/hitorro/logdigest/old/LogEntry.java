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

public class LogEntry {

    public static final String DIGEST_TABLE = "access_log_digest";

    private int _id;
    private int _summaryId;

    private int _recordId;
    private int _agentId;
    private int _referrerId;
    private int _remoteHostId;
    private int _requestMethodId;
    private int _requestProtocolId;
    private int _requestLineId;
    private int _requestURIId;
    private int _statusId;
    private int _breadcrumbId;
    private int _referrerDomainId;
    private int _HiTorroidId;
    private int _mediaTypeId;
    private int _requestTypeId;

    private int _timeStamp;
    private int _requestDuration;
    private int _bytesSent;

    public LogEntry() {
    }

    public static final String getInsertColumns() {
        StringBuffer query = new StringBuffer("INSERT INTO ");
        query.append(DIGEST_TABLE);
        query.append(" (agent_id, bytes_sent, referer_id, remote_host_id, request_duration, request_method_id, request_protocol_id, " +
                "request_line_id, request_uri_id, status_id, time_stamp, summary_id, breadcrumb_id, referer_domain_id, HiTorroid_id, requesttype_id, mediatype_id) ");
        query.append(" VALUES ");
        return query.toString();
    }

    public String getInsertSQL() {
        return getInsertStatement();
    }

    public String getInsertStatement() {
        StringBuffer query = new StringBuffer(getInsertColumns());
        query.append(getInsertValues());
        return query.toString();
    }

    public String getInsertValues() {
        StringBuffer query = new StringBuffer("(");
        query.append(getAgentId());
        query.append(", '");
        query.append(getBytesSent());
        query.append("', ");
        query.append(getReferrerId());
        query.append(", ");
        query.append(getRemoteHostId());
        query.append(", ");
        query.append(getRequestDuration());
        query.append(", ");
        query.append(getRequestMethodId());
        query.append(", ");
        query.append(getRequestProtocolId());
        query.append(", ");
        query.append(getRequestLineId());
        query.append(", ");
        query.append(getRequestURIId());
        query.append(", ");
        query.append(getStatusId());
        query.append(", ");
        query.append(getTimeStamp());
        query.append(", ");
        query.append(getSummaryId());
        query.append(", ");
        query.append(getBreadcrumbId());
        query.append(", ");
        query.append(getReferrerDomainId());
        query.append(", ");
        query.append(getHiTorroidId());
        query.append(", ");
        query.append(getRequestTypeId());
        query.append(", ");
        query.append(getMediaTypeId());
        query.append(") ");
        return query.toString();
    }


    public int getAgentId() {
        return _agentId;
    }

    public void setAgentId(int agentId) {
        _agentId = agentId;
    }

    public int getBytesSent() {
        return _bytesSent;
    }

    public void setBytesSent(int bytesSent) {
        _bytesSent = bytesSent;
    }

    public int getId() {
        return _id;
    }

    public void setId(int id) {
        _id = id;
    }

    public int getRecordId() {
        return _recordId;
    }

    public void setRecordId(int recordId) {
        _recordId = recordId;
    }

    public int getReferrerId() {
        return _referrerId;
    }

    public void setReferrerId(int referrerId) {
        _referrerId = referrerId;
    }

    public int getRemoteHostId() {
        return _remoteHostId;
    }

    public void setRemoteHostId(int remoteHostId) {
        _remoteHostId = remoteHostId;
    }

    public int getRequestDuration() {
        return _requestDuration;
    }

    public void setRequestDuration(int requestDuration) {
        _requestDuration = requestDuration;
    }

    public int getRequestLineId() {
        return _requestLineId;
    }

    public void setRequestLineId(int requestLineId) {
        _requestLineId = requestLineId;
    }

    public int getRequestMethodId() {
        return _requestMethodId;
    }

    public void setRequestMethodId(int requestMethodId) {
        _requestMethodId = requestMethodId;
    }

    public int getRequestProtocolId() {
        return _requestProtocolId;
    }

    public void setRequestProtocolId(int requestProtocolId) {
        _requestProtocolId = requestProtocolId;
    }

    public int getRequestURIId() {
        return _requestURIId;
    }

    public void setRequestURIId(int requestURIId) {
        _requestURIId = requestURIId;
    }

    public int getStatusId() {
        return _statusId;
    }

    public void setStatusId(int statusId) {
        _statusId = statusId;
    }

    public int getTimeStamp() {
        return _timeStamp;
    }

    public void setTimeStamp(int timeStamp) {
        _timeStamp = timeStamp;
    }

    public int getSummaryId() {
        return _summaryId;
    }

    public void setSummaryId(int summaryId) {
        _summaryId = summaryId;
    }

    public int getBreadcrumbId() {
        return _breadcrumbId;
    }

    public void setBreadcrumbId(int val) {
        _breadcrumbId = val;
    }

    public int getReferrerDomainId() {
        return _referrerDomainId;
    }

    public void setReferrerDomainId(int val) {
        _referrerDomainId = val;
    }

    public int getHiTorroidId() {
        return _HiTorroidId;
    }

    public void setHiTorroidId(int val) {
        _HiTorroidId = val;
    }

    public int getMediaTypeId() {
        return _mediaTypeId;
    }

    public void setMediaTypeId(int val) {
        _mediaTypeId = val;
    }

    public int getRequestTypeId() {
        return _requestTypeId;
    }

    public void setRequestTypeId(int val) {
        _requestTypeId = val;
    }
}
