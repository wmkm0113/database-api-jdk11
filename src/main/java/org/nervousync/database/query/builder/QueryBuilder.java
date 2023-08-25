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
package org.nervousync.database.query.builder;

import jakarta.annotation.Nonnull;
import org.nervousync.commons.Globals;
import org.nervousync.database.beans.configs.table.TableConfig;
import org.nervousync.database.commons.DatabaseCommons;
import org.nervousync.database.entity.EntityManager;
import org.nervousync.database.enumerations.join.JoinType;
import org.nervousync.database.enumerations.lock.LockOption;
import org.nervousync.database.enumerations.query.OrderType;
import org.nervousync.database.enumerations.query.operate.ConditionCode;
import org.nervousync.database.enumerations.query.operate.ConnectionCode;
import org.nervousync.database.query.QueryInfo;
import org.nervousync.database.query.condition.MatchCondition;
import org.nervousync.database.query.condition.QueryCondition;
import org.nervousync.database.query.group.GroupByColumn;
import org.nervousync.database.query.item.QueryItem;
import org.nervousync.database.query.item.impl.QueryColumn;
import org.nervousync.database.query.item.impl.QueryFunction;
import org.nervousync.database.query.join.JoinColumn;
import org.nervousync.database.query.join.JoinTable;
import org.nervousync.database.query.orderby.OrderByColumn;
import org.nervousync.database.query.table.QueryTable;
import org.nervousync.utils.CollectionUtils;
import org.nervousync.utils.ObjectUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * <h2 class="en-US">Query information builder</h2>
 * <h2 class="zh-CN">查询信息构建器</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Jul 30, 2023 15:37:53 $
 */
public final class QueryBuilder {
    /**
     * <span class="en-US">Query table builder</span>
     * <span class="zh-CN">查询数据表构建器</span>
     */
    private final TableBuilder tableBuilder;
    /**
     * <span class="en-US">Query order by columns list</span>
     * <span class="zh-CN">查询排序数据列列表</span>
     */
    private final List<OrderByColumn> orderByColumns;
    /**
     * <span class="en-US">Query group by columns list</span>
     * <span class="zh-CN">查询分组数据列列表</span>
     */
    private final List<GroupByColumn> groupByColumns;
    /**
     * <span class="en-US">Query page limit</span>
     * <span class="zh-CN">查询分页记录数</span>
     */
    private int pageLimit;
    /**
     * <span class="en-US">Query offset value</span>
     * <span class="zh-CN">查询起始记录数</span>
     */
    private int offset;
    /**
     * <span class="en-US">Query result can cacheable</span>
     * <span class="zh-CN">查询结果可以缓存</span>
     */
    private boolean cacheables;
    /**
     * <span class="en-US">Query result for update</span>
     * <span class="zh-CN">查询结果用于批量更新记录</span>
     */
    private boolean forUpdate;
    /**
     * <span class="en-US">Query record lock option</span>
     * <span class="zh-CN">查询记录锁定选项</span>
     */
    private LockOption lockOption;

    /**
     * <h3 class="en-US">Private constructor method for QueryBuilder</h3>
     * <h3 class="zh-CN">查询信息构建器的私有构造方法</h3>
     *
     * @param entityClass  <span class="en-US">Query table entity class</span>
     *                     <span class="zh-CN">查询驱动表实体类</span>
     * @param identifyKeys <span class="en-US">Query columns identify key array</span>
     *                     <span class="zh-CN">查询数据列的识别代码数组</span>
     */
    private QueryBuilder(final Class<?> entityClass, final String... identifyKeys) {
        this.tableBuilder = new TableBuilder(entityClass, "T_0", identifyKeys);
        this.orderByColumns = new ArrayList<>();
        this.groupByColumns = new ArrayList<>();
    }

    /**
     * <h3 class="en-US">Static method for generate new QueryBuilder instance</h3>
     * <h3 class="zh-CN">静态方法用于生成新的查询信息构建器实例对象</h3>
     *
     * @param entityClass  <span class="en-US">Query table entity class</span>
     *                     <span class="zh-CN">查询驱动表实体类</span>
     * @param identifyKeys <span class="en-US">Query columns identify key array</span>
     *                     <span class="zh-CN">查询数据列的识别代码数组</span>
     * @return <span class="en-US">Generated builder instance</span>
     * <span class="zh-CN">生成的构建器实例对象</span>
     */
    public static QueryBuilder newBuilder(final Class<?> entityClass, final String... identifyKeys) {
        return new QueryBuilder(entityClass, identifyKeys);
    }

    /**
     * <h3 class="en-US">Add query column to query item list</h3>
     * <h3 class="zh-CN">添加查询列信息到查询项目列表中</h3>
     *
     * @param entityClass <span class="en-US">Table entity class</span>
     *                    <span class="zh-CN">数据表实体类</span>
     * @param identifyKey <span class="en-US">Identify key</span>
     *                    <span class="zh-CN">识别代码</span>
     * @return <span class="en-US">Current builder instance</span>
     * <span class="zh-CN">当前构建器实例对象</span>
     */
    public QueryBuilder queryColumn(final Class<?> entityClass, final String identifyKey) {
        return this.queryColumn(entityClass, identifyKey, Globals.DEFAULT_VALUE_STRING);
    }

    /**
     * <h3 class="en-US">Add query column to query item list</h3>
     * <h3 class="zh-CN">添加查询列信息到查询项目列表中</h3>
     *
     * @param entityClass <span class="en-US">Table entity class</span>
     *                    <span class="zh-CN">数据表实体类</span>
     * @param identifyKey <span class="en-US">Identify key</span>
     *                    <span class="zh-CN">识别代码</span>
     * @param aliasName   <span class="en-US">Item alias name</span>
     *                    <span class="zh-CN">查询项别名</span>
     * @return <span class="en-US">Current builder instance</span>
     * <span class="zh-CN">当前构建器实例对象</span>
     */
    public QueryBuilder queryColumn(final Class<?> entityClass, final String identifyKey, final String aliasName) {
        return this.queryColumn(entityClass, identifyKey, Boolean.FALSE, aliasName);
    }

    /**
     * <h3 class="en-US">Add query column to query item list</h3>
     * <h3 class="zh-CN">添加查询列信息到查询项目列表中</h3>
     *
     * @param entityClass <span class="en-US">Table entity class</span>
     *                    <span class="zh-CN">数据表实体类</span>
     * @param identifyKey <span class="en-US">Identify key</span>
     *                    <span class="zh-CN">识别代码</span>
     * @param distinct    <span class="en-US">Column distinct</span>
     *                    <span class="zh-CN">数据列去重</span>
     * @param aliasName   <span class="en-US">Item alias name</span>
     *                    <span class="zh-CN">查询项别名</span>
     * @return <span class="en-US">Current builder instance</span>
     * <span class="zh-CN">当前构建器实例对象</span>
     */
    public QueryBuilder queryColumn(final Class<?> entityClass, final String identifyKey,
                                    final boolean distinct, final String aliasName) {
        this.tableBuilder.queryColumn(entityClass, identifyKey, distinct, aliasName);
        return this;
    }

    /**
     * <h3 class="en-US">Add query function to query item list</h3>
     * <h3 class="zh-CN">添加查询函数信息到查询项目列表中</h3>
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
    public QueryBuilder queryFunction(final String aliasName, final String sqlFunction,
                                      final QueryItem... functionParams) {
        this.tableBuilder.queryFunction(aliasName, sqlFunction, functionParams);
        return this;
    }

    /**
     * <h3 class="en-US">Add associated query information table</h3>
     * <h3 class="zh-CN">添加关联查询信息表</h3>
     *
     * @param joinType       <span class="en-US">Table join type</span>
     *                       <span class="zh-CN">数据表关联类型</span>
     * @param entityClass    <span class="en-US">Entity class</span>
     *                       <span class="zh-CN">实体类</span>
     * @param referenceClass <span class="en-US">Reference entity class</span>
     *                       <span class="zh-CN">关联表实体类</span>
     * @return <span class="en-US">Current builder instance</span>
     * <span class="zh-CN">当前构建器实例对象</span>
     */
    public QueryBuilder joinTable(final JoinType joinType, final Class<?> entityClass,
                                  final Class<?> referenceClass) {
        this.tableBuilder.joinTable(joinType, entityClass, referenceClass);
        return this;
    }

    /**
     * <h3 class="en-US">Add a query condition greater than a certain value</h3>
     * <h3 class="zh-CN">添加大于某值的查询条件</h3>
     *
     * @param entityClass <span class="en-US">Entity class</span>
     *                    <span class="zh-CN">实体类</span>
     * @param identifyKey <span class="en-US">Identify key</span>
     *                    <span class="zh-CN">识别代码</span>
     * @param matchValue  <span class="en-US">Match value</span>
     *                    <span class="zh-CN">匹配值</span>
     * @return <span class="en-US">Current builder instance</span>
     * <span class="zh-CN">当前构建器实例对象</span>
     */
    public QueryBuilder greater(final Class<?> entityClass, final String identifyKey, final Object matchValue) {
        return this.greater(ConnectionCode.AND, entityClass, identifyKey, matchValue);
    }

