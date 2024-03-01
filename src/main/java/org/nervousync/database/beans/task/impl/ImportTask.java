/*
 * Licensed to the Nervousync Studio (NSYC) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.nervousync.database.beans.task.impl;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import org.nervousync.database.beans.task.AbstractTask;

/**
 * <h2 class="en-US">Data import task information</h2>
 * <h2 class="zh-CN">数据导入任务信息</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Mar 23, 2021 05:12:18 $
 */
@XmlRootElement(name = "import_task")
@XmlAccessorType(XmlAccessType.NONE)
public final class ImportTask extends AbstractTask {

    /**
     * <span class="en-US">Serial version UID</span>
     * <span class="zh-CN">序列化UID</span>
     */
    private static final long serialVersionUID = 5099054054731371174L;

    /**
     * <span class="en-US">Task data storage path</span>
     * <span class="zh-CN">任务数据存储路径</span>
     */
    @XmlElement(name = "data_path")
    private String dataPath;
    /**
     * <span class="en-US">Import tasks using transactions</span>
     * <span class="zh-CN">导入任务使用事务</span>
     */
    @XmlElement(name = "transactional")
    private boolean transactional;
    /**
     * <span class="en-US">Transaction timeout</span>
     * <span class="zh-CN">事务超时时间</span>
     */
    @XmlElement(name = "timeout")
    private int timeout;

	/**
	 * <h4 class="en-US">Constructor method for data import task information</h4>
	 * <h4 class="zh-CN">数据导入任务信息的构建方法</h4>
	 */
    public ImportTask() {
    }

    /**
     * <h4 class="en-US">Getter method for task data storage path</h4>
     * <h4 class="zh-CN">任务数据存储路径的Getter方法</h4>
     *
     * @return <span class="en-US">Task data storage path</span>
     * <span class="zh-CN">任务数据存储路径</span>
     */
    public String getDataPath() {
        return dataPath;
    }

    /**
     * <h4 class="en-US">Setter method for task data storage path</h4>
     * <h4 class="zh-CN">任务数据存储路径的Setter方法</h4>
     *
     * @param dataPath <span class="en-US">Task data storage path</span>
     *                 <span class="zh-CN">任务数据存储路径</span>
     */
    public void setDataPath(String dataPath) {
        this.dataPath = dataPath;
    }

    /**
     * <h4 class="en-US">Getter method for import tasks using transactions</h4>
     * <h4 class="zh-CN">导入任务使用事务的Getter方法</h4>
     *
     * @return <span class="en-US">Import tasks using transactions</span>
     * <span class="zh-CN">导入任务使用事务</span>
     */
    public boolean isTransactional() {
        return transactional;
    }

    /**
     * <h4 class="en-US">Setter method for import tasks using transactions</h4>
     * <h4 class="zh-CN">导入任务使用事务的Setter方法</h4>
     *
     * @param transactional <span class="en-US">Import tasks using transactions</span>
     *                      <span class="zh-CN">导入任务使用事务</span>
     */
    public void setTransactional(boolean transactional) {
        this.transactional = transactional;
    }

    /**
     * <h4 class="en-US">Getter method for transaction timeout</h4>
     * <h4 class="zh-CN">事务超时时间的Getter方法</h4>
     *
     * @return <span class="en-US">Transaction timeout</span>
     * <span class="zh-CN">事务超时时间</span>
     */
    public int getTimeout() {
        return timeout;
    }

    /**
     * <h4 class="en-US">Setter method for transaction timeout</h4>
     * <h4 class="zh-CN">事务超时时间的Setter方法</h4>
     *
     * @param timeout <span class="en-US">Transaction timeout</span>
     *                <span class="zh-CN">事务超时时间</span>
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
