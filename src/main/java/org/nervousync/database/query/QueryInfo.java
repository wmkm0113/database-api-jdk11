/*
 * Copyright © 2003 Nervousync® Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of
 * Nervousync Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with Nervousync Studio.
 */
package org.nervousync.database.query;

import org.nervousync.commons.core.Globals;
import org.nervousync.database.commons.DatabaseCommons;
import org.nervousync.database.enumerations.join.JoinType;
import org.nervousync.database.enumerations.lock.LockOption;
import org.nervousync.database.exceptions.entity.EntityStatusException;
import org.nervousync.database.exceptions.record.QueryException;
import org.nervousync.database.query.condition.MatchCondition;
import org.nervousync.database.query.condition.QueryCondition;
import org.nervousync.database.query.core.QueryItem;
import org.nervousync.database.query.core.QueryTable;
import org.nervousync.database.query.group.GroupByColumn;
import org.nervousync.database.query.operate.ConditionCode;
import org.nervousync.database.query.operate.ConnectionCode;
import org.nervousync.database.query.orderby.OrderByColumn;
import org.nervousync.utils.ConvertUtils;
import org.nervousync.utils.SecurityUtils;

import java.util.*;

/**
 * The type Query info.
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision : 1.0 $ $Date: 10/28/2020 11:46 $
 */
public final class QueryInfo {

	/**
	 * The Query table.
	 */
	private final QueryTable queryTable;
	/**
	 * Query order by cause
	 */
	private final List<OrderByColumn> orderByColumns;
	/**
	 * Query order by cause
	 */
	private final List<GroupByColumn> groupByColumns;
	/**
	 * Number of records to include in result set
	 */
	private final int pageLimit;
	/**
	 * Value to start counting records from
	 */
	private final int offset;
	/**
	 * Value for cacheables status
	 */
	private final boolean cacheables;
	/**
	 * Value for update status
	 */
	private final boolean forUpdate;
	/**
	 * Lock option configure
	 */
	private final LockOption lockOption;
	/**
	 * Query was analyzed
	 */
	private final boolean analyzed;

	/**
	 * Instantiates a new Query info.
	 *
	 * @param queryTable     the query table
	 * @param orderByColumns the order by columns
	 * @param groupByColumns the group by columns
	 * @param pageLimit      the limit size
	 * @param offset         the offset
	 * @param cacheables     the cacheables
	 * @param forUpdate      the for update
	 * @param lockOption     the lock option
	 * @param analyzed       the analyzed
	 */
	private QueryInfo(final QueryTable queryTable, final List<OrderByColumn> orderByColumns,
	                  final List<GroupByColumn> groupByColumns, final int pageLimit, final int offset,
	                  final boolean cacheables, final boolean forUpdate, final LockOption lockOption,
	                  final boolean analyzed) {
		this.queryTable = queryTable;
		this.orderByColumns = orderByColumns;
		this.groupByColumns = groupByColumns;
		this.pageLimit = pageLimit;
		this.offset = offset;
		this.cacheables = cacheables;
		this.forUpdate = forUpdate;
		this.lockOption = lockOption;
		this.analyzed = analyzed;
	}

	/**
	 * Gets query table.
	 *
	 * @return the query table
	 */
	public QueryTable getQueryTable() {
		return queryTable;
	}

	/**
	 * Gets order by columns.
	 *
	 * @return the order by columns
	 */
	public List<OrderByColumn> getOrderByColumns() {
		return orderByColumns;
	}

	/**
	 * Gets group by columns.
	 *
	 * @return the group by columns
	 */
	public List<GroupByColumn> getGroupByColumns() {
		return groupByColumns;
	}

	/**
	 * Gets page limit.
	 *
	 * @return the page limit
	 */
	public int getPageLimit() {
		return pageLimit;
	}

	/**
	 * Gets offset.
	 *
	 * @return the offset
	 */
	public int getOffset() {
		return offset;
	}

	/**
	 * Is cacheables boolean.
	 *
	 * @return the boolean
	 */
	public boolean isCacheables() {
		return cacheables;
	}

	/**
	 * Is for update boolean.
	 *
	 * @return the boolean
	 */
	public boolean isForUpdate() {
		return forUpdate;
	}

	/**
	 * Gets lock option.
	 *
	 * @return the lock option
	 */
	public LockOption getLockOption() {
		return lockOption;
	}