    /**
     * <h3 class="en-US">Add a query condition greater than a certain value</h3>
     * <h3 class="zh-CN">添加大于某值的查询条件</h3>
     *
     * @param connectionCode <span class="en-US">Connection type code</span>
     *                       <span class="zh-CN">连接类型代码</span>
     * @param entityClass <span class="en-US">Entity class</span>
     *                    <span class="zh-CN">实体类</span>
     * @param identifyKey <span class="en-US">Identify key</span>
     *                    <span class="zh-CN">识别代码</span>
     * @param matchValue  <span class="en-US">Match value</span>
     *                    <span class="zh-CN">匹配值</span>
     * @return <span class="en-US">Current builder instance</span>
     * <span class="zh-CN">当前构建器实例对象</span>
     */
    public QueryBuilder greater(final ConnectionCode connectionCode, final Class<?> entityClass,
                                final String identifyKey, final Object matchValue) {
        return this.greater(connectionCode, entityClass, identifyKey, MatchCondition.condition(matchValue));
    }

    /**
     * <h3 class="en-US">Add a query condition greater than a certain value</h3>
     * <h3 class="zh-CN">添加大于某值的查询条件</h3>
     *
     * @param entityClass    <span class="en-US">Entity class</span>
     *                       <span class="zh-CN">实体类</span>
     * @param identifyKey    <span class="en-US">Identify key</span>
     *                       <span class="zh-CN">识别代码</span>
     * @param matchCondition <span class="en-US">Matching condition information</span>
     *                       <span class="zh-CN">匹配条件信息</span>
     * @return <span class="en-US">Current builder instance</span>
     * <span class="zh-CN">当前构建器实例对象</span>
     */
    public QueryBuilder greater(final Class<?> entityClass, final String identifyKey,
                                final MatchCondition matchCondition) {
        return this.greater(ConnectionCode.AND, entityClass, identifyKey, matchCondition);
    }

    /**
     * <h3 class="en-US">Add a query condition greater than a certain value</h3>
     * <h3 class="zh-CN">添加大于某值的查询条件</h3>
     *
     * @param connectionCode <span class="en-US">Connection type code</span>
     *                       <span class="zh-CN">连接类型代码</span>
     * @param entityClass    <span class="en-US">Entity class</span>
     *                       <span class="zh-CN">实体类</span>
     * @param identifyKey    <span class="en-US">Identify key</span>
     *                       <span class="zh-CN">识别代码</span>
     * @param matchCondition <span class="en-US">Matching condition information</span>
     *                       <span class="zh-CN">匹配条件信息</span>
     * @return <span class="en-US">Current builder instance</span>
     * <span class="zh-CN">当前构建器实例对象</span>
     */
    public QueryBuilder greater(final ConnectionCode connectionCode, final Class<?> entityClass,
                                final String identifyKey, final MatchCondition matchCondition) {
        this.tableBuilder.addCondition(connectionCode, ConditionCode.GREATER, entityClass, identifyKey, matchCondition);
        return this;
    }

    /**
     * <h3 class="en-US">Add a query condition greater than or equal to a certain value</h3>
     * <h3 class="zh-CN">添加大于等于某值的查询条件</h3>
     *
     * @param entityClass <span class="en-US">Entity class</span>
     *                    <span class="zh-CN">实体类</span>
     * @param identifyKey <span class="en-US">Identify key</span>
     *                    <span class="zh-CN">识别代码</span>
     * @param matchValue  <span class="en-US">Match value</span>
     *                    <span class="zh-CN">匹配值</span>
     * @return <span class="en-US">Current builder instance</span>
     * <span class="zh-CN">当前构建器实例对象</span>
     */
    public QueryBuilder greaterEqual(final Class<?> entityClass, final String identifyKey, final Object matchValue) {
        return this.greaterEqual(ConnectionCode.AND, entityClass, identifyKey, matchValue);
    }

    /**
     * <h3 class="en-US">Add a query condition greater than or equal to a certain value</h3>
     * <h3 class="zh-CN">添加大于等于某值的查询条件</h3>
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
     */
    public QueryBuilder greaterEqual(final ConnectionCode connectionCode, final Class<?> entityClass,
                                     final String identifyKey, final Object matchValue) {
        this.tableBuilder.addCondition(connectionCode, ConditionCode.GREATER_EQUAL,
                entityClass, identifyKey, MatchCondition.condition(matchValue));
        return this;
    }

    /**
     * <h3 class="en-US">Add a query condition greater than or equal to a certain value</h3>
     * <h3 class="zh-CN">添加大于等于某值的查询条件</h3>
     *
     * @param entityClass    <span class="en-US">Entity class</span>
     *                       <span class="zh-CN">实体类</span>
     * @param identifyKey    <span class="en-US">Identify key</span>
     *                       <span class="zh-CN">识别代码</span>
     * @param matchCondition <span class="en-US">Matching condition information</span>
     *                       <span class="zh-CN">匹配条件信息</span>
     * @return <span class="en-US">Current builder instance</span>
     * <span class="zh-CN">当前构建器实例对象</span>
     */
    public QueryBuilder greaterEqual(final Class<?> entityClass, final String identifyKey,
                                     final MatchCondition matchCondition) {
        return this.greaterEqual(ConnectionCode.AND, entityClass, identifyKey, matchCondition);
    }

    /**
     * <h3 class="en-US">Add a query condition greater than or equal to a certain value</h3>
     * <h3 class="zh-CN">添加大于等于某值的查询条件</h3>
     *
     * @param connectionCode <span class="en-US">Connection type code</span>
     *                       <span class="zh-CN">连接类型代码</span>
     * @param entityClass    <span class="en-US">Entity class</span>
     *                       <span class="zh-CN">实体类</span>
     * @param identifyKey    <span class="en-US">Identify key</span>
     *                       <span class="zh-CN">识别代码</span>
     * @param matchCondition <span class="en-US">Matching condition information</span>
     *                       <span class="zh-CN">匹配条件信息</span>
     * @return <span class="en-US">Current builder instance</span>
     * <span class="zh-CN">当前构建器实例对象</span>
     */
    public QueryBuilder greaterEqual(final ConnectionCode connectionCode, final Class<?> entityClass,
                                     final String identifyKey, final MatchCondition matchCondition) {
        this.tableBuilder.addCondition(connectionCode, ConditionCode.GREATER_EQUAL,
                entityClass, identifyKey, matchCondition);
        return this;
    }

    /**
     * <h3 class="en-US">Add a query condition less than a certain value</h3>
     * <h3 class="zh-CN">添加小于某值的查询条件</h3>
     *
     * @param entityClass <span class="en-US">Entity class</span>
     *                    <span class="zh-CN">实体类</span>
     * @param identifyKey <span class="en-US">Identify key</span>
     *                    <span class="zh-CN">识别代码</span>
     * @param matchValue  <span class="en-US">Match value</span>
     *                    <span class="zh-CN">匹配值</span>
     * @return <span class="en-US">Current builder instance</span>
     * <span class="zh-CN">当前构建器实例对象</span>
     */
    public QueryBuilder less(final Class<?> entityClass, final String identifyKey, final Object matchValue) {
        return this.less(ConnectionCode.AND, entityClass, identifyKey, matchValue);
    }

    /**
     * <h3 class="en-US">Add a query condition less than a certain value</h3>
     * <h3 class="zh-CN">添加小于某值的查询条件</h3>
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
     */
    public QueryBuilder less(final ConnectionCode connectionCode, final Class<?> entityClass,
                             final String identifyKey, final Object matchValue) {
        this.tableBuilder.addCondition(connectionCode, ConditionCode.LESS,
                entityClass, identifyKey, MatchCondition.condition(matchValue));
        return this;
    }

    /**
     * <h3 class="en-US">Add a query condition less than a certain value</h3>
     * <h3 class="zh-CN">添加小于某值的查询条件</h3>
     *
     * @param entityClass    <span class="en-US">Entity class</span>
     *                       <span class="zh-CN">实体类</span>
     * @param identifyKey    <span class="en-US">Identify key</span>
     *                       <span class="zh-CN">识别代码</span>
     * @param matchCondition <span class="en-US">Matching condition information</span>
     *                       <span class="zh-CN">匹配条件信息</span>
     * @return <span class="en-US">Current builder instance</span>
     * <span class="zh-CN">当前构建器实例对象</span>
     */
    public QueryBuilder less(final Class<?> entityClass, final String identifyKey,
                             final MatchCondition matchCondition) {
        return this.less(ConnectionCode.AND, entityClass, identifyKey, matchCondition);
    }

