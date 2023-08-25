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
package org.nervousync.database.dialects;

import org.nervousync.database.beans.configs.column.ColumnConfig;

/**
 * <h2 class="en-US">Data converter interface from Java types to database types</h2>
 * <h2 class="zh-CN">从Java类型到数据库类型的数据转换器接口</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Apr 1, 2021 13:14:37 $
 */
public interface Converter {
    /**
     * <h3 class="en-US">Convert data</h3>
     * <span class="en-US">
     *     According to the given data column configuration information instance
     *     and column value object instance converted to database type
     * </span>
     * <h3 class="zh-CN">转换数据</h3>
     * <span class="zh-CN">根据给定的数据列配置信息实例和列值对象实例转换为数据库类型</span>
     *
     * @param columnConfig <span class="en-US">Column configure information instance</span>
     *                     <span class="zh-CN">数据列配置信息实例对象</span>
     * @param columnValue  <span class="en-US">Column value</span>
     *                     <span class="zh-CN">数据列值</span>
     * @return <span class="en-US">Converted data</span>
     * <span class="zh-CN">转换后的数据</span>
     */
    Object convertValue(final ColumnConfig columnConfig, final Object columnValue);

}
