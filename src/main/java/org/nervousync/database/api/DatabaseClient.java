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

import jakarta.annotation.Nonnull;
import org.nervousync.database.entity.core.BaseObject;
import org.nervousync.database.exceptions.operate.RetrieveException;
import org.nervousync.database.exceptions.query.QueryException;
import org.nervousync.database.query.QueryResult;
import org.nervousync.database.query.QueryInfo;

import java.util.Map;

/**
 * <h2 class="en-US">The interface of database client</h2>
 * <h2 class="zh-CN">数据操作客户端的接口</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Mar 30, 2021 13:57:28 $
 */
public interface DatabaseClient {

	/**
	 * <h3 class="en-US">Rollback current transactional</h3>
	 * <h3 class="zh-CN">回滚当前事务</h3>
	 */
	void rollbackTransactional();

	/**
	 * <h3 class="en-US">Finish current transactional</h3>
	 * <h3 class="zh-CN">结束当前事务</h3>
	 */
	void endTransactional();

	/**
	 * <h3 class="en-US">Check whether the given exception information belongs to a rollback exception</h3>
	 * <h3 class="zh-CN">检查给定的异常信息是否属于回滚异常</h3>
	 *
	 * @param e <span class="en-US">Catch exception instance</span>
	 *          <span class="zh-CN">捕获的异常实例</span>
	 * @return <span class="en-US">Check result</span>
	 * <span class="zh-CN">检查结果</span>
	 */
	boolean rollbackException(final Exception e);

	/**
	 * <h3 class="en-US">Save the given records object to database</h3>
	 * <h3 class="zh-CN">保存给定的记录实例对象到数据库</h3>
	 *
	 * @param recordObjects <span class="en-US">Record object array</span>
	 *                      <span class="zh-CN">记录实例对象数组</span>
	 * @throws Exception <span class="en-US">If an exception occurs during execution of the operation</span>
	 *                   <span class="zh-CN">如果执行操作过程中出现异常</span>
	 */
	void saveRecords(final BaseObject... recordObjects) throws Exception;

	/**
	 * <h3 class="en-US">Update the given records object to database</h3>
	 * <h3 class="zh-CN">更新给定的记录实例对象到数据库</h3>
	 *
	 * @param recordObjects <span class="en-US">Record object array</span>
	 *                      <span class="zh-CN">记录实例对象数组</span>
	 * @throws Exception <span class="en-US">If an exception occurs during execution of the operation</span>
	 *                   <span class="zh-CN">如果执行操作过程中出现异常</span>
	 */
	void updateRecords(final BaseObject... recordObjects) throws Exception;

	/**
	 * <h3 class="en-US">Drop the given records object to database</h3>
	 * <h3 class="zh-CN">从数据库中删除给定的记录实例对象</h3>
	 *
	 * @param recordObjects <span class="en-US">Record object array</span>
	 *                      <span class="zh-CN">记录实例对象数组</span>
	 * @throws Exception <span class="en-US">If an exception occurs during execution of the operation</span>
	 *                   <span class="zh-CN">如果执行操作过程中出现异常</span>
	 */
	void dropRecords(final BaseObject... recordObjects) throws Exception;

	/**
	 * <h3 class="en-US">Read lazy loaded column data</h3>
	 * <h3 class="zh-CN">读取懒加载的列数据</h3>
	 *
	 * @param primaryKeyMap <span class="en-US">Composite primary key map</span>
	 *                      <span class="zh-CN">联合主键值映射表</span>
	 * @param entityClass   <span class="en-US">Target entity class</span>
	 *                      <span class="zh-CN">目标实体类</span>
	 * @param identifyKey   <span class="en-US">Identify key</span>
	 *                      <span class="zh-CN">识别代码</span>
	 * @return <span class="en-US">Retrieved column data or null if not found</span>
	 * <span class="zh-CN">查询到的列数据，如果未找到返回空</span>
	 */
	Object lazyColumn(final Map<String, Object> primaryKeyMap, final Class<?> entityClass,
	                  final String identifyKey);

	/**
	 * <h3 class="en-US">Retrieve record from database by given primary key value</h3>
	 * <h3 class="zh-CN">根据给定的主键值，从数据库中查询唯一记录</h3>
	 *
	 * @param primaryKey  <span class="en-US">Primary key value</span>
	 *                    <span class="zh-CN">主键值</span>
	 * @param entityClass <span class="en-US">Target entity class</span>
	 *                    <span class="zh-CN">目标实体类</span>
	 * @param forUpdate   <span class="en-US">Retrieve record for update</span>
	 *                    <span class="zh-CN">读取记录用于更新操作</span>
	 * @param <T>         <span class="en-US">Target entity class</span>
	 *                    <span class="zh-CN">目标实体类</span>
	 * @return <span class="en-US">Retrieved record or <code>null</code> if not found</span>
	 * <span class="zh-CN">查询到的数据记录对象，如果未找到记录则返回 <code>null</code></span>
	 * @throws RetrieveException <span class="en-US">If an error occurs when query record from database</span>
	 *                           <span class="zh-CN">如果从数据库中查询记录出现异常</span>
	 */
	<T> T retrieve(final Object primaryKey, final Class<T> entityClass, final boolean forUpdate)
			throws RetrieveException;

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
	 * @throws RetrieveException <span class="en-US">If an error occurs when query record from database</span>
	 *                           <span class="zh-CN">如果从数据库中查询记录出现异常</span>
	 */
	<T> T retrieve(final Map<String, Object> primaryKeyMap, final Class<T> entityClass, final boolean forUpdate)
			throws RetrieveException;

	/**
	 * <h3 class="en-US">Query total record count by given query information</h3>
	 * <h3 class="zh-CN">查询满足给定查询条件的记录数</h3>
	 *
	 * @param queryInfo <span class="en-US">Query information instance object</span>
	 *                  <span class="zh-CN">查询信息实例对象</span>
	 * @return <span class="en-US">Total record count or <code>-1</code> if table not existed</span>
	 * <span class="zh-CN">满足查询条件的记录数，如果表不存在则返回 <code>-1</code></span>
	 * @throws QueryException <span class="en-US">If an error occurs when query record from database</span>
	 *                        <span class="zh-CN">如果从数据库中查询记录出现异常</span>
	 */
	long queryTotal(@Nonnull final QueryInfo queryInfo) throws QueryException;

	/**
	 * <h3 class="en-US">Query record by given query information</h3>
	 * <h3 class="zh-CN">查询满足给定查询条件的记录</h3>
	 *
	 * @param queryInfo   <span class="en-US">Query information instance object</span>
	 *                    <span class="zh-CN">查询信息实例对象</span>
	 * @return <span class="en-US">Query results wrapped by QueryResult class</span>
	 * <span class="zh-CN">使用 QueryResult 类包装的查询结果集</span>
	 * @throws QueryException <span class="en-US">If an error occurs when query record from database</span>
	 *                        <span class="zh-CN">如果从数据库中查询记录出现异常</span>
	 */
	QueryResult queryList(final QueryInfo queryInfo) throws QueryException;
}