    /**
     * <h3 class="en-US">Add a query condition less than a certain value</h3>
     * <h3 class="zh-CN">添加小于某值的查询条件</h3>
     *
     * @param connectionCode <span class="en-US">Connection type code</span>
     *                       <span class="zh-CN">连接类型代码</span>
     * @param entityClass    <span class="en-US">Entity class</span>
     *                       <span class="zh-CN">实体类</span>
     * @param identifyKey    <span class="en-US">Identify key</span>
     *                       <span class="zh-CN">识别代码</span>
     * @param matchCondition <span class="en-US">Matching condition information</span>
     *                       <span class="zh-CN">匹配条件信息</span>
     * @return <span class="en-US">Current builder instance</span>
     * <span class="zh-CN">当前构建器实例对象</span>
     */
    public QueryBuilder less(final ConnectionCode connectionCode, final Class<?> entityClass,
                             final String identifyKey, final MatchCondition matchCondition) {
        this.tableBuilder.addCondition(connectionCode, ConditionCode.LESS, entityClass, identifyKey, matchCondition);
        return this;
    }

    /**
     * <h3 class="en-US">Add a query condition less than or equal to a certain value</h3>
     * <h3 class="zh-CN">添加小于等于某值的查询条件</h3>
     *
     * @param entityClass <span class="en-US">Entity class</span>
     *                    <span class="zh-CN">实体类</span>
     * @param identifyKey <span class="en-US">Identify key</span>
     *                    <span class="zh-CN">识别代码</span>
     * @param matchValue  <span class="en-US">Match value</span>
     *                    <span class="zh-CN">匹配值</span>
     * @return <span class="en-US">Current builder instance</span>
     * <span class="zh-CN">当前构建器实例对象</span>
     */
    public QueryBuilder lessEqual(final Class<?> entityClass, final String identifyKey, final Object matchValue) {
        return this.lessEqual(ConnectionCode.AND, entityClass, identifyKey, matchValue);
    }

    /**
     * <h3 class="en-US">Add a query condition less than or equal to a certain value</h3>
     * <h3 class="zh-CN">添加小于等于某值的查询条件</h3>
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
     */
    public QueryBuilder lessEqual(final ConnectionCode connectionCode, final Class<?> entityClass,
                                  final String identifyKey, final Object matchValue) {
        this.tableBuilder.addCondition(connectionCode, ConditionCode.LESS_EQUAL,
                entityClass, identifyKey, MatchCondition.condition(matchValue));
        return this;
    }

    /**
     * <h3 class="en-US">Add a query condition less than or equal to a certain value</h3>
     * <h3 class="zh-CN">添加小于等于某值的查询条件</h3>
     *
     * @param entityClass    <span class="en-US">Entity class</span>
     *                       <span class="zh-CN">实体类</span>
     * @param identifyKey    <span class="en-US">Identify key</span>
     *                       <span class="zh-CN">识别代码</span>
     * @param matchCondition <span class="en-US">Matching condition information</span>
     *                       <span class="zh-CN">匹配条件信息</span>
     * @return <span class="en-US">Current builder instance</span>
     * <span class="zh-CN">当前构建器实例对象</span>
     */
    public QueryBuilder lessEqual(final Class<?> entityClass, final String identifyKey,
                                  final MatchCondition matchCondition) {
        return this.lessEqual(ConnectionCode.AND, entityClass, identifyKey, matchCondition);
    }

    /**
     * <h3 class="en-US">Add a query condition less than or equal to a certain value</h3>
     * <h3 class="zh-CN">添加小于等于某值的查询条件</h3>
     *
     * @param connectionCode <span class="en-US">Connection type code</span>
     *                       <span class="zh-CN">连接类型代码</span>
     * @param entityClass    <span class="en-US">Entity class</span>
     *                       <span class="zh-CN">实体类</span>
     * @param identifyKey    <span class="en-US">Identify key</span>
     *                       <span class="zh-CN">识别代码</span>
     * @param matchCondition <span class="en-US">Matching condition information</span>
     *                       <span class="zh-CN">匹配条件信息</span>
     * @return <span class="en-US">Current builder instance</span>
     * <span class="zh-CN">当前构建器实例对象</span>
     */
    public QueryBuilder lessEqual(final ConnectionCode connectionCode, final Class<?> entityClass,
                                  final String identifyKey, final MatchCondition matchCondition) {
        this.tableBuilder.addCondition(connectionCode, ConditionCode.LESS_EQUAL,
                entityClass, identifyKey, matchCondition);
        return this;
    }

    /**
     * <h3 class="en-US">Add a query condition equal to a certain value</h3>
     * <h3 class="zh-CN">添加等于某值的查询条件</h3>
     *
     * @param entityClass <span class="en-US">Entity class</span>
     *                    <span class="zh-CN">实体类</span>
     * @param identifyKey <span class="en-US">Identify key</span>
     *                    <span class="zh-CN">识别代码</span>
     * @param matchValue  <span class="en-US">Match value</span>
     *                    <span class="zh-CN">匹配值</span>
     * @return <span class="en-US">Current builder instance</span>
     * <span class="zh-CN">当前构建器实例对象</span>
     */
    public QueryBuilder equal(final Class<?> entityClass, final String identifyKey, final Object matchValue) {
        return this.equal(ConnectionCode.AND, entityClass, identifyKey, matchValue);
    }

    /**
     * <h3 class="en-US">Add a query condition equal to a certain value</h3>
     * <h3 class="zh-CN">添加等于某值的查询条件</h3>
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
     */
    public QueryBuilder equal(final ConnectionCode connectionCode, final Class<?> entityClass,
                              final String identifyKey, final Object matchValue) {
        this.tableBuilder.addCondition(connectionCode, ConditionCode.EQUAL,
                entityClass, identifyKey, MatchCondition.condition(matchValue));
        return this;
    }

    /**
     * <h3 class="en-US">Add a query condition equal to a certain value</h3>
     * <h3 class="zh-CN">添加等于某值的查询条件</h3>
     *
     * @param entityClass    <span class="en-US">Entity class</span>
     *                       <span class="zh-CN">实体类</span>
     * @param identifyKey    <span class="en-US">Identify key</span>
     *                       <span class="zh-CN">识别代码</span>
     * @param matchCondition <span class="en-US">Matching condition information</span>
     *                       <span class="zh-CN">匹配条件信息</span>
     * @return <span class="en-US">Current builder instance</span>
     * <span class="zh-CN">当前构建器实例对象</span>
     */
    public QueryBuilder equal(final Class<?> entityClass, final String identifyKey,
                              final MatchCondition matchCondition) {
        return this.equal(ConnectionCode.AND, entityClass, identifyKey, matchCondition);
    }

    /**
     * <h3 class="en-US">Add a query condition equal to a certain value</h3>
     * <h3 class="zh-CN">添加等于某值的查询条件</h3>
     *
     * @param connectionCode <span class="en-US">Connection type code</span>
     *                       <span class="zh-CN">连接类型代码</span>
     * @param entityClass    <span class="en-US">Entity class</span>
     *                       <span class="zh-CN">实体类</span>
     * @param identifyKey    <span class="en-US">Identify key</span>
     *                       <span class="zh-CN">识别代码</span>
     * @param matchCondition <span class="en-US">Matching condition information</span>
     *                       <span class="zh-CN">匹配条件信息</span>
     * @return <span class="en-US">Current builder instance</span>
     * <span class="zh-CN">当前构建器实例对象</span>
     */
    public QueryBuilder equal(final ConnectionCode connectionCode, final Class<?> entityClass,
                              final String identifyKey, final MatchCondition matchCondition) {
        this.tableBuilder.addCondition(connectionCode, ConditionCode.EQUAL, entityClass, identifyKey, matchCondition);
        return this;
    }

    /**
     * <h3 class="en-US">Add a query condition not equal to a certain value</h3>
     * <h3 class="zh-CN">添加不等于某值的查询条件</h3>
     *
     * @param entityClass <span class="en-US">Entity class</span>
     *                    <span class="zh-CN">实体类</span>
     * @param identifyKey <span class="en-US">Identify key</span>
     *                    <span class="zh-CN">识别代码</span>
     * @param matchValue  <span class="en-US">Match value</span>
     *                    <span class="zh-CN">匹配值</span>
     * @return <span class="en-US">Current builder instance</span>
     * <span class="zh-CN">当前构建器实例对象</span>
     */
    public QueryBuilder notEqual(final Class<?> entityClass, final String identifyKey, final Object matchValue) {
        return this.notEqual(ConnectionCode.AND, entityClass, identifyKey, matchValue);
    }

