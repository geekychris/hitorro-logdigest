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

import com.hitorro.basetext.dfindex.DFIndexSingletonMapper;
import com.hitorro.network.monitor.MonitorAlertUtil;
import com.hitorro.network.rpc.cluster.ClusterService;
import com.hitorro.util.core.Constants;
import com.hitorro.util.startupframework.phases.ServiceDefinition;

@ServiceDefinition(dependentService = {},
        shortName = "logdigestwordpress",
        description = "log digest wordpress service",
        debugCommands = {},
        typeManagedClasses = {},
        uiDirectories = {})
public class LogDigesterWordpressService {
    public static final String LogDigesterWordpressKey = "logdigesterwordpress";
    private static LogDigesterWordpressService s_service = null;
    private boolean m_isStarted = false;

    public String init(boolean dbInit, final boolean upgrading, final long currentVersion, final long targetVersion) {
        s_service = this;

        DFIndexSingletonMapper.getSingleton();
        ClusterService.getThisInstanceDefinition().addInstanceCapability(LogDigesterWordpressKey, Constants.EmptyString, Constants.EmptyString, Constants.EmptyString, true
        );
        MonitorAlertUtil.addLowDiskAlert();


        return null;
    }


    public String start(boolean dbInit) {
        if (m_isStarted) {
            return null;
        }

        m_isStarted = true;


        return null;
    }

    public String deInit() {
        if (m_isStarted) {
            m_isStarted = false;
        }

        s_service = null;

        return null;
    }
}