	/**
	 * Is analyzed boolean.
	 *
	 * @return the boolean
	 */
	public boolean isAnalyzed() {
		return analyzed;
	}

	/**
	 * Pager query boolean.
	 *
	 * @return boolean
	 */
	public boolean pagerQuery() {
		return this.offset != Globals.DEFAULT_VALUE_INT && this.pageLimit != Globals.DEFAULT_VALUE_INT;
	}

	/**
	 * Cache key string.
	 *
	 * @return the string
	 */
	public String cacheKey() {
		if (this.forUpdate || !this.cacheables) {
			return Globals.DEFAULT_VALUE_STRING;
		}

		SortedMap<String, Object> queryMap = new TreeMap<>();

		queryMap.put("QueryTable", this.queryTable.cacheKey());

		Map<String, String> orderByMap = new HashMap<>();
		this.orderByColumns.forEach(orderByColumn ->
				orderByMap.put(orderByColumn.cacheKey(), orderByColumn.getOrderByType().toString()));
		queryMap.put("OrderBy", orderByMap);

		Map<Class<?>, String> groupByMap = new HashMap<>();
		this.groupByColumns.forEach(groupByColumn ->
				groupByMap.put(groupByColumn.getEntityClass(), groupByColumn.getIdentifyKey()));
		queryMap.put("GroupBy", groupByMap);

		queryMap.put("PageLimit", this.pageLimit);
		queryMap.put("Offset", this.offset);

		return ConvertUtils.byteToHex(SecurityUtils.SHA256(queryMap));
	}

	/**
	 * New builder builder.
	 *
	 * @param entityClass the entity class
	 * @return the builder
	 */
	public static Builder newBuilder(final Class<?> entityClass) {
		return new Builder(Globals.DEFAULT_VALUE_STRING, entityClass, Boolean.FALSE);
	}

	/**
	 * New builder builder.
	 *
	 * @param aliasName   the alias name
	 * @param entityClass the entity class
	 * @return the builder
	 */
	public static Builder newBuilder(final String aliasName, final Class<?> entityClass) {
		return new Builder(aliasName, entityClass, Boolean.FALSE);
	}

	/**
	 * Analyze builder builder.
	 *
	 * @param aliasName   the alias name
	 * @param entityClass the entity class
	 * @return the builder
	 */
	public static Builder analyzeBuilder(final String aliasName, final Class<?> entityClass) {
		return new Builder(aliasName, entityClass, Boolean.TRUE);
	}

	/**
	 * The type Builder.
	 */
	public static final class Builder {

		/**
		 * The Query builder.
		 */
		private final QueryTable.QueryBuilder queryBuilder;
		/**
		 * Query order by cause
		 */
		private final List<OrderByColumn> orderByColumns;
		/**
		 * Query order by cause
		 */
		private final List<GroupByColumn> groupByColumns;
		/**
		 * Number of records to include in result set
		 */
		private int pageLimit = Globals.DEFAULT_VALUE_INT;
		/**
		 * Value to start counting records from
		 */
		private int offset = Globals.INITIALIZE_INT_VALUE;
		/**
		 * Value for cacheables status
		 */
		private boolean cacheables = Boolean.FALSE;
		/**
		 * Value for update status
		 */
		private boolean forUpdate = Boolean.FALSE;
		/**
		 * Lock option configure
		 */
		private LockOption lockOption = LockOption.NONE;
		/**
		 * Build an analysis query
		 */
		private final boolean analyzed;

		/**
		 * Instantiates a new Builder.
		 *
		 * @param aliasName   the alias name
		 * @param entityClass the entity class
		 * @param analyzed    the analyzed
		 */
		private Builder(final String aliasName, final Class<?> entityClass, final boolean analyzed) {
			this.queryBuilder = QueryTable.newBuilder(aliasName, entityClass, Globals.INITIALIZE_INT_VALUE);
			this.orderByColumns = new ArrayList<>();
			this.groupByColumns = new ArrayList<>();
			this.analyzed = analyzed;
		}

		/**
		 * Add join table builder.
		 *
		 * @param joinType       the join type
		 * @param aliasName      the alias name
		 * @param entityClass    the entity class
		 * @param referenceClass the reference class
		 * @throws EntityStatusException the entity status exception
		 */
		public void joinTable(final JoinType joinType, final String aliasName, final Class<?> entityClass,
		                      final Class<?> referenceClass) throws EntityStatusException {
			if (this.analyzed && !this.queryBuilder.analyzeCheck(entityClass, referenceClass)) {
				throw new EntityStatusException("Analyze check failed! ");
			}
			this.queryBuilder.joinTable(joinType, aliasName, entityClass, referenceClass);
		}

