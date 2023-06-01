/*
 * Copyright © 2003 Nervousync® Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of
 * Nervousync Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with Nervousync Studio.
 */
package org.nervousync.database.query.core;

import org.nervousync.database.beans.configs.table.TableConfig;
import org.nervousync.database.entity.EntityManager;
import org.nervousync.database.enumerations.join.JoinType;
import org.nervousync.database.exceptions.entity.EntityStatusException;
import org.nervousync.database.exceptions.record.QueryException;
import org.nervousync.database.query.condition.MatchCondition;
import org.nervousync.database.query.condition.QueryCondition;
import org.nervousync.database.query.operate.ConditionCode;
import org.nervousync.database.query.operate.ConnectionCode;
import org.nervousync.utils.ConvertUtils;
import org.nervousync.utils.SecurityUtils;
import org.nervousync.utils.StringUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The type Query table.
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision : 1.0 $ $Date: 2/8/2021 01:25 PM $
 */
public final class QueryTable {

    /**
     * The Entity class name.
     */
    private final String aliasName;
    /**
     * The Entity class name.
     */
    private final String tableName;
    /**
     * The Entity class.
     */
    private final Class<?> entityClass;
    /**
     * The Entity class name.
     */
    private final String databaseAlias;
    /**
     * The Query join list.
     */
    private final List<QueryJoin> queryJoinList;
    /**
     * The Query item list.
     */
    private final List<QueryItem> queryItemList;
    /**
     * The Query condition list.
     */
    private final List<QueryCondition> queryConditionList;

    /**
     * Instantiates a new Query table.
     *
     * @param tableConfig        the table config
     * @param aliasName          the alias name
     * @param queryJoinList      the query join list
     * @param queryItemList      the query item list
     * @param queryConditionList the query condition list
     */
    private QueryTable(TableConfig tableConfig, String aliasName, List<QueryJoin> queryJoinList,
                       List<QueryItem> queryItemList, List<QueryCondition> queryConditionList) {
        this.aliasName = StringUtils.isEmpty(aliasName) ? StringUtils.randomString(4) : aliasName;
        this.tableName = tableConfig.getTableName();
        this.entityClass = tableConfig.getDefineClass();
        this.databaseAlias = tableConfig.getAliasName();
        this.queryJoinList = queryJoinList;
        this.queryItemList = queryItemList;
        this.queryConditionList = queryConditionList;
    }

    /**
     * Gets alias name.
     *
     * @return the alias name
     */
    public String getAliasName() {
        return aliasName;
    }

    /**
     * Gets database alias.
     *
     * @return the database alias
     */
    public String getDatabaseAlias() {
        return databaseAlias;
    }

    /**
     * Gets table name.
     *
     * @return the table name
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * Gets entity class.
     *
     * @return the entity class
     */
    public Class<?> getEntityClass() {
        return entityClass;
    }

    /**
     * Gets the query join list.
     *
     * @return the query join list
     */
    public List<QueryJoin> getQueryJoinList() {
        return queryJoinList;
    }

    /**
     * Gets query item list.
     *
     * @return the query item list
     */
    public List<QueryItem> getQueryItemList() {
        return queryItemList;
    }

    /**
     * Gets the query condition list.
     *
     * @return the query condition list
     */
    public List<QueryCondition> getQueryConditionList() {
        return queryConditionList;
    }

    /**
     * Add query condition query builder.
     *
     * @param connCode       the connection code
     * @param conditionCode  the condition code
     * @param identifyName   column identify name
     * @param matchCondition the match condition
     * @throws QueryException the query exception
     */
    public void addQueryCondition(final ConnectionCode connCode, final ConditionCode conditionCode,
                                  final String identifyName, final MatchCondition matchCondition)
            throws QueryException {
        if (this.queryConditionList.stream().noneMatch(queryCondition ->
                queryCondition.match(connCode, conditionCode, identifyName, matchCondition))) {
            this.queryConditionList.add(new QueryCondition(connCode, conditionCode, identifyName, matchCondition));
        }
    }

    /**
     * New builder query builder.
     *
     * @param aliasName   the alias name
     * @param entityClass the entity class
     * @param countPrefix the count prefix
     * @return the query builder
     */
    public static QueryBuilder newBuilder(final String aliasName, final Class<?> entityClass, final int countPrefix) {
        return new QueryBuilder(aliasName, entityClass, countPrefix * 10);
    }

