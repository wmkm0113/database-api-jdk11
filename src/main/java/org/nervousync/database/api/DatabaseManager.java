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
package org.nervousync.database.api;

/**
 * <h2 class="en-US">The interface of database manager</h2>
 * <h2 class="zh-CN">数据库管理器的接口</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Mar 30, 2021 14:18:46 $
 */
public interface DatabaseManager {
    /**
     * <h3 class="en-US">Initialize current manager</h3>
     * <h3 class="zh-CN">初始化当前管理器</h3>
     *
     * @return <span class="en-US">The result of initialize operate</span>
     * <span class="zh-CN">初始化操作的执行结果</span>
     */
    boolean initialize();

    /**
     * <h3 class="en-US">Register entity class array</h3>
     * <h3 class="zh-CN">注册实体类数组</h3>
     *
     * @param entityClasses <span class="en-US">Entity class array</span>
     *                      <span class="zh-CN">实体类数组</span>
     * @return <span class="en-US">Register succeed count of given entity class array</span>
     * <span class="zh-CN">给定实体类数组注册成功的数量</span>
     */
    int registerTable(final Class<?>... entityClasses);

    /**
     * <h3 class="en-US">Truncate entity class array</h3>
     * <h3 class="zh-CN">清空实体类数组的数据记录</h3>
     *
     * @param entityClasses <span class="en-US">Entity class array</span>
     *                      <span class="zh-CN">实体类数组</span>
     */
    void truncateTable(final Class<?>... entityClasses);

    /**
     * <h3 class="en-US">Remove entity class array</h3>
     * <h3 class="zh-CN">删除实体类数组</h3>
     *
     * @param entityClasses <span class="en-US">Entity class array</span>
     *                      <span class="zh-CN">实体类数组</span>
     * @return <span class="en-US">Drop succeed count of given entity class array</span>
     * <span class="zh-CN">给定实体类数组删除成功的数量</span>
     */
    int dropTable(final Class<?>... entityClasses);

    /**
     * <h3 class="en-US">Generate database client in read only mode</h3>
     * <h3 class="zh-CN">生成只读模式的数据操作客户端实例对象</h3>
     *
     * @return <span class="en-US">Generated database client instance</span>
     * <span class="zh-CN">生成的数据操作客户端实例对象</span>
     */
    DatabaseClient readOnlyClient();

    /**
     * <h3 class="en-US">Generate database client</h3>
     * <h3 class="zh-CN">生成数据操作客户端实例对象</h3>
     *
     * @return <span class="en-US">Generated database client instance</span>
     * <span class="zh-CN">生成的数据操作客户端实例对象</span>
     */
    DatabaseClient generateClient();

    /**
     * <h3 class="en-US">Generate database client in transactional mode</h3>
     * <h3 class="zh-CN">生成事务模式的数据操作客户端实例对象</h3>
     *
     * @param clazz      <span class="en-US">The database client using for class</span>
     *                   <span class="zh-CN">使用数据操作客户端的类</span>
     * @param methodName <span class="en-US">The database client using for method name</span>
     *                   <span class="zh-CN">使用数据操作客户端的方法名</span>
     * @return <span class="en-US">Generated database client instance</span>
     * <span class="zh-CN">生成的数据操作客户端实例对象</span>
     */
    DatabaseClient generateClient(final Class<?> clazz, final String methodName);

    /**
     * <h3 class="en-US">Destroy current database manager instance</h3>
     * <h3 class="zh-CN">销毁当前数据库管理器实例对象</h3>
     */
    void destroy();
}