		/**
		 * Add query column query builder.
		 *
		 * @param entityClass the entity class
		 * @param identifyKey the identify key
		 */
		public void queryColumn(final Class<?> entityClass, final String identifyKey) {
			this.queryColumn(entityClass, identifyKey, Boolean.FALSE);
		}

		/**
		 * Add query column query builder.
		 *
		 * @param entityClass the entity class
		 * @param identifyKey the identify key
		 * @param distinct    the distinct
		 */
		public void queryColumn(final Class<?> entityClass, final String identifyKey, final boolean distinct) {
			this.queryColumn(entityClass, identifyKey, distinct, Globals.DEFAULT_VALUE_STRING);
		}

		/**
		 * Add query column query builder.
		 *
		 * @param entityClass the entity class
		 * @param identifyKey the identify key
		 * @param distinct    the distinct
		 * @param aliasName   the alias name
		 */
		public void queryColumn(final Class<?> entityClass, final String identifyKey, final boolean distinct,
		                        final String aliasName) {
			this.queryBuilder.addQueryColumn(entityClass, identifyKey, distinct, aliasName);
		}

		/**
		 * Add query function.
		 *
		 * @param entityClass    the entity class
		 * @param aliasName      the alias name
		 * @param sqlFunction    SQL function
		 * @param functionParams Function parameter array
		 */
		public void queryFunction(final Class<?> entityClass, final String aliasName, final String sqlFunction,
		                          final QueryItem... functionParams) {
			this.queryBuilder.addQueryFunction(entityClass, aliasName, sqlFunction, functionParams);
		}

		/**
		 * Add greater condition query builder.
		 *
		 * @param entityClass    the entity class
		 * @param identifyKey    the identify key
		 * @param matchCondition the match condition
		 */
		public void greater(final Class<?> entityClass, final String identifyKey, final MatchCondition matchCondition) {
			this.greater(ConnectionCode.AND, entityClass, identifyKey, matchCondition);
		}

		/**
		 * Add greater condition query builder.
		 *
		 * @param connCode       the connection code
		 * @param entityClass    the entity class
		 * @param identifyKey    the identify key
		 * @param matchCondition the match condition
		 */
		public void greater(final ConnectionCode connCode, final Class<?> entityClass, final String identifyKey,
		                    final MatchCondition matchCondition) {
			this.add(connCode, ConditionCode.GREATER, entityClass, identifyKey, matchCondition);
		}

		/**
		 * Add greater equal condition query builder.
		 *
		 * @param entityClass    the entity class
		 * @param identifyKey    the identify key
		 * @param matchCondition the match condition
		 */
		public void greaterEqual(final Class<?> entityClass, final String identifyKey,
		                         final MatchCondition matchCondition) {
			this.greaterEqual(ConnectionCode.AND, entityClass, identifyKey, matchCondition);
		}

		/**
		 * Add greater equal condition query builder.
		 *
		 * @param connCode       the connection code
		 * @param entityClass    the entity class
		 * @param identifyKey    the identify key
		 * @param matchCondition the match condition
		 */
		public void greaterEqual(final ConnectionCode connCode, final Class<?> entityClass, final String identifyKey,
		                         final MatchCondition matchCondition) {
			this.add(connCode, ConditionCode.GREATER_EQUAL, entityClass, identifyKey, matchCondition);
		}

		/**
		 * Add less condition query builder.
		 *
		 * @param entityClass    the entity class
		 * @param identifyKey    the identify key
		 * @param matchCondition the match condition
		 */
		public void less(final Class<?> entityClass, final String identifyKey, final MatchCondition matchCondition) {
			this.less(ConnectionCode.AND, entityClass, identifyKey, matchCondition);
		}

		/**
		 * Add less condition query builder.
		 *
		 * @param connCode       the connection code
		 * @param entityClass    the entity class
		 * @param identifyKey    the identify key
		 * @param matchCondition the match condition
		 */
		public void less(final ConnectionCode connCode, final Class<?> entityClass, final String identifyKey,
		                 final MatchCondition matchCondition) {
			this.add(connCode, ConditionCode.LESS, entityClass, identifyKey, matchCondition);
		}

