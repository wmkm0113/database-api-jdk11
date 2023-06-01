/*
 * Copyright © 2003 Nervousync® Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of
 * Nervousync Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with Nervousync Studio.
 */

package org.nervousync.database.query.core;

import org.nervousync.database.enumerations.join.JoinType;
import org.nervousync.database.exceptions.entity.EntityStatusException;
import org.nervousync.database.exceptions.record.QueryException;
import org.nervousync.database.query.condition.MatchCondition;
import org.nervousync.database.query.operate.ConditionCode;
import org.nervousync.database.query.operate.ConnectionCode;
import org.nervousync.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Query join.
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision : 1.0 $ $Date: 2/8/2021 01:33 PM $
 */
public final class QueryJoin {

	/**
	 * The Join type.
	 */
	private final JoinType joinType;
	/**
	 * The Query table.
	 */
	private final QueryTable queryTable;
	/**
	 * The Join columns.
	 */
	private final List<JoinColumn> joinColumns;

	/**
	 * Instantiates a new Query join.
	 *
	 * @param joinType    the join type
	 * @param queryTable  the query table
	 * @param joinColumns the join columns
	 */
	private QueryJoin(JoinType joinType, QueryTable queryTable, List<JoinColumn> joinColumns) {
		this.joinType = joinType;
		this.queryTable = queryTable;
		this.joinColumns = joinColumns;
	}

	/**
	 * Gets join type.
	 *
	 * @return the join type
	 */
	public JoinType getJoinType() {
		return joinType;
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
	 * Gets join columns.
	 *
	 * @return the join columns
	 */
	public List<JoinColumn> getJoinColumns() {
		return joinColumns;
	}

	/**
	 * New builder join builder.
	 *
	 * @param joinType    the join type
	 * @param aliasName   the alias name
	 * @param entityClass the entity class
	 * @param countPrefix the count prefix
	 * @return the join builder
	 * @throws QueryException the query exception
	 */
	static JoinBuilder newBuilder(final JoinType joinType, final String aliasName, final Class<?> entityClass,
	                              final int countPrefix) {
		return new JoinBuilder(joinType, aliasName, entityClass, countPrefix);
	}

	/**
	 * The type Join builder.
	 */
	static final class JoinBuilder {

		/**
		 * The Join type.
		 */
		private final JoinType joinType;
		/**
		 * The Query builder.
		 */
		private final QueryTable.QueryBuilder queryBuilder;
		/**
		 * The Join columns.
		 */
		private final List<JoinColumn> joinColumns;

		/**
		 * Instantiates a new Join builder.
		 *
		 * @param joinType    the join type
		 * @param aliasName   the alias name
		 * @param entityClass the entity class
		 * @throws QueryException the query exception
		 */
		private JoinBuilder(final JoinType joinType, final String aliasName, final Class<?> entityClass,
		                    final int countPrefix) {
			this.joinType = joinType;
			this.queryBuilder = QueryTable.newBuilder(aliasName, entityClass, countPrefix);
			this.joinColumns = new ArrayList<>();
		}

		/**
		 * Add join column.
		 *
		 * @param joinKey      the join key
		 * @param identifyName the identify name
		 */
		void addJoinColumn(String joinKey, String identifyName) {
			if (StringUtils.isEmpty(joinKey)) {
				return;
			}
			if (StringUtils.isEmpty(identifyName)) {
				return;
			}

			for (JoinColumn joinColumn : this.joinColumns) {
				if (joinColumn.getJoinKey().equalsIgnoreCase(joinKey)
						&& joinColumn.getReferenceKey().equalsIgnoreCase(identifyName)) {
					return;
				}
			}
			this.joinColumns.add(new JoinColumn(joinKey, identifyName));
		}

		/**
		 * Build query join.
		 *
		 * @return the query join
		 */
		QueryJoin build() {
			if (this.joinColumns.isEmpty()) {
				throw new QueryException("Join columns must not be empty");
			}
			return new QueryJoin(this.joinType, this.queryBuilder.build(), this.joinColumns);
		}

		/**
		 * Match boolean.
		 *
		 * @param entityClass the entity class
		 * @return the boolean
		 */
		boolean match(Class<?> entityClass) {
			return this.queryBuilder.match(entityClass);
		}

		/**
		 * Analyze check boolean.
		 *
		 * @param referenceClass the reference class
		 * @return the boolean
		 */
		boolean analyzeCheck(Class<?> referenceClass) {
			return this.queryBuilder.analyzeCheck(referenceClass);
		}

		/**
		 * Add query item.
		 *
		 * @param entityClass  the entity class
		 * @param identifyName column identify name
		 * @param distinct     the distinct
		 * @param aliasName    the alias name
		 */
		void addQueryColumn(Class<?> entityClass, String identifyName, boolean distinct, String aliasName) {
			this.queryBuilder.addQueryColumn(entityClass, identifyName, distinct, aliasName);
		}

		/**
		 * Add query function.
		 *
		 * @param entityClass    the entity class
		 * @param aliasName      the alias name
		 * @param sqlFunction    the sql function
		 * @param functionParams the function params
		 * @throws QueryException the query exception
		 */
		void addQueryFunction(Class<?> entityClass, String aliasName, String sqlFunction,
		                      QueryItem... functionParams) throws QueryException {
			this.queryBuilder.addQueryFunction(entityClass, aliasName, sqlFunction, functionParams);
		}

		/**
		 * Add join table.
		 *
		 * @param joinType       the join type
		 * @param aliasName      the alias name
		 * @param entityClass    the entity class
		 * @param referenceClass the reference class
		 */
		void addJoinTable(JoinType joinType, final String aliasName, Class<?> entityClass, Class<?> referenceClass) {
			if (entityClass == null) {
				throw new EntityStatusException("Entity class is null");
			}
			if (this.queryBuilder.match(entityClass)) {
				this.queryBuilder.addJoinTable(joinType, aliasName, referenceClass);
			}
		}

		/**
		 * Add query condition.
		 *
		 * @param entityClass    the entity class
		 * @param connectionCode the connection code
		 * @param conditionCode  the condition code
		 * @param identifyName   column identify name
		 * @param matchCondition the match condition
		 */
		void addQueryCondition(Class<?> entityClass, ConnectionCode connectionCode, ConditionCode conditionCode,
		                       String identifyName, MatchCondition matchCondition) {
			this.queryBuilder.addQueryCondition(entityClass, connectionCode, conditionCode, 
					identifyName, matchCondition);
		}
	}
}
