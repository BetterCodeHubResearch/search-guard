/*
 * Copyright 2017 floragunn GmbH
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package com.floragunn.searchguard.auditlog;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.rest.RestRequest;
import org.elasticsearch.tasks.Task;
import org.elasticsearch.transport.TransportRequest;

import com.floragunn.searchguard.ssl.SslExceptionHandler;

public class AuditLogSslExceptionHandler implements SslExceptionHandler{

    private final AuditLog auditLog;

    public AuditLogSslExceptionHandler(final AuditLog auditLog) {
        super();
        this.auditLog = auditLog;
    }

    @Override
    public void logError(Throwable t, RestRequest request, int type) {
        switch (type) {
        case 0:
            auditLog.logSSLException(request, t);
            break;
        case 1:
            auditLog.logBadHeaders(request);
            break;
        default:
            break;
        }
    }

    @Override
    public void logError(Throwable t, boolean isRest) {
        if (isRest) {
            auditLog.logSSLException(null, t);
        } else {
            auditLog.logSSLException(null, t, null, null);
        }
    }

    @Override
    public void logError(Throwable t, TransportRequest request, String action, Task task, int type) {
        switch (type) {
        case 0:
            if(t instanceof ElasticsearchException) {
                auditLog.logMissingPrivileges(action, request, task);
            } else {
                auditLog.logSSLException(request, t, action, task);
            }
            break;
        case 1:
            auditLog.logBadHeaders(request, action, task);
            break;
        default:
            break;
        }
    }

}
