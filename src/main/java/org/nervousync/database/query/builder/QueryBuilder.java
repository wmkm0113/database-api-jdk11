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

package org.nervousync.database.query.builder;

import jakarta.annotation.Nonnull;
import org.nervousync.builder.Builder;
import org.nervousync.commons.Globals;
import org.nervousync.database.annotations.query.ResultSet;
import org.nervousync.database.annotations.query.join.JoinEntities;
import org.nervousync.database.beans.configs.column.ColumnConfig;
import org.nervousync.database.beans.configs.table.TableConfig;
import org.nervousync.database.commons.DatabaseUtils;
import org.nervousync.database.entity.EntityManager;
import org.nervousync.database.enumerations.join.JoinType;
import org.nervousync.database.enumerations.lock.LockOption;
import org.nervousync.database.enumerations.query.OrderType;
import org.nervousync.database.query.QueryInfo;
import org.nervousync.database.query.condition.Condition;
import org.nervousync.database.query.core.AbstractItem;
import org.nervousync.database.query.core.SortedItem;
import org.nervousync.database.query.filter.GroupBy;
import org.nervousync.database.query.filter.OrderBy;
import org.nervousync.database.query.join.JoinInfo;
import org.nervousync.database.query.join.QueryJoin;
import org.nervousync.database.query.param.AbstractParameter;
import org.nervousync.enumerations.core.ConnectionCode;
import org.nervousync.exceptions.builder.BuilderException;
import org.nervousync.utils.ObjectUtils;
import org.nervousync.utils.ReflectionUtils;
import org.nervousync.utils.StringUtils;

import java.lang.reflect.Field;
import java.util.*;

/**
 * <h2 class="en-US">Query information builder</h2>
 * <h2 class="zh-CN">查询信息构建器</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Jul 30, 2023 15:37:53 $
 */
public final class QueryBuilder implements Builder<QueryInfo> {

	/**
	 * <span class="en-US">Query name</span>
	 * <span class="zh-CN">查询名称</span>
	 */
	private String identifyName = Globals.DEFAULT_VALUE_STRING;
	/**
	 * <span class="en-US">Query driven table entity class</span>
	 * <span class="zh-CN">查询驱动表实体类</span>
	 */
	private final Class<?> mainEntity;
	/**
	 * <span class="en-US">Related query information list</span>
	 * <span class="zh-CN">关联查询信息列表</span>
	 */
	private final List<QueryJoin> queryJoins;
	/**
	 * <span class="en-US">Query item instance list</span>
	 * <span class="zh-CN">查询项目实例对象列表</span>
	 */
	private final List<AbstractItem> itemList;
	/**
	 * <span class="en-US">Query condition instance list</span>
	 * <span class="zh-CN">查询条件实例对象列表</span>
	 */
	private final List<Condition> conditionList;
	/**
	 * <span class="en-US">Query order by columns list</span>
	 * <span class="zh-CN">查询排序数据列列表</span>
	 */
	private final List<OrderBy> orderByList;
	/**
	 * <span class="en-US">Query group by columns list</span>
	 * <span class="zh-CN">查询分组数据列列表</span>
	 */
	private final List<GroupBy> groupByList;
	/**
	 * <span class="en-US">Current page number</span>
	 * <span class="zh-CN">当前页数</span>
	 */
	private int pageNo = Globals.DEFAULT_VALUE_INT;
	/**
	 * <span class="en-US">Page limit records count</span>
	 * <span class="zh-CN">每页的记录数</span>
	 */
	private int pageLimit = Globals.DEFAULT_VALUE_INT;
	/**
	 * <span class="en-US">Query result can cacheable</span>
	 * <span class="zh-CN">查询结果可以缓存</span>
	 */
	private boolean cacheables = Boolean.FALSE;
	/**
	 * <span class="en-US">Query result for update</span>
	 * <span class="zh-CN">查询结果用于批量更新记录</span>
	 */
	private boolean forUpdate = Boolean.FALSE;
	/**
	 * <span class="en-US">Query record lock option</span>
	 * <span class="zh-CN">查询记录锁定选项</span>
	 */
	private LockOption lockOption = LockOption.NONE;

	/**
	 * <h4 class="en-US">Private constructor method for querying information builder</h4>
	 * <h4 class="zh-CN">查询信息构建器的私有构造方法</h4>
	 *
	 * @param mainEntity <span class="en-US">Query driven table entity class</span>
	 *                   <span class="zh-CN">查询驱动表实体类</span>
	 */
	private QueryBuilder(final Class<?> mainEntity) {
		this.mainEntity = mainEntity;
		this.queryJoins = new ArrayList<>();
		this.itemList = new ArrayList<>();
		this.conditionList = new ArrayList<>();
		this.orderByList = new ArrayList<>();
		this.groupByList = new ArrayList<>();
	}

	/**
	 * <h4 class="en-US">Static method used to initialize query builder</h4>
	 * <h4 class="zh-CN">静态方法用于初始化查询信息构建器</h4>
	 *
	 * @param mainEntity <span class="en-US">Query driven table entity class</span>
	 *                   <span class="zh-CN">查询驱动表实体类</span>
	 * @return <span class="en-US">Generated builder instance</span>
	 * <span class="zh-CN">生成的构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册</span>
	 */
	public static QueryBuilder newBuilder(@Nonnull final Class<?> mainEntity) throws BuilderException {
		if (!EntityManager.tableExists(mainEntity)) {
			throw new BuilderException(0x00DB00000001L);
		}
		return new QueryBuilder(mainEntity);
	}

