/*
 * Licensed to the Nervousync Studio (NSYC) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
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