    /**
     * Cache key string.
     *
     * @return the string
     */
    public String cacheKey() {
        SortedMap<String, Object> cacheMap = new TreeMap<>();

        cacheMap.put("TableName", this.tableName);
        cacheMap.put("DatabaseAlias", this.databaseAlias);

        Map<String, String> joinMap = new HashMap<>();
        this.queryJoinList.forEach(queryJoin ->
                joinMap.put(queryJoin.getJoinType().toString(), queryJoin.getQueryTable().getTableName()));
        cacheMap.put("QueryJoin", joinMap);

        List<String> queryItems = new ArrayList<>();
        this.queryItemList.forEach(queryItem -> queryItems.add(queryItem.cacheKey()));
        queryItems.sort(String::compareTo);
        cacheMap.put("QueryItems", queryItems);

        List<String> conditionList = new ArrayList<>();
        this.queryConditionList.forEach(queryCondition -> conditionList.add(queryCondition.cacheKey()));
        conditionList.sort(String::compareTo);
        cacheMap.put("ConditionList", conditionList);

        return ConvertUtils.byteToHex(SecurityUtils.SHA256(cacheMap));
    }

    /**
     * The type Builder.
     */
    public static final class QueryBuilder {

        /**
         * The Entity class name.
         */
        private final String aliasName;
        /**
         * The Entity table config.
         */
        private final TableConfig tableConfig;
        /**
         * The Query join list.
         */
        private final List<QueryJoin.JoinBuilder> joinBuilderList;
        /**
         * The Query item list.
         */
        private final List<QueryItem> queryItemList;
        /**
         * The Query condition list.
         */
        private final List<QueryCondition> queryConditionList;
        private final AtomicInteger countPrefix;

        /**
         * Instantiates a new Builder.
         *
         * @param aliasName   the alias name
         * @param entityClass the entity class
         * @throws EntityStatusException the entity status exception
         */
        private QueryBuilder(final String aliasName, final Class<?> entityClass, final int countPrefix)
                throws EntityStatusException {
            if (entityClass == null) {
                throw new EntityStatusException("Entity class is null");
            }
            this.tableConfig = EntityManager.getInstance().retrieveTableConfig(entityClass);
            if (this.tableConfig == null) {
                throw new EntityStatusException("Entity not found! ");
            }
            this.countPrefix = new AtomicInteger(countPrefix);
            this.aliasName = StringUtils.isEmpty(aliasName) ? "T_" + this.countPrefix : aliasName;
            this.joinBuilderList = new ArrayList<>();
            this.queryItemList = new ArrayList<>();
            this.queryConditionList = new ArrayList<>();
        }

        /**
         * Join table builder.
         *
         * @param joinType       the join type
         * @param aliasName      the alias name
         * @param entityClass    the entity class
         * @param referenceClass the reference class
         * @throws EntityStatusException the entity status exception
         */
        public void joinTable(final JoinType joinType, final String aliasName, final Class<?> entityClass,
                              final Class<?> referenceClass) throws EntityStatusException {
            if (entityClass == null) {
                throw new EntityStatusException("Entity class is null");
            }
            if (this.match(entityClass)) {
                this.addJoinTable(joinType, aliasName, referenceClass);
            } else {
                this.joinBuilderList.forEach(joinBuilder ->
                        joinBuilder.addJoinTable(joinType, aliasName, entityClass, referenceClass));
            }
        }

        /**
         * Add join table.
         *
         * @param joinType       the join type
         * @param aliasName      the alias name
         * @param referenceClass the reference class
         */
        void addJoinTable(final JoinType joinType, final String aliasName, final Class<?> referenceClass) {
            if (referenceClass == null) {
                throw new EntityStatusException("Reference class is null");
            }

            for (QueryJoin.JoinBuilder joinBuilder : this.joinBuilderList) {
                if (joinBuilder.match(referenceClass)) {
                    return;
                }
            }
            QueryJoin.JoinBuilder joinBuilder =
                    Optional.ofNullable(this.tableConfig.findReferenceConfig(referenceClass.getName()))
                            .map(referenceConfig -> {
                                QueryJoin.JoinBuilder builder = QueryJoin.newBuilder(joinType, aliasName, referenceClass,
                                        this.countPrefix.incrementAndGet());
                                referenceConfig.getReferenceColumnList().forEach(joinColumnConfig ->
                                        builder.addJoinColumn(joinColumnConfig.getCurrentField(),
                                                joinColumnConfig.getReferenceField()));
                                return builder;
                            })
                            .orElseThrow(() -> new EntityStatusException("Reference config not found! "));
            this.joinBuilderList.add(joinBuilder);
        }

        /**
         * Add query column query builder.
         *
         * @param entityClass  the entity class
         * @param identifyName column identify name
         * @param distinct     the distinct
         * @param aliasName    the alias name
         * @throws QueryException the query exception
         */
        public void addQueryColumn(final Class<?> entityClass, final String identifyName, final boolean distinct,
                                   final String aliasName) throws QueryException {
            if (this.match(entityClass)) {
                this.addQueryItem(QueryItem.queryColumn(identifyName, distinct, aliasName));
            } else {
                this.joinBuilderList.forEach(joinBuilder ->
                        joinBuilder.addQueryColumn(entityClass, identifyName, distinct, aliasName));
            }
        }