    /**
     * <h3 class="en-US">Add a query condition not equal to a certain value</h3>
     * <h3 class="zh-CN">添加不等于某值的查询条件</h3>
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
     */
    public QueryBuilder notEqual(final ConnectionCode connectionCode, final Class<?> entityClass,
                                 final String identifyKey, final Object matchValue) {
        this.tableBuilder.addCondition(connectionCode, ConditionCode.NOT_EQUAL,
                entityClass, identifyKey, MatchCondition.condition(matchValue));
        return this;
    }

    /**
     * <h3 class="en-US">Add a query condition not equal to a certain value</h3>
     * <h3 class="zh-CN">添加不等于某值的查询条件</h3>
     *
     * @param entityClass    <span class="en-US">Entity class</span>
     *                       <span class="zh-CN">实体类</span>
     * @param identifyKey    <span class="en-US">Identify key</span>
     *                       <span class="zh-CN">识别代码</span>
     * @param matchCondition <span class="en-US">Matching condition information</span>
     *                       <span class="zh-CN">匹配条件信息</span>
     * @return <span class="en-US">Current builder instance</span>
     * <span class="zh-CN">当前构建器实例对象</span>
     */
    public QueryBuilder notEqual(final Class<?> entityClass, final String identifyKey,
                                 final MatchCondition matchCondition) {
        return this.notEqual(ConnectionCode.AND, entityClass, identifyKey, matchCondition);
    }

    /**
     * <h3 class="en-US">Add a query condition not equal to a certain value</h3>
     * <h3 class="zh-CN">添加不等于某值的查询条件</h3>
     *
     * @param connectionCode <span class="en-US">Connection type code</span>
     *                       <span class="zh-CN">连接类型代码</span>
     * @param entityClass    <span class="en-US">Entity class</span>
     *                       <span class="zh-CN">实体类</span>
     * @param identifyKey    <span class="en-US">Identify key</span>
     *                       <span class="zh-CN">识别代码</span>
     * @param matchCondition <span class="en-US">Matching condition information</span>
     *                       <span class="zh-CN">匹配条件信息</span>
     * @return <span class="en-US">Current builder instance</span>
     * <span class="zh-CN">当前构建器实例对象</span>
     */
    public QueryBuilder notEqual(final ConnectionCode connectionCode, final Class<?> entityClass,
                                 final String identifyKey, final MatchCondition matchCondition) {
        this.tableBuilder.addCondition(connectionCode, ConditionCode.NOT_EQUAL,
                entityClass, identifyKey, matchCondition);
        return this;
    }

    /**
     * <h3 class="en-US">Add a query condition between certain two values</h3>
     * <h3 class="zh-CN">添加介于某两个值之间的查询条件</h3>
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
     */
    public QueryBuilder betweenAnd(final Class<?> entityClass, final String identifyKey,
                                   final Object beginValue, final Object endValue) {
        return this.betweenAnd(ConnectionCode.AND, entityClass, identifyKey, beginValue, endValue);
    }

    /**
     * <h3 class="en-US">Add a query condition between certain two values</h3>
     * <h3 class="zh-CN">添加介于某两个值之间的查询条件</h3>
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
     */
    public QueryBuilder betweenAnd(final ConnectionCode connectionCode, final Class<?> entityClass,
                                   final String identifyKey, final Object beginValue, final Object endValue) {
        this.tableBuilder.addCondition(connectionCode, ConditionCode.BETWEEN_AND,
                entityClass, identifyKey, MatchCondition.condition(beginValue, endValue));
        return this;
    }

    /**
     * <h3 class="en-US">Add a query condition between certain two values</h3>
     * <h3 class="zh-CN">添加介于某两个值之间的查询条件</h3>
     *
     * @param entityClass    <span class="en-US">Entity class</span>
     *                       <span class="zh-CN">实体类</span>
     * @param identifyKey    <span class="en-US">Identify key</span>
     *                       <span class="zh-CN">识别代码</span>
     * @param matchCondition <span class="en-US">Matching condition information</span>
     *                       <span class="zh-CN">匹配条件信息</span>
     * @return <span class="en-US">Current builder instance</span>
     * <span class="zh-CN">当前构建器实例对象</span>
     */
    public QueryBuilder betweenAnd(final Class<?> entityClass, final String identifyKey,
                                   final MatchCondition matchCondition) {
        return this.betweenAnd(ConnectionCode.AND, entityClass, identifyKey, matchCondition);
    }

    /**
     * <h3 class="en-US">Add a query condition between certain two values</h3>
     * <h3 class="zh-CN">添加介于某两个值之间的查询条件</h3>
     *
     * @param connectionCode <span class="en-US">Connection type code</span>
     *                       <span class="zh-CN">连接类型代码</span>
     * @param entityClass    <span class="en-US">Entity class</span>
     *                       <span class="zh-CN">实体类</span>
     * @param identifyKey    <span class="en-US">Identify key</span>
     *                       <span class="zh-CN">识别代码</span>
     * @param matchCondition <span class="en-US">Matching condition information</span>
     *                       <span class="zh-CN">匹配条件信息</span>
     * @return <span class="en-US">Current builder instance</span>
     * <span class="zh-CN">当前构建器实例对象</span>
     */
    public QueryBuilder betweenAnd(final ConnectionCode connectionCode, final Class<?> entityClass,
                                   final String identifyKey, final MatchCondition matchCondition) {
        this.tableBuilder.addCondition(connectionCode, ConditionCode.BETWEEN_AND,
                entityClass, identifyKey, matchCondition);
        return this;
    }

    /**
     * <h3 class="en-US">Add a query condition not between certain two values</h3>
     * <h3 class="zh-CN">添加不介于某两个值之间的查询条件</h3>
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
     */
    public QueryBuilder notBetweenAnd(final Class<?> entityClass, final String identifyKey,
                                      final Object beginValue, final Object endValue) {
        return this.notBetweenAnd(ConnectionCode.AND, entityClass, identifyKey, beginValue, endValue);
    }

    /**
     * <h3 class="en-US">Add a query condition not between certain two values</h3>
     * <h3 class="zh-CN">添加不介于某两个值之间的查询条件</h3>
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
     */
    public QueryBuilder notBetweenAnd(final ConnectionCode connectionCode, final Class<?> entityClass,
                                   final String identifyKey, final Object beginValue, final Object endValue) {
        this.tableBuilder.addCondition(connectionCode, ConditionCode.NOT_BETWEEN_AND,
                entityClass, identifyKey, MatchCondition.condition(beginValue, endValue));
        return this;
    }

    /**
     * <h3 class="en-US">Add a query condition not between certain two values</h3>
     * <h3 class="zh-CN">添加不介于某两个值之间的查询条件</h3>
     *
     * @param entityClass    <span class="en-US">Entity class</span>
     *                       <span class="zh-CN">实体类</span>
     * @param identifyKey    <span class="en-US">Identify key</span>
     *                       <span class="zh-CN">识别代码</span>
     * @param matchCondition <span class="en-US">Matching condition information</span>
     *                       <span class="zh-CN">匹配条件信息</span>
     * @return <span class="en-US">Current builder instance</span>
     * <span class="zh-CN">当前构建器实例对象</span>
     */
    public QueryBuilder notBetweenAnd(final Class<?> entityClass, final String identifyKey,
                                      final MatchCondition matchCondition) {
        return this.notBetweenAnd(ConnectionCode.AND, entityClass, identifyKey, matchCondition);
    }

    /**
     * <h3 class="en-US">Add a query condition not between certain two values</h3>
     * <h3 class="zh-CN">添加不介于某两个值之间的查询条件</h3>
     *
     * @param connectionCode <span class="en-US">Connection type code</span>
     *                       <span class="zh-CN">连接类型代码</span>
     * @param entityClass    <span class="en-US">Entity class</span>
     *                       <span class="zh-CN">实体类</span>
     * @param identifyKey    <span class="en-US">Identify key</span>
     *                       <span class="zh-CN">识别代码</span>
     * @param matchCondition <span class="en-US">Matching condition information</span>
     *                       <span class="zh-CN">匹配条件信息</span>
     * @return <span class="en-US">Current builder instance</span>
     * <span class="zh-CN">当前构建器实例对象</span>
     */
    public QueryBuilder notBetweenAnd(final ConnectionCode connectionCode, final Class<?> entityClass,
                                   final String identifyKey, final MatchCondition matchCondition) {
        this.tableBuilder.addCondition(connectionCode, ConditionCode.NOT_BETWEEN_AND,
                entityClass, identifyKey, matchCondition);
        return this;
    }

