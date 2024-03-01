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

package org.nervousync.database.query.condition;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlTransient;
import org.nervousync.commons.Globals;
import org.nervousync.database.entity.EntityManager;
import org.nervousync.database.enumerations.query.ConditionCode;
import org.nervousync.database.query.QueryInfo;
import org.nervousync.database.query.condition.impl.ColumnCondition;
import org.nervousync.database.query.condition.impl.GroupCondition;
import org.nervousync.database.query.core.SortedItem;
import org.nervousync.database.query.param.AbstractParameter;
import org.nervousync.enumerations.core.ConnectionCode;
import org.nervousync.exceptions.builder.BuilderException;

import java.util.Arrays;

/**
 * <h2 class="en-US">Abstract class for query condition information define</h2>
 * <h2 class="zh-CN">查询匹配条件定义抽象类</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Oct 9, 2020 19:10:21 $
 */
@XmlTransient
public abstract class Condition extends SortedItem {

	/**
	 * <span class="en-US">Serial version UID</span>
	 * <span class="zh-CN">序列化UID</span>
	 */
	private static final long serialVersionUID = -1483012369723138515L;

	/**
	 * <span class="en-US">Query condition code</span>
	 * <span class="zh-CN">查询条件运算代码</span>
	 */
	@XmlElement(name = "condition_code")
	private ConditionCode conditionCode;
	/**
	 * <span class="en-US">Query connection code</span>
	 * <span class="zh-CN">查询条件连接代码</span>
	 */
	@XmlElement(name = "connection_code")
	private ConnectionCode connectionCode;

	/**
	 * <h3 class="en-US">Getter method for query condition code</h3>
	 * <h3 class="zh-CN">查询条件运算代码的Getter方法</h3>
	 *
	 * @return <span class="en-US">Query condition code</span>
	 * <span class="zh-CN">查询条件运算代码</span>
	 */
	public final ConditionCode getConditionCode() {
		return conditionCode;
	}

	/**
	 * <h3 class="en-US">Setter method for query condition code</h3>
	 * <h3 class="zh-CN">查询条件运算代码的Setter方法</h3>
	 *
	 * @param conditionCode <span class="en-US">Query condition code</span>
	 *                      <span class="zh-CN">查询条件运算代码</span>
	 */
	public final void setConditionCode(ConditionCode conditionCode) {
		this.conditionCode = conditionCode;
	}

	/**
	 * <h3 class="en-US">Getter method for query connection code</h3>
	 * <h3 class="zh-CN">查询条件连接代码的Getter方法</h3>
	 *
	 * @return <span class="en-US">Query connection code</span>
	 * <span class="zh-CN">查询条件连接代码</span>
	 */
	public final ConnectionCode getConnectionCode() {
		return connectionCode;
	}

	/**
	 * <h3 class="en-US">Setter method for query connection code</h3>
	 * <h3 class="zh-CN">查询条件连接代码的Setter方法</h3>
	 *
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 */
	public final void setConnectionCode(ConnectionCode connectionCode) {
		this.connectionCode = connectionCode;
	}