		/**
		 * Add less equal condition query builder.
		 *
		 * @param entityClass    the entity class
		 * @param identifyKey    the identify key
		 * @param matchCondition the match condition
		 */
		public void lessEqual(final Class<?> entityClass, final String identifyKey,
		                      final MatchCondition matchCondition) {
			this.lessEqual(ConnectionCode.AND, entityClass, identifyKey, matchCondition);
		}

		/**
		 * Add less equal condition query builder.
		 *
		 * @param connCode       the connection code
		 * @param entityClass    the entity class
		 * @param identifyKey    the identify key
		 * @param matchCondition the match condition
		 */
		public void lessEqual(final ConnectionCode connCode, final Class<?> entityClass, final String identifyKey,
		                      final MatchCondition matchCondition) {
			this.add(connCode, ConditionCode.LESS_EQUAL, entityClass, identifyKey, matchCondition);
		}

		/**
		 * Add equal condition query builder.
		 *
		 * @param entityClass    the entity class
		 * @param identifyKey    the identify key
		 * @param matchCondition the match condition
		 */
		public void equal(final Class<?> entityClass, final String identifyKey, final MatchCondition matchCondition) {
			this.equal(ConnectionCode.AND, entityClass, identifyKey, matchCondition);
		}

		/**
		 * Add equal condition query builder.
		 *
		 * @param connCode       the connection code
		 * @param entityClass    the entity class
		 * @param identifyKey    the identify key
		 * @param matchCondition the match condition
		 */
		public void equal(final ConnectionCode connCode, final Class<?> entityClass, final String identifyKey,
		                  final MatchCondition matchCondition) {
			this.add(connCode, ConditionCode.EQUAL, entityClass, identifyKey, matchCondition);
		}

		/**
		 * Add not equal condition query builder.
		 *
		 * @param entityClass    the entity class
		 * @param identifyKey    the identify key
		 * @param matchCondition the match condition
		 */
		public void notEqual(final Class<?> entityClass, final String identifyKey,
		                     final MatchCondition matchCondition) {
			this.notEqual(ConnectionCode.AND, entityClass, identifyKey, matchCondition);
		}

		/**
		 * Add not equal condition query builder.
		 *
		 * @param connCode       the connection code
		 * @param entityClass    the entity class
		 * @param identifyKey    the identify key
		 * @param matchCondition the match condition
		 */
		public void notEqual(final ConnectionCode connCode, final Class<?> entityClass, final String identifyKey,
		                     final MatchCondition matchCondition) {
			this.add(connCode, ConditionCode.NOT_EQUAL, entityClass, identifyKey, matchCondition);
		}

		/**
		 * Add between and condition query builder.
		 *
		 * @param entityClass    the entity class
		 * @param identifyKey    the identify key
		 * @param matchCondition the match condition
		 */
		public void betweenAnd(final Class<?> entityClass, final String identifyKey,
		                       final MatchCondition matchCondition) {
			this.betweenAnd(ConnectionCode.AND, identifyKey, entityClass, matchCondition);
		}

		/**
		 * Add between and condition query builder.
		 *
		 * @param connCode       the connection code
		 * @param identifyKey    the identify key
		 * @param entityClass    the entity class
		 * @param matchCondition the match condition
		 */
		public void betweenAnd(final ConnectionCode connCode, final String identifyKey, final Class<?> entityClass,
		                       final MatchCondition matchCondition) {
			this.add(connCode, ConditionCode.BETWEEN_AND, entityClass, identifyKey, matchCondition);
		}

		/**
		 * Add not between and condition query builder.
		 *
		 * @param entityClass    the entity class
		 * @param identifyKey    the identify key
		 * @param matchCondition the match condition
		 */
		public void notBetweenAnd(final Class<?> entityClass, final String identifyKey,
		                          final MatchCondition matchCondition) {
			this.notBetweenAnd(ConnectionCode.AND, identifyKey, entityClass, matchCondition);
		}

		/**
		 * Add not between and condition query builder.
		 *
		 * @param connCode       the connection code
		 * @param identifyKey    the identify key
		 * @param entityClass    the entity class
		 * @param matchCondition the match condition
		 */
		public void notBetweenAnd(final ConnectionCode connCode, final String identifyKey,
		                          final Class<?> entityClass, final MatchCondition matchCondition) {
			this.add(connCode, ConditionCode.NOT_BETWEEN_AND,
					entityClass, identifyKey, matchCondition);
		}