    /**
     * <h3 class="en-US">Add query conditions for fuzzy matching values</h3>
     * <h3 class="zh-CN">添加模糊匹配值的查询条件</h3>
     *
     * @param entityClass <span class="en-US">Entity class</span>
     *                    <span class="zh-CN">实体类</span>
     * @param identifyKey <span class="en-US">Identify key</span>
     *                    <span class="zh-CN">识别代码</span>
     * @param matchRule   <span class="en-US">match rule string</span>
     *                    <span class="zh-CN">匹配规则字符串</span>
     * @return <span class="en-US">Current builder instance</span>
     * <span class="zh-CN">当前构建器实例对象</span>
     */
    public QueryBuilder like(final Class<?> entityClass, final String identifyKey, final String matchRule) {
        return this.like(ConnectionCode.AND, entityClass, identifyKey, matchRule);
    }

    /**
     * <h3 class="en-US">Add query conditions for fuzzy matching values</h3>
     * <h3 class="zh-CN">添加模糊匹配值的查询条件</h3>
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
     */
    public QueryBuilder like(final ConnectionCode connectionCode, final Class<?> entityClass,
                             final String identifyKey, final String matchRule) {
        this.tableBuilder.addCondition(connectionCode, ConditionCode.LIKE,
                entityClass, identifyKey, MatchCondition.condition(matchRule));
        return this;
    }

    /**
     * <h3 class="en-US">Add query conditions for fuzzy matching values</h3>
     * <h3 class="zh-CN">添加模糊匹配值的查询条件</h3>
     *
     * @param entityClass    <span class="en-US">Entity class</span>
     *                       <span class="zh-CN">实体类</span>
     * @param identifyKey    <span class="en-US">Identify key</span>
     *                       <span class="zh-CN">识别代码</span>
     * @param matchCondition <span class="en-US">Matching condition information</span>
     *                       <span class="zh-CN">匹配条件信息</span>
     * @return <span class="en-US">Current builder instance</span>
     * <span class="zh-CN">当前构建器实例对象</span>
     */
    public QueryBuilder like(final Class<?> entityClass, final String identifyKey,
                             final MatchCondition matchCondition) {
        return this.like(ConnectionCode.AND, entityClass, identifyKey, matchCondition);
    }

    /**
     * <h3 class="en-US">Add query conditions for fuzzy matching values</h3>
     * <h3 class="zh-CN">添加模糊匹配值的查询条件</h3>
     *
     * @param connectionCode <span class="en-US">Connection type code</span>
     *                       <span class="zh-CN">连接类型代码</span>
     * @param entityClass    <span class="en-US">Entity class</span>
     *                       <span class="zh-CN">实体类</span>
     * @param identifyKey    <span class="en-US">Identify key</span>
     *                       <span class="zh-CN">识别代码</span>
     * @param matchCondition <span class="en-US">Matching condition information</span>
     *                       <span class="zh-CN">匹配条件信息</span>
     * @return <span class="en-US">Current builder instance</span>
     * <span class="zh-CN">当前构建器实例对象</span>
     */
    public QueryBuilder like(final ConnectionCode connectionCode, final Class<?> entityClass,
                             final String identifyKey, final MatchCondition matchCondition) {
        this.tableBuilder.addCondition(connectionCode, ConditionCode.LIKE, entityClass, identifyKey, matchCondition);
        return this;
    }

    /**
     * <h3 class="en-US">Add query conditions for not fuzzy matching values</h3>
     * <h3 class="zh-CN">添加非模糊匹配值的查询条件</h3>
     *
     * @param entityClass <span class="en-US">Entity class</span>
     *                    <span class="zh-CN">实体类</span>
     * @param identifyKey <span class="en-US">Identify key</span>
     *                    <span class="zh-CN">识别代码</span>
     * @param matchRule   <span class="en-US">match rule string</span>
     *                    <span class="zh-CN">匹配规则字符串</span>
     * @return <span class="en-US">Current builder instance</span>
     * <span class="zh-CN">当前构建器实例对象</span>
     */
    public QueryBuilder notLike(final Class<?> entityClass, final String identifyKey, final String matchRule) {
        return this.notLike(ConnectionCode.AND, entityClass, identifyKey, matchRule);
    }

    /**
     * <h3 class="en-US">Add query conditions for not fuzzy matching values</h3>
     * <h3 class="zh-CN">添加非模糊匹配值的查询条件</h3>
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
     */
    public QueryBuilder notLike(final ConnectionCode connectionCode, final Class<?> entityClass,
                                final String identifyKey, final String matchRule) {
        this.tableBuilder.addCondition(connectionCode, ConditionCode.NOT_LIKE,
                entityClass, identifyKey, MatchCondition.condition(matchRule));
        return this;
    }

    /**
     * <h3 class="en-US">Add query conditions for not fuzzy matching values</h3>
     * <h3 class="zh-CN">添加非模糊匹配值的查询条件</h3>
     *
     * @param entityClass    <span class="en-US">Entity class</span>
     *                       <span class="zh-CN">实体类</span>
     * @param identifyKey    <span class="en-US">Identify key</span>
     *                       <span class="zh-CN">识别代码</span>
     * @param matchCondition <span class="en-US">Matching condition information</span>
     *                       <span class="zh-CN">匹配条件信息</span>
     * @return <span class="en-US">Current builder instance</span>
     * <span class="zh-CN">当前构建器实例对象</span>
     */
    public QueryBuilder notLike(final Class<?> entityClass, final String identifyKey,
                                final MatchCondition matchCondition) {
        return this.notLike(ConnectionCode.AND, entityClass, identifyKey, matchCondition);
    }

    /**
     * <h3 class="en-US">Add query conditions for not fuzzy matching values</h3>
     * <h3 class="zh-CN">添加非模糊匹配值的查询条件</h3>
     *
     * @param connectionCode <span class="en-US">Connection type code</span>
     *                       <span class="zh-CN">连接类型代码</span>
     * @param entityClass    <span class="en-US">Entity class</span>
     *                       <span class="zh-CN">实体类</span>
     * @param identifyKey    <span class="en-US">Identify key</span>
     *                       <span class="zh-CN">识别代码</span>
     * @param matchCondition <span class="en-US">Matching condition information</span>
     *                       <span class="zh-CN">匹配条件信息</span>
     * @return <span class="en-US">Current builder instance</span>
     * <span class="zh-CN">当前构建器实例对象</span>
     */
    public QueryBuilder notLike(final ConnectionCode connectionCode, final Class<?> entityClass,
                                final String identifyKey, final MatchCondition matchCondition) {
        this.tableBuilder.addCondition(connectionCode, ConditionCode.NOT_LIKE, entityClass, identifyKey, matchCondition);
        return this;
    }

    /**
     * <h3 class="en-US">Add query condition with null value</h3>
     * <h3 class="zh-CN">添加空值的查询条件</h3>
     *
     * @param entityClass <span class="en-US">Entity class</span>
     *                    <span class="zh-CN">实体类</span>
     * @param identifyKey <span class="en-US">Identify key</span>
     *                    <span class="zh-CN">识别代码</span>
     * @return <span class="en-US">Current builder instance</span>
     * <span class="zh-CN">当前构建器实例对象</span>
     */
    public QueryBuilder isNull(final Class<?> entityClass, final String identifyKey) {
        return this.isNull(ConnectionCode.AND, entityClass, identifyKey);
    }

    /**
     * <h3 class="en-US">Add query condition with null value</h3>
     * <h3 class="zh-CN">添加空值的查询条件</h3>
     *
     * @param connectionCode <span class="en-US">Connection type code</span>
     *                       <span class="zh-CN">连接类型代码</span>
     * @param entityClass    <span class="en-US">Entity class</span>
     *                       <span class="zh-CN">实体类</span>
     * @param identifyKey    <span class="en-US">Identify key</span>
     *                       <span class="zh-CN">识别代码</span>
     * @return <span class="en-US">Current builder instance</span>
     * <span class="zh-CN">当前构建器实例对象</span>
     */
    public QueryBuilder isNull(final ConnectionCode connectionCode, final Class<?> entityClass,
                               final String identifyKey) {
        this.tableBuilder.addCondition(connectionCode, ConditionCode.IS_NULL,
                entityClass, identifyKey, null);
        return this;
    }

    /**
     * <h3 class="en-US">Add query condition with not null value</h3>
     * <h3 class="zh-CN">添加非空值的查询条件</h3>
     *
     * @param entityClass <span class="en-US">Entity class</span>
     *                    <span class="zh-CN">实体类</span>
     * @param identifyKey <span class="en-US">Identify key</span>
     *                    <span class="zh-CN">识别代码</span>
     * @return <span class="en-US">Current builder instance</span>
     * <span class="zh-CN">当前构建器实例对象</span>
     */
    public QueryBuilder notNull(final Class<?> entityClass, final String identifyKey) {
        return this.notNull(ConnectionCode.AND, entityClass, identifyKey);
    }