	/**
	 * <h3 class="en-US">Add a query condition greater than a certain value</h3>
	 * <h3 class="zh-CN">添加大于某值的查询条件</h3>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param entityClass    <span class="en-US">Entity class where the data column is located</span>
	 *                       <span class="zh-CN">数据列所在实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param matchValue     <span class="en-US">Match value</span>
	 *                       <span class="zh-CN">匹配值</span>
	 * @return <span class="en-US">Generated object instance</span>
	 * <span class="zh-CN">生成的对象实例</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public static ColumnCondition greater(final int sortCode, final ConnectionCode connectionCode,
	                                      final Class<?> entityClass, final String identifyKey,
	                                      final Object matchValue) throws BuilderException {
		return column(sortCode, connectionCode, ConditionCode.GREATER, entityClass, identifyKey,
				AbstractParameter.constant(matchValue));
	}

	/**
	 * <h3 class="en-US">Add a query condition greater than a certain value</h3>
	 * <h3 class="zh-CN">添加大于某值的查询条件</h3>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param entityClass    <span class="en-US">Entity class where the data column is located</span>
	 *                       <span class="zh-CN">数据列所在实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param targetClass    <span class="en-US">Target data table entity class</span>
	 *                       <span class="zh-CN">目标数据表实体类</span>
	 * @param columnKey      <span class="en-US">Target data column identification name</span>
	 *                       <span class="zh-CN">目标数据列识别名称</span>
	 * @return <span class="en-US">Generated object instance</span>
	 * <span class="zh-CN">生成的对象实例</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public static ColumnCondition greater(final int sortCode, final ConnectionCode connectionCode,
	                                      final Class<?> entityClass, final String identifyKey,
	                                      final Class<?> targetClass, final String columnKey) throws BuilderException {
		return column(sortCode, connectionCode, ConditionCode.GREATER, entityClass, identifyKey,
				AbstractParameter.column(targetClass, columnKey, Globals.DEFAULT_VALUE_STRING));
	}

	/**
	 * <h3 class="en-US">Add a query condition greater than a certain value</h3>
	 * <h3 class="zh-CN">添加大于某值的查询条件</h3>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param entityClass    <span class="en-US">Entity class where the data column is located</span>
	 *                       <span class="zh-CN">数据列所在实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param sqlFunction    <span class="en-US">Function name</span>
	 *                       <span class="zh-CN">函数名称</span>
	 * @param functionParams <span class="en-US">Function parameter values</span>
	 *                       <span class="zh-CN">函数参数值</span>
	 * @return <span class="en-US">Generated object instance</span>
	 * <span class="zh-CN">生成的对象实例</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public static ColumnCondition greater(final int sortCode, final ConnectionCode connectionCode,
	                                      final Class<?> entityClass, final String identifyKey,
	                                      final String sqlFunction, final AbstractParameter<?>... functionParams)
			throws BuilderException {
		return column(sortCode, connectionCode, ConditionCode.GREATER, entityClass, identifyKey,
				AbstractParameter.function(sqlFunction, functionParams));
	}

	/**
	 * <h3 class="en-US">Add a query condition greater than a certain value</h3>
	 * <h3 class="zh-CN">添加大于某值的查询条件</h3>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param entityClass    <span class="en-US">Entity class where the data column is located</span>
	 *                       <span class="zh-CN">数据列所在实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param subQuery       <span class="en-US">Sub-query instance object</span>
	 *                       <span class="zh-CN">子查询实例对象</span>
	 * @return <span class="en-US">Generated object instance</span>
	 * <span class="zh-CN">生成的对象实例</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public static ColumnCondition greater(final int sortCode, final ConnectionCode connectionCode,
	                                      final Class<?> entityClass, final String identifyKey,
	                                      final QueryInfo subQuery) throws BuilderException {
		return column(sortCode, connectionCode, ConditionCode.GREATER, entityClass, identifyKey,
				AbstractParameter.subQuery(subQuery));
	}

	/**
	 * <h3 class="en-US">Add a query condition greater than or equal to a certain value</h3>
	 * <h3 class="zh-CN">添加大于等于某值的查询条件</h3>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param entityClass    <span class="en-US">Entity class where the data column is located</span>
	 *                       <span class="zh-CN">数据列所在实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param targetClass    <span class="en-US">Target data table entity class</span>
	 *                       <span class="zh-CN">目标数据表实体类</span>
	 * @param columnKey      <span class="en-US">Target data column identification name</span>
	 *                       <span class="zh-CN">目标数据列识别名称</span>
	 * @return <span class="en-US">Generated object instance</span>
	 * <span class="zh-CN">生成的对象实例</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public static ColumnCondition greaterEqual(final int sortCode, final ConnectionCode connectionCode,
	                                           final Class<?> entityClass, final String identifyKey,
	                                           final Class<?> targetClass, final String columnKey)
			throws BuilderException {
		return column(sortCode, connectionCode, ConditionCode.GREATER_EQUAL, entityClass, identifyKey,
				AbstractParameter.column(targetClass, columnKey, Globals.DEFAULT_VALUE_STRING));
	}

	/**
	 * <h3 class="en-US">Add a query condition greater than or equal to a certain value</h3>
	 * <h3 class="zh-CN">添加大于等于某值的查询条件</h3>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param entityClass    <span class="en-US">Entity class where the data column is located</span>
	 *                       <span class="zh-CN">数据列所在实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param sqlFunction    <span class="en-US">Function name</span>
	 *                       <span class="zh-CN">函数名称</span>
	 * @param functionParams <span class="en-US">Function parameter values</span>
	 *                       <span class="zh-CN">函数参数值</span>
	 * @return <span class="en-US">Generated object instance</span>
	 * <span class="zh-CN">生成的对象实例</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public static ColumnCondition greaterEqual(final int sortCode, final ConnectionCode connectionCode,
	                                           final Class<?> entityClass, final String identifyKey,
	                                           final String sqlFunction, final AbstractParameter<?>... functionParams)
			throws BuilderException {
		return column(sortCode, connectionCode, ConditionCode.GREATER_EQUAL, entityClass, identifyKey,
				AbstractParameter.function(sqlFunction, functionParams));
	}

	/**
	 * <h3 class="en-US">Add a query condition greater than or equal to a certain value</h3>
	 * <h3 class="zh-CN">添加大于等于某值的查询条件</h3>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param entityClass    <span class="en-US">Entity class where the data column is located</span>
	 *                       <span class="zh-CN">数据列所在实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param subQuery       <span class="en-US">Sub-query instance object</span>
	 *                       <span class="zh-CN">子查询实例对象</span>
	 * @return <span class="en-US">Generated object instance</span>
	 * <span class="zh-CN">生成的对象实例</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public static ColumnCondition greaterEqual(final int sortCode, final ConnectionCode connectionCode,
	                                           final Class<?> entityClass, final String identifyKey,
	                                           final QueryInfo subQuery) throws BuilderException {
		return column(sortCode, connectionCode, ConditionCode.GREATER_EQUAL, entityClass, identifyKey,
				AbstractParameter.subQuery(subQuery));
	}

	/**
	 * <h3 class="en-US">Add a query condition greater than or equal to a certain value</h3>
	 * <h3 class="zh-CN">添加大于等于某值的查询条件</h3>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param entityClass    <span class="en-US">Entity class where the data column is located</span>
	 *                       <span class="zh-CN">数据列所在实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param matchValue     <span class="en-US">Match value</span>
	 *                       <span class="zh-CN">匹配值</span>
	 * @return <span class="en-US">Generated object instance</span>
	 * <span class="zh-CN">生成的对象实例</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public static ColumnCondition greaterEqual(final int sortCode, final ConnectionCode connectionCode,
	                                           final Class<?> entityClass, final String identifyKey,
	                                           final Object matchValue) throws BuilderException {
		return column(sortCode, connectionCode, ConditionCode.GREATER_EQUAL, entityClass, identifyKey,
				AbstractParameter.constant(matchValue));
	}

	/**
	 * <h3 class="en-US">Add a query condition less than a certain value</h3>
	 * <h3 class="zh-CN">添加大于某值的查询条件</h3>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param entityClass    <span class="en-US">Entity class where the data column is located</span>
	 *                       <span class="zh-CN">数据列所在实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param targetClass    <span class="en-US">Target data table entity class</span>
	 *                       <span class="zh-CN">目标数据表实体类</span>
	 * @param columnKey      <span class="en-US">Target data column identification name</span>
	 *                       <span class="zh-CN">目标数据列识别名称</span>
	 * @return <span class="en-US">Generated object instance</span>
	 * <span class="zh-CN">生成的对象实例</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public static ColumnCondition less(final int sortCode, final ConnectionCode connectionCode,
	                                   final Class<?> entityClass, final String identifyKey,
	                                   final Class<?> targetClass, final String columnKey) throws BuilderException {
		return column(sortCode, connectionCode, ConditionCode.LESS, entityClass, identifyKey,
				AbstractParameter.column(targetClass, columnKey, Globals.DEFAULT_VALUE_STRING));
	}

	/**
	 * <h3 class="en-US">Add a query condition less than a certain value</h3>
	 * <h3 class="zh-CN">添加大于某值的查询条件</h3>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param entityClass    <span class="en-US">Entity class where the data column is located</span>
	 *                       <span class="zh-CN">数据列所在实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param sqlFunction    <span class="en-US">Function name</span>
	 *                       <span class="zh-CN">函数名称</span>
	 * @param functionParams <span class="en-US">Function parameter values</span>
	 *                       <span class="zh-CN">函数参数值</span>
	 * @return <span class="en-US">Generated object instance</span>
	 * <span class="zh-CN">生成的对象实例</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public static ColumnCondition less(final int sortCode, final ConnectionCode connectionCode,
	                                   final Class<?> entityClass, final String identifyKey,
	                                   final String sqlFunction, final AbstractParameter<?>... functionParams)
			throws BuilderException {
		return column(sortCode, connectionCode, ConditionCode.LESS, entityClass, identifyKey,
				AbstractParameter.function(sqlFunction, functionParams));
	}

	/**
	 * <h3 class="en-US">Add a query condition less than a certain value</h3>
	 * <h3 class="zh-CN">添加大于某值的查询条件</h3>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param entityClass    <span class="en-US">Entity class where the data column is located</span>
	 *                       <span class="zh-CN">数据列所在实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param subQuery       <span class="en-US">Sub-query instance object</span>
	 *                       <span class="zh-CN">子查询实例对象</span>
	 * @return <span class="en-US">Generated object instance</span>
	 * <span class="zh-CN">生成的对象实例</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public static ColumnCondition less(final int sortCode, final ConnectionCode connectionCode,
	                                   final Class<?> entityClass, final String identifyKey,
	                                   final QueryInfo subQuery) throws BuilderException {
		return column(sortCode, connectionCode, ConditionCode.LESS, entityClass, identifyKey,
				AbstractParameter.subQuery(subQuery));
	}

	/**
	 * <h3 class="en-US">Add a query condition less than a certain value</h3>
	 * <h3 class="zh-CN">添加大于某值的查询条件</h3>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param entityClass    <span class="en-US">Entity class where the data column is located</span>
	 *                       <span class="zh-CN">数据列所在实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param matchValue     <span class="en-US">Match value</span>
	 *                       <span class="zh-CN">匹配值</span>
	 * @return <span class="en-US">Generated object instance</span>
	 * <span class="zh-CN">生成的对象实例</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public static ColumnCondition less(final int sortCode, final ConnectionCode connectionCode,
	                                   final Class<?> entityClass, final String identifyKey,
	                                   final Object matchValue) throws BuilderException {
		return column(sortCode, connectionCode, ConditionCode.LESS, entityClass, identifyKey,
				AbstractParameter.constant(matchValue));
	}

	/**
	 * <h3 class="en-US">Add a query condition less than or equal to a certain value</h3>
	 * <h3 class="zh-CN">添加大于等于某值的查询条件</h3>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param entityClass    <span class="en-US">Entity class where the data column is located</span>
	 *                       <span class="zh-CN">数据列所在实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param targetClass    <span class="en-US">Target data table entity class</span>
	 *                       <span class="zh-CN">目标数据表实体类</span>
	 * @param columnKey      <span class="en-US">Target data column identification name</span>
	 *                       <span class="zh-CN">目标数据列识别名称</span>
	 * @return <span class="en-US">Generated object instance</span>
	 * <span class="zh-CN">生成的对象实例</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public static ColumnCondition lessEqual(final int sortCode, final ConnectionCode connectionCode,
	                                        final Class<?> entityClass, final String identifyKey,
	                                        final Class<?> targetClass, final String columnKey)
			throws BuilderException {
		return column(sortCode, connectionCode, ConditionCode.LESS_EQUAL, entityClass, identifyKey,
				AbstractParameter.column(targetClass, columnKey, Globals.DEFAULT_VALUE_STRING));
	}

	/**
	 * <h3 class="en-US">Add a query condition less than or equal to a certain value</h3>
	 * <h3 class="zh-CN">添加大于等于某值的查询条件</h3>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param entityClass    <span class="en-US">Entity class where the data column is located</span>
	 *                       <span class="zh-CN">数据列所在实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param sqlFunction    <span class="en-US">Function name</span>
	 *                       <span class="zh-CN">函数名称</span>
	 * @param functionParams <span class="en-US">Function parameter values</span>
	 *                       <span class="zh-CN">函数参数值</span>
	 * @return <span class="en-US">Generated object instance</span>
	 * <span class="zh-CN">生成的对象实例</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public static ColumnCondition lessEqual(final int sortCode, final ConnectionCode connectionCode,
	                                        final Class<?> entityClass, final String identifyKey,
	                                        final String sqlFunction, final AbstractParameter<?>... functionParams)
			throws BuilderException {
		return column(sortCode, connectionCode, ConditionCode.LESS_EQUAL, entityClass, identifyKey,
				AbstractParameter.function(sqlFunction, functionParams));
	}

	/**
	 * <h3 class="en-US">Add a query condition less than or equal to a certain value</h3>
	 * <h3 class="zh-CN">添加大于等于某值的查询条件</h3>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param entityClass    <span class="en-US">Entity class where the data column is located</span>
	 *                       <span class="zh-CN">数据列所在实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param subQuery       <span class="en-US">Sub-query instance object</span>
	 *                       <span class="zh-CN">子查询实例对象</span>
	 * @return <span class="en-US">Generated object instance</span>
	 * <span class="zh-CN">生成的对象实例</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public static ColumnCondition lessEqual(final int sortCode, final ConnectionCode connectionCode,
	                                        final Class<?> entityClass, final String identifyKey,
	                                        final QueryInfo subQuery) throws BuilderException {
		return column(sortCode, connectionCode, ConditionCode.LESS_EQUAL, entityClass, identifyKey,
				AbstractParameter.subQuery(subQuery));
	}

	/**
	 * <h3 class="en-US">Add a query condition less than or equal to a certain value</h3>
	 * <h3 class="zh-CN">添加大于等于某值的查询条件</h3>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param entityClass    <span class="en-US">Entity class where the data column is located</span>
	 *                       <span class="zh-CN">数据列所在实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param matchValue     <span class="en-US">Match value</span>
	 *                       <span class="zh-CN">匹配值</span>
	 * @return <span class="en-US">Generated object instance</span>
	 * <span class="zh-CN">生成的对象实例</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public static ColumnCondition lessEqual(final int sortCode, final ConnectionCode connectionCode,
	                                        final Class<?> entityClass, final String identifyKey,
	                                        final Object matchValue) throws BuilderException {
		return column(sortCode, connectionCode, ConditionCode.LESS_EQUAL, entityClass, identifyKey,
				AbstractParameter.constant(matchValue));
	}

	/**
	 * <h3 class="en-US">Add a query condition equal to a certain value</h3>
	 * <h3 class="zh-CN">添加等于某值的查询条件</h3>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param entityClass    <span class="en-US">Entity class where the data column is located</span>
	 *                       <span class="zh-CN">数据列所在实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param targetClass    <span class="en-US">Target data table entity class</span>
	 *                       <span class="zh-CN">目标数据表实体类</span>
	 * @param columnKey      <span class="en-US">Target data column identification name</span>
	 *                       <span class="zh-CN">目标数据列识别名称</span>
	 * @return <span class="en-US">Generated object instance</span>
	 * <span class="zh-CN">生成的对象实例</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public static ColumnCondition equalTo(final int sortCode, final ConnectionCode connectionCode,
	                                      final Class<?> entityClass, final String identifyKey,
	                                      final Class<?> targetClass, final String columnKey) throws BuilderException {
		return column(sortCode, connectionCode, ConditionCode.EQUAL, entityClass, identifyKey,
				AbstractParameter.column(targetClass, columnKey, Globals.DEFAULT_VALUE_STRING));
	}

	/**
	 * <h3 class="en-US">Add a query condition equal to a certain value</h3>
	 * <h3 class="zh-CN">添加等于某值的查询条件</h3>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param entityClass    <span class="en-US">Entity class where the data column is located</span>
	 *                       <span class="zh-CN">数据列所在实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param sqlFunction    <span class="en-US">Function name</span>
	 *                       <span class="zh-CN">函数名称</span>
	 * @param functionParams <span class="en-US">Function parameter values</span>
	 *                       <span class="zh-CN">函数参数值</span>
	 * @return <span class="en-US">Generated object instance</span>
	 * <span class="zh-CN">生成的对象实例</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public static ColumnCondition equalTo(final int sortCode, final ConnectionCode connectionCode,
	                                      final Class<?> entityClass, final String identifyKey,
	                                      final String sqlFunction, final AbstractParameter<?>... functionParams)
			throws BuilderException {
		return column(sortCode, connectionCode, ConditionCode.EQUAL, entityClass, identifyKey,
				AbstractParameter.function(sqlFunction, functionParams));
	}

	/**
	 * <h3 class="en-US">Add a query condition equal to a certain value</h3>
	 * <h3 class="zh-CN">添加等于某值的查询条件</h3>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param entityClass    <span class="en-US">Entity class where the data column is located</span>
	 *                       <span class="zh-CN">数据列所在实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param subQuery       <span class="en-US">Sub-query instance object</span>
	 *                       <span class="zh-CN">子查询实例对象</span>
	 * @return <span class="en-US">Generated object instance</span>
	 * <span class="zh-CN">生成的对象实例</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public static ColumnCondition equalTo(final int sortCode, final ConnectionCode connectionCode,
	                                      final Class<?> entityClass, final String identifyKey,
	                                      final QueryInfo subQuery) throws BuilderException {
		return column(sortCode, connectionCode, ConditionCode.EQUAL, entityClass, identifyKey,
				AbstractParameter.subQuery(subQuery));
	}

	/**
	 * <h3 class="en-US">Add a query condition equal to a certain value</h3>
	 * <h3 class="zh-CN">添加等于某值的查询条件</h3>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param entityClass    <span class="en-US">Entity class where the data column is located</span>
	 *                       <span class="zh-CN">数据列所在实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param matchValue     <span class="en-US">Match value</span>
	 *                       <span class="zh-CN">匹配值</span>
	 * @return <span class="en-US">Generated object instance</span>
	 * <span class="zh-CN">生成的对象实例</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public static ColumnCondition equalTo(final int sortCode, final ConnectionCode connectionCode,
	                                      final Class<?> entityClass, final String identifyKey,
	                                      final Object matchValue) throws BuilderException {
		return column(sortCode, connectionCode, ConditionCode.EQUAL, entityClass, identifyKey,
				AbstractParameter.constant(matchValue));
	}

	/**
	 * <h3 class="en-US">Add a query condition not equal to a certain value</h3>
	 * <h3 class="zh-CN">添加不等于某值的查询条件</h3>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param entityClass    <span class="en-US">Entity class where the data column is located</span>
	 *                       <span class="zh-CN">数据列所在实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param targetClass    <span class="en-US">Target data table entity class</span>
	 *                       <span class="zh-CN">目标数据表实体类</span>
	 * @param columnKey      <span class="en-US">Target data column identification name</span>
	 *                       <span class="zh-CN">目标数据列识别名称</span>
	 * @return <span class="en-US">Generated object instance</span>
	 * <span class="zh-CN">生成的对象实例</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public static ColumnCondition notEqual(final int sortCode, final ConnectionCode connectionCode,
	                                       final Class<?> entityClass, final String identifyKey,
	                                       final Class<?> targetClass, final String columnKey) throws BuilderException {
		return column(sortCode, connectionCode, ConditionCode.NOT_EQUAL, entityClass, identifyKey,
				AbstractParameter.column(targetClass, columnKey, Globals.DEFAULT_VALUE_STRING));
	}

	/**
	 * <h3 class="en-US">Add a query condition not equal to a certain value</h3>
	 * <h3 class="zh-CN">添加不等于某值的查询条件</h3>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param entityClass    <span class="en-US">Entity class where the data column is located</span>
	 *                       <span class="zh-CN">数据列所在实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param sqlFunction    <span class="en-US">Function name</span>
	 *                       <span class="zh-CN">函数名称</span>
	 * @param functionParams <span class="en-US">Function parameter values</span>
	 *                       <span class="zh-CN">函数参数值</span>
	 * @return <span class="en-US">Generated object instance</span>
	 * <span class="zh-CN">生成的对象实例</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public static ColumnCondition notEqual(final int sortCode, final ConnectionCode connectionCode,
	                                       final Class<?> entityClass, final String identifyKey,
	                                       final String sqlFunction, final AbstractParameter<?>... functionParams)
			throws BuilderException {
		return column(sortCode, connectionCode, ConditionCode.NOT_EQUAL, entityClass, identifyKey,
				AbstractParameter.function(sqlFunction, functionParams));
	}

	/**
	 * <h3 class="en-US">Add a query condition not equal to a certain value</h3>
	 * <h3 class="zh-CN">添加不等于某值的查询条件</h3>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param entityClass    <span class="en-US">Entity class where the data column is located</span>
	 *                       <span class="zh-CN">数据列所在实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param subQuery       <span class="en-US">Sub-query instance object</span>
	 *                       <span class="zh-CN">子查询实例对象</span>
	 * @return <span class="en-US">Generated object instance</span>
	 * <span class="zh-CN">生成的对象实例</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public static ColumnCondition notEqual(final int sortCode, final ConnectionCode connectionCode,
	                                       final Class<?> entityClass, final String identifyKey,
	                                       final QueryInfo subQuery) throws BuilderException {
		return column(sortCode, connectionCode, ConditionCode.NOT_EQUAL, entityClass, identifyKey,
				AbstractParameter.subQuery(subQuery));
	}

	/**
	 * <h3 class="en-US">Add a query condition not equal to a certain value</h3>
	 * <h3 class="zh-CN">添加不等于某值的查询条件</h3>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param entityClass    <span class="en-US">Entity class where the data column is located</span>
	 *                       <span class="zh-CN">数据列所在实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param matchValue     <span class="en-US">Match value</span>
	 *                       <span class="zh-CN">匹配值</span>
	 * @return <span class="en-US">Generated object instance</span>
	 * <span class="zh-CN">生成的对象实例</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public static ColumnCondition notEqual(final int sortCode, final ConnectionCode connectionCode,
	                                       final Class<?> entityClass, final String identifyKey,
	                                       final Object matchValue) throws BuilderException {
		return column(sortCode, connectionCode, ConditionCode.NOT_EQUAL, entityClass, identifyKey,
				AbstractParameter.constant(matchValue));
	}

	/**
	 * <h3 class="en-US">Add a query condition between certain two values</h3>
	 * <h3 class="zh-CN">添加介于某两个值之间的查询条件</h3>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param entityClass    <span class="en-US">Entity class where the data column is located</span>
	 *                       <span class="zh-CN">数据列所在实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param beginValue     <span class="en-US">Begin value</span>
	 *                       <span class="zh-CN">起始值</span>
	 * @param endValue       <span class="en-US">End value</span>
	 *                       <span class="zh-CN">终止值</span>
	 * @return <span class="en-US">Generated object instance</span>
	 * <span class="zh-CN">生成的对象实例</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public static ColumnCondition inRanges(final int sortCode, final ConnectionCode connectionCode,
	                                       final Class<?> entityClass, final String identifyKey,
	                                       final Object beginValue, final Object endValue) throws BuilderException {
		return column(sortCode, connectionCode, ConditionCode.BETWEEN_AND, entityClass, identifyKey,
				AbstractParameter.ranges(beginValue, endValue));
	}

	/**
	 * <h3 class="en-US">Add a query condition not between certain two values</h3>
	 * <h3 class="zh-CN">添加不介于某两个值之间的查询条件</h3>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param entityClass    <span class="en-US">Entity class where the data column is located</span>
	 *                       <span class="zh-CN">数据列所在实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param beginValue     <span class="en-US">Begin value</span>
	 *                       <span class="zh-CN">起始值</span>
	 * @param endValue       <span class="en-US">End value</span>
	 *                       <span class="zh-CN">终止值</span>
	 * @return <span class="en-US">Generated object instance</span>
	 * <span class="zh-CN">生成的对象实例</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public static ColumnCondition notInRanges(final int sortCode, final ConnectionCode connectionCode,
	                                          final Class<?> entityClass, final String identifyKey,
	                                          final Object beginValue, final Object endValue) throws BuilderException {
		return column(sortCode, connectionCode, ConditionCode.NOT_BETWEEN_AND, entityClass, identifyKey,
				AbstractParameter.ranges(beginValue, endValue));
	}

	/**
	 * <h3 class="en-US">Add query conditions for fuzzy matching values</h3>
	 * <h3 class="zh-CN">添加模糊匹配值的查询条件</h3>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param entityClass    <span class="en-US">Entity class where the data column is located</span>
	 *                       <span class="zh-CN">数据列所在实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param matchRule      <span class="en-US">match rule string</span>
	 *                       <span class="zh-CN">匹配规则字符串</span>
	 * @return <span class="en-US">Generated object instance</span>
	 * <span class="zh-CN">生成的对象实例</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public static ColumnCondition like(final int sortCode, final ConnectionCode connectionCode,
	                                   final Class<?> entityClass, final String identifyKey, final String matchRule)
			throws BuilderException {
		return column(sortCode, connectionCode, ConditionCode.LIKE, entityClass, identifyKey,
				AbstractParameter.constant(matchRule));
	}

	/**
	 * <h3 class="en-US">Add query conditions for not fuzzy matching values</h3>
	 * <h3 class="zh-CN">添加非模糊匹配值的查询条件</h3>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param entityClass    <span class="en-US">Entity class where the data column is located</span>
	 *                       <span class="zh-CN">数据列所在实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param matchRule      <span class="en-US">match rule string</span>
	 *                       <span class="zh-CN">匹配规则字符串</span>
	 * @return <span class="en-US">Generated object instance</span>
	 * <span class="zh-CN">生成的对象实例</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public static ColumnCondition notLike(final int sortCode, final ConnectionCode connectionCode,
	                                      final Class<?> entityClass, final String identifyKey, final String matchRule)
			throws BuilderException {
		return column(sortCode, connectionCode, ConditionCode.NOT_LIKE, entityClass, identifyKey,
				AbstractParameter.constant(matchRule));
	}

	/**
	 * <h3 class="en-US">Add query condition with null value</h3>
	 * <h3 class="zh-CN">添加空值的查询条件</h3>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param entityClass    <span class="en-US">Entity class where the data column is located</span>
	 *                       <span class="zh-CN">数据列所在实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @return <span class="en-US">Generated object instance</span>
	 * <span class="zh-CN">生成的对象实例</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public static ColumnCondition matchNull(final int sortCode, final ConnectionCode connectionCode,
	                                        final Class<?> entityClass, final String identifyKey)
			throws BuilderException {
		return column(sortCode, connectionCode, ConditionCode.IS_NULL, entityClass, identifyKey, null);
	}

	/**
	 * <h3 class="en-US">Add query condition with not null value</h3>
	 * <h3 class="zh-CN">添加非空值的查询条件</h3>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param entityClass    <span class="en-US">Entity class where the data column is located</span>
	 *                       <span class="zh-CN">数据列所在实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @return <span class="en-US">Generated object instance</span>
	 * <span class="zh-CN">生成的对象实例</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public static ColumnCondition notNull(final int sortCode, final ConnectionCode connectionCode,
	                                      final Class<?> entityClass, final String identifyKey) throws BuilderException {
		return column(sortCode, connectionCode, ConditionCode.NOT_NULL, entityClass, identifyKey, null);
	}

	/**
	 * <h3 class="en-US">Adds a query condition where the value is contained in the given data</h3>
	 * <h3 class="zh-CN">添加值包含在给定数据中的查询条件</h3>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param entityClass    <span class="en-US">Entity class where the data column is located</span>
	 *                       <span class="zh-CN">数据列所在实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param subQuery       <span class="en-US">Sub-query instance object</span>
	 *                       <span class="zh-CN">子查询实例对象</span>
	 * @return <span class="en-US">Generated object instance</span>
	 * <span class="zh-CN">生成的对象实例</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public static ColumnCondition in(final int sortCode, final ConnectionCode connectionCode,
	                                 final Class<?> entityClass, final String identifyKey,
	                                 final QueryInfo subQuery) throws BuilderException {
		return column(sortCode, connectionCode, ConditionCode.IN, entityClass, identifyKey,
				AbstractParameter.subQuery(subQuery));
	}

	/**
	 * <h3 class="en-US">Adds a query condition where the value is contained in the given data</h3>
	 * <h3 class="zh-CN">添加值包含在给定数据中的查询条件</h3>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param entityClass    <span class="en-US">Entity class where the data column is located</span>
	 *                       <span class="zh-CN">数据列所在实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param matchValues    <span class="en-US">Condition data array</span>
	 *                       <span class="zh-CN">匹配值数组</span>
	 * @return <span class="en-US">Generated object instance</span>
	 * <span class="zh-CN">生成的对象实例</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public static ColumnCondition in(final int sortCode, final ConnectionCode connectionCode,
	                                 final Class<?> entityClass, final String identifyKey, final Object... matchValues)
			throws BuilderException {
		return column(sortCode, connectionCode, ConditionCode.IN, entityClass, identifyKey,
				AbstractParameter.arrays(matchValues));
	}

	/**
	 * <h3 class="en-US">Adds a query condition where the value is not contained in the given data</h3>
	 * <h3 class="zh-CN">添加值非包含在给定数据中的查询条件</h3>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param entityClass    <span class="en-US">Entity class where the data column is located</span>
	 *                       <span class="zh-CN">数据列所在实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param subQuery       <span class="en-US">Sub-query instance object</span>
	 *                       <span class="zh-CN">子查询实例对象</span>
	 * @return <span class="en-US">Generated object instance</span>
	 * <span class="zh-CN">生成的对象实例</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public static ColumnCondition notIn(final int sortCode, final ConnectionCode connectionCode,
	                                    final Class<?> entityClass, final String identifyKey,
	                                    final QueryInfo subQuery) throws BuilderException {
		return column(sortCode, connectionCode, ConditionCode.NOT_IN, entityClass, identifyKey,
				AbstractParameter.subQuery(subQuery));
	}

	/**
	 * <h3 class="en-US">Adds a query condition where the value is not contained in the given data</h3>
	 * <h3 class="zh-CN">添加值非包含在给定数据中的查询条件</h3>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param entityClass    <span class="en-US">Entity class where the data column is located</span>
	 *                       <span class="zh-CN">数据列所在实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param matchValues    <span class="en-US">Condition data array</span>
	 *                       <span class="zh-CN">匹配值数组</span>
	 * @return <span class="en-US">Generated object instance</span>
	 * <span class="zh-CN">生成的对象实例</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public static ColumnCondition notIn(final int sortCode, final ConnectionCode connectionCode,
	                                    final Class<?> entityClass, final String identifyKey,
	                                    final Object... matchValues) throws BuilderException {
		return column(sortCode, connectionCode, ConditionCode.NOT_IN, entityClass, identifyKey,
				AbstractParameter.arrays(matchValues));
	}

	/**
	 * <h3 class="en-US">Static method is used to generate query matching condition group instance objects</h3>
	 * <h3 class="zh-CN">静态方法用于生成查询匹配条件组实例对象</h3>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param conditions     <span class="en-US">Query condition information array</span>
	 *                       <span class="zh-CN">查询匹配条件组数组</span>
	 * @return <span class="en-US">Generated object instance</span>
	 * <span class="zh-CN">生成的对象实例</span>
	 */
	public static GroupCondition group(final int sortCode, final ConnectionCode connectionCode,
	                                   final Condition... conditions) {
		if (conditions == null || conditions.length == 0) {
			return null;
		}
		GroupCondition groupCondition = new GroupCondition();

		groupCondition.setConnectionCode(connectionCode);
		groupCondition.setConditionCode(ConditionCode.GROUP);
		groupCondition.setSortCode((sortCode < Globals.INITIALIZE_INT_VALUE) ? Globals.DEFAULT_VALUE_INT : sortCode);
		groupCondition.setConditionList(Arrays.asList(conditions));

		return groupCondition;
	}