		/**
		 * Add like condition query builder.
		 *
		 * @param entityClass    the entity class
		 * @param identifyKey    the identify key
		 * @param matchCondition the match condition
		 */
		public void like(final Class<?> entityClass, final String identifyKey, final MatchCondition matchCondition) {
			this.like(ConnectionCode.AND, entityClass, identifyKey, matchCondition);
		}

		/**
		 * Add like condition query builder.
		 *
		 * @param connCode       the connection code
		 * @param entityClass    the entity class
		 * @param identifyKey    the identify key
		 * @param matchCondition the match condition
		 */
		public void like(final ConnectionCode connCode, final Class<?> entityClass, final String identifyKey,
		                 final MatchCondition matchCondition) {
			this.add(connCode, ConditionCode.LIKE,
					entityClass, identifyKey, matchCondition);
		}

		/**
		 * Add not like condition query builder.
		 *
		 * @param entityClass    the entity class
		 * @param identifyKey    the identify key
		 * @param matchCondition the match condition
		 */
		public void notLike(final Class<?> entityClass, final String identifyKey, final MatchCondition matchCondition) {
			this.notLike(ConnectionCode.AND, entityClass, identifyKey, matchCondition);
		}

		/**
		 * Add not like condition query builder.
		 *
		 * @param connCode       the connection code
		 * @param entityClass    the entity class
		 * @param identifyKey    the identify key
		 * @param matchCondition the match condition
		 */
		public void notLike(final ConnectionCode connCode, final Class<?> entityClass, final String identifyKey,
		                    final MatchCondition matchCondition) {
			this.add(connCode, ConditionCode.NOT_LIKE,
					entityClass, identifyKey, matchCondition);
		}

		/**
		 * Add is null condition query builder.
		 *
		 * @param entityClass the entity class
		 * @param identifyKey the identify key
		 */
		public void isNull(final Class<?> entityClass, final String identifyKey) {
			this.isNull(ConnectionCode.AND, entityClass, identifyKey);
		}

		/**
		 * Add is null condition query builder.
		 *
		 * @param connCode    the connection code
		 * @param entityClass the entity class
		 * @param identifyKey the identify key
		 */
		public void isNull(final ConnectionCode connCode, final Class<?> entityClass, final String identifyKey) {
			this.add(connCode, ConditionCode.IS_NULL, entityClass, identifyKey, null);
		}

		/**
		 * Add not null condition query builder.
		 *
		 * @param entityClass the entity class
		 * @param identifyKey the identify key
		 */
		public void notNull(final Class<?> entityClass, final String identifyKey) {
			this.notNull(ConnectionCode.AND, entityClass, identifyKey);
		}

		/**
		 * Add not null condition query builder.
		 *
		 * @param connCode    the connection code
		 * @param entityClass the entity class
		 * @param identifyKey the identify key
		 */
		public void notNull(final ConnectionCode connCode, final Class<?> entityClass, final String identifyKey) {
			this.add(connCode, ConditionCode.NOT_NULL,
					entityClass, identifyKey, null);
		}

		/**
		 * Add in condition query builder.
		 *
		 * @param entityClass    the entity class
		 * @param identifyKey    the identify key
		 * @param matchCondition the match condition
		 */
		public void in(final Class<?> entityClass, final String identifyKey, final MatchCondition matchCondition) {
			this.in(ConnectionCode.AND, entityClass, identifyKey, matchCondition);
		}

		/**
		 * Add in condition query builder.
		 *
		 * @param connCode       the connection code
		 * @param entityClass    the entity class
		 * @param identifyKey    the identify key
		 * @param matchCondition the match condition
		 */
		public void in(final ConnectionCode connCode, final Class<?> entityClass, final String identifyKey,
		               final MatchCondition matchCondition) {
			this.add(connCode, ConditionCode.IN, entityClass, identifyKey, matchCondition);
		}

		/**
		 * Add group condition query builder.
		 *
		 * @param queryConditions the query conditions
		 */
		public void group(final QueryCondition... queryConditions) {
			this.group(ConnectionCode.AND, queryConditions);
		}