    /**
     * <h3 class="en-US">Add query condition with not null value</h3>
     * <h3 class="zh-CN">添加非空值的查询条件</h3>
     *
     * @param connectionCode <span class="en-US">Connection type code</span>
     *                       <span class="zh-CN">连接类型代码</span>
     * @param entityClass    <span class="en-US">Entity class</span>
     *                       <span class="zh-CN">实体类</span>
     * @param identifyKey    <span class="en-US">Identify key</span>
     *                       <span class="zh-CN">识别代码</span>
     * @return <span class="en-US">Current builder instance</span>
     * <span class="zh-CN">当前构建器实例对象</span>
     */
    public QueryBuilder notNull(final ConnectionCode connectionCode, final Class<?> entityClass,
                                final String identifyKey) {
        this.tableBuilder.addCondition(connectionCode, ConditionCode.NOT_NULL,
                entityClass, identifyKey, null);
        return this;
    }

    /**
     * <h3 class="en-US">Adds a query condition where the value is contained in the given data</h3>
     * <h3 class="zh-CN">添加值包含在给定数据中的查询条件</h3>
     *
     * @param entityClass  <span class="en-US">Entity class</span>
     *                     <span class="zh-CN">实体类</span>
     * @param identifyKey  <span class="en-US">Identify key</span>
     *                     <span class="zh-CN">识别代码</span>
     * @param matchObjects <span class="en-US">array of matching datasets</span>
     *                     <span class="zh-CN">匹配数据集数组</span>
     * @return <span class="en-US">Current builder instance</span>
     * <span class="zh-CN">当前构建器实例对象</span>
     */
    public QueryBuilder in(final Class<?> entityClass, final String identifyKey, final Object... matchObjects) {
        return this.in(ConnectionCode.AND, entityClass, identifyKey, MatchCondition.condition(matchObjects));
    }

    /**
     * <h3 class="en-US">Adds a query condition where the value is contained in the given data</h3>
     * <h3 class="zh-CN">添加值包含在给定数据中的查询条件</h3>
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
     */
    public QueryBuilder in(final ConnectionCode connectionCode, final Class<?> entityClass, final String identifyKey,
                           final Object... matchObjects) {
        this.tableBuilder.addCondition(connectionCode, ConditionCode.IN,
                entityClass, identifyKey, MatchCondition.condition(matchObjects));
        return this;
    }

    /**
     * <h3 class="en-US">Adds a query condition where the value is contained in the given data</h3>
     * <h3 class="zh-CN">添加值包含在给定数据中的查询条件</h3>
     *
     * @param entityClass    <span class="en-US">Entity class</span>
     *                       <span class="zh-CN">实体类</span>
     * @param identifyKey    <span class="en-US">Identify key</span>
     *                       <span class="zh-CN">识别代码</span>
     * @param matchCondition <span class="en-US">Matching condition information</span>
     *                       <span class="zh-CN">匹配条件信息</span>
     * @return <span class="en-US">Current builder instance</span>
     * <span class="zh-CN">当前构建器实例对象</span>
     */
    public QueryBuilder in(final Class<?> entityClass, final String identifyKey, final MatchCondition matchCondition) {
        return this.in(ConnectionCode.AND, entityClass, identifyKey, matchCondition);
    }

    /**
     * <h3 class="en-US">Adds a query condition where the value is contained in the given data</h3>
     * <h3 class="zh-CN">添加值包含在给定数据中的查询条件</h3>
     *
     * @param connectionCode <span class="en-US">Connection type code</span>
     *                       <span class="zh-CN">连接类型代码</span>
     * @param entityClass    <span class="en-US">Entity class</span>
     *                       <span class="zh-CN">实体类</span>
     * @param identifyKey    <span class="en-US">Identify key</span>
     *                       <span class="zh-CN">识别代码</span>
     * @param matchCondition <span class="en-US">Matching condition information</span>
     *                       <span class="zh-CN">匹配条件信息</span>
     * @return <span class="en-US">Current builder instance</span>
     * <span class="zh-CN">当前构建器实例对象</span>
     */
    public QueryBuilder in(final ConnectionCode connectionCode, final Class<?> entityClass, final String identifyKey,
                           final MatchCondition matchCondition) {
        this.tableBuilder.addCondition(connectionCode, ConditionCode.IN, entityClass, identifyKey, matchCondition);
        return this;
    }

    /**
     * <h3 class="en-US">Add query conditions for condition groups</h3>
     * <h3 class="zh-CN">添加条件组的查询条件</h3>
     *
     * @param queryConditions <span class="en-US">Query conditions array</span>
     *                        <span class="zh-CN">查询条件数组</span>
     * @return <span class="en-US">Current builder instance</span>
     * <span class="zh-CN">当前构建器实例对象</span>
     */
    public QueryBuilder group(final QueryCondition... queryConditions) {
        return this.group(ConnectionCode.AND, queryConditions);
    }

    /**
     * <h3 class="en-US">Add query conditions for condition groups</h3>
     * <h3 class="zh-CN">添加条件组的查询条件</h3>
     *
     * @param connectionCode  <span class="en-US">Connection type code</span>
     *                        <span class="zh-CN">连接类型代码</span>
     * @param queryConditions <span class="en-US">Query conditions array</span>
     *                        <span class="zh-CN">查询条件数组</span>
     * @return <span class="en-US">Current builder instance</span>
     * <span class="zh-CN">当前构建器实例对象</span>
     */
    public QueryBuilder group(final ConnectionCode connectionCode, final QueryCondition... queryConditions) {
        this.tableBuilder.addCondition(connectionCode, ConditionCode.GROUP, null,
                Globals.DEFAULT_VALUE_STRING, MatchCondition.group(queryConditions));
        return this;
    }

    /**
     * <h3 class="en-US">Add or update sort types</h3>
     * <h3 class="zh-CN">添加或更新排序类型</h3>
     *
     * @param entityClass <span class="en-US">Entity class</span>
     *                    <span class="zh-CN">实体类</span>
     * @param identifyKey <span class="en-US">Identify key</span>
     *                    <span class="zh-CN">识别代码</span>
     * @return <span class="en-US">Current builder instance</span>
     * <span class="zh-CN">当前构建器实例对象</span>
     */
    public QueryBuilder orderByItem(final Class<?> entityClass, final String identifyKey) {
        return this.orderByItem(entityClass, identifyKey, OrderType.ASC);
    }

    /**
     * <h3 class="en-US">Add or update sort types</h3>
     * <h3 class="zh-CN">添加或更新排序类型</h3>
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
    public QueryBuilder orderByItem(final Class<?> entityClass, final String identifyKey, final OrderType orderType) {
        if (this.tableBuilder.matchEntity(entityClass)) {
            this.orderByColumns.stream()
                    .filter(currentColumn -> currentColumn.match(entityClass, identifyKey, orderType))
                    .findFirst()
                    .ifPresent(this.orderByColumns::remove);
            OrderByColumn orderByColumn = new OrderByColumn();
            orderByColumn.setEntityClass(entityClass);
            orderByColumn.setIdentifyKey(identifyKey);
            orderByColumn.setOrderType(orderType);
            this.orderByColumns.add(orderByColumn);
        }
        return this;
    }

    /**
     * <h3 class="en-US">Add or update group information</h3>
     * <h3 class="zh-CN">添加或更新分组信息</h3>
     *
     * @param entityClass <span class="en-US">Entity class</span>
     *                    <span class="zh-CN">实体类</span>
     * @param identifyKey <span class="en-US">Identify key</span>
     *                    <span class="zh-CN">识别代码</span>
     * @return <span class="en-US">Current builder instance</span>
     * <span class="zh-CN">当前构建器实例对象</span>
     */
    public QueryBuilder groupByItem(final Class<?> entityClass, final String identifyKey) {
        if (this.tableBuilder.matchEntity(entityClass)) {
            this.groupByColumns.stream()
                    .filter(currentColumn -> currentColumn.match(entityClass, identifyKey))
                    .findFirst()
                    .ifPresent(this.groupByColumns::remove);
            GroupByColumn groupByColumn = new GroupByColumn();
            groupByColumn.setEntityClass(entityClass);
            groupByColumn.setIdentifyKey(identifyKey);
            this.groupByColumns.add(groupByColumn);
        }
        return this;
    }

    /**
     * <h3 class="en-US">Configure current query using cache</h3>
     * <h3 class="zh-CN">设置当前查询使用缓存</h3>
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
     * <h3 class="en-US">Configure pager information of current query</h3>
     * <h3 class="zh-CN">设置当前查询的分页配置</h3>
     *
     * @param pageNo    <span class="en-US">Current page number</span>
     *                  <span class="zh-CN">当前页数</span>
     * @param pageLimit <span class="en-US">Page limit records count</span>
     *                  <span class="zh-CN">每页的记录数</span>
     * @return <span class="en-US">Current builder instance</span>
     * <span class="zh-CN">当前构建器实例对象</span>
     */
    public QueryBuilder configPager(final int pageNo, final int pageLimit) {
        this.pageLimit = queryLimit(pageLimit);
        this.offset = queryOffset(pageNo, this.pageLimit);
        return this;
    }