	/**
	 * <h4 class="en-US">Generate query information instance by given information</h4>
	 * <h4 class="zh-CN">根据给定的信息生成查询信息实例对象</h4>
	 *
	 * @param targetClass <span class="en-US">Query result JavaBean class</span>
	 *                    <span class="zh-CN">查询结果实体类</span>
	 * @param conditions  <span class="en-US">Query condition array</span>
	 *                    <span class="zh-CN">查询条件数组</span>
	 * @return <span class="en-US">Generated query information instance</span>
	 * <span class="zh-CN">生成的查询信息实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public static QueryInfo newQuery(@Nonnull final Class<?> targetClass, final Condition... conditions)
			throws BuilderException {
		return newQuery(targetClass, Boolean.FALSE, conditions);
	}

	/**
	 * <h4 class="en-US">Generate query information instance by given information</h4>
	 * <h4 class="zh-CN">根据给定的信息生成查询信息实例对象</h4>
	 *
	 * @param targetClass <span class="en-US">Query result JavaBean class</span>
	 *                    <span class="zh-CN">查询结果实体类</span>
	 * @param forUpdate   <span class="en-US">For update status</span>
	 *                    <span class="zh-CN">为更新记录状态</span>
	 * @param conditions  <span class="en-US">Query condition array</span>
	 *                    <span class="zh-CN">查询条件数组</span>
	 * @return <span class="en-US">Generated query information instance</span>
	 * <span class="zh-CN">生成的查询信息实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public static QueryInfo newQuery(@Nonnull final Class<?> targetClass, final boolean forUpdate,
	                                 final Condition... conditions)
			throws BuilderException {
		return newQuery(targetClass, forUpdate, Arrays.asList(conditions));
	}

	/**
	 * <h4 class="en-US">Generate query information instance by given information</h4>
	 * <h4 class="zh-CN">根据给定的信息生成查询信息实例对象</h4>
	 *
	 * @param targetClass <span class="en-US">Query result JavaBean class</span>
	 *                    <span class="zh-CN">查询结果实体类</span>
	 * @param pageNo      <span class="en-US">Current page number</span>
	 *                    <span class="zh-CN">当前页数</span>
	 * @param pageLimit   <span class="en-US">Page limit records count</span>
	 *                    <span class="zh-CN">每页的记录数</span>
	 * @param conditions  <span class="en-US">Query condition array</span>
	 *                    <span class="zh-CN">查询条件数组</span>
	 * @return <span class="en-US">Generated query information instance</span>
	 * <span class="zh-CN">生成的查询信息实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public static QueryInfo newQuery(@Nonnull final Class<?> targetClass, final int pageNo, final int pageLimit,
	                                 final Condition... conditions) throws BuilderException {
		return newQuery(targetClass, Boolean.FALSE, pageNo, pageLimit, conditions);
	}

	/**
	 * <h4 class="en-US">Generate query information instance by given information</h4>
	 * <h4 class="zh-CN">根据给定的信息生成查询信息实例对象</h4>
	 *
	 * @param targetClass <span class="en-US">Query result JavaBean class</span>
	 *                    <span class="zh-CN">查询结果实体类</span>
	 * @param forUpdate   <span class="en-US">For update status</span>
	 *                    <span class="zh-CN">为更新记录状态</span>
	 * @param pageNo      <span class="en-US">Current page number</span>
	 *                    <span class="zh-CN">当前页数</span>
	 * @param pageLimit   <span class="en-US">Page limit records count</span>
	 *                    <span class="zh-CN">每页的记录数</span>
	 * @param conditions  <span class="en-US">Query condition array</span>
	 *                    <span class="zh-CN">查询条件数组</span>
	 * @return <span class="en-US">Generated query information instance</span>
	 * <span class="zh-CN">生成的查询信息实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public static QueryInfo newQuery(@Nonnull final Class<?> targetClass, final boolean forUpdate,
	                                 final int pageNo, final int pageLimit, final Condition... conditions)
			throws BuilderException {
		return newQuery(targetClass, forUpdate, pageNo, pageLimit, Arrays.asList(conditions));
	}

	/**
	 * <h4 class="en-US">Generate query information instance by given information</h4>
	 * <h4 class="zh-CN">根据给定的信息生成查询信息实例对象</h4>
	 *
	 * @param targetClass   <span class="en-US">Query result JavaBean class</span>
	 *                      <span class="zh-CN">查询结果实体类</span>
	 * @param conditionList <span class="en-US">Query condition list</span>
	 *                      <span class="zh-CN">查询条件列表</span>
	 * @return <span class="en-US">Generated query information instance</span>
	 * <span class="zh-CN">生成的查询信息实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public static QueryInfo newQuery(@Nonnull final Class<?> targetClass, final List<Condition> conditionList)
			throws BuilderException {
		return newQuery(targetClass, Boolean.FALSE, conditionList);
	}

	/**
	 * <h4 class="en-US">Generate query information instance by given information</h4>
	 * <h4 class="zh-CN">根据给定的信息生成查询信息实例对象</h4>
	 *
	 * @param targetClass   <span class="en-US">Query result JavaBean class</span>
	 *                      <span class="zh-CN">查询结果实体类</span>
	 * @param forUpdate     <span class="en-US">For update status</span>
	 *                      <span class="zh-CN">为更新记录状态</span>
	 * @param conditionList <span class="en-US">Query condition list</span>
	 *                      <span class="zh-CN">查询条件列表</span>
	 * @return <span class="en-US">Generated query information instance</span>
	 * <span class="zh-CN">生成的查询信息实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public static QueryInfo newQuery(@Nonnull final Class<?> targetClass, final boolean forUpdate,
	                                 final List<Condition> conditionList) throws BuilderException {
		return newQuery(targetClass, forUpdate, Globals.DEFAULT_VALUE_INT, Globals.DEFAULT_VALUE_INT, conditionList);
	}

	/**
	 * <h4 class="en-US">Generate query information instance by given information</h4>
	 * <h4 class="zh-CN">根据给定的信息生成查询信息实例对象</h4>
	 *
	 * @param targetClass   <span class="en-US">Query result JavaBean class</span>
	 *                      <span class="zh-CN">查询结果实体类</span>
	 * @param pageNo        <span class="en-US">Current page number</span>
	 *                      <span class="zh-CN">当前页数</span>
	 * @param pageLimit     <span class="en-US">Page limit records count</span>
	 *                      <span class="zh-CN">每页的记录数</span>
	 * @param conditionList <span class="en-US">Query condition list</span>
	 *                      <span class="zh-CN">查询条件列表</span>
	 * @return <span class="en-US">Generated query information instance</span>
	 * <span class="zh-CN">生成的查询信息实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public static QueryInfo newQuery(@Nonnull final Class<?> targetClass, final int pageNo, final int pageLimit,
	                                 final List<Condition> conditionList) throws BuilderException {
		return newQuery(targetClass, Boolean.FALSE, pageNo, pageLimit, conditionList);
	}

	/**
	 * <h4 class="en-US">Generate query information instance by given information</h4>
	 * <h4 class="zh-CN">根据给定的信息生成查询信息实例对象</h4>
	 *
	 * @param targetClass   <span class="en-US">Query result JavaBean class</span>
	 *                      <span class="zh-CN">查询结果实体类</span>
	 * @param forUpdate     <span class="en-US">For update status</span>
	 *                      <span class="zh-CN">为更新记录状态</span>
	 * @param pageNo        <span class="en-US">Current page number</span>
	 *                      <span class="zh-CN">当前页数</span>
	 * @param pageLimit     <span class="en-US">Page limit records count</span>
	 *                      <span class="zh-CN">每页的记录数</span>
	 * @param conditionList <span class="en-US">Query condition list</span>
	 *                      <span class="zh-CN">查询条件列表</span>
	 * @return <span class="en-US">Generated query information instance</span>
	 * <span class="zh-CN">生成的查询信息实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public static QueryInfo newQuery(@Nonnull final Class<?> targetClass, final boolean forUpdate,
	                                 final int pageNo, final int pageLimit, final List<Condition> conditionList)
			throws BuilderException {
		final QueryBuilder queryBuilder;
		if (targetClass.isAnnotationPresent(ResultSet.class)) {
			ResultSet resultSet = targetClass.getAnnotation(ResultSet.class);
			queryBuilder = newBuilder(resultSet.mainEntity());

			for (JoinEntities joinEntities : resultSet.joinConfigs()) {
				List<JoinInfo> joinInfos = new ArrayList<>();
				Arrays.stream(joinEntities.keys()).map(JoinInfo::newInstance).forEach(joinInfos::add);
				queryBuilder.joinTable(joinEntities.mainEntity(), joinEntities.type(),
						joinEntities.referenceEntity(), joinInfos);
			}

			for (Field field :
					ReflectionUtils.getAllDeclaredFields(targetClass, Boolean.TRUE, DatabaseUtils::resultDataMember)) {
				queryBuilder.addItem(AbstractItem.column(field));
			}
			queryBuilder.identifyName(resultSet.name());
			if (forUpdate) {
				queryBuilder.useCache(Boolean.FALSE);
				queryBuilder.forUpdate(Boolean.TRUE);
				queryBuilder.lockOption(resultSet.lockOption());
			} else {
				queryBuilder.useCache(resultSet.cacheables());
				queryBuilder.forUpdate(Boolean.FALSE);
				queryBuilder.lockOption(LockOption.NONE);
			}
			Arrays.asList(resultSet.orderColumns())
					.forEach(orderColumn ->
							queryBuilder.orderBy(orderColumn.entity(), orderColumn.identifyKey(),
									orderColumn.type(), orderColumn.sortCode()));
			Arrays.asList(resultSet.groupColumns())
					.forEach(groupColumn ->
							queryBuilder.groupBy(groupColumn.entity(), groupColumn.identifyKey(),
									groupColumn.sortCode()));
		} else {
			queryBuilder = newBuilder(targetClass);
			for (ColumnConfig columnConfig : columnConfigs(targetClass)) {
				if (!columnConfig.isLazyLoad()) {
					queryBuilder.addItem(AbstractItem.column(targetClass, columnConfig));
				}
			}
			queryBuilder.forUpdate(forUpdate);
			Optional.ofNullable(EntityManager.tableConfig(targetClass))
					.map(TableConfig::getLockOption)
					.ifPresent(queryBuilder::lockOption);
		}

		queryBuilder.forUpdate(forUpdate).configPager(pageNo, pageLimit);
		conditionList.forEach(queryBuilder::addCondition);
		return queryBuilder.confirm();
	}

	/**
	 * <h4 class="en-US">Generate query information instance</h4>
	 * <h4 class="zh-CN">生成查询信息实例对象</h4>
	 *
	 * @return <span class="en-US">Generated query information instance</span>
	 * <span class="zh-CN">生成的查询信息实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	@Override
	public QueryInfo confirm() throws BuilderException {
		QueryInfo queryInfo = new QueryInfo();

		if (this.itemList.isEmpty()) {
			for (ColumnConfig columnConfig : columnConfigs(this.mainEntity)) {
				this.addColumn(this.mainEntity, columnConfig.columnName(), columnConfig.getFieldName());
			}
		}

		queryInfo.setIdentifyName(this.identifyName);
		queryInfo.setMainEntity(this.mainEntity);
		queryInfo.setQueryJoins(this.queryJoins);
		queryInfo.setItemList(this.itemList);
		queryInfo.setConditionList(this.conditionList);
		queryInfo.setOrderByList(this.orderByList);
		queryInfo.setGroupByList(this.groupByList);

		queryInfo.setCacheables(this.cacheables);
		queryInfo.setForUpdate(this.forUpdate);
		queryInfo.setLockOption(this.lockOption);

		queryInfo.setPageNo(this.pageNo);
		queryInfo.setPageLimit(this.pageLimit);

		return queryInfo;
	}

	/**
	 * <h4 class="en-US">Setting for query identification name</h4>
	 * <h4 class="zh-CN">设置查询名称</h4>
	 *
	 * @param identifyName <span class="en-US">query identification name</span>
	 *                     <span class="zh-CN">查询名称</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public QueryBuilder identifyName(final String identifyName) {
		if (StringUtils.notBlank(identifyName)) {
			this.identifyName = identifyName;
		}
		return this;
	}

	/**
	 * <h4 class="en-US">Static method for generate query column information instance</h4>
	 * <h4 class="zh-CN">静态方法用于生成数据列查询对象实例</h4>
	 *
	 * @param entityClass <span class="en-US">Entity class</span>
	 *                    <span class="zh-CN">实体类</span>
	 * @param identifyKey <span class="en-US">Identify key</span>
	 *                    <span class="zh-CN">识别代码</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder addColumn(@Nonnull final Class<?> entityClass, final String identifyKey)
			throws BuilderException {
		return this.addColumn(entityClass, identifyKey, Globals.DEFAULT_VALUE_STRING);
	}

	/**
	 * <h4 class="en-US">Static method for generate query column information instance</h4>
	 * <h4 class="zh-CN">静态方法用于生成数据列查询对象实例</h4>
	 *
	 * @param entityClass <span class="en-US">Entity class</span>
	 *                    <span class="zh-CN">实体类</span>
	 * @param identifyKey <span class="en-US">Identify key</span>
	 *                    <span class="zh-CN">识别代码</span>
	 * @param aliasName   <span class="en-US">Item alias name</span>
	 *                    <span class="zh-CN">查询项别名</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder addColumn(@Nonnull final Class<?> entityClass, final String identifyKey,
	                              final String aliasName) throws BuilderException {
		return this.addColumn(entityClass, identifyKey, Boolean.FALSE, aliasName);
	}

	/**
	 * <h4 class="en-US">Static method for generate query column information instance</h4>
	 * <h4 class="zh-CN">静态方法用于生成数据列查询对象实例</h4>
	 *
	 * @param entityClass <span class="en-US">Entity class</span>
	 *                    <span class="zh-CN">实体类</span>
	 * @param identifyKey <span class="en-US">Identify key</span>
	 *                    <span class="zh-CN">识别代码</span>
	 * @param distinct    <span class="en-US">Column distinct</span>
	 *                    <span class="zh-CN">数据列去重</span>
	 * @param aliasName   <span class="en-US">Item alias name</span>
	 *                    <span class="zh-CN">查询项别名</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder addColumn(@Nonnull final Class<?> entityClass, final String identifyKey,
	                              final boolean distinct, final String aliasName) throws BuilderException {
		return this.addColumn(entityClass, identifyKey, distinct, aliasName, Globals.DEFAULT_VALUE_INT);
	}

	/**
	 * <h4 class="en-US">Add query column information instance</h4>
	 * <h4 class="zh-CN">添加数据列查询对象实例</h4>
	 *
	 * @param entityClass <span class="en-US">Entity class</span>
	 *                    <span class="zh-CN">实体类</span>
	 * @param identifyKey <span class="en-US">Identify key</span>
	 *                    <span class="zh-CN">识别代码</span>
	 * @param distinct    <span class="en-US">Column distinct</span>
	 *                    <span class="zh-CN">数据列去重</span>
	 * @param aliasName   <span class="en-US">Item alias name</span>
	 *                    <span class="zh-CN">查询项别名</span>
	 * @param sortCode    <span class="en-US">Sort code</span>
	 *                    <span class="zh-CN">排序代码</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder addColumn(@Nonnull final Class<?> entityClass, final String identifyKey,
	                              final boolean distinct, final String aliasName, final int sortCode)
			throws BuilderException {
		this.addItem(AbstractItem.column(entityClass, identifyKey, distinct, aliasName, sortCode));
		return this;
	}

	/**
	 * <h4 class="en-US">Add query function information instance</h4>
	 * <h4 class="zh-CN">添加数据列函数查询对象实例</h4>
	 *
	 * @param aliasName      <span class="en-US">Item alias name</span>
	 *                       <span class="zh-CN">查询项别名</span>
	 * @param sqlFunction    <span class="en-US">Function name</span>
	 *                       <span class="zh-CN">函数名</span>
	 * @param functionParams <span class="en-US">Function arguments array</span>
	 *                       <span class="zh-CN">函数参数数组</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public QueryBuilder addFunction(final String aliasName, final String sqlFunction,
	                                final AbstractParameter<?>... functionParams) {
		return this.addFunction(aliasName, Globals.DEFAULT_VALUE_INT, sqlFunction, functionParams);
	}

	/**
	 * <h4 class="en-US">Add query function information instance</h4>
	 * <h4 class="zh-CN">添加数据列函数查询对象实例</h4>
	 *
	 * @param aliasName      <span class="en-US">Item alias name</span>
	 *                       <span class="zh-CN">查询项别名</span>
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param sqlFunction    <span class="en-US">Function name</span>
	 *                       <span class="zh-CN">函数名</span>
	 * @param functionParams <span class="en-US">Function arguments array</span>
	 *                       <span class="zh-CN">函数参数数组</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public QueryBuilder addFunction(final String aliasName, final int sortCode, final String sqlFunction,
	                                final AbstractParameter<?>... functionParams) {
		this.addItem(AbstractItem.function(aliasName, sortCode, sqlFunction, functionParams));
		return this;
	}

	/**
	 * <h4 class="en-US">Add sub-query information instance</h4>
	 * <h4 class="zh-CN">添加子函数查询对象实例</h4>
	 *
	 * @param queryInfo <span class="en-US">Query information</span>
	 *                  <span class="zh-CN">查询信息</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public QueryBuilder addSubQuery(final QueryInfo queryInfo) {
		return addSubQuery(Globals.DEFAULT_VALUE_STRING, queryInfo);
	}

	/**
	 * <h4 class="en-US">Add sub-query information instance</h4>
	 * <h4 class="zh-CN">添加子函数查询对象实例</h4>
	 *
	 * @param aliasName <span class="en-US">Item alias name</span>
	 *                  <span class="zh-CN">查询项别名</span>
	 * @param queryInfo <span class="en-US">Query information</span>
	 *                  <span class="zh-CN">查询信息</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public QueryBuilder addSubQuery(final String aliasName, final QueryInfo queryInfo) {
		return addSubQuery(aliasName, Globals.DEFAULT_VALUE_INT, queryInfo);
	}

	/**
	 * <h4 class="en-US">Add sub-query information instance</h4>
	 * <h4 class="zh-CN">添加子函数查询对象实例</h4>
	 *
	 * @param aliasName <span class="en-US">Item alias name</span>
	 *                  <span class="zh-CN">查询项别名</span>
	 * @param sortCode  <span class="en-US">Sort code</span>
	 *                  <span class="zh-CN">排序代码</span>
	 * @param queryInfo <span class="en-US">Query information</span>
	 *                  <span class="zh-CN">查询信息</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public QueryBuilder addSubQuery(final String aliasName, final int sortCode, final QueryInfo queryInfo) {
		this.addItem(AbstractItem.query(aliasName, sortCode, queryInfo));
		return this;
	}

	/**
	 * <h4 class="en-US">Add associated query information table</h4>
	 * <h4 class="zh-CN">添加关联查询信息表</h4>
	 *
	 * @param mainEntity <span class="en-US">Driver table entity class</span>
	 *                   <span class="zh-CN">驱动表实体类</span>
	 * @param joinEntity <span class="en-US">Reference table entity class</span>
	 *                   <span class="zh-CN">关联表实体类</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public QueryBuilder joinTable(final Class<?> mainEntity, final Class<?> joinEntity) {
		return joinTable(mainEntity, joinEntity, Collections.emptyList());
	}

	/**
	 * <h4 class="en-US">Add associated query information table</h4>
	 * <h4 class="zh-CN">添加关联查询信息表</h4>
	 *
	 * @param mainEntity <span class="en-US">Driver table entity class</span>
	 *                   <span class="zh-CN">驱动表实体类</span>
	 * @param joinEntity <span class="en-US">Reference table entity class</span>
	 *                   <span class="zh-CN">关联表实体类</span>
	 * @param joinInfos  <span class="en-US">Related column information definition list</span>
	 *                   <span class="zh-CN">关联列信息定义列表</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public QueryBuilder joinTable(final Class<?> mainEntity, final Class<?> joinEntity, final List<JoinInfo> joinInfos) {
		return this.joinTable(mainEntity, JoinType.INNER, joinEntity, joinInfos);
	}

	/**
	 * <h4 class="en-US">Add associated query information table</h4>
	 * <h4 class="zh-CN">添加关联查询信息表</h4>
	 *
	 * @param mainEntity <span class="en-US">Driver table entity class</span>
	 *                   <span class="zh-CN">驱动表实体类</span>
	 * @param joinType   <span class="en-US">Table join type</span>
	 *                   <span class="zh-CN">数据表关联类型</span>
	 * @param joinEntity <span class="en-US">Reference table entity class</span>
	 *                   <span class="zh-CN">关联表实体类</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public QueryBuilder joinTable(@Nonnull final Class<?> mainEntity, @Nonnull final JoinType joinType,
	                              @Nonnull final Class<?> joinEntity) {
		return joinTable(mainEntity, joinType, joinEntity, Collections.emptyList());
	}

	private boolean containsEntity(final Class<?> entityClass) {
		if (ObjectUtils.nullSafeEquals(this.mainEntity, entityClass)) {
			return Boolean.TRUE;
		}
		return this.queryJoins.stream().anyMatch(queryJoin ->
				ObjectUtils.nullSafeEquals(queryJoin.getJoinEntity(), entityClass));
	}

	/**
	 * <h4 class="en-US">Add associated query information table</h4>
	 * <h4 class="zh-CN">添加关联查询信息表</h4>
	 *
	 * @param mainEntity <span class="en-US">Driver table entity class</span>
	 *                   <span class="zh-CN">驱动表实体类</span>
	 * @param joinType   <span class="en-US">Table join type</span>
	 *                   <span class="zh-CN">数据表关联类型</span>
	 * @param joinEntity <span class="en-US">Reference table entity class</span>
	 *                   <span class="zh-CN">关联表实体类</span>
	 * @param joinInfos  <span class="en-US">Related column information definition list</span>
	 *                   <span class="zh-CN">关联列信息定义列表</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public QueryBuilder joinTable(@Nonnull final Class<?> mainEntity, @Nonnull final JoinType joinType,
	                              @Nonnull final Class<?> joinEntity, @Nonnull final List<JoinInfo> joinInfos) {
		if (EntityManager.tableExists(mainEntity) && EntityManager.tableExists(joinEntity)
				&& this.containsEntity(mainEntity)) {
			final List<JoinInfo> joinInfoList = new ArrayList<>();
			if (joinInfos.isEmpty()) {
				Optional.ofNullable(EntityManager.tableConfig(mainEntity))
						.map(tableConfig -> tableConfig.referenceConfig(joinEntity))
						.ifPresent(referenceConfig ->
								referenceConfig.getJoinColumnList()
										.forEach(joinConfig -> joinInfoList.add(JoinInfo.newInstance(joinConfig))));
			} else {
				joinInfoList.addAll(joinInfos);
			}

			if (this.queryJoins.stream().anyMatch(queryJoin -> queryJoin.match(mainEntity, joinEntity))) {
				this.queryJoins.replaceAll(queryJoin -> {
					if (queryJoin.match(mainEntity, joinEntity)) {
						queryJoin.setJoinType(joinType);
						queryJoin.setJoinInfos(joinInfoList);
					}
					return queryJoin;
				});
			} else {
				this.queryJoins.add(new QueryJoin(mainEntity, joinEntity, joinType, joinInfoList));
			}
		}
		return this;
	}

	/**
	 * <h4 class="en-US">Add a query condition greater than a certain value</h4>
	 * <h4 class="zh-CN">添加大于某值的查询条件</h4>
	 *
	 * @param entityClass <span class="en-US">Entity class</span>
	 *                    <span class="zh-CN">实体类</span>
	 * @param identifyKey <span class="en-US">Identify key</span>
	 *                    <span class="zh-CN">识别代码</span>
	 * @param matchValue  <span class="en-US">Match value</span>
	 *                    <span class="zh-CN">匹配值</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder greater(final Class<?> entityClass, final String identifyKey, final Object matchValue)
			throws BuilderException {
		return this.greater(ConnectionCode.AND, entityClass, identifyKey, matchValue);
	}

	/**
	 * <h4 class="en-US">Add a query condition greater than a certain value</h4>
	 * <h4 class="zh-CN">添加大于某值的查询条件</h4>
	 *
	 * @param entityClass    <span class="en-US">Data table entity class</span>
	 *                       <span class="zh-CN">数据表实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param sqlFunction    <span class="en-US">Function name</span>
	 *                       <span class="zh-CN">函数名称</span>
	 * @param functionParams <span class="en-US">Function parameter values</span>
	 *                       <span class="zh-CN">函数参数值</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder greater(final Class<?> entityClass, final String identifyKey,
	                            final String sqlFunction, final AbstractParameter<?>... functionParams)
			throws BuilderException {
		return this.greater(ConnectionCode.AND, entityClass, identifyKey, sqlFunction, functionParams);
	}

	/**
	 * <h4 class="en-US">Add a query condition greater than a certain value</h4>
	 * <h4 class="zh-CN">添加大于某值的查询条件</h4>
	 *
	 * @param entityClass <span class="en-US">Data table entity class</span>
	 *                    <span class="zh-CN">数据表实体类</span>
	 * @param identifyKey <span class="en-US">Identify key</span>
	 *                    <span class="zh-CN">识别代码</span>
	 * @param subQuery    <span class="en-US">Sub-query instance object</span>
	 *                    <span class="zh-CN">子查询实例对象</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder greater(final Class<?> entityClass, final String identifyKey, final QueryInfo subQuery)
			throws BuilderException {
		return this.greater(ConnectionCode.AND, entityClass, identifyKey, subQuery);
	}

	/**
	 * <h4 class="en-US">Add a query condition greater than a certain value</h4>
	 * <h4 class="zh-CN">添加大于某值的查询条件</h4>
	 *
	 * @param entityClass <span class="en-US">Data table entity class</span>
	 *                    <span class="zh-CN">数据表实体类</span>
	 * @param identifyKey <span class="en-US">Identify key</span>
	 *                    <span class="zh-CN">识别代码</span>
	 * @param matchEntity <span class="en-US">Target data table entity class</span>
	 *                    <span class="zh-CN">目标数据表实体类</span>
	 * @param columnKey   <span class="en-US">Target data column identification name</span>
	 *                    <span class="zh-CN">目标数据列识别名称</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder greater(final Class<?> entityClass, final String identifyKey,
	                            final Class<?> matchEntity, final String columnKey) throws BuilderException {
		return this.greater(ConnectionCode.AND, entityClass, identifyKey, matchEntity, columnKey);
	}

	/**
	 * <h4 class="en-US">Add a query condition greater than or equal to a certain value</h4>
	 * <h4 class="zh-CN">添加大于等于某值的查询条件</h4>
	 *
	 * @param entityClass <span class="en-US">Entity class</span>
	 *                    <span class="zh-CN">实体类</span>
	 * @param identifyKey <span class="en-US">Identify key</span>
	 *                    <span class="zh-CN">识别代码</span>
	 * @param matchValue  <span class="en-US">Match value</span>
	 *                    <span class="zh-CN">匹配值</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder greaterEqual(final Class<?> entityClass, final String identifyKey, final Object matchValue)
			throws BuilderException {
		return this.greaterEqual(ConnectionCode.AND, entityClass, identifyKey, matchValue);
	}

	/**
	 * <h4 class="en-US">Add a query condition greater than or equal to a certain value</h4>
	 * <h4 class="zh-CN">添加大于等于某值的查询条件</h4>
	 *
	 * @param entityClass    <span class="en-US">Data table entity class</span>
	 *                       <span class="zh-CN">数据表实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param sqlFunction    <span class="en-US">Function name</span>
	 *                       <span class="zh-CN">函数名称</span>
	 * @param functionParams <span class="en-US">Function parameter values</span>
	 *                       <span class="zh-CN">函数参数值</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder greaterEqual(final Class<?> entityClass, final String identifyKey,
	                                 final String sqlFunction, final AbstractParameter<?>... functionParams)
			throws BuilderException {
		return this.greaterEqual(ConnectionCode.AND, entityClass, identifyKey, sqlFunction, functionParams);
	}

	/**
	 * <h4 class="en-US">Add a query condition greater than or equal to a certain value</h4>
	 * <h4 class="zh-CN">添加大于等于某值的查询条件</h4>
	 *
	 * @param entityClass <span class="en-US">Data table entity class</span>
	 *                    <span class="zh-CN">数据表实体类</span>
	 * @param identifyKey <span class="en-US">Identify key</span>
	 *                    <span class="zh-CN">识别代码</span>
	 * @param subQuery    <span class="en-US">Sub-query instance object</span>
	 *                    <span class="zh-CN">子查询实例对象</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder greaterEqual(final Class<?> entityClass, final String identifyKey, final QueryInfo subQuery)
			throws BuilderException {
		return this.greaterEqual(ConnectionCode.AND, entityClass, identifyKey, subQuery);
	}

	/**
	 * <h4 class="en-US">Add a query condition greater than or equal to a certain value</h4>
	 * <h4 class="zh-CN">添加大于等于某值的查询条件</h4>
	 *
	 * @param entityClass <span class="en-US">Data table entity class</span>
	 *                    <span class="zh-CN">数据表实体类</span>
	 * @param identifyKey <span class="en-US">Identify key</span>
	 *                    <span class="zh-CN">识别代码</span>
	 * @param matchEntity <span class="en-US">Target data table entity class</span>
	 *                    <span class="zh-CN">目标数据表实体类</span>
	 * @param columnKey   <span class="en-US">Target data column identification name</span>
	 *                    <span class="zh-CN">目标数据列识别名称</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder greaterEqual(final Class<?> entityClass, final String identifyKey,
	                                 final Class<?> matchEntity, final String columnKey) throws BuilderException {
		return this.greaterEqual(ConnectionCode.AND, entityClass, identifyKey, matchEntity, columnKey);
	}

	/**
	 * <h4 class="en-US">Add a query condition less than a certain value</h4>
	 * <h4 class="zh-CN">添加小于某值的查询条件</h4>
	 *
	 * @param entityClass <span class="en-US">Entity class</span>
	 *                    <span class="zh-CN">实体类</span>
	 * @param identifyKey <span class="en-US">Identify key</span>
	 *                    <span class="zh-CN">识别代码</span>
	 * @param matchValue  <span class="en-US">Match value</span>
	 *                    <span class="zh-CN">匹配值</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder less(final Class<?> entityClass, final String identifyKey, final Object matchValue)
			throws BuilderException {
		return this.less(ConnectionCode.AND, entityClass, identifyKey, matchValue);
	}

	/**
	 * <h4 class="en-US">Add a query condition less than a certain value</h4>
	 * <h4 class="zh-CN">添加小于某值的查询条件</h4>
	 *
	 * @param entityClass    <span class="en-US">Data table entity class</span>
	 *                       <span class="zh-CN">数据表实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param sqlFunction    <span class="en-US">Function name</span>
	 *                       <span class="zh-CN">函数名称</span>
	 * @param functionParams <span class="en-US">Function parameter values</span>
	 *                       <span class="zh-CN">函数参数值</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder less(final Class<?> entityClass, final String identifyKey,
	                         final String sqlFunction, final AbstractParameter<?>... functionParams)
			throws BuilderException {
		return this.less(ConnectionCode.AND, entityClass, identifyKey, sqlFunction, functionParams);
	}

	/**
	 * <h4 class="en-US">Add a query condition less than a certain value</h4>
	 * <h4 class="zh-CN">添加小于某值的查询条件</h4>
	 *
	 * @param entityClass <span class="en-US">Data table entity class</span>
	 *                    <span class="zh-CN">数据表实体类</span>
	 * @param identifyKey <span class="en-US">Identify key</span>
	 *                    <span class="zh-CN">识别代码</span>
	 * @param subQuery    <span class="en-US">Sub-query instance object</span>
	 *                    <span class="zh-CN">子查询实例对象</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder less(final Class<?> entityClass, final String identifyKey, final QueryInfo subQuery)
			throws BuilderException {
		return this.less(ConnectionCode.AND, entityClass, identifyKey, subQuery);
	}

	/**
	 * <h4 class="en-US">Add a query condition less than a certain value</h4>
	 * <h4 class="zh-CN">添加小于某值的查询条件</h4>
	 *
	 * @param entityClass <span class="en-US">Data table entity class</span>
	 *                    <span class="zh-CN">数据表实体类</span>
	 * @param identifyKey <span class="en-US">Identify key</span>
	 *                    <span class="zh-CN">识别代码</span>
	 * @param matchEntity <span class="en-US">Target data table entity class</span>
	 *                    <span class="zh-CN">目标数据表实体类</span>
	 * @param columnKey   <span class="en-US">Target data column identification name</span>
	 *                    <span class="zh-CN">目标数据列识别名称</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder less(final Class<?> entityClass, final String identifyKey,
	                         final Class<?> matchEntity, final String columnKey) throws BuilderException {
		return this.less(ConnectionCode.AND, entityClass, identifyKey, matchEntity, columnKey);
	}

	/**
	 * <h4 class="en-US">Add a query condition less than or equal to a certain value</h4>
	 * <h4 class="zh-CN">添加小于等于某值的查询条件</h4>
	 *
	 * @param entityClass <span class="en-US">Entity class</span>
	 *                    <span class="zh-CN">实体类</span>
	 * @param identifyKey <span class="en-US">Identify key</span>
	 *                    <span class="zh-CN">识别代码</span>
	 * @param matchValue  <span class="en-US">Match value</span>
	 *                    <span class="zh-CN">匹配值</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder lessEqual(final Class<?> entityClass, final String identifyKey, final Object matchValue)
			throws BuilderException {
		return this.lessEqual(ConnectionCode.AND, entityClass, identifyKey, matchValue);
	}

	/**
	 * <h4 class="en-US">Add a query condition less than or equal to a certain value</h4>
	 * <h4 class="zh-CN">添加小于等于某值的查询条件</h4>
	 *
	 * @param entityClass    <span class="en-US">Entity class</span>
	 *                       <span class="zh-CN">实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param sqlFunction    <span class="en-US">Function name</span>
	 *                       <span class="zh-CN">函数名称</span>
	 * @param functionParams <span class="en-US">Function parameter values</span>
	 *                       <span class="zh-CN">函数参数值</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder lessEqual(final Class<?> entityClass, final String identifyKey,
	                              final String sqlFunction, final AbstractParameter<?>... functionParams)
			throws BuilderException {
		return this.lessEqual(ConnectionCode.AND, entityClass, identifyKey, sqlFunction, functionParams);
	}

	/**
	 * <h4 class="en-US">Add a query condition less than or equal to a certain value</h4>
	 * <h4 class="zh-CN">添加小于等于某值的查询条件</h4>
	 *
	 * @param entityClass <span class="en-US">Data table entity class</span>
	 *                    <span class="zh-CN">数据表实体类</span>
	 * @param identifyKey <span class="en-US">Identify key</span>
	 *                    <span class="zh-CN">识别代码</span>
	 * @param subQuery    <span class="en-US">Sub-query instance object</span>
	 *                    <span class="zh-CN">子查询实例对象</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder lessEqual(final Class<?> entityClass, final String identifyKey, final QueryInfo subQuery)
			throws BuilderException {
		return this.lessEqual(ConnectionCode.AND, entityClass, identifyKey, subQuery);
	}

	/**
	 * <h4 class="en-US">Add a query condition less than or equal to a certain value</h4>
	 * <h4 class="zh-CN">添加小于等于某值的查询条件</h4>
	 *
	 * @param entityClass <span class="en-US">Data table entity class</span>
	 *                    <span class="zh-CN">数据表实体类</span>
	 * @param identifyKey <span class="en-US">Identify key</span>
	 *                    <span class="zh-CN">识别代码</span>
	 * @param matchEntity <span class="en-US">Target data table entity class</span>
	 *                    <span class="zh-CN">目标数据表实体类</span>
	 * @param columnKey   <span class="en-US">Target data column identification name</span>
	 *                    <span class="zh-CN">目标数据列识别名称</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder lessEqual(final Class<?> entityClass, final String identifyKey,
	                              final Class<?> matchEntity, final String columnKey) throws BuilderException {
		return this.lessEqual(ConnectionCode.AND, entityClass, identifyKey, matchEntity, columnKey);
	}

	/**
	 * <h4 class="en-US">Add a query condition equal to a certain value</h4>
	 * <h4 class="zh-CN">添加等于某值的查询条件</h4>
	 *
	 * @param entityClass <span class="en-US">Entity class</span>
	 *                    <span class="zh-CN">实体类</span>
	 * @param identifyKey <span class="en-US">Identify key</span>
	 *                    <span class="zh-CN">识别代码</span>
	 * @param matchValue  <span class="en-US">Match value</span>
	 *                    <span class="zh-CN">匹配值</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder equalTo(final Class<?> entityClass, final String identifyKey, final Object matchValue)
			throws BuilderException {
		return this.equalTo(ConnectionCode.AND, entityClass, identifyKey, matchValue);
	}

	/**
	 * <h4 class="en-US">Add a query condition equal to a certain value</h4>
	 * <h4 class="zh-CN">添加等于某值的查询条件</h4>
	 *
	 * @param entityClass    <span class="en-US">Entity class</span>
	 *                       <span class="zh-CN">实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param sqlFunction    <span class="en-US">Function name</span>
	 *                       <span class="zh-CN">函数名称</span>
	 * @param functionParams <span class="en-US">Function parameter values</span>
	 *                       <span class="zh-CN">函数参数值</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder equalTo(final Class<?> entityClass, final String identifyKey,
	                            final String sqlFunction, final AbstractParameter<?>... functionParams)
			throws BuilderException {
		return this.equalTo(ConnectionCode.AND, entityClass, identifyKey, sqlFunction, functionParams);
	}

	/**
	 * <h4 class="en-US">Add a query condition equal to a certain value</h4>
	 * <h4 class="zh-CN">添加等于某值的查询条件</h4>
	 *
	 * @param entityClass <span class="en-US">Data table entity class</span>
	 *                    <span class="zh-CN">数据表实体类</span>
	 * @param identifyKey <span class="en-US">Identify key</span>
	 *                    <span class="zh-CN">识别代码</span>
	 * @param subQuery    <span class="en-US">Sub-query instance object</span>
	 *                    <span class="zh-CN">子查询实例对象</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder equalTo(final Class<?> entityClass, final String identifyKey, final QueryInfo subQuery)
			throws BuilderException {
		return this.equalTo(ConnectionCode.AND, entityClass, identifyKey, subQuery);
	}

	/**
	 * <h4 class="en-US">Add a query condition equal to a certain value</h4>
	 * <h4 class="zh-CN">添加等于某值的查询条件</h4>
	 *
	 * @param entityClass <span class="en-US">Data table entity class</span>
	 *                    <span class="zh-CN">数据表实体类</span>
	 * @param identifyKey <span class="en-US">Identify key</span>
	 *                    <span class="zh-CN">识别代码</span>
	 * @param matchEntity <span class="en-US">Target data table entity class</span>
	 *                    <span class="zh-CN">目标数据表实体类</span>
	 * @param columnKey   <span class="en-US">Target data column identification name</span>
	 *                    <span class="zh-CN">目标数据列识别名称</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder equalTo(final Class<?> entityClass, final String identifyKey,
	                            final Class<?> matchEntity, final String columnKey) throws BuilderException {
		return this.equalTo(ConnectionCode.AND, entityClass, identifyKey, matchEntity, columnKey);
	}

	/**
	 * <h4 class="en-US">Add a query condition not equal to a certain value</h4>
	 * <h4 class="zh-CN">添加不等于某值的查询条件</h4>
	 *
	 * @param entityClass <span class="en-US">Entity class</span>
	 *                    <span class="zh-CN">实体类</span>
	 * @param identifyKey <span class="en-US">Identify key</span>
	 *                    <span class="zh-CN">识别代码</span>
	 * @param matchValue  <span class="en-US">Match value</span>
	 *                    <span class="zh-CN">匹配值</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder notEqual(final Class<?> entityClass, final String identifyKey, final Object matchValue)
			throws BuilderException {
		return this.notEqual(ConnectionCode.AND, entityClass, identifyKey, matchValue);
	}

	/**
	 * <h4 class="en-US">Add a query condition not equal to a certain value</h4>
	 * <h4 class="zh-CN">添加不等于某值的查询条件</h4>
	 *
	 * @param entityClass    <span class="en-US">Entity class</span>
	 *                       <span class="zh-CN">实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param sqlFunction    <span class="en-US">Function name</span>
	 *                       <span class="zh-CN">函数名称</span>
	 * @param functionParams <span class="en-US">Function parameter values</span>
	 *                       <span class="zh-CN">函数参数值</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder notEqual(final Class<?> entityClass, final String identifyKey,
	                             final String sqlFunction, final AbstractParameter<?>... functionParams)
			throws BuilderException {
		return this.notEqual(ConnectionCode.AND, entityClass, identifyKey, sqlFunction, functionParams);
	}

	/**
	 * <h4 class="en-US">Add a query condition not equal to a certain value</h4>
	 * <h4 class="zh-CN">添加不等于某值的查询条件</h4>
	 *
	 * @param entityClass <span class="en-US">Data table entity class</span>
	 *                    <span class="zh-CN">数据表实体类</span>
	 * @param identifyKey <span class="en-US">Identify key</span>
	 *                    <span class="zh-CN">识别代码</span>
	 * @param subQuery    <span class="en-US">Sub-query instance object</span>
	 *                    <span class="zh-CN">子查询实例对象</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder notEqual(final Class<?> entityClass, final String identifyKey, final QueryInfo subQuery)
			throws BuilderException {
		return this.notEqual(ConnectionCode.AND, entityClass, identifyKey, subQuery);
	}

	/**
	 * <h4 class="en-US">Add a query condition not equal to a certain value</h4>
	 * <h4 class="zh-CN">添加不等于某值的查询条件</h4>
	 *
	 * @param entityClass <span class="en-US">Data table entity class</span>
	 *                    <span class="zh-CN">数据表实体类</span>
	 * @param identifyKey <span class="en-US">Identify key</span>
	 *                    <span class="zh-CN">识别代码</span>
	 * @param matchEntity <span class="en-US">Target data table entity class</span>
	 *                    <span class="zh-CN">目标数据表实体类</span>
	 * @param columnKey   <span class="en-US">Target data column identification name</span>
	 *                    <span class="zh-CN">目标数据列识别名称</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder notEqual(final Class<?> entityClass, final String identifyKey,
	                             final Class<?> matchEntity, final String columnKey) throws BuilderException {
		return this.notEqual(ConnectionCode.AND, entityClass, identifyKey, matchEntity, columnKey);
	}

	/**
	 * <h4 class="en-US">Add a query condition between certain two values</h4>
	 * <h4 class="zh-CN">添加介于某两个值之间的查询条件</h4>
	 *
	 * @param entityClass <span class="en-US">Entity class</span>
	 *                    <span class="zh-CN">实体类</span>
	 * @param identifyKey <span class="en-US">Identify key</span>
	 *                    <span class="zh-CN">识别代码</span>
	 * @param beginValue  <span class="en-US">Interval starting value</span>
	 *                    <span class="zh-CN">区间起始值</span>
	 * @param endValue    <span class="en-US">Interval end value</span>
	 *                    <span class="zh-CN">区间终止值</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder betweenAnd(final Class<?> entityClass, final String identifyKey,
	                               final Object beginValue, final Object endValue) throws BuilderException {
		return this.betweenAnd(ConnectionCode.AND, entityClass, identifyKey, beginValue, endValue);
	}

	/**
	 * <h4 class="en-US">Add a query condition not between certain two values</h4>
	 * <h4 class="zh-CN">添加不介于某两个值之间的查询条件</h4>
	 *
	 * @param entityClass <span class="en-US">Entity class</span>
	 *                    <span class="zh-CN">实体类</span>
	 * @param identifyKey <span class="en-US">Identify key</span>
	 *                    <span class="zh-CN">识别代码</span>
	 * @param beginValue  <span class="en-US">Interval starting value</span>
	 *                    <span class="zh-CN">区间起始值</span>
	 * @param endValue    <span class="en-US">Interval end value</span>
	 *                    <span class="zh-CN">区间终止值</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder notBetweenAnd(final Class<?> entityClass, final String identifyKey,
	                                  final Object beginValue, final Object endValue) throws BuilderException {
		return this.notBetweenAnd(ConnectionCode.AND, entityClass, identifyKey, beginValue, endValue);
	}

	/**
	 * <h4 class="en-US">Add query conditions for fuzzy matching values</h4>
	 * <h4 class="zh-CN">添加模糊匹配值的查询条件</h4>
	 *
	 * @param entityClass <span class="en-US">Entity class</span>
	 *                    <span class="zh-CN">实体类</span>
	 * @param identifyKey <span class="en-US">Identify key</span>
	 *                    <span class="zh-CN">识别代码</span>
	 * @param matchRule   <span class="en-US">match rule string</span>
	 *                    <span class="zh-CN">匹配规则字符串</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder like(final Class<?> entityClass, final String identifyKey, final String matchRule)
			throws BuilderException {
		return this.like(ConnectionCode.AND, entityClass, identifyKey, matchRule);
	}

	/**
	 * <h4 class="en-US">Add query conditions for not fuzzy matching values</h4>
	 * <h4 class="zh-CN">添加非模糊匹配值的查询条件</h4>
	 *
	 * @param entityClass <span class="en-US">Entity class</span>
	 *                    <span class="zh-CN">实体类</span>
	 * @param identifyKey <span class="en-US">Identify key</span>
	 *                    <span class="zh-CN">识别代码</span>
	 * @param matchRule   <span class="en-US">match rule string</span>
	 *                    <span class="zh-CN">匹配规则字符串</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder notLike(final Class<?> entityClass, final String identifyKey, final String matchRule)
			throws BuilderException {
		return this.notLike(ConnectionCode.AND, entityClass, identifyKey, matchRule);
	}

	/**
	 * <h4 class="en-US">Add query condition with null value</h4>
	 * <h4 class="zh-CN">添加空值的查询条件</h4>
	 *
	 * @param entityClass <span class="en-US">Entity class</span>
	 *                    <span class="zh-CN">实体类</span>
	 * @param identifyKey <span class="en-US">Identify key</span>
	 *                    <span class="zh-CN">识别代码</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder matchNull(final Class<?> entityClass, final String identifyKey) throws BuilderException {
		return this.matchNull(ConnectionCode.AND, entityClass, identifyKey);
	}

	/**
	 * <h4 class="en-US">Add query condition with not null value</h4>
	 * <h4 class="zh-CN">添加非空值的查询条件</h4>
	 *
	 * @param entityClass <span class="en-US">Entity class</span>
	 *                    <span class="zh-CN">实体类</span>
	 * @param identifyKey <span class="en-US">Identify key</span>
	 *                    <span class="zh-CN">识别代码</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder notNull(final Class<?> entityClass, final String identifyKey) throws BuilderException {
		return this.notNull(ConnectionCode.AND, entityClass, identifyKey);
	}

	/**
	 * <h4 class="en-US">Adds a query condition where the value is contained in the given data</h4>
	 * <h4 class="zh-CN">添加值包含在给定数据中的查询条件</h4>
	 *
	 * @param entityClass  <span class="en-US">Entity class</span>
	 *                     <span class="zh-CN">实体类</span>
	 * @param identifyKey  <span class="en-US">Identify key</span>
	 *                     <span class="zh-CN">识别代码</span>
	 * @param matchObjects <span class="en-US">array of matching datasets</span>
	 *                     <span class="zh-CN">匹配数据集数组</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder in(final Class<?> entityClass, final String identifyKey, final Object... matchObjects)
			throws BuilderException {
		return this.in(ConnectionCode.AND, entityClass, identifyKey, matchObjects);
	}

	/**
	 * <h4 class="en-US">Adds a query condition where the value is contained in the given data</h4>
	 * <h4 class="zh-CN">添加值包含在给定数据中的查询条件</h4>
	 *
	 * @param entityClass <span class="en-US">Entity class</span>
	 *                    <span class="zh-CN">实体类</span>
	 * @param identifyKey <span class="en-US">Identify key</span>
	 *                    <span class="zh-CN">识别代码</span>
	 * @param subQuery    <span class="en-US">Sub-query instance object</span>
	 *                    <span class="zh-CN">子查询实例对象</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder in(final Class<?> entityClass, final String identifyKey, final QueryInfo subQuery)
			throws BuilderException {
		return this.in(ConnectionCode.AND, entityClass, identifyKey, subQuery);
	}

	/**
	 * <h4 class="en-US">Adds a query condition where the value is contained in the given data</h4>
	 * <h4 class="zh-CN">添加值包含在给定数据中的查询条件</h4>
	 *
	 * @param entityClass <span class="en-US">Entity class</span>
	 *                    <span class="zh-CN">实体类</span>
	 * @param identifyKey <span class="en-US">Identify key</span>
	 *                    <span class="zh-CN">识别代码</span>
	 * @param queryCode   <span class="en-US">Query code</span>
	 *                    <span class="zh-CN">查询代码</span>
	 * @param resultKey   <span class="en-US">Result identify key</span>
	 *                    <span class="zh-CN">结果集识别代码</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder in(final Class<?> entityClass, final String identifyKey,
	                       final long queryCode, final String resultKey) throws BuilderException {
		return this.in(ConnectionCode.AND, entityClass, identifyKey, queryCode, resultKey);
	}

	/**
	 * <h4 class="en-US">Adds a query condition where the value is not contained in the given data</h4>
	 * <h4 class="zh-CN">添加值没有包含在给定数据中的查询条件</h4>
	 *
	 * @param entityClass  <span class="en-US">Entity class</span>
	 *                     <span class="zh-CN">实体类</span>
	 * @param identifyKey  <span class="en-US">Identify key</span>
	 *                     <span class="zh-CN">识别代码</span>
	 * @param matchObjects <span class="en-US">array of matching datasets</span>
	 *                     <span class="zh-CN">匹配数据集数组</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder notIn(final Class<?> entityClass, final String identifyKey, final Object... matchObjects)
			throws BuilderException {
		return this.notIn(ConnectionCode.AND, entityClass, identifyKey, matchObjects);
	}

	/**
	 * <h4 class="en-US">Adds a query condition where the value is not contained in the given data</h4>
	 * <h4 class="zh-CN">添加值没有包含在给定数据中的查询条件</h4>
	 *
	 * @param entityClass <span class="en-US">Entity class</span>
	 *                    <span class="zh-CN">实体类</span>
	 * @param identifyKey <span class="en-US">Identify key</span>
	 *                    <span class="zh-CN">识别代码</span>
	 * @param subQuery    <span class="en-US">Sub-query instance object</span>
	 *                    <span class="zh-CN">子查询实例对象</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder notIn(final Class<?> entityClass, final String identifyKey, final QueryInfo subQuery)
			throws BuilderException {
		return this.notIn(ConnectionCode.AND, entityClass, identifyKey, subQuery);
	}

	/**
	 * <h4 class="en-US">Adds a query condition where the value is not contained in the given data</h4>
	 * <h4 class="zh-CN">添加值没有包含在给定数据中的查询条件</h4>
	 *
	 * @param entityClass <span class="en-US">Entity class</span>
	 *                    <span class="zh-CN">实体类</span>
	 * @param identifyKey <span class="en-US">Identify key</span>
	 *                    <span class="zh-CN">识别代码</span>
	 * @param queryCode   <span class="en-US">Query code</span>
	 *                    <span class="zh-CN">查询代码</span>
	 * @param resultKey   <span class="en-US">Result identify key</span>
	 *                    <span class="zh-CN">结果集识别代码</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder notIn(final Class<?> entityClass, final String identifyKey,
	                          final long queryCode, final String resultKey) throws BuilderException {
		return this.notIn(ConnectionCode.AND, entityClass, identifyKey, queryCode, resultKey);
	}

	/**
	 * <h4 class="en-US">Add query conditions for condition groups</h4>
	 * <h4 class="zh-CN">添加条件组的查询条件</h4>
	 *
	 * @param Conditions <span class="en-US">Query conditions array</span>
	 *                   <span class="zh-CN">查询条件数组</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public QueryBuilder group(final Condition... Conditions) {
		return this.group(ConnectionCode.AND, Conditions);
	}

	/**
	 * <h4 class="en-US">Add a query condition greater than a certain value</h4>
	 * <h4 class="zh-CN">添加大于某值的查询条件</h4>
	 *
	 * @param connectionCode <span class="en-US">Connection type code</span>
	 *                       <span class="zh-CN">连接类型代码</span>
	 * @param entityClass    <span class="en-US">Entity class</span>
	 *                       <span class="zh-CN">实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param matchValue     <span class="en-US">Match value</span>
	 *                       <span class="zh-CN">匹配值</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder greater(final ConnectionCode connectionCode, final Class<?> entityClass,
	                            final String identifyKey, final Object matchValue) throws BuilderException {
		return this.greater(Globals.DEFAULT_VALUE_INT, connectionCode, entityClass, identifyKey, matchValue);
	}

	/**
	 * <h4 class="en-US">Add a query condition greater than a certain value</h4>
	 * <h4 class="zh-CN">添加大于某值的查询条件</h4>
	 *
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param entityClass    <span class="en-US">Data table entity class</span>
	 *                       <span class="zh-CN">数据表实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param subQuery       <span class="en-US">Sub-query instance object</span>
	 *                       <span class="zh-CN">子查询实例对象</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder greater(final ConnectionCode connectionCode, final Class<?> entityClass,
	                            final String identifyKey, final QueryInfo subQuery) throws BuilderException {
		return this.greater(Globals.DEFAULT_VALUE_INT, connectionCode, entityClass, identifyKey, subQuery);
	}

	/**
	 * <h4 class="en-US">Add a query condition greater than a certain value</h4>
	 * <h4 class="zh-CN">添加大于某值的查询条件</h4>
	 *
	 * @param connectionCode <span class="en-US">Connection type code</span>
	 *                       <span class="zh-CN">连接类型代码</span>
	 * @param entityClass    <span class="en-US">Entity class</span>
	 *                       <span class="zh-CN">实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param sqlFunction    <span class="en-US">Function name</span>
	 *                       <span class="zh-CN">函数名称</span>
	 * @param functionParams <span class="en-US">Function parameter values</span>
	 *                       <span class="zh-CN">函数参数值</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder greater(final ConnectionCode connectionCode, final Class<?> entityClass,
	                            final String identifyKey, final String sqlFunction,
	                            final AbstractParameter<?>... functionParams) throws BuilderException {
		return this.greater(Globals.DEFAULT_VALUE_INT, connectionCode, entityClass, identifyKey,
				sqlFunction, functionParams);
	}

	/**
	 * <h4 class="en-US">Add a query condition greater than a certain value</h4>
	 * <h4 class="zh-CN">添加大于某值的查询条件</h4>
	 *
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param entityClass    <span class="en-US">Data table entity class</span>
	 *                       <span class="zh-CN">数据表实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param matchEntity    <span class="en-US">Target data table entity class</span>
	 *                       <span class="zh-CN">目标数据表实体类</span>
	 * @param columnKey      <span class="en-US">Target data column identification name</span>
	 *                       <span class="zh-CN">目标数据列识别名称</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder greater(final ConnectionCode connectionCode, final Class<?> entityClass,
	                            final String identifyKey, final Class<?> matchEntity, final String columnKey)
			throws BuilderException {
		return this.greater(Globals.DEFAULT_VALUE_INT, connectionCode, entityClass, identifyKey,
				matchEntity, columnKey);
	}

	/**
	 * <h4 class="en-US">Add a query condition greater than or equal to a certain value</h4>
	 * <h4 class="zh-CN">添加大于等于某值的查询条件</h4>
	 *
	 * @param connectionCode <span class="en-US">Connection type code</span>
	 *                       <span class="zh-CN">连接类型代码</span>
	 * @param entityClass    <span class="en-US">Entity class</span>
	 *                       <span class="zh-CN">实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param matchValue     <span class="en-US">Match value</span>
	 *                       <span class="zh-CN">匹配值</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder greaterEqual(final ConnectionCode connectionCode, final Class<?> entityClass,
	                                 final String identifyKey, final Object matchValue) throws BuilderException {
		return this.greaterEqual(Globals.DEFAULT_VALUE_INT, connectionCode, entityClass, identifyKey, matchValue);
	}

	/**
	 * <h4 class="en-US">Add a query condition greater than or equal to a certain value</h4>
	 * <h4 class="zh-CN">添加大于等于某值的查询条件</h4>
	 *
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param entityClass    <span class="en-US">Data table entity class</span>
	 *                       <span class="zh-CN">数据表实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param subQuery       <span class="en-US">Sub-query instance object</span>
	 *                       <span class="zh-CN">子查询实例对象</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder greaterEqual(final ConnectionCode connectionCode, final Class<?> entityClass,
	                                 final String identifyKey, final QueryInfo subQuery) throws BuilderException {
		return this.greaterEqual(Globals.DEFAULT_VALUE_INT, connectionCode, entityClass, identifyKey, subQuery);
	}

	/**
	 * <h4 class="en-US">Add a query condition greater than or equal to a certain value</h4>
	 * <h4 class="zh-CN">添加大于等于某值的查询条件</h4>
	 *
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param entityClass    <span class="en-US">Data table entity class</span>
	 *                       <span class="zh-CN">数据表实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param matchEntity    <span class="en-US">Target data table entity class</span>
	 *                       <span class="zh-CN">目标数据表实体类</span>
	 * @param columnKey      <span class="en-US">Target data column identification name</span>
	 *                       <span class="zh-CN">目标数据列识别名称</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder greaterEqual(final ConnectionCode connectionCode, final Class<?> entityClass,
	                                 final String identifyKey, final Class<?> matchEntity, final String columnKey)
			throws BuilderException {
		return this.greaterEqual(Globals.DEFAULT_VALUE_INT, connectionCode,
				entityClass, identifyKey, matchEntity, columnKey);
	}

	/**
	 * <h4 class="en-US">Add a query condition greater than or equal to a certain value</h4>
	 * <h4 class="zh-CN">添加大于等于某值的查询条件</h4>
	 *
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param entityClass    <span class="en-US">Data table entity class</span>
	 *                       <span class="zh-CN">数据表实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param sqlFunction    <span class="en-US">Function name</span>
	 *                       <span class="zh-CN">函数名称</span>
	 * @param functionParams <span class="en-US">Function parameter values</span>
	 *                       <span class="zh-CN">函数参数值</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder greaterEqual(final ConnectionCode connectionCode, final Class<?> entityClass,
	                                 final String identifyKey, final String sqlFunction,
	                                 final AbstractParameter<?>... functionParams) throws BuilderException {
		return this.greaterEqual(Globals.DEFAULT_VALUE_INT, connectionCode,
				entityClass, identifyKey, sqlFunction, functionParams);
	}

	/**
	 * <h4 class="en-US">Add a query condition less than a certain value</h4>
	 * <h4 class="zh-CN">添加小于某值的查询条件</h4>
	 *
	 * @param connectionCode <span class="en-US">Connection type code</span>
	 *                       <span class="zh-CN">连接类型代码</span>
	 * @param entityClass    <span class="en-US">Entity class</span>
	 *                       <span class="zh-CN">实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param matchValue     <span class="en-US">Match value</span>
	 *                       <span class="zh-CN">匹配值</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder less(final ConnectionCode connectionCode, final Class<?> entityClass,
	                         final String identifyKey, final Object matchValue) throws BuilderException {
		return this.less(Globals.DEFAULT_VALUE_INT, connectionCode, entityClass, identifyKey, matchValue);
	}

	/**
	 * <h4 class="en-US">Add a query condition less than a certain value</h4>
	 * <h4 class="zh-CN">添加小于某值的查询条件</h4>
	 *
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param entityClass    <span class="en-US">Data table entity class</span>
	 *                       <span class="zh-CN">数据表实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param sqlFunction    <span class="en-US">Function name</span>
	 *                       <span class="zh-CN">函数名称</span>
	 * @param functionParams <span class="en-US">Function parameter values</span>
	 *                       <span class="zh-CN">函数参数值</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder less(final ConnectionCode connectionCode, final Class<?> entityClass, final String identifyKey,
	                         final String sqlFunction, final AbstractParameter<?>... functionParams)
			throws BuilderException {
		return this.less(Globals.DEFAULT_VALUE_INT, connectionCode,
				entityClass, identifyKey, sqlFunction, functionParams);
	}

	/**
	 * <h4 class="en-US">Add a query condition less than a certain value</h4>
	 * <h4 class="zh-CN">添加小于某值的查询条件</h4>
	 *
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param entityClass    <span class="en-US">Data table entity class</span>
	 *                       <span class="zh-CN">数据表实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param subQuery       <span class="en-US">Sub-query instance object</span>
	 *                       <span class="zh-CN">子查询实例对象</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder less(final ConnectionCode connectionCode, final Class<?> entityClass,
	                         final String identifyKey, final QueryInfo subQuery) throws BuilderException {
		return this.less(Globals.DEFAULT_VALUE_INT, connectionCode, entityClass, identifyKey, subQuery);
	}

	/**
	 * <h4 class="en-US">Add a query condition less than a certain value</h4>
	 * <h4 class="zh-CN">添加小于某值的查询条件</h4>
	 *
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param entityClass    <span class="en-US">Data table entity class</span>
	 *                       <span class="zh-CN">数据表实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param matchEntity    <span class="en-US">Target data table entity class</span>
	 *                       <span class="zh-CN">目标数据表实体类</span>
	 * @param columnKey      <span class="en-US">Target data column identification name</span>
	 *                       <span class="zh-CN">目标数据列识别名称</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder less(final ConnectionCode connectionCode, final Class<?> entityClass, final String identifyKey,
	                         final Class<?> matchEntity, final String columnKey) throws BuilderException {
		return this.less(Globals.DEFAULT_VALUE_INT, connectionCode,
				entityClass, identifyKey, matchEntity, columnKey);
	}

	/**
	 * <h4 class="en-US">Add a query condition less than or equal to a certain value</h4>
	 * <h4 class="zh-CN">添加小于等于某值的查询条件</h4>
	 *
	 * @param connectionCode <span class="en-US">Connection type code</span>
	 *                       <span class="zh-CN">连接类型代码</span>
	 * @param entityClass    <span class="en-US">Entity class</span>
	 *                       <span class="zh-CN">实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param matchValue     <span class="en-US">Match value</span>
	 *                       <span class="zh-CN">匹配值</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder lessEqual(final ConnectionCode connectionCode, final Class<?> entityClass,
	                              final String identifyKey, final Object matchValue) throws BuilderException {
		return this.lessEqual(Globals.DEFAULT_VALUE_INT, connectionCode, entityClass, identifyKey, matchValue);
	}

	/**
	 * <h4 class="en-US">Add a query condition less than or equal to a certain value</h4>
	 * <h4 class="zh-CN">添加小于等于某值的查询条件</h4>
	 *
	 * @param connectionCode <span class="en-US">Connection type code</span>
	 *                       <span class="zh-CN">连接类型代码</span>
	 * @param entityClass    <span class="en-US">Entity class</span>
	 *                       <span class="zh-CN">实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param sqlFunction    <span class="en-US">Function name</span>
	 *                       <span class="zh-CN">函数名称</span>
	 * @param functionParams <span class="en-US">Function parameter values</span>
	 *                       <span class="zh-CN">函数参数值</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder lessEqual(final ConnectionCode connectionCode, final Class<?> entityClass,
	                              final String identifyKey, final String sqlFunction,
	                              final AbstractParameter<?>... functionParams) throws BuilderException {
		return this.lessEqual(Globals.DEFAULT_VALUE_INT, connectionCode,
				entityClass, identifyKey, sqlFunction, functionParams);
	}

	/**
	 * <h4 class="en-US">Add a query condition less than or equal to a certain value</h4>
	 * <h4 class="zh-CN">添加小于等于某值的查询条件</h4>
	 *
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param entityClass    <span class="en-US">Data table entity class</span>
	 *                       <span class="zh-CN">数据表实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param subQuery       <span class="en-US">Sub-query instance object</span>
	 *                       <span class="zh-CN">子查询实例对象</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder lessEqual(final ConnectionCode connectionCode, final Class<?> entityClass,
	                              final String identifyKey, final QueryInfo subQuery) throws BuilderException {
		return this.lessEqual(Globals.DEFAULT_VALUE_INT, connectionCode, entityClass, identifyKey, subQuery);
	}

	/**
	 * <h4 class="en-US">Add a query condition less than or equal to a certain value</h4>
	 * <h4 class="zh-CN">添加小于等于某值的查询条件</h4>
	 *
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param entityClass    <span class="en-US">Data table entity class</span>
	 *                       <span class="zh-CN">数据表实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param matchEntity    <span class="en-US">Target data table entity class</span>
	 *                       <span class="zh-CN">目标数据表实体类</span>
	 * @param columnKey      <span class="en-US">Target data column identification name</span>
	 *                       <span class="zh-CN">目标数据列识别名称</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder lessEqual(final ConnectionCode connectionCode, final Class<?> entityClass,
	                              final String identifyKey, final Class<?> matchEntity, final String columnKey)
			throws BuilderException {
		return this.lessEqual(Globals.DEFAULT_VALUE_INT, connectionCode,
				entityClass, identifyKey, matchEntity, columnKey);
	}

	/**
	 * <h4 class="en-US">Add a query condition equal to a certain value</h4>
	 * <h4 class="zh-CN">添加等于某值的查询条件</h4>
	 *
	 * @param connectionCode <span class="en-US">Connection type code</span>
	 *                       <span class="zh-CN">连接类型代码</span>
	 * @param entityClass    <span class="en-US">Entity class</span>
	 *                       <span class="zh-CN">实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param matchValue     <span class="en-US">Match value</span>
	 *                       <span class="zh-CN">匹配值</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder equalTo(final ConnectionCode connectionCode, final Class<?> entityClass,
	                            final String identifyKey, final Object matchValue) throws BuilderException {
		return this.equalTo(Globals.DEFAULT_VALUE_INT, connectionCode, entityClass, identifyKey, matchValue);
	}

	/**
	 * <h4 class="en-US">Add a query condition equal to a certain value</h4>
	 * <h4 class="zh-CN">添加等于某值的查询条件</h4>
	 *
	 * @param connectionCode <span class="en-US">Connection type code</span>
	 *                       <span class="zh-CN">连接类型代码</span>
	 * @param entityClass    <span class="en-US">Entity class</span>
	 *                       <span class="zh-CN">实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param sqlFunction    <span class="en-US">Function name</span>
	 *                       <span class="zh-CN">函数名称</span>
	 * @param functionParams <span class="en-US">Function parameter values</span>
	 *                       <span class="zh-CN">函数参数值</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder equalTo(final ConnectionCode connectionCode, final Class<?> entityClass,
	                            final String identifyKey, final String sqlFunction,
	                            final AbstractParameter<?>... functionParams) throws BuilderException {
		return this.equalTo(Globals.DEFAULT_VALUE_INT, connectionCode,
				entityClass, identifyKey, sqlFunction, functionParams);
	}

	/**
	 * <h4 class="en-US">Add a query condition equal to a certain value</h4>
	 * <h4 class="zh-CN">添加等于某值的查询条件</h4>
	 *
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param entityClass    <span class="en-US">Data table entity class</span>
	 *                       <span class="zh-CN">数据表实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param subQuery       <span class="en-US">Sub-query instance object</span>
	 *                       <span class="zh-CN">子查询实例对象</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder equalTo(final ConnectionCode connectionCode, final Class<?> entityClass,
	                            final String identifyKey, final QueryInfo subQuery) throws BuilderException {
		return this.equalTo(Globals.DEFAULT_VALUE_INT, connectionCode, entityClass, identifyKey, subQuery);
	}

	/**
	 * <h4 class="en-US">Add a query condition equal to a certain value</h4>
	 * <h4 class="zh-CN">添加等于某值的查询条件</h4>
	 *
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param entityClass    <span class="en-US">Data table entity class</span>
	 *                       <span class="zh-CN">数据表实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param matchEntity    <span class="en-US">Target data table entity class</span>
	 *                       <span class="zh-CN">目标数据表实体类</span>
	 * @param columnKey      <span class="en-US">Target data column identification name</span>
	 *                       <span class="zh-CN">目标数据列识别名称</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder equalTo(final ConnectionCode connectionCode, final Class<?> entityClass,
	                            final String identifyKey, final Class<?> matchEntity, final String columnKey)
			throws BuilderException {
		return this.equalTo(Globals.DEFAULT_VALUE_INT, connectionCode,
				entityClass, identifyKey, matchEntity, columnKey);
	}

	/**
	 * <h4 class="en-US">Add a query condition not equal to a certain value</h4>
	 * <h4 class="zh-CN">添加不等于某值的查询条件</h4>
	 *
	 * @param connectionCode <span class="en-US">Connection type code</span>
	 *                       <span class="zh-CN">连接类型代码</span>
	 * @param entityClass    <span class="en-US">Entity class</span>
	 *                       <span class="zh-CN">实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param matchValue     <span class="en-US">Match value</span>
	 *                       <span class="zh-CN">匹配值</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder notEqual(final ConnectionCode connectionCode, final Class<?> entityClass,
	                             final String identifyKey, final Object matchValue) throws BuilderException {
		return this.notEqual(Globals.DEFAULT_VALUE_INT, connectionCode, entityClass, identifyKey, matchValue);
	}

	/**
	 * <h4 class="en-US">Add a query condition not equal to a certain value</h4>
	 * <h4 class="zh-CN">添加不等于某值的查询条件</h4>
	 *
	 * @param connectionCode <span class="en-US">Connection type code</span>
	 *                       <span class="zh-CN">连接类型代码</span>
	 * @param entityClass    <span class="en-US">Entity class</span>
	 *                       <span class="zh-CN">实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param sqlFunction    <span class="en-US">Function name</span>
	 *                       <span class="zh-CN">函数名称</span>
	 * @param functionParams <span class="en-US">Function parameter values</span>
	 *                       <span class="zh-CN">函数参数值</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder notEqual(final ConnectionCode connectionCode, final Class<?> entityClass,
	                             final String identifyKey, final String sqlFunction,
	                             final AbstractParameter<?>... functionParams) throws BuilderException {
		return this.notEqual(Globals.DEFAULT_VALUE_INT, connectionCode,
				entityClass, identifyKey, sqlFunction, functionParams);
	}

	/**
	 * <h4 class="en-US">Add a query condition not equal to a certain value</h4>
	 * <h4 class="zh-CN">添加不等于某值的查询条件</h4>
	 *
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param entityClass    <span class="en-US">Data table entity class</span>
	 *                       <span class="zh-CN">数据表实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param subQuery       <span class="en-US">Sub-query instance object</span>
	 *                       <span class="zh-CN">子查询实例对象</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder notEqual(final ConnectionCode connectionCode, final Class<?> entityClass,
	                             final String identifyKey, final QueryInfo subQuery) throws BuilderException {
		return this.notEqual(Globals.DEFAULT_VALUE_INT, connectionCode, entityClass, identifyKey, subQuery);
	}

	/**
	 * <h4 class="en-US">Add a query condition not equal to a certain value</h4>
	 * <h4 class="zh-CN">添加不等于某值的查询条件</h4>
	 *
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param entityClass    <span class="en-US">Data table entity class</span>
	 *                       <span class="zh-CN">数据表实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param matchEntity    <span class="en-US">Target data table entity class</span>
	 *                       <span class="zh-CN">目标数据表实体类</span>
	 * @param columnKey      <span class="en-US">Target data column identification name</span>
	 *                       <span class="zh-CN">目标数据列识别名称</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder notEqual(final ConnectionCode connectionCode, final Class<?> entityClass,
	                             final String identifyKey, final Class<?> matchEntity, final String columnKey)
			throws BuilderException {
		return this.notEqual(Globals.DEFAULT_VALUE_INT, connectionCode,
				entityClass, identifyKey, matchEntity, columnKey);
	}

	/**
	 * <h4 class="en-US">Add a query condition between certain two values</h4>
	 * <h4 class="zh-CN">添加介于某两个值之间的查询条件</h4>
	 *
	 * @param connectionCode <span class="en-US">Connection type code</span>
	 *                       <span class="zh-CN">连接类型代码</span>
	 * @param entityClass    <span class="en-US">Entity class</span>
	 *                       <span class="zh-CN">实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param beginValue     <span class="en-US">Interval starting value</span>
	 *                       <span class="zh-CN">区间起始值</span>
	 * @param endValue       <span class="en-US">Interval end value</span>
	 *                       <span class="zh-CN">区间终止值</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder betweenAnd(final ConnectionCode connectionCode, final Class<?> entityClass,
	                               final String identifyKey, final Object beginValue, final Object endValue)
			throws BuilderException {
		return this.betweenAnd(Globals.DEFAULT_VALUE_INT, connectionCode,
				entityClass, identifyKey, beginValue, endValue);
	}

	/**
	 * <h4 class="en-US">Add a query condition not between certain two values</h4>
	 * <h4 class="zh-CN">添加不介于某两个值之间的查询条件</h4>
	 *
	 * @param connectionCode <span class="en-US">Connection type code</span>
	 *                       <span class="zh-CN">连接类型代码</span>
	 * @param entityClass    <span class="en-US">Entity class</span>
	 *                       <span class="zh-CN">实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param beginValue     <span class="en-US">Interval starting value</span>
	 *                       <span class="zh-CN">区间起始值</span>
	 * @param endValue       <span class="en-US">Interval end value</span>
	 *                       <span class="zh-CN">区间终止值</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder notBetweenAnd(final ConnectionCode connectionCode, final Class<?> entityClass,
	                                  final String identifyKey, final Object beginValue, final Object endValue)
			throws BuilderException {
		return this.notBetweenAnd(Globals.DEFAULT_VALUE_INT, connectionCode,
				entityClass, identifyKey, beginValue, endValue);
	}

	/**
	 * <h4 class="en-US">Add query conditions for fuzzy matching values</h4>
	 * <h4 class="zh-CN">添加模糊匹配值的查询条件</h4>
	 *
	 * @param connectionCode <span class="en-US">Connection type code</span>
	 *                       <span class="zh-CN">连接类型代码</span>
	 * @param entityClass    <span class="en-US">Entity class</span>
	 *                       <span class="zh-CN">实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param matchRule      <span class="en-US">match rule string</span>
	 *                       <span class="zh-CN">匹配规则字符串</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder like(final ConnectionCode connectionCode, final Class<?> entityClass,
	                         final String identifyKey, final String matchRule) throws BuilderException {
		return this.like(Globals.DEFAULT_VALUE_INT, connectionCode, entityClass, identifyKey, matchRule);
	}

	/**
	 * <h4 class="en-US">Add query conditions for not fuzzy matching values</h4>
	 * <h4 class="zh-CN">添加非模糊匹配值的查询条件</h4>
	 *
	 * @param connectionCode <span class="en-US">Connection type code</span>
	 *                       <span class="zh-CN">连接类型代码</span>
	 * @param entityClass    <span class="en-US">Entity class</span>
	 *                       <span class="zh-CN">实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param matchRule      <span class="en-US">match rule string</span>
	 *                       <span class="zh-CN">匹配规则字符串</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder notLike(final ConnectionCode connectionCode, final Class<?> entityClass,
	                            final String identifyKey, final String matchRule) throws BuilderException {
		return this.notLike(Globals.DEFAULT_VALUE_INT, connectionCode, entityClass, identifyKey, matchRule);
	}

	/**
	 * <h4 class="en-US">Add query condition with null value</h4>
	 * <h4 class="zh-CN">添加空值的查询条件</h4>
	 *
	 * @param connectionCode <span class="en-US">Connection type code</span>
	 *                       <span class="zh-CN">连接类型代码</span>
	 * @param entityClass    <span class="en-US">Entity class</span>
	 *                       <span class="zh-CN">实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder matchNull(final ConnectionCode connectionCode, final Class<?> entityClass,
	                              final String identifyKey) throws BuilderException {
		return this.matchNull(Globals.DEFAULT_VALUE_INT, connectionCode, entityClass, identifyKey);
	}

	/**
	 * <h4 class="en-US">Add query condition with not null value</h4>
	 * <h4 class="zh-CN">添加非空值的查询条件</h4>
	 *
	 * @param connectionCode <span class="en-US">Connection type code</span>
	 *                       <span class="zh-CN">连接类型代码</span>
	 * @param entityClass    <span class="en-US">Entity class</span>
	 *                       <span class="zh-CN">实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder notNull(final ConnectionCode connectionCode, final Class<?> entityClass,
	                            final String identifyKey) throws BuilderException {
		return this.notNull(Globals.DEFAULT_VALUE_INT, connectionCode, entityClass, identifyKey);
	}

	/**
	 * <h4 class="en-US">Adds a query condition where the value is contained in the given data</h4>
	 * <h4 class="zh-CN">添加值包含在给定数据中的查询条件</h4>
	 *
	 * @param connectionCode <span class="en-US">Connection type code</span>
	 *                       <span class="zh-CN">连接类型代码</span>
	 * @param entityClass    <span class="en-US">Entity class</span>
	 *                       <span class="zh-CN">实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param matchObjects   <span class="en-US">array of matching datasets</span>
	 *                       <span class="zh-CN">匹配数据集数组</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder in(final ConnectionCode connectionCode, final Class<?> entityClass,
	                       final String identifyKey, final Object... matchObjects) throws BuilderException {
		return this.in(Globals.DEFAULT_VALUE_INT, connectionCode, entityClass, identifyKey, matchObjects);
	}

	/**
	 * <h4 class="en-US">Adds a query condition where the value is contained in the given data</h4>
	 * <h4 class="zh-CN">添加值包含在给定数据中的查询条件</h4>
	 *
	 * @param connectionCode <span class="en-US">Connection type code</span>
	 *                       <span class="zh-CN">连接类型代码</span>
	 * @param entityClass    <span class="en-US">Entity class</span>
	 *                       <span class="zh-CN">实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param subQuery       <span class="en-US">Sub-query instance object</span>
	 *                       <span class="zh-CN">子查询实例对象</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder in(final ConnectionCode connectionCode, final Class<?> entityClass,
	                       final String identifyKey, final QueryInfo subQuery) throws BuilderException {
		return this.in(Globals.DEFAULT_VALUE_INT, connectionCode, entityClass, identifyKey, subQuery);
	}

	/**
	 * <h4 class="en-US">Adds a query condition where the value is contained in the given data</h4>
	 * <h4 class="zh-CN">添加值包含在给定数据中的查询条件</h4>
	 *
	 * @param connectionCode <span class="en-US">Connection type code</span>
	 *                       <span class="zh-CN">连接类型代码</span>
	 * @param entityClass    <span class="en-US">Entity class</span>
	 *                       <span class="zh-CN">实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param queryCode      <span class="en-US">Query code</span>
	 *                       <span class="zh-CN">查询代码</span>
	 * @param resultKey      <span class="en-US">Result identify key</span>
	 *                       <span class="zh-CN">结果集识别代码</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder in(final ConnectionCode connectionCode, final Class<?> entityClass, final String identifyKey,
	                       final long queryCode, final String resultKey) throws BuilderException {
		return this.in(Globals.DEFAULT_VALUE_INT, connectionCode, entityClass, identifyKey, queryCode, resultKey);
	}

	/**
	 * <h4 class="en-US">Adds a query condition where the value is not contained in the given data</h4>
	 * <h4 class="zh-CN">添加值没有包含在给定数据中的查询条件</h4>
	 *
	 * @param connectionCode <span class="en-US">Connection type code</span>
	 *                       <span class="zh-CN">连接类型代码</span>
	 * @param entityClass    <span class="en-US">Entity class</span>
	 *                       <span class="zh-CN">实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param matchObjects   <span class="en-US">array of matching datasets</span>
	 *                       <span class="zh-CN">匹配数据集数组</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder notIn(final ConnectionCode connectionCode, final Class<?> entityClass,
	                          final String identifyKey, final Object... matchObjects) throws BuilderException {
		return this.notIn(Globals.DEFAULT_VALUE_INT, connectionCode, entityClass, identifyKey, matchObjects);
	}

	/**
	 * <h4 class="en-US">Adds a query condition where the value is not contained in the given data</h4>
	 * <h4 class="zh-CN">添加值没有包含在给定数据中的查询条件</h4>
	 *
	 * @param connectionCode <span class="en-US">Connection type code</span>
	 *                       <span class="zh-CN">连接类型代码</span>
	 * @param entityClass    <span class="en-US">Entity class</span>
	 *                       <span class="zh-CN">实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param subQuery       <span class="en-US">Sub-query instance object</span>
	 *                       <span class="zh-CN">子查询实例对象</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder notIn(final ConnectionCode connectionCode, final Class<?> entityClass,
	                          final String identifyKey, final QueryInfo subQuery) throws BuilderException {
		return this.notIn(Globals.DEFAULT_VALUE_INT, connectionCode, entityClass, identifyKey, subQuery);
	}

	/**
	 * <h4 class="en-US">Adds a query condition where the value is not contained in the given data</h4>
	 * <h4 class="zh-CN">添加值没有包含在给定数据中的查询条件</h4>
	 *
	 * @param connectionCode <span class="en-US">Connection type code</span>
	 *                       <span class="zh-CN">连接类型代码</span>
	 * @param entityClass    <span class="en-US">Entity class</span>
	 *                       <span class="zh-CN">实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param queryCode      <span class="en-US">Query code</span>
	 *                       <span class="zh-CN">查询代码</span>
	 * @param resultKey      <span class="en-US">Result identify key</span>
	 *                       <span class="zh-CN">结果集识别代码</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder notIn(final ConnectionCode connectionCode, final Class<?> entityClass,
	                          final String identifyKey, final long queryCode, final String resultKey)
			throws BuilderException {
		return this.notIn(Globals.DEFAULT_VALUE_INT, connectionCode, entityClass, identifyKey, queryCode, resultKey);
	}

	/**
	 * <h4 class="en-US">Add query conditions for condition groups</h4>
	 * <h4 class="zh-CN">添加条件组的查询条件</h4>
	 *
	 * @param connectionCode <span class="en-US">Connection type code</span>
	 *                       <span class="zh-CN">连接类型代码</span>
	 * @param Conditions     <span class="en-US">Query conditions array</span>
	 *                       <span class="zh-CN">查询条件数组</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public QueryBuilder group(final ConnectionCode connectionCode, final Condition... Conditions) {
		return this.group(Globals.DEFAULT_VALUE_INT, connectionCode, Conditions);
	}

	/**
	 * <h4 class="en-US">Add a query condition greater than a certain value</h4>
	 * <h4 class="zh-CN">添加大于某值的查询条件</h4>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Connection type code</span>
	 *                       <span class="zh-CN">连接类型代码</span>
	 * @param entityClass    <span class="en-US">Entity class</span>
	 *                       <span class="zh-CN">实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param matchValue     <span class="en-US">Match value</span>
	 *                       <span class="zh-CN">匹配值</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder greater(final int sortCode, final ConnectionCode connectionCode,
	                            final Class<?> entityClass, final String identifyKey,
	                            final Object matchValue) throws BuilderException {
		this.addCondition(Condition.greater(sortCode, connectionCode, entityClass, identifyKey, matchValue));
		return this;
	}

	/**
	 * <h4 class="en-US">Add a query condition greater than a certain value</h4>
	 * <h4 class="zh-CN">添加大于某值的查询条件</h4>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param entityClass    <span class="en-US">Data table entity class</span>
	 *                       <span class="zh-CN">数据表实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param matchEntity    <span class="en-US">Target data table entity class</span>
	 *                       <span class="zh-CN">目标数据表实体类</span>
	 * @param columnKey      <span class="en-US">Target data column identification name</span>
	 *                       <span class="zh-CN">目标数据列识别名称</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder greater(final int sortCode, final ConnectionCode connectionCode,
	                            final Class<?> entityClass, final String identifyKey,
	                            final Class<?> matchEntity, final String columnKey) throws BuilderException {
		this.addCondition(Condition.greater(sortCode, connectionCode, entityClass, identifyKey, matchEntity, columnKey));
		return this;
	}

	/**
	 * <h4 class="en-US">Add a query condition greater than a certain value</h4>
	 * <h4 class="zh-CN">添加大于某值的查询条件</h4>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param entityClass    <span class="en-US">Data table entity class</span>
	 *                       <span class="zh-CN">数据表实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param sqlFunction    <span class="en-US">Function name</span>
	 *                       <span class="zh-CN">函数名称</span>
	 * @param functionParams <span class="en-US">Function parameter values</span>
	 *                       <span class="zh-CN">函数参数值</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder greater(final int sortCode, final ConnectionCode connectionCode,
	                            final Class<?> entityClass, final String identifyKey,
	                            final String sqlFunction, final AbstractParameter<?>... functionParams)
			throws BuilderException {
		this.addCondition(Condition.greater(sortCode, connectionCode, entityClass, identifyKey, sqlFunction, functionParams));
		return this;
	}

	/**
	 * <h4 class="en-US">Add a query condition greater than a certain value</h4>
	 * <h4 class="zh-CN">添加大于某值的查询条件</h4>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param entityClass    <span class="en-US">Data table entity class</span>
	 *                       <span class="zh-CN">数据表实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param subQuery       <span class="en-US">Sub-query instance object</span>
	 *                       <span class="zh-CN">子查询实例对象</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder greater(final int sortCode, final ConnectionCode connectionCode, final Class<?> entityClass,
	                            final String identifyKey, final QueryInfo subQuery) throws BuilderException {
		this.addCondition(Condition.greater(sortCode, connectionCode, entityClass, identifyKey, subQuery));
		return this;
	}

	/**
	 * <h4 class="en-US">Add a query condition greater than or equal to a certain value</h4>
	 * <h4 class="zh-CN">添加大于等于某值的查询条件</h4>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Connection type code</span>
	 *                       <span class="zh-CN">连接类型代码</span>
	 * @param entityClass    <span class="en-US">Entity class</span>
	 *                       <span class="zh-CN">实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param matchValue     <span class="en-US">Match value</span>
	 *                       <span class="zh-CN">匹配值</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder greaterEqual(final int sortCode, final ConnectionCode connectionCode,
	                                 final Class<?> entityClass, final String identifyKey,
	                                 final Object matchValue) throws BuilderException {
		this.addCondition(Condition.greaterEqual(sortCode, connectionCode, entityClass, identifyKey, matchValue));
		return this;
	}

	/**
	 * <h4 class="en-US">Add a query condition greater than or equal to a certain value</h4>
	 * <h4 class="zh-CN">添加大于等于某值的查询条件</h4>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Connection type code</span>
	 *                       <span class="zh-CN">连接类型代码</span>
	 * @param entityClass    <span class="en-US">Entity class</span>
	 *                       <span class="zh-CN">实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param sqlFunction    <span class="en-US">Function name</span>
	 *                       <span class="zh-CN">函数名称</span>
	 * @param functionParams <span class="en-US">Function parameter values</span>
	 *                       <span class="zh-CN">函数参数值</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder greaterEqual(final int sortCode, final ConnectionCode connectionCode,
	                                 final Class<?> entityClass, final String identifyKey,
	                                 final String sqlFunction, final AbstractParameter<?>... functionParams)
			throws BuilderException {
		this.addCondition(
				Condition.greaterEqual(sortCode, connectionCode, entityClass, identifyKey, sqlFunction, functionParams));
		return this;
	}

	/**
	 * <h4 class="en-US">Add a query condition greater than or equal to a certain value</h4>
	 * <h4 class="zh-CN">添加大于等于某值的查询条件</h4>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param entityClass    <span class="en-US">Data table entity class</span>
	 *                       <span class="zh-CN">数据表实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param matchEntity    <span class="en-US">Target data table entity class</span>
	 *                       <span class="zh-CN">目标数据表实体类</span>
	 * @param columnKey      <span class="en-US">Target data column identification name</span>
	 *                       <span class="zh-CN">目标数据列识别名称</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder greaterEqual(final int sortCode, final ConnectionCode connectionCode,
	                                 final Class<?> entityClass, final String identifyKey,
	                                 final Class<?> matchEntity, final String columnKey) throws BuilderException {
		this.addCondition(
				Condition.greaterEqual(sortCode, connectionCode, entityClass, identifyKey, matchEntity, columnKey));
		return this;
	}

	/**
	 * <h4 class="en-US">Add a query condition greater than or equal to a certain value</h4>
	 * <h4 class="zh-CN">添加大于等于某值的查询条件</h4>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param entityClass    <span class="en-US">Data table entity class</span>
	 *                       <span class="zh-CN">数据表实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param subQuery       <span class="en-US">Sub-query instance object</span>
	 *                       <span class="zh-CN">子查询实例对象</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder greaterEqual(final int sortCode, final ConnectionCode connectionCode,
	                                 final Class<?> entityClass, final String identifyKey,
	                                 final QueryInfo subQuery) throws BuilderException {
		this.addCondition(Condition.greaterEqual(sortCode, connectionCode, entityClass, identifyKey, subQuery));
		return this;
	}

	/**
	 * <h4 class="en-US">Add a query condition less than a certain value</h4>
	 * <h4 class="zh-CN">添加小于某值的查询条件</h4>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Connection type code</span>
	 *                       <span class="zh-CN">连接类型代码</span>
	 * @param entityClass    <span class="en-US">Entity class</span>
	 *                       <span class="zh-CN">实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param matchValue     <span class="en-US">Match value</span>
	 *                       <span class="zh-CN">匹配值</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder less(final int sortCode, final ConnectionCode connectionCode,
	                         final Class<?> entityClass, final String identifyKey, final Object matchValue)
			throws BuilderException {
		this.addCondition(Condition.less(sortCode, connectionCode, entityClass, identifyKey, matchValue));
		return this;
	}

	/**
	 * <h4 class="en-US">Add a query condition less than a certain value</h4>
	 * <h4 class="zh-CN">添加小于某值的查询条件</h4>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param entityClass    <span class="en-US">Data table entity class</span>
	 *                       <span class="zh-CN">数据表实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param matchEntity    <span class="en-US">Target data table entity class</span>
	 *                       <span class="zh-CN">目标数据表实体类</span>
	 * @param columnKey      <span class="en-US">Target data column identification name</span>
	 *                       <span class="zh-CN">目标数据列识别名称</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder less(final int sortCode, final ConnectionCode connectionCode,
	                         final Class<?> entityClass, final String identifyKey,
	                         final Class<?> matchEntity, final String columnKey) throws BuilderException {
		this.addCondition(Condition.less(sortCode, connectionCode, entityClass, identifyKey, matchEntity, columnKey));
		return this;
	}

	/**
	 * <h4 class="en-US">Add a query condition less than a certain value</h4>
	 * <h4 class="zh-CN">添加小于某值的查询条件</h4>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param entityClass    <span class="en-US">Data table entity class</span>
	 *                       <span class="zh-CN">数据表实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param sqlFunction    <span class="en-US">Function name</span>
	 *                       <span class="zh-CN">函数名称</span>
	 * @param functionParams <span class="en-US">Function parameter values</span>
	 *                       <span class="zh-CN">函数参数值</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder less(final int sortCode, final ConnectionCode connectionCode,
	                         final Class<?> entityClass, final String identifyKey,
	                         final String sqlFunction, final AbstractParameter<?>... functionParams)
			throws BuilderException {
		this.addCondition(Condition.less(sortCode, connectionCode, entityClass, identifyKey, sqlFunction, functionParams));
		return this;
	}

	/**
	 * <h4 class="en-US">Add a query condition less than a certain value</h4>
	 * <h4 class="zh-CN">添加小于某值的查询条件</h4>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param entityClass    <span class="en-US">Data table entity class</span>
	 *                       <span class="zh-CN">数据表实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param subQuery       <span class="en-US">Sub-query instance object</span>
	 *                       <span class="zh-CN">子查询实例对象</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder less(final int sortCode, final ConnectionCode connectionCode,
	                         final Class<?> entityClass, final String identifyKey,
	                         final QueryInfo subQuery) throws BuilderException {
		this.addCondition(Condition.less(sortCode, connectionCode, entityClass, identifyKey, subQuery));
		return this;
	}

	/**
	 * <h4 class="en-US">Add a query condition less than or equal to a certain value</h4>
	 * <h4 class="zh-CN">添加小于等于某值的查询条件</h4>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Connection type code</span>
	 *                       <span class="zh-CN">连接类型代码</span>
	 * @param entityClass    <span class="en-US">Entity class</span>
	 *                       <span class="zh-CN">实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param matchValue     <span class="en-US">Match value</span>
	 *                       <span class="zh-CN">匹配值</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder lessEqual(final int sortCode, final ConnectionCode connectionCode,
	                              final Class<?> entityClass, final String identifyKey,
	                              final Object matchValue) throws BuilderException {
		this.addCondition(Condition.lessEqual(sortCode, connectionCode, entityClass, identifyKey, matchValue));
		return this;
	}

	/**
	 * <h4 class="en-US">Add a query condition less than or equal to a certain value</h4>
	 * <h4 class="zh-CN">添加小于等于某值的查询条件</h4>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param entityClass    <span class="en-US">Data table entity class</span>
	 *                       <span class="zh-CN">数据表实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param matchEntity    <span class="en-US">Target data table entity class</span>
	 *                       <span class="zh-CN">目标数据表实体类</span>
	 * @param columnKey      <span class="en-US">Target data column identification name</span>
	 *                       <span class="zh-CN">目标数据列识别名称</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder lessEqual(final int sortCode, final ConnectionCode connectionCode,
	                              final Class<?> entityClass, final String identifyKey,
	                              final Class<?> matchEntity, final String columnKey) throws BuilderException {
		this.addCondition(Condition.lessEqual(sortCode, connectionCode, entityClass, identifyKey, matchEntity, columnKey));
		return this;
	}

	/**
	 * <h4 class="en-US">Add a query condition less than or equal to a certain value</h4>
	 * <h4 class="zh-CN">添加小于等于某值的查询条件</h4>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param entityClass    <span class="en-US">Data table entity class</span>
	 *                       <span class="zh-CN">数据表实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param sqlFunction    <span class="en-US">Function name</span>
	 *                       <span class="zh-CN">函数名称</span>
	 * @param functionParams <span class="en-US">Function parameter values</span>
	 *                       <span class="zh-CN">函数参数值</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder lessEqual(final int sortCode, final ConnectionCode connectionCode,
	                              final Class<?> entityClass, final String identifyKey,
	                              final String sqlFunction, final AbstractParameter<?>... functionParams)
			throws BuilderException {
		this.addCondition(
				Condition.lessEqual(sortCode, connectionCode, entityClass, identifyKey, sqlFunction, functionParams));
		return this;
	}

	/**
	 * <h4 class="en-US">Add a query condition less than or equal to a certain value</h4>
	 * <h4 class="zh-CN">添加小于等于某值的查询条件</h4>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param entityClass    <span class="en-US">Data table entity class</span>
	 *                       <span class="zh-CN">数据表实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param subQuery       <span class="en-US">Sub-query instance object</span>
	 *                       <span class="zh-CN">子查询实例对象</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder lessEqual(final int sortCode, final ConnectionCode connectionCode,
	                              final Class<?> entityClass, final String identifyKey,
	                              final QueryInfo subQuery) throws BuilderException {
		this.addCondition(Condition.lessEqual(sortCode, connectionCode, entityClass, identifyKey, subQuery));
		return this;
	}

	/**
	 * <h4 class="en-US">Add a query condition equal to a certain value</h4>
	 * <h4 class="zh-CN">添加等于某值的查询条件</h4>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Connection type code</span>
	 *                       <span class="zh-CN">连接类型代码</span>
	 * @param entityClass    <span class="en-US">Entity class</span>
	 *                       <span class="zh-CN">实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param matchValue     <span class="en-US">Match value</span>
	 *                       <span class="zh-CN">匹配值</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder equalTo(final int sortCode, final ConnectionCode connectionCode,
	                            final Class<?> entityClass, final String identifyKey, final Object matchValue)
			throws BuilderException {
		this.addCondition(Condition.equalTo(sortCode, connectionCode, entityClass, identifyKey, matchValue));
		return this;
	}

	/**
	 * <h4 class="en-US">Add a query condition equal to a certain value</h4>
	 * <h4 class="zh-CN">添加等于某值的查询条件</h4>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param entityClass    <span class="en-US">Data table entity class</span>
	 *                       <span class="zh-CN">数据表实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param matchEntity    <span class="en-US">Target data table entity class</span>
	 *                       <span class="zh-CN">目标数据表实体类</span>
	 * @param columnKey      <span class="en-US">Target data column identification name</span>
	 *                       <span class="zh-CN">目标数据列识别名称</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder equalTo(final int sortCode, final ConnectionCode connectionCode,
	                            final Class<?> entityClass, final String identifyKey,
	                            final Class<?> matchEntity, final String columnKey) throws BuilderException {
		this.addCondition(Condition.equalTo(sortCode, connectionCode, entityClass, identifyKey, matchEntity, columnKey));
		return this;
	}

	/**
	 * <h4 class="en-US">Add a query condition equal to a certain value</h4>
	 * <h4 class="zh-CN">添加等于某值的查询条件</h4>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param entityClass    <span class="en-US">Data table entity class</span>
	 *                       <span class="zh-CN">数据表实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param sqlFunction    <span class="en-US">Function name</span>
	 *                       <span class="zh-CN">函数名称</span>
	 * @param functionParams <span class="en-US">Function parameter values</span>
	 *                       <span class="zh-CN">函数参数值</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder equalTo(final int sortCode, final ConnectionCode connectionCode,
	                            final Class<?> entityClass, final String identifyKey,
	                            final String sqlFunction, final AbstractParameter<?>... functionParams)
			throws BuilderException {
		this.addCondition(
				Condition.equalTo(sortCode, connectionCode, entityClass, identifyKey, sqlFunction, functionParams));
		return this;
	}

	/**
	 * <h4 class="en-US">Add a query condition equal to a certain value</h4>
	 * <h4 class="zh-CN">添加等于某值的查询条件</h4>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param entityClass    <span class="en-US">Data table entity class</span>
	 *                       <span class="zh-CN">数据表实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param subQuery       <span class="en-US">Sub-query instance object</span>
	 *                       <span class="zh-CN">子查询实例对象</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder equalTo(final int sortCode, final ConnectionCode connectionCode,
	                            final Class<?> entityClass, final String identifyKey,
	                            final QueryInfo subQuery) throws BuilderException {
		this.addCondition(Condition.equalTo(sortCode, connectionCode, entityClass, identifyKey, subQuery));
		return this;
	}

	/**
	 * <h4 class="en-US">Add a query condition not equal to a certain value</h4>
	 * <h4 class="zh-CN">添加不等于某值的查询条件</h4>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Connection type code</span>
	 *                       <span class="zh-CN">连接类型代码</span>
	 * @param entityClass    <span class="en-US">Entity class</span>
	 *                       <span class="zh-CN">实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param matchValue     <span class="en-US">Match value</span>
	 *                       <span class="zh-CN">匹配值</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder notEqual(final int sortCode, final ConnectionCode connectionCode,
	                             final Class<?> entityClass, final String identifyKey,
	                             final Object matchValue) throws BuilderException {
		this.addCondition(Condition.notEqual(sortCode, connectionCode, entityClass, identifyKey, matchValue));
		return this;
	}

	/**
	 * <h4 class="en-US">Add a query condition not equal to a certain value</h4>
	 * <h4 class="zh-CN">添加不等于某值的查询条件</h4>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param entityClass    <span class="en-US">Data table entity class</span>
	 *                       <span class="zh-CN">数据表实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param matchEntity    <span class="en-US">Target data table entity class</span>
	 *                       <span class="zh-CN">目标数据表实体类</span>
	 * @param columnKey      <span class="en-US">Target data column identification name</span>
	 *                       <span class="zh-CN">目标数据列识别名称</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder notEqual(final int sortCode, final ConnectionCode connectionCode,
	                             final Class<?> entityClass, final String identifyKey,
	                             final Class<?> matchEntity, final String columnKey) throws BuilderException {
		this.addCondition(Condition.notEqual(sortCode, connectionCode, entityClass, identifyKey, matchEntity, columnKey));
		return this;
	}

	/**
	 * <h4 class="en-US">Add a query condition not equal to a certain value</h4>
	 * <h4 class="zh-CN">添加不等于某值的查询条件</h4>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param entityClass    <span class="en-US">Data table entity class</span>
	 *                       <span class="zh-CN">数据表实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param sqlFunction    <span class="en-US">Function name</span>
	 *                       <span class="zh-CN">函数名称</span>
	 * @param functionParams <span class="en-US">Function parameter values</span>
	 *                       <span class="zh-CN">函数参数值</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder notEqual(final int sortCode, final ConnectionCode connectionCode,
	                             final Class<?> entityClass, final String identifyKey,
	                             final String sqlFunction, final AbstractParameter<?>... functionParams)
			throws BuilderException {
		this.addCondition(Condition.notEqual(sortCode, connectionCode, entityClass, identifyKey, sqlFunction, functionParams));
		return this;
	}

	/**
	 * <h4 class="en-US">Add a query condition not equal to a certain value</h4>
	 * <h4 class="zh-CN">添加不等于某值的查询条件</h4>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param entityClass    <span class="en-US">Data table entity class</span>
	 *                       <span class="zh-CN">数据表实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param subQuery       <span class="en-US">Sub-query instance object</span>
	 *                       <span class="zh-CN">子查询实例对象</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder notEqual(final int sortCode, final ConnectionCode connectionCode,
	                             final Class<?> entityClass, final String identifyKey,
	                             final QueryInfo subQuery) throws BuilderException {
		this.addCondition(Condition.notEqual(sortCode, connectionCode, entityClass, identifyKey, subQuery));
		return this;
	}

	/**
	 * <h4 class="en-US">Add a query condition between certain two values</h4>
	 * <h4 class="zh-CN">添加介于某两个值之间的查询条件</h4>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Connection type code</span>
	 *                       <span class="zh-CN">连接类型代码</span>
	 * @param entityClass    <span class="en-US">Entity class</span>
	 *                       <span class="zh-CN">实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param beginValue     <span class="en-US">Interval starting value</span>
	 *                       <span class="zh-CN">区间起始值</span>
	 * @param endValue       <span class="en-US">Interval end value</span>
	 *                       <span class="zh-CN">区间终止值</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder betweenAnd(final int sortCode, final ConnectionCode connectionCode,
	                               final Class<?> entityClass, final String identifyKey,
	                               final Object beginValue, final Object endValue) throws BuilderException {
		this.addCondition(Condition.inRanges(sortCode, connectionCode, entityClass, identifyKey, beginValue, endValue));
		return this;
	}

	/**
	 * <h4 class="en-US">Add a query condition not between certain two values</h4>
	 * <h4 class="zh-CN">添加不介于某两个值之间的查询条件</h4>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Connection type code</span>
	 *                       <span class="zh-CN">连接类型代码</span>
	 * @param entityClass    <span class="en-US">Entity class</span>
	 *                       <span class="zh-CN">实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param beginValue     <span class="en-US">Interval starting value</span>
	 *                       <span class="zh-CN">区间起始值</span>
	 * @param endValue       <span class="en-US">Interval end value</span>
	 *                       <span class="zh-CN">区间终止值</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder notBetweenAnd(final int sortCode, final ConnectionCode connectionCode,
	                                  final Class<?> entityClass, final String identifyKey,
	                                  final Object beginValue, final Object endValue) throws BuilderException {
		this.addCondition(Condition.notInRanges(sortCode, connectionCode, entityClass, identifyKey, beginValue, endValue));
		return this;
	}

	/**
	 * <h4 class="en-US">Add query conditions for fuzzy matching values</h4>
	 * <h4 class="zh-CN">添加模糊匹配值的查询条件</h4>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Connection type code</span>
	 *                       <span class="zh-CN">连接类型代码</span>
	 * @param entityClass    <span class="en-US">Entity class</span>
	 *                       <span class="zh-CN">实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param matchRule      <span class="en-US">match rule string</span>
	 *                       <span class="zh-CN">匹配规则字符串</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder like(final int sortCode, final ConnectionCode connectionCode,
	                         final Class<?> entityClass, final String identifyKey, final String matchRule)
			throws BuilderException {
		this.addCondition(Condition.like(sortCode, connectionCode, entityClass, identifyKey, matchRule));
		return this;
	}

	/**
	 * <h4 class="en-US">Add query conditions for not fuzzy matching values</h4>
	 * <h4 class="zh-CN">添加非模糊匹配值的查询条件</h4>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Connection type code</span>
	 *                       <span class="zh-CN">连接类型代码</span>
	 * @param entityClass    <span class="en-US">Entity class</span>
	 *                       <span class="zh-CN">实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param matchRule      <span class="en-US">match rule string</span>
	 *                       <span class="zh-CN">匹配规则字符串</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder notLike(final int sortCode, final ConnectionCode connectionCode,
	                            final Class<?> entityClass, final String identifyKey, final String matchRule)
			throws BuilderException {
		this.addCondition(Condition.notLike(sortCode, connectionCode, entityClass, identifyKey, matchRule));
		return this;
	}

	/**
	 * <h4 class="en-US">Add query condition with null value</h4>
	 * <h4 class="zh-CN">添加空值的查询条件</h4>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Connection type code</span>
	 *                       <span class="zh-CN">连接类型代码</span>
	 * @param entityClass    <span class="en-US">Entity class</span>
	 *                       <span class="zh-CN">实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder matchNull(final int sortCode, final ConnectionCode connectionCode,
	                              final Class<?> entityClass, final String identifyKey) throws BuilderException {
		this.addCondition(Condition.matchNull(sortCode, connectionCode, entityClass, identifyKey));
		return this;
	}

	/**
	 * <h4 class="en-US">Add query condition with not null value</h4>
	 * <h4 class="zh-CN">添加非空值的查询条件</h4>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Connection type code</span>
	 *                       <span class="zh-CN">连接类型代码</span>
	 * @param entityClass    <span class="en-US">Entity class</span>
	 *                       <span class="zh-CN">实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder notNull(final int sortCode, final ConnectionCode connectionCode,
	                            final Class<?> entityClass, final String identifyKey) throws BuilderException {
		this.addCondition(Condition.notNull(sortCode, connectionCode, entityClass, identifyKey));
		return this;
	}

	/**
	 * <h4 class="en-US">Adds a query condition where the value is contained in the given data</h4>
	 * <h4 class="zh-CN">添加值包含在给定数据中的查询条件</h4>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Connection type code</span>
	 *                       <span class="zh-CN">连接类型代码</span>
	 * @param entityClass    <span class="en-US">Entity class</span>
	 *                       <span class="zh-CN">实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param matchObjects   <span class="en-US">array of matching datasets</span>
	 *                       <span class="zh-CN">匹配数据集数组</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder in(final int sortCode, final ConnectionCode connectionCode, final Class<?> entityClass,
	                       final String identifyKey, final Object... matchObjects) throws BuilderException {
		this.addCondition(Condition.in(sortCode, connectionCode, entityClass, identifyKey, matchObjects));
		return this;
	}

	/**
	 * <h4 class="en-US">Adds a query condition where the value is contained in the given data</h4>
	 * <h4 class="zh-CN">添加值包含在给定数据中的查询条件</h4>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Connection type code</span>
	 *                       <span class="zh-CN">连接类型代码</span>
	 * @param entityClass    <span class="en-US">Entity class</span>
	 *                       <span class="zh-CN">实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param subQuery       <span class="en-US">Sub-query instance object</span>
	 *                       <span class="zh-CN">子查询实例对象</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder in(final int sortCode, final ConnectionCode connectionCode,
	                       final Class<?> entityClass, final String identifyKey, final QueryInfo subQuery)
			throws BuilderException {
		this.addCondition(Condition.in(sortCode, connectionCode, entityClass, identifyKey, subQuery));
		return this;
	}

	/**
	 * <h4 class="en-US">Adds a query condition where the value is contained in the given data</h4>
	 * <h4 class="zh-CN">添加值包含在给定数据中的查询条件</h4>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Connection type code</span>
	 *                       <span class="zh-CN">连接类型代码</span>
	 * @param entityClass    <span class="en-US">Entity class</span>
	 *                       <span class="zh-CN">实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param queryCode      <span class="en-US">Query code</span>
	 *                       <span class="zh-CN">查询代码</span>
	 * @param resultKey      <span class="en-US">Result identify key</span>
	 *                       <span class="zh-CN">结果集识别代码</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder in(final int sortCode, final ConnectionCode connectionCode,
	                       final Class<?> entityClass, final String identifyKey,
	                       final long queryCode, final String resultKey) throws BuilderException {
		this.addCondition(Condition.in(sortCode, connectionCode, entityClass, identifyKey, queryCode, resultKey));
		return this;
	}

	/**
	 * <h4 class="en-US">Adds a query condition where the value is not contained in the given data</h4>
	 * <h4 class="zh-CN">添加值没有包含在给定数据中的查询条件</h4>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Connection type code</span>
	 *                       <span class="zh-CN">连接类型代码</span>
	 * @param entityClass    <span class="en-US">Entity class</span>
	 *                       <span class="zh-CN">实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param matchObjects   <span class="en-US">array of matching datasets</span>
	 *                       <span class="zh-CN">匹配数据集数组</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder notIn(final int sortCode, final ConnectionCode connectionCode,
	                          final Class<?> entityClass, final String identifyKey,
	                          final Object... matchObjects) throws BuilderException {
		this.addCondition(Condition.notIn(sortCode, connectionCode, entityClass, identifyKey, matchObjects));
		return this;
	}

	/**
	 * <h4 class="en-US">Adds a query condition where the value is not contained in the given data</h4>
	 * <h4 class="zh-CN">添加值没有包含在给定数据中的查询条件</h4>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Connection type code</span>
	 *                       <span class="zh-CN">连接类型代码</span>
	 * @param entityClass    <span class="en-US">Entity class</span>
	 *                       <span class="zh-CN">实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param queryCode      <span class="en-US">Query code</span>
	 *                       <span class="zh-CN">查询代码</span>
	 * @param resultKey      <span class="en-US">Result identify key</span>
	 *                       <span class="zh-CN">结果集识别代码</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder notIn(final int sortCode, final ConnectionCode connectionCode,
	                          final Class<?> entityClass, final String identifyKey,
	                          final long queryCode, final String resultKey) throws BuilderException {
		this.addCondition(Condition.notIn(sortCode, connectionCode, entityClass, identifyKey, queryCode, resultKey));
		return this;
	}

	/**
	 * <h4 class="en-US">Adds a query condition where the value is not contained in the given data</h4>
	 * <h4 class="zh-CN">添加值没有包含在给定数据中的查询条件</h4>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Connection type code</span>
	 *                       <span class="zh-CN">连接类型代码</span>
	 * @param entityClass    <span class="en-US">Entity class</span>
	 *                       <span class="zh-CN">实体类</span>
	 * @param identifyKey    <span class="en-US">Identify key</span>
	 *                       <span class="zh-CN">识别代码</span>
	 * @param subQuery       <span class="en-US">Sub-query instance object</span>
	 *                       <span class="zh-CN">子查询实例对象</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
	 */
	public QueryBuilder notIn(final int sortCode, final ConnectionCode connectionCode,
	                          final Class<?> entityClass, final String identifyKey, final QueryInfo subQuery)
			throws BuilderException {
		this.addCondition(Condition.notIn(sortCode, connectionCode, entityClass, identifyKey, subQuery));
		return this;
	}