		/**
		 * Add group condition query builder.
		 *
		 * @param connCode        the connection code
		 * @param queryConditions the query conditions
		 */
		public void group(final ConnectionCode connCode, final QueryCondition... queryConditions) {
			this.add(connCode, ConditionCode.GROUP, null,
					Globals.DEFAULT_VALUE_STRING, MatchCondition.group(queryConditions));
		}

		/**
		 * Add condition.
		 *
		 * @param connCode the connection code
		 * @param conditionCode  the condition code
		 * @param entityClass    the entity class
		 * @param identifyKey    the identify key
		 * @param matchCondition the match condition
		 */
		private void add(final ConnectionCode connCode, final ConditionCode conditionCode,
		                 final Class<?> entityClass, final String identifyKey, final MatchCondition matchCondition) {
			this.queryBuilder.addQueryCondition(entityClass, connCode, conditionCode,
					identifyKey, matchCondition);
		}

		/**
		 * Add order by column
		 *
		 * @param entityClass the entity class
		 * @param columnName  column name
		 */
		public void orderByItem(final Class<?> entityClass, final String columnName) {
			this.orderByItem(entityClass, columnName, OrderByColumn.OrderByType.ASC);
		}

		/**
		 * Add order by column and sort type
		 *
		 * @param entityClass  the entity class
		 * @param identifyName column identify name
		 * @param orderByType  sort type
		 * @throws QueryException the query exception
		 */
		public void orderByItem(final Class<?> entityClass, final String identifyName,
		                        final OrderByColumn.OrderByType orderByType) throws QueryException {
			if (this.queryBuilder.contains(entityClass)) {
				this.orderByColumns.add(new OrderByColumn(orderByType, entityClass, identifyName));
			}
		}

		/**
		 * Add order by column and sort type
		 *
		 * @param entityClass  the entity class
		 * @param identifyName column identify name
		 * @throws QueryException the query exception
		 */
		public void groupByItem(final Class<?> entityClass, final String identifyName) throws QueryException {
			if (this.queryBuilder.contains(entityClass)) {
				this.groupByColumns.add(new GroupByColumn(entityClass, identifyName));
			}
		}

		/**
		 * Use cache builder.
		 *
		 * @param cacheables the cacheables
		 */
		public void useCache(final boolean cacheables) {
			this.cacheables = cacheables;
		}

		/**
		 * Config pager query builder.
		 *
		 * @param pageNo    the page no
		 * @param pageLimit the page limit
		 */
		public void configPager(final int pageNo, final int pageLimit) {
			this.pageLimit = queryLimit(pageLimit);
			this.offset = queryOffset(pageNo, this.pageLimit);
		}

		public void pagerParameter(final int offset, final int pageLimit) {
			this.offset = (offset < 0) ? Globals.INITIALIZE_INT_VALUE : offset;
			this.pageLimit = queryLimit(pageLimit);
		}

		/**
		 * For update query builder.
		 *
		 * @param forUpdate the for update
		 */
		public void forUpdate(final boolean forUpdate) {
			this.forUpdate = forUpdate;
		}

		/**
		 * Lock option builder.
		 *
		 * @param lockOption the lock option
		 */
		public void lockOption(final LockOption lockOption) {
			this.lockOption = this.forUpdate ? lockOption : LockOption.NONE;
		}

		/**
		 * Analyze builder boolean.
		 *
		 * @return the boolean
		 */
		public boolean analyzeBuilder() {
			return this.analyzed;
		}

		/**
		 * Match database alias boolean.
		 *
		 * @param databaseName the database name
		 * @return the boolean
		 */
		public boolean match(final String databaseName) {
			return this.queryBuilder.match(databaseName);
		}

		/**
		 * Build query info.
		 *
		 * @return the query info
		 */
		public QueryInfo build() {
			return new QueryInfo(this.queryBuilder.build(), this.orderByColumns, this.groupByColumns, this.pageLimit,
					this.offset, this.cacheables, this.forUpdate, this.lockOption, this.analyzed);
		}
	}

	private static int queryOffset(final int pageNo, final int pageLimit) {
		int currentPage = (pageNo <= 0) ? DatabaseCommons.DEFAULT_PAGE_NO : pageNo;
		return (currentPage - 1) * queryLimit(pageLimit);
	}

	private static int queryLimit(final int pageLimit) {
		return pageLimit <= 0 ? DatabaseCommons.DEFAULT_PAGE_LIMIT : pageLimit;
	}
}