    /**
     * <h3 class="en-US">Configure current query will using for update record</h3>
     * <h3 class="zh-CN">设置当前查询是为了更新记录</h3>
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
     * <h3 class="en-US">Configure lock option for update record</h3>
     * <h3 class="zh-CN">设置更新记录的锁定选项</h3>
     *
     * @param lockOption <span class="en-US">Lock option</span>
     *                   <span class="zh-CN">锁定选项</span>
     * @return <span class="en-US">Current builder instance</span>
     * <span class="zh-CN">当前构建器实例对象</span>
     */
    public QueryBuilder lockOption(final LockOption lockOption) {
        this.lockOption = this.forUpdate ? lockOption : LockOption.NONE;
        return this;
    }

    /**
     * <h3 class="en-US">Generate QueryInfo instance by current configure information and using default query analyzer</h3>
     * <h3 class="zh-CN">器根据当前的配置信息生成查询信息实例对象</h3>
     *
     * @return <span class="en-US">Generated QueryInfo instance</span>
     * <span class="zh-CN">生成的查询条件信息实例对象</span>
     */
    public QueryInfo build() {
        QueryInfo queryInfo = new QueryInfo();
        queryInfo.setQueryTable(this.tableBuilder.generate());
        queryInfo.setCacheables(this.cacheables);
        queryInfo.setForUpdate(this.forUpdate);
        queryInfo.setOffset(this.offset);
        queryInfo.setLockOption(this.lockOption);
        queryInfo.setPageLimit(this.pageLimit);
        queryInfo.setGroupByColumns(this.groupByColumns);
        queryInfo.setOrderByColumns(this.orderByColumns);
        return queryInfo;
    }

    /**
     * <h3 class="en-US">Calculate query offset value</h3>
     * <h3 class="zh-CN">计算查询起始记录数</h3>
     *
     * @param pageNo    <span class="en-US">Page number</span>
     *                  <span class="zh-CN">页数</span>
     * @param pageLimit <span class="en-US">Page limit</span>
     *                  <span class="zh-CN">每页记录数</span>
     * @return <span class="en-US">Calculate result</span>
     * <span class="zh-CN">计算结果</span>
     */
    private static int queryOffset(final int pageNo, final int pageLimit) {
        int currentPage = (pageNo <= 0) ? DatabaseCommons.DEFAULT_PAGE_NO : pageNo;
        return (currentPage - 1) * queryLimit(pageLimit);
    }

    /**
     * <h3 class="en-US">Calculate query limit value</h3>
     * <h3 class="zh-CN">计算查询分页记录数</h3>
     *
     * @param pageLimit <span class="en-US">Page limit</span>
     *                  <span class="zh-CN">每页记录数</span>
     * @return <span class="en-US">Calculate result</span>
     * <span class="zh-CN">计算结果</span>
     */
    private static int queryLimit(final int pageLimit) {
        return pageLimit <= 0 ? DatabaseCommons.DEFAULT_PAGE_LIMIT : pageLimit;
    }

    /**
     * <h2 class="en-US">Abstract class for join builder</h2>
     * <h2 class="zh-CN">关联表构建器抽象类</h2>
     *
     * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
     * @version $Revision: 1.0.0 $ $Date: Aug 2, 2021 13:28:05 $
     */
    private static class AbstractJoinBuilder {
        /**
         * <span class="en-US">Entity class</span>
         * <span class="zh-CN">实体类</span>
         */
        protected final Class<?> entityClass;
        /**
         * <span class="en-US">Query count prefix</span>
         * <span class="zh-CN">查询计数前缀</span>
         */
        private final String aliasPrefix;
        /**
         * <span class="en-US">Query join instance list</span>
         * <span class="zh-CN">关联查询实例对象列表</span>
         */
        protected final List<JoinBuilder> joinBuilders;

        /**
         * <h3 class="en-US">Protected constructor method for abstract join builder class</h3>
         * <h3 class="zh-CN">关联表构建器抽象类的构造方法</h3>
         *
         * @param entityClass <span class="en-US">Entity class</span>
         *                    <span class="zh-CN">实体类</span>
         * @param aliasPrefix <span class="en-US">Query count prefix</span>
         *                    <span class="zh-CN">查询计数前缀</span>
         */
        protected AbstractJoinBuilder(final Class<?> entityClass, final String aliasPrefix) {
            this.entityClass = entityClass;
            this.aliasPrefix = aliasPrefix;
            this.joinBuilders = new ArrayList<>();
        }

        /**
         * <h3 class="en-US">Add associated query information table</h3>
         * <h3 class="zh-CN">添加关联查询信息表</h3>
         *
         * @param joinType       <span class="en-US">Table join type</span>
         *                       <span class="zh-CN">数据表关联类型</span>
         * @param entityClass    <span class="en-US">Entity class</span>
         *                       <span class="zh-CN">实体类</span>
         * @param referenceClass <span class="en-US">Reference entity class</span>
         *                       <span class="zh-CN">关联表实体类</span>
         */
        protected void joinTable(final JoinType joinType, final Class<?> entityClass, final Class<?> referenceClass) {
            if (ObjectUtils.nullSafeEquals(this.entityClass, entityClass)) {
                if (this.joinBuilders.stream().noneMatch(joinBuilder -> joinBuilder.match(joinType, referenceClass))) {
                    List<JoinColumn> joinColumns = new ArrayList<>();
                    EntityManager.tableConfig(entityClass)
                            .flatMap(tableConfig -> tableConfig.getReferenceConfigs().stream()
                                    .filter(referenceConfig -> referenceConfig.match(referenceClass))
                                    .findFirst())
                            .ifPresent(referenceConfig ->
                                    referenceConfig.getJoinColumnList()
                                            .forEach(joinConfig -> {
                                                JoinColumn joinColumn = new JoinColumn();
                                                joinColumn.setJoinKey(joinConfig.getCurrentField());
                                                joinColumn.setReferenceKey(joinConfig.getReferenceField());
                                                joinColumns.add(joinColumn);
                                            }));
                    this.joinBuilders.add(new JoinBuilder(joinType, referenceClass, this.joinAlias(), joinColumns));
                }
            } else {
                this.joinBuilders.forEach(joinBuilder ->
                        joinBuilder.joinTable(joinType, entityClass, referenceClass));
            }
        }

        /**
         * <h3 class="en-US">Checks if the given entity class is the current driver or driven table</h3>
         * <h3 class="zh-CN">检查给定的实体类是否是当前的驱动表或被驱动表</h3>
         *
         * @param entityClass <span class="en-US">Entity class</span>
         *                    <span class="zh-CN">实体类</span>
         * @return <span class="en-US">Match result</span>
         * <span class="zh-CN">匹配结果</span>
         */
        public boolean matchEntity(@Nonnull final Class<?> entityClass) {
            return this.entityClass.equals(entityClass)
                    || this.joinBuilders.stream().anyMatch(joinBuilder -> joinBuilder.matchEntity(entityClass));
        }

        /**
         * <h3 class="en-US">Table alias name</h3>
         * <h3 class="zh-CN">查询表的别名</h3>
         *
         * @return <span class="en-US">Generated alias name</span>
         * <span class="zh-CN">生成的别名</span>
         */
        protected final String joinAlias() {
            return this.aliasPrefix + "_" + this.joinBuilders.size();
        }
    }

    /**
     * <h2 class="en-US">Join table builder</h2>
     * <h2 class="zh-CN">数据表关联构建器</h2>
     *
     * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
     * @version $Revision: 1.0.0 $ $Date: Aug 2, 2021 13:36:27 $
     */
    private static final class JoinBuilder extends AbstractJoinBuilder {
        /**
         * <span class="en-US">Table join type</span>
         * <span class="zh-CN">数据表关联类型</span>
         */
        private final JoinType joinType;
        /**
         * <span class="en-US">Join columns list</span>
         * <span class="zh-CN">关联列信息列表</span>
         */
        private final List<JoinColumn> joinColumns;

        /**
         * <h3 class="en-US">Constructor method for join builder class</h3>
         * <h3 class="zh-CN">关联表构建器的构造方法</h3>
         *
         * @param joinType    <span class="en-US">Table join type</span>
         *                    <span class="zh-CN">数据表关联类型</span>
         * @param entityClass <span class="en-US">Entity class</span>
         *                    <span class="zh-CN">实体类</span>
         * @param aliasPrefix <span class="en-US">Query count prefix</span>
         *                    <span class="zh-CN">查询计数前缀</span>
         * @param joinColumns <span class="en-US">Join columns list</span>
         *                    <span class="zh-CN">关联列信息列表</span>
         */
        public JoinBuilder(final JoinType joinType, final Class<?> entityClass, final String aliasPrefix,
                           final List<JoinColumn> joinColumns) {
            super(entityClass, aliasPrefix);
            this.joinType = joinType;
            this.joinColumns = joinColumns;
        }