        /**
         * Add query function query builder.
         *
         * @param entityClass    the entity class
         * @param aliasName      the alias name
         * @param sqlFunction    the sql function
         * @param functionParams the function params
         * @throws QueryException the query exception
         */
        public void addQueryFunction(final Class<?> entityClass, final String aliasName, final String sqlFunction,
                                     final QueryItem... functionParams) throws QueryException {
            if (this.match(entityClass)) {
                this.addQueryItem(QueryItem.queryFunction(aliasName, sqlFunction, functionParams));
            } else {
                this.joinBuilderList.stream()
                        .filter(joinBuilder -> joinBuilder.match(entityClass))
                        .forEach(joinBuilder ->
                                joinBuilder.addQueryFunction(entityClass, aliasName, sqlFunction, functionParams));
            }
        }

        /**
         * Add query condition query builder.
         *
         * @param entityClass    the entity class
         * @param connCode       the connection code
         * @param conditionCode  the condition code
         * @param identifyName   column identify name
         * @param matchCondition the match condition
         * @throws QueryException the query exception
         */
        public void addQueryCondition(final Class<?> entityClass, final ConnectionCode connCode,
                                      final ConditionCode conditionCode, final String identifyName,
                                      final MatchCondition matchCondition) throws QueryException {
            if (this.match(entityClass)) {
                this.addQueryCondition(
                        new QueryCondition(connCode, conditionCode, identifyName, matchCondition));
            } else {
                this.joinBuilderList
                        .forEach(joinBuilder ->
                                joinBuilder.addQueryCondition(entityClass, connCode, conditionCode,
                                        identifyName, matchCondition));
            }
        }

        /**
         * Build query table.
         *
         * @return the query table
         */
        public QueryTable build() {
            List<QueryJoin> queryJoinList = new ArrayList<>();
            this.joinBuilderList.forEach(joinBuilder -> queryJoinList.add(joinBuilder.build()));
            return new QueryTable(this.tableConfig, this.aliasName,
                    queryJoinList, this.queryItemList, this.queryConditionList);
        }

        /**
         * Contains boolean.
         *
         * @param entityClass the entity class
         * @return the boolean
         */
        public boolean contains(final Class<?> entityClass) {
            if (this.tableConfig.getDefineClass().equals(entityClass)) {
                return true;
            }

            for (QueryJoin.JoinBuilder joinBuilder : this.joinBuilderList) {
                if (joinBuilder.match(entityClass)) {
                    return true;
                }
            }

            return Boolean.FALSE;
        }

        /**
         * Match boolean.
         *
         * @param entityClass the entity class
         * @return the boolean
         */
        boolean match(final Class<?> entityClass) {
            return this.tableConfig.getDefineClass().equals(entityClass);
        }

        /**
         * Match boolean.
         *
         * @param databaseName the database name
         * @return the boolean
         */
        public boolean match(final String databaseName) {
            return Objects.equals(this.tableConfig.getAliasName(), databaseName);
        }

        /**
         * Analyze check boolean.
         *
         * @param entityClass    the entity class
         * @param referenceClass the reference class
         * @return the boolean
         */
        public boolean analyzeCheck(final Class<?> entityClass, final Class<?> referenceClass) {
            if (this.match(entityClass)) {
                return this.analyzeCheck(referenceClass);
            }

            for (QueryJoin.JoinBuilder joinBuilder : this.joinBuilderList) {
                if (joinBuilder.match(entityClass)) {
                    return joinBuilder.analyzeCheck(referenceClass);
                }
            }
            return Boolean.FALSE;
        }

        /**
         * Analyze check boolean.
         *
         * @param referenceClass the reference class
         * @return the boolean
         */
        boolean analyzeCheck(final Class<?> referenceClass) {
            return Optional.ofNullable(EntityManager.getInstance().retrieveTableConfig(referenceClass))
                    .map(referenceTable -> Objects.equals(this.tableConfig.getAliasName(), referenceTable.getAliasName()))
                    .orElse(Boolean.FALSE);
        }

        /**
         * Add query item.
         *
         * @param queryItem the query item
         */
        void addQueryItem(final QueryItem queryItem) {
            if (this.queryItemList.stream().noneMatch(currentItem -> currentItem.match(queryItem))) {
                this.queryItemList.add(queryItem);
            }
        }

        /**
         * Add query condition.
         *
         * @param queryCondition the query condition
         */
        void addQueryCondition(final QueryCondition queryCondition) {
            if (this.queryConditionList.stream().noneMatch(curCondition -> curCondition.equals(queryCondition))) {
                this.queryConditionList.add(queryCondition);
            }
        }
    }
}
