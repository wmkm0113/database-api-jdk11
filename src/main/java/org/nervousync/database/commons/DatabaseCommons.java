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
package org.nervousync.database.commons;

/**
 * <h2 class="en-US">Constant value define</h2>
 * <h2 class="zh-CN">常量定义</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Mar 30, 2016 16:07:13 $
 */
public final class DatabaseCommons {
	/**
     * <span class="en-US">Default page number</span>
     * <span class="zh-CN">默认起始页</span>
	 */
	public static final int DEFAULT_PAGE_NO = 1;
	/**
     * <span class="en-US">Default page limit</span>
     * <span class="zh-CN">默认每页记录数</span>
	 */
	public static final int DEFAULT_PAGE_LIMIT = 20;
	/**
     * <span class="en-US">The number of threads executed simultaneously by the default data import and export task</span>
     * <span class="zh-CN">默认数据导入导出任务同时执行的线程数</span>
	 */
	public static final int DEFAULT_PROCESS_THREAD_LIMIT = 20;
	/**
     * <span class="en-US">The default expiration time after the data import and export task is completed</span>
     * <span class="zh-CN">默认数据导入导出任务完成后的过期时间</span>
	 */
	public static final long DEFAULT_STORAGE_EXPIRE_TIME = 7 * 24 * 60 * 60 * 1000L;
	/**
     * <span class="en-US">Data import and export task status: Create</span>
     * <span class="zh-CN">数据导入导出任务状态：创建</span>
	 */
	public static final int DATA_TASK_STATUS_CREATE = 0;
	/**
     * <span class="en-US">Data import and export task status: Processing</span>
     * <span class="zh-CN">数据导入导出任务状态：处理中</span>
	 */
	public static final int DATA_TASK_STATUS_PROCESS = 1;
	/**
     * <span class="en-US">Data import and export task status: Finished</span>
     * <span class="zh-CN">数据导入导出任务状态：已完成</span>
	 */
	public static final int DATA_TASK_STATUS_FINISH = 2;
	/**
     * <span class="en-US">Data file extension</span>
     * <span class="zh-CN">数据文件的扩展名</span>
	 */
	public static final String DATA_FILE_EXTENSION_NAME = ".dat";
	/**
     * <span class="en-US">Data file extension</span>
     * <span class="zh-CN">数据文件的扩展名</span>
	 */
	public static final String DATA_TMP_FILE_EXTENSION_NAME = ".tmp";
	/**
     * <span class="en-US">Default database alias</span>
     * <span class="zh-CN">默认的数据库别名</span>
	 */
	public static final String DEFAULT_DATABASE_ALIAS = "DefaultDatabase";
	/**
     * <span class="en-US">Database name of cache map key</span>
     * <span class="zh-CN">缓存数据表中数据库名称键值</span>
	 */
	public static final String CONTENT_MAP_KEY_DATABASE_NAME = "NSYC_DATABASE_NAME";
	/**
     * <span class="en-US">Table name of cache map key</span>
     * <span class="zh-CN">缓存数据表中数据表名称键值</span>
	 */
	public static final String CONTENT_MAP_KEY_TABLE_NAME = "NSYC_TABLE_NAME";
	/**
     * <span class="en-US">Table name of cache data type key</span>
     * <span class="zh-CN">缓存数据表中数据类型键值</span>
	 */
	public static final String CONTENT_MAP_KEY_DATA_TYPE = "NSYC_DATA_TYPE";
	/**
     * <span class="en-US">Lazy load map key item of cache map key</span>
     * <span class="zh-CN">缓存数据表中懒加载列名称键值</span>
	 */
	public static final String CONTENT_MAP_KEY_ITEM = "NSYC_CONTENT_ITEM";
	/**
	 * The constant TOTAL_COUNT_KEY.
	 */
	public static final String TOTAL_COUNT_KEY = "NSYC_RESULT_TOTAL_COUNT";
	/**
	 * The constant RESULT_LIST_KEY.
	 */
	public static final String RESULT_LIST_KEY = "NSYC_RESULT_RECORD_LIST";

}
