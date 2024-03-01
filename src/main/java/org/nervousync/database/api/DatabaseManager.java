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
package org.nervousync.database.api;

import org.nervousync.database.beans.configs.table.TableConfig;
import org.nervousync.database.beans.configs.transactional.TransactionalConfig;

/**
 * <h2 class="en-US">The interface of database manager</h2>
 * <h2 class="zh-CN">数据库管理器的接口</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Mar 30, 2021 14:18:46 $
 */
public interface DatabaseManager {
    /**
     * <h4 class="en-US">Initialize current manager</h4>
     * <h4 class="zh-CN">初始化当前管理器</h4>
     *
     * @return <span class="en-US">The result of initialize operate</span>
     * <span class="zh-CN">初始化操作的执行结果</span>
     */
    boolean initialize();

    /**
     * <h4 class="en-US">Initialize the data table according to the given data table configuration information</h4>
     * <h4 class="zh-CN">根据给定的数据表配置信息初始化数据表</h4>
     *
     * @param tableConfig <span class="en-US">Table configure information</span>
     *                    <span class="zh-CN">数据表配置信息</span>
     * @return <span class="en-US">Initialize result</span>
     * <span class="zh-CN">初始化结果</span>
     */
    boolean initTable(final TableConfig tableConfig);

    /**
     * <h4 class="en-US">Truncate entity class array</h4>
     * <h4 class="zh-CN">清空实体类数组的数据记录</h4>
     *
     * @param entityClasses <span class="en-US">Entity class array</span>
     *                      <span class="zh-CN">实体类数组</span>
     */
    void truncateTable(final Class<?>... entityClasses);

    /**
     * <h4 class="en-US">Drop the data table according to the given data table configuration information</h4>
     * <h4 class="zh-CN">根据给定的数据表配置信息删除数据表</h4>
     *
     * @param tableConfig <span class="en-US">Table configure information</span>
     *                    <span class="zh-CN">数据表配置信息</span>
     * @return <span class="en-US">Initialize result</span>
     * <span class="zh-CN">初始化结果</span>
     */
    boolean dropTable(final TableConfig tableConfig);

    /**
     * <h4 class="en-US">Generate database client in data restore mode</h4>
     * <h4 class="zh-CN">生成数据恢复模式的数据操作客户端实例对象</h4>
     *
     * @return <span class="en-US">Generated database client instance</span>
     * <span class="zh-CN">生成的数据操作客户端实例对象</span>
     */
    DatabaseClient restoreClient();

    /**
     * <h4 class="en-US">Generate database client in read only mode</h4>
     * <h4 class="zh-CN">生成只读模式的数据操作客户端实例对象</h4>
     *
     * @return <span class="en-US">Generated database client instance</span>
     * <span class="zh-CN">生成的数据操作客户端实例对象</span>
     */
    DatabaseClient readOnlyClient();

    /**
     * <h4 class="en-US">Generate database client</h4>
     * <h4 class="zh-CN">生成数据操作客户端实例对象</h4>
     *
     * @return <span class="en-US">Generated database client instance</span>
     * <span class="zh-CN">生成的数据操作客户端实例对象</span>
     */
    DatabaseClient generateClient();

    /**
     * <h4 class="en-US">Generate database client in transactional mode</h4>
     * <h4 class="zh-CN">生成事务模式的数据操作客户端实例对象</h4>
     *
     * @param txConfig <span class="en-US">Transactional configure information object instance</span>
     *                 <span class="zh-CN">事务配置信息实例对象</span>
     * @return <span class="en-US">Generated database client instance</span>
     * <span class="zh-CN">生成的数据操作客户端实例对象</span>
     */
    default DatabaseClient generateClient(final TransactionalConfig txConfig) {
        return this.generateClient(txConfig, Boolean.FALSE);
    }

    /**
     * <h4 class="en-US">Generate database client in transactional mode</h4>
     * <h4 class="zh-CN">生成事务模式的数据操作客户端实例对象</h4>
     *
     * @param txConfig    <span class="en-US">Transactional configure information object instance</span>
     *                    <span class="zh-CN">事务配置信息实例对象</span>
     * @param restoreMode <span class="en-US">Data restore mode</span>
     *                    <span class="zh-CN">数据恢复模式</span>
     * @return <span class="en-US">Generated database client instance</span>
     * <span class="zh-CN">生成的数据操作客户端实例对象</span>
     */
    DatabaseClient generateClient(final TransactionalConfig txConfig, final boolean restoreMode);

    /**
     * <h4 class="en-US">Find the corresponding client instance object based on the given transaction identification code</h4>
     * <h4 class="zh-CN">根据给定的事务识别代码查找对应的客户端实例对象</h4>
     *
     * @param transactionalCode <span class="en-US">transaction identification code</span>
     *                          <span class="zh-CN">事务识别代码</span>
     * @return <span class="en-US">Retrieved database client instance, return <code>null</code> if not found</span>
     * <span class="zh-CN">找到的数据操作客户端实例对象，如果未找到则返回<code>null</code></span>
     */
    DatabaseClient retrieveClient(final long transactionalCode);

    /**
     * <h4 class="en-US">Destroy current database manager instance</h4>
     * <h4 class="zh-CN">销毁当前数据库管理器实例对象</h4>
     */
    void destroy();
}