	/**
	 * <h4 class="en-US">Add query conditions for condition groups</h4>
	 * <h4 class="zh-CN">添加条件组的查询条件</h4>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Connection type code</span>
	 *                       <span class="zh-CN">连接类型代码</span>
	 * @param Conditions     <span class="en-US">Query conditions array</span>
	 *                       <span class="zh-CN">查询条件数组</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public QueryBuilder group(final int sortCode, final ConnectionCode connectionCode,
	                          final Condition... Conditions) {
		this.addCondition(Condition.group(sortCode, connectionCode, Conditions));
		return this;
	}

	/**
	 * <h4 class="en-US">Add or update sort types</h4>
	 * <h4 class="zh-CN">添加或更新排序类型</h4>
	 *
	 * @param entityClass <span class="en-US">Entity class</span>
	 *                    <span class="zh-CN">实体类</span>
	 * @param identifyKey <span class="en-US">Identify key</span>
	 *                    <span class="zh-CN">识别代码</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public QueryBuilder orderBy(@Nonnull final Class<?> entityClass, @Nonnull final String identifyKey) {
		return orderBy(entityClass, identifyKey, OrderType.ASC);
	}

	/**
	 * <h4 class="en-US">Add or update sort types</h4>
	 * <h4 class="zh-CN">添加或更新排序类型</h4>
	 *
	 * @param entityClass <span class="en-US">Entity class</span>
	 *                    <span class="zh-CN">实体类</span>
	 * @param identifyKey <span class="en-US">Identify key</span>
	 *                    <span class="zh-CN">识别代码</span>
	 * @param orderType   <span class="en-US">Query order type</span>
	 *                    <span class="zh-CN">查询结果集排序类型</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public QueryBuilder orderBy(@Nonnull final Class<?> entityClass, @Nonnull final String identifyKey,
	                            @Nonnull final OrderType orderType) {
		return this.orderBy(entityClass, identifyKey, orderType, Globals.DEFAULT_VALUE_INT);
	}

	/**
	 * <h4 class="en-US">Add or update sort types</h4>
	 * <h4 class="zh-CN">添加或更新排序类型</h4>
	 *
	 * @param entityClass <span class="en-US">Entity class</span>
	 *                    <span class="zh-CN">实体类</span>
	 * @param identifyKey <span class="en-US">Identify key</span>
	 *                    <span class="zh-CN">识别代码</span>
	 * @param orderType   <span class="en-US">Query order type</span>
	 *                    <span class="zh-CN">查询结果集排序类型</span>
	 * @param sortCode    <span class="en-US">Sort code</span>
	 *                    <span class="zh-CN">排序代码</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public QueryBuilder orderBy(@Nonnull final Class<?> entityClass, @Nonnull final String identifyKey,
	                            @Nonnull final OrderType orderType, final int sortCode) {
		if (this.orderByList.stream().anyMatch(groupBy -> groupBy.match(entityClass, identifyKey))) {
			this.orderByList.replaceAll(orderBy -> {
				if (orderBy.match(entityClass, identifyKey)) {
					orderBy.setOrderType(orderType);
					orderBy.setSortCode(sortCode);
				}
				return orderBy;
			});
		} else {
			this.orderByList.add(new OrderBy(entityClass, identifyKey, orderType, sortCode));
		}
		this.orderByList.sort(SortedItem.desc());
		return this;
	}

	/**
	 * <h4 class="en-US">Add or update group information</h4>
	 * <h4 class="zh-CN">添加或更新分组信息</h4>
	 *
	 * @param entityClass <span class="en-US">Entity class</span>
	 *                    <span class="zh-CN">实体类</span>
	 * @param identifyKey <span class="en-US">Identify key</span>
	 *                    <span class="zh-CN">识别代码</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public QueryBuilder groupBy(@Nonnull final Class<?> entityClass, @Nonnull final String identifyKey) {
		return this.groupBy(entityClass, identifyKey, Globals.DEFAULT_VALUE_INT);
	}

	/**
	 * <h4 class="en-US">Add or update group information</h4>
	 * <h4 class="zh-CN">添加或更新分组信息</h4>
	 *
	 * @param entityClass <span class="en-US">Entity class</span>
	 *                    <span class="zh-CN">实体类</span>
	 * @param identifyKey <span class="en-US">Identify key</span>
	 *                    <span class="zh-CN">识别代码</span>
	 * @param sortCode    <span class="en-US">Sort code</span>
	 *                    <span class="zh-CN">排序代码</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public QueryBuilder groupBy(@Nonnull final Class<?> entityClass, @Nonnull final String identifyKey,
	                            final int sortCode) {
		if (this.groupByList.stream().anyMatch(groupBy -> groupBy.match(entityClass, identifyKey))) {
			this.groupByList.replaceAll(groupBy -> {
				if (groupBy.match(entityClass, identifyKey)) {
					groupBy.setSortCode(sortCode);
				}
				return groupBy;
			});
		} else {
			this.groupByList.add(new GroupBy(entityClass, identifyKey, sortCode));
		}
		this.groupByList.sort(SortedItem.desc());
		return this;
	}

	/**
	 * <h4 class="en-US">Configure current query using cache</h4>
	 * <h4 class="zh-CN">设置当前查询使用缓存</h4>
	 *
	 * @param cacheables <span class="en-US">Using cache status</span>
	 *                   <span class="zh-CN">使用缓存状态</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public QueryBuilder useCache(final boolean cacheables) {
		this.cacheables = cacheables;
		return this;
	}

	/**
	 * <h4 class="en-US">Configure current query will using for update record</h4>
	 * <h4 class="zh-CN">设置当前查询是为了更新记录</h4>
	 *
	 * @param forUpdate <span class="en-US">For update status</span>
	 *                  <span class="zh-CN">为更新记录状态</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public QueryBuilder forUpdate(final boolean forUpdate) {
		this.forUpdate = forUpdate;
		return this;
	}

	/**
	 * <h4 class="en-US">Configure lock option for update record</h4>
	 * <h4 class="zh-CN">设置更新记录的锁定选项</h4>
	 *
	 * @param lockOption <span class="en-US">Lock option</span>
	 *                   <span class="zh-CN">锁定选项</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public QueryBuilder lockOption(final LockOption lockOption) {
		this.lockOption = lockOption;
		return this;
	}

	/**
	 * <h4 class="en-US">Configure pager information of current query</h4>
	 * <h4 class="zh-CN">设置当前查询的分页配置</h4>
	 *
	 * @param pageNo    <span class="en-US">Current page number</span>
	 *                  <span class="zh-CN">当前页数</span>
	 * @param pageLimit <span class="en-US">Page limit records count</span>
	 *                  <span class="zh-CN">每页的记录数</span>
	 * @return <span class="en-US">Current builder instance</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public QueryBuilder configPager(final int pageNo, final int pageLimit) {
		this.pageNo = pageNo;
		this.pageLimit = pageLimit;
		return this;
	}

	private static List<ColumnConfig> columnConfigs(final Class<?> entityClass) throws BuilderException {
		return Optional.ofNullable(EntityManager.tableConfig(entityClass))
				.map(TableConfig::getColumnConfigs)
				.orElseThrow(() -> new BuilderException(0x00DB00000001L));
	}

	private void addItem(final AbstractItem abstractItem) {
		if (abstractItem == null) {
			return;
		}
		if (this.itemList.stream().noneMatch(existItem -> ObjectUtils.nullSafeEquals(existItem, abstractItem))) {
			this.itemList.add(abstractItem);
		}
	}

	private void addCondition(final Condition condition) {
		if (this.conditionList.stream().noneMatch(existCondition ->
				ObjectUtils.nullSafeEquals(existCondition, condition))) {
			this.conditionList.add(condition);
		}
	}
}
