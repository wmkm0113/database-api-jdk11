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

import org.nervousync.database.exceptions.query.QueryException;
import org.nervousync.database.exceptions.record.RecordException;
import org.nervousync.database.query.PartialCollection;
import org.nervousync.database.query.QueryInfo;

import java.io.Serializable;
import java.util.Map;

/**
 * <h2 class="en-US">The interface of database client</h2>
 * <h2 class="zh-CN">数据操作客户端的接口</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Mar 30, 2021 13:57:28 $
 */
public interface DatabaseClient {
    /**
     * <h3 class="en-US">Rollback current transactional</h3>
     * <h3 class="zh-CN">回滚当前事务</h3>
     *
     * @param e <span class="en-US">Catch exception instance</span>
     *          <span class="zh-CN">捕获的异常实例</span>
     */
    void rollbackTransactional(final Exception e);

    /**
     * <h3 class="en-US">Finish current transactional</h3>
     * <h3 class="zh-CN">结束当前事务</h3>
     */
    void endTransactional();

    /**
     * <h3 class="en-US">Save the given records object to database</h3>
     * <h3 class="zh-CN">保存给定的记录实例对象到数据库</h3>
     *
     * @param recordObjects <span class="en-US">Record object array</span>
     *                      <span class="zh-CN">记录实例对象数组</span>
     * @throws RecordException <span class="en-US">If an error occurs when save record to database</span>
     *                         <span class="zh-CN">如果写入记录到数据库中出现异常</span>
     */
    void saveRecords(final Object... recordObjects) throws RecordException;

    /**
     * <h3 class="en-US">Update the given records object to database</h3>
     * <h3 class="zh-CN">更新给定的记录实例对象到数据库</h3>
     *
     * @param recordObjects <span class="en-US">Record object array</span>
     *                      <span class="zh-CN">记录实例对象数组</span>
     * @throws RecordException <span class="en-US">If an error occurs when update record to database</span>
     *                         <span class="zh-CN">如果更新记录到数据库中出现异常</span>
     */
    void updateRecords(final Object... recordObjects) throws RecordException;

    /**
     * <h3 class="en-US">Drop the given records object to database</h3>
     * <h3 class="zh-CN">从数据库中删除给定的记录实例对象</h3>
     *
     * @param recordObjects <span class="en-US">Record object array</span>
     *                      <span class="zh-CN">记录实例对象数组</span>
     * @throws RecordException <span class="en-US">If an error occurs when drop record to database</span>
     *                         <span class="zh-CN">如果删除记录到数据库中出现异常</span>
     */
    void dropRecords(final Object... recordObjects) throws RecordException;

    /**
     * <h3 class="en-US">Retrieve record from database by given primary key value</h3>
     * <h3 class="zh-CN">根据给定的主键值，从数据库中查询唯一记录</h3>
     *
     * @param <T>         <span class="en-US">Target entity class</span>
     *                    <span class="zh-CN">目标实体类</span>
     * @param primaryKey  <span class="en-US">Primary key value</span>
     *                    <span class="zh-CN">主键值</span>
     * @param entityClass <span class="en-US">Target entity class</span>
     *                    <span class="zh-CN">目标实体类</span>
     * @param forUpdate   <span class="en-US">Retrieve record for update</span>
     *                    <span class="zh-CN">读取记录用于更新操作</span>
     * @return <span class="en-US">Retrieved record or <code>null</code> if not found</span>
     * <span class="zh-CN">查询到的数据记录对象，如果未找到记录则返回 <code>null</code></span>
     * @throws RecordException <span class="en-US">If an error occurs when query record from database</span>
     *                         <span class="zh-CN">如果从数据库中查询记录出现异常</span>
     */
    <T> T retrieve(final Serializable primaryKey, final Class<T> entityClass, final boolean forUpdate)
            throws RecordException;

    /**
     * <h3 class="en-US">Retrieve record from database by given composite primary key map</h3>
     * <h3 class="zh-CN">根据给定的联合主键值映射表，从数据库中查询唯一记录</h3>
     *
     * @param <T>           <span class="en-US">Target entity class</span>
     *                      <span class="zh-CN">目标实体类</span>
     * @param primaryKeyMap <span class="en-US">Composite primary key map</span>
     *                      <span class="zh-CN">联合主键值映射表</span>
     * @param entityClass   <span class="en-US">Target entity class</span>
     *                      <span class="zh-CN">目标实体类</span>
     * @param forUpdate     <span class="en-US">Retrieve record for update</span>
     *                      <span class="zh-CN">读取记录用于更新操作</span>
     * @return <span class="en-US">Retrieved record or <code>null</code> if not found</span>
     * <span class="zh-CN">查询到的数据记录对象，如果未找到记录则返回 <code>null</code></span>
     * @throws RecordException <span class="en-US">If an error occurs when query record from database</span>
     *                         <span class="zh-CN">如果从数据库中查询记录出现异常</span>
     */
    <T> T retrieve(final Map<String, Object> primaryKeyMap, final Class<T> entityClass, final boolean forUpdate)
            throws RecordException;

    /**
     * <h3 class="en-US">Query total record count by given query information</h3>
     * <h3 class="zh-CN">查询满足给定查询条件的记录数</h3>
     *
     * @param queryInfo <span class="en-US">Query information</span>
     *                  <span class="zh-CN">查询条件</span>
     * @return <span class="en-US">Total record count or <code>-1</code> if table not existed</span>
     * <span class="zh-CN">满足查询条件的记录数，如果表不存在则返回 <code>-1</code></span>
     * @throws QueryException <span class="en-US">If an error occurs when query record from database</span>
     *                        <span class="zh-CN">如果从数据库中查询记录出现异常</span>
     */
    long queryTotal(final QueryInfo queryInfo) throws QueryException;

    /**
     * <h3 class="en-US">Query record by given query information</h3>
     * <h3 class="zh-CN">查询满足给定查询条件的记录</h3>
     *
     * @param <T>         <span class="en-US">Target result class</span>
     *                    <span class="zh-CN">目标结果集实体类</span>
     * @param queryInfo   <span class="en-US">Query information instance</span>
     *                    <span class="zh-CN">查询条件信息实例对象</span>
     * @param targetClass <span class="en-US">Target record class</span>
     *                    <span class="zh-CN">目标记录类</span>
     * @return <span class="en-US">Query results wrapped by PartialCollection</span>
     * <span class="zh-CN">使用PartialCollection包装的查询结果集</span>
     * @throws QueryException <span class="en-US">If an error occurs when query record from database</span>
     *                        <span class="zh-CN">如果从数据库中查询记录出现异常</span>
     */
    <T> PartialCollection<T> queryList(final QueryInfo queryInfo, final Class<T> targetClass) throws QueryException;

    /**
     * <h3 class="en-US">Query record by given query information</h3>
     * <h3 class="zh-CN">查询满足给定查询条件的记录</h3>
     *
     * @param <T>         <span class="en-US">Target result class</span>
     *                    <span class="zh-CN">目标结果集实体类</span>
     * @param queryInfo   <span class="en-US">Query information instance</span>
     *                    <span class="zh-CN">查询条件信息实例对象</span>
     * @param targetClass <span class="en-US">Target record class</span>
     *                    <span class="zh-CN">目标记录类</span>
     * @return <span class="en-US">Target result instance</span>
     * <span class="zh-CN">目标结果实例对象</span>
     * @throws QueryException <span class="en-US">If an error occurs when query record from database</span>
     *                        <span class="zh-CN">如果从数据库中查询记录出现异常</span>
     */
    <T> T query(final QueryInfo queryInfo, final Class<T> targetClass) throws QueryException;

}