        /**
         * <h3 class="en-US">Checks if the given join type and entity class was same as current join type and entity class</h3>
         * <h3 class="zh-CN">检查给定的关联类型和实体类是否与当前的配置信息一致</h3>
         *
         * @param joinType    <span class="en-US">Table join type</span>
         *                    <span class="zh-CN">数据表关联类型</span>
         * @param entityClass <span class="en-US">Entity class</span>
         *                    <span class="zh-CN">实体类</span>
         * @return <span class="en-US">Match result</span>
         * <span class="zh-CN">匹配结果</span>
         */
        public boolean match(final JoinType joinType, final Class<?> entityClass) {
            return ObjectUtils.nullSafeEquals(this.joinType, joinType)
                    && ObjectUtils.nullSafeEquals(this.entityClass, entityClass);
        }

        /**
         * <h3 class="en-US">Generate JoinTable instance using current configure information</h3>
         * <h3 class="zh-CN">使用当前的配置信息生成查询关联信息定义实例对象</h3>
         *
         * @return <span class="en-US">Generated JoinTable instance</span>
         * <span class="zh-CN">生成的查询关联信息定义实例对象</span>
         */
        public JoinTable generate() {
            if (CollectionUtils.isEmpty(this.joinColumns)) {
                return null;
            }
            List<JoinTable> joinTables = new ArrayList<>();
            this.joinBuilders.forEach(joinBuilder ->
                    Optional.ofNullable(joinBuilder.generate()).ifPresent(joinTables::add));
            JoinTable joinTable = new JoinTable();
            joinTable.setJoinType(this.joinType);
            joinTable.setEntityClass(this.entityClass);
            joinTable.setAliasName(this.joinAlias());
            joinTable.setJoinColumns(this.joinColumns);
            joinTable.setJoinTables(joinTables);
            return joinTable;
        }
    }

    /**
     * <h2 class="en-US">Query table define builder</h2>
     * <h2 class="zh-CN">查询数据表信息定义构建器</h2>
     *
     * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
     * @version $Revision: 1.0.0 $ $Date: Aug 2, 2021 13:47:07 $
     */
    private static final class TableBuilder extends AbstractJoinBuilder {
        /**
         * <span class="en-US">Query item instance list</span>
         * <span class="zh-CN">查询项目实例对象列表</span>
         */
        private final List<QueryItem> itemList;
        /**
         * <span class="en-US">Query condition instance list</span>
         * <span class="zh-CN">查询条件实例对象列表</span>
         */
        private final List<QueryCondition> conditionList;

        /**
         * <h3 class="en-US">Private constructor method for query table builder class</h3>
         * <h3 class="zh-CN">查询信息表构建器的私有构造方法</h3>
         *
         * @param entityClass  <span class="en-US">Entity class</span>
         *                     <span class="zh-CN">实体类</span>
         * @param aliasPrefix  <span class="en-US">Query count prefix</span>
         *                     <span class="zh-CN">查询计数前缀</span>
         * @param identifyKeys <span class="en-US">Query columns identify key array</span>
         *                     <span class="zh-CN">查询列的识别代码数组</span>
         */
        private TableBuilder(final Class<?> entityClass, final String aliasPrefix, final String... identifyKeys) {
            super(entityClass, aliasPrefix);
            this.itemList = new ArrayList<>();
            this.conditionList = new ArrayList<>();
            Arrays.asList(identifyKeys)
                    .forEach(identifyKey -> this.itemList.add(QueryItem.queryColumn(entityClass, identifyKey)));
        }

        /**
         * <h3 class="en-US">Add query column to query item list</h3>
         * <h3 class="zh-CN">添加查询列信息到查询项目列表中</h3>
         *
         * @param entityClass <span class="en-US">Entity class</span>
         *                    <span class="zh-CN">实体类</span>
         * @param identifyKey <span class="en-US">Identify key</span>
         *                    <span class="zh-CN">识别代码</span>
         * @param distinct    <span class="en-US">Column distinct</span>
         *                    <span class="zh-CN">数据列去重</span>
         * @param aliasName   <span class="en-US">Item alias name</span>
         *                    <span class="zh-CN">查询项别名</span>
         */
        public void queryColumn(final Class<?> entityClass, final String identifyKey,
                                final boolean distinct, final String aliasName) {
            QueryColumn queryColumn =
                    QueryItem.queryColumn(entityClass, identifyKey, distinct, aliasName, Globals.DEFAULT_VALUE_INT);
            if (this.itemList.stream().noneMatch(queryItem -> queryItem.match(queryColumn))) {
                this.itemList.add(queryColumn);
            }
        }

        /**
         * <h3 class="en-US">Add query function to query item list</h3>
         * <h3 class="zh-CN">添加查询函数信息到查询项目列表中</h3>
         *
         * @param aliasName      <span class="en-US">Item alias name</span>
         *                       <span class="zh-CN">查询项别名</span>
         * @param sqlFunction    <span class="en-US">Function name</span>
         *                       <span class="zh-CN">函数名</span>
         * @param functionParams <span class="en-US">Function arguments array</span>
         *                       <span class="zh-CN">函数参数数组</span>
         */
        public void queryFunction(final String aliasName, final String sqlFunction, final QueryItem... functionParams) {
            int index = Globals.INITIALIZE_INT_VALUE;
            for (QueryItem functionParam : functionParams) {
                functionParam.setSortCode(index);
                index++;
            }
            QueryFunction queryFunction = QueryItem.queryFunction(aliasName, sqlFunction, functionParams);
            if (this.itemList.stream().noneMatch(queryItem -> queryItem.match(queryFunction))) {
                this.itemList.add(queryFunction);
            }
        }

        /**
         * <h3 class="en-US">Generate QueryTable instance using current configure information</h3>
         * <h3 class="zh-CN">使用当前的配置信息生成查询数据表信息定义实例对象</h3>
         *
         * @return <span class="en-US">Generated QueryTable instance</span>
         * <span class="zh-CN">生成的查询数据表信息定义实例对象</span>
         */
        public QueryTable generate() {
            if (CollectionUtils.isEmpty(this.itemList)) {
                EntityManager.tableConfig(this.entityClass)
                        .map(TableConfig::getColumnConfigs)
                        .ifPresent(columnConfigs ->
                                columnConfigs.forEach(columnConfig ->
                                        this.itemList.add(
                                                QueryItem.queryColumn(this.entityClass, columnConfig.columnName()))));
            }
            List<JoinTable> joinList = new ArrayList<>();
            this.joinBuilders.forEach(joinBuilder ->
                    Optional.ofNullable(joinBuilder.generate()).ifPresent(joinList::add));
            QueryTable queryTable = new QueryTable();
            queryTable.setAliasName(this.joinAlias());
            queryTable.setEntityClass(this.entityClass);
            queryTable.setConditionList(this.conditionList);
            queryTable.setItemList(this.itemList);
            queryTable.setJoinList(joinList);
            return queryTable;
        }

        /**
         * <h3 class="en-US">Add matching information to the matching list</h3>
         * <h3 class="zh-CN">添加匹配信息到匹配列表</h3>
         *
         * @param connectionCode <span class="en-US">Connection type code</span>
         *                       <span class="zh-CN">连接类型代码</span>
         * @param conditionCode  <span class="en-US">Match type code</span>
         *                       <span class="zh-CN">匹配类型代码</span>
         * @param entityClass    <span class="en-US">Entity class</span>
         *                       <span class="zh-CN">实体类</span>
         * @param identifyKey    <span class="en-US">Identify key</span>
         *                       <span class="zh-CN">识别代码</span>
         * @param matchCondition <span class="en-US">Matching condition information</span>
         *                       <span class="zh-CN">匹配条件信息</span>
         */
        private void addCondition(final ConnectionCode connectionCode, final ConditionCode conditionCode,
                                  final Class<?> entityClass, final String identifyKey,
                                  final MatchCondition matchCondition) {
            if (this.conditionList.stream().noneMatch(currentCondition ->
                    currentCondition.match(connectionCode, conditionCode, identifyKey, matchCondition))) {
                QueryCondition queryCondition = new QueryCondition();
                queryCondition.setConditionCode(conditionCode);
                queryCondition.setConnectionCode(connectionCode);
                queryCondition.setEntityClass(entityClass);
                queryCondition.setIdentifyKey(identifyKey);
                queryCondition.setMatchCondition(matchCondition);
                this.conditionList.add(queryCondition);
            } else {
                System.out.println("Condition exists");
            }
        }
    }
}