	/**
	 * <h3 class="en-US">Static method is used to generate query data column matching condition instance object</h3>
	 * <h3 class="zh-CN">静态方法用于生成查询数据列匹配条件实例对象</h3>
	 *
	 * @param sortCode              <span class="en-US">Sort code</span>
	 *                              <span class="zh-CN">排序代码</span>
	 * @param connectionCode        <span class="en-US">Query connection code</span>
	 *                              <span class="zh-CN">查询条件连接代码</span>
	 * @param conditionCode         <span class="en-US">Query condition code</span>
	 *                              <span class="zh-CN">查询条件运算代码</span>
	 * @param entityClass           <span class="en-US">Entity class where the data column is located</span>
	 *                              <span class="zh-CN">数据列所在实体类</span>
	 * @param identifyKey           <span class="en-US">Identify key</span>
	 *                              <span class="zh-CN">识别代码</span>
	 * @param conditionParameter    <span class="en-US">Match condition</span>
	 *                              <span class="zh-CN">匹配结果</span>
	 * @return <span class="en-US">Generated object instance</span>
	 * <span class="zh-CN">生成的对象实例</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public static ColumnCondition column(final int sortCode, final ConnectionCode connectionCode,
	                                     final ConditionCode conditionCode, final Class<?> entityClass,
	                                     final String identifyKey, final AbstractParameter<?> conditionParameter)
			throws BuilderException {
		if (ConditionCode.GROUP.equals(conditionCode)) {
			return null;
		}
		if (!EntityManager.tableExists(entityClass)) {
			throw new BuilderException(0x00DB00000001L);
		}
		if (!EntityManager.columnExists(entityClass, identifyKey)) {
			throw new BuilderException(0x00DB00000002L);
		}
		ColumnCondition columnCondition = new ColumnCondition();

		columnCondition.setEntityClass(entityClass);
		columnCondition.setConnectionCode(connectionCode);
		columnCondition.setConditionCode(conditionCode);
		columnCondition.setIdentifyKey(identifyKey);
		columnCondition.setSortCode((sortCode < Globals.INITIALIZE_INT_VALUE) ? Globals.DEFAULT_VALUE_INT : sortCode);
		columnCondition.setConditionParameter(conditionParameter);

		return columnCondition;
	}
}
