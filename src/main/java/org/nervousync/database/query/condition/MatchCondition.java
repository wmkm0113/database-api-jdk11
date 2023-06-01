/*
 * Copyright © 2003 Nervousync® Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of
 * Nervousync Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with Nervousync Studio.
 */
package org.nervousync.database.query.condition;

import org.nervousync.commons.core.Globals;
import org.nervousync.utils.ConvertUtils;
import org.nervousync.utils.SecurityUtils;

import java.util.*;

/**
 * The type Match condition.
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision : 1.0 $ $Date: 11/1/2020 17:37 $
 */
public final class MatchCondition {

	/**
	 * The Condition type.
	 */
	private final QueryCondition.ConditionType conditionType;
	/**
	 * The Plan code.
	 */
	private final long planCode;
	/**
	 * The Identify key.
	 */
	private final String identifyKey;
	/**
	 * Condition match value array
	 */
	private final Object[] matchValues;

	/**
	 * Instantiates a new Match condition.
	 *
	 * @param planCode    the plan code
	 * @param identifyKey the identify key
	 */
	private MatchCondition(long planCode, String identifyKey) {
		this.conditionType = QueryCondition.ConditionType.DYNAMIC;
		this.planCode = planCode;
		this.identifyKey = identifyKey;
		this.matchValues = new Object[0];
	}

	/**
	 * Instantiates a new Match condition.
	 *
	 * @param matchValues the match values
	 */
	private MatchCondition(Object... matchValues) {
		this.conditionType = QueryCondition.ConditionType.CONSTANT;
		this.planCode = Globals.DEFAULT_VALUE_LONG;
		this.identifyKey = Globals.DEFAULT_VALUE_STRING;
		this.matchValues = matchValues;
	}

	/**
	 * Instantiates a new Match condition.
	 *
	 * @param queryConditions the query conditions
	 */
	private MatchCondition(QueryCondition... queryConditions) {
		this.conditionType = QueryCondition.ConditionType.GROUP;
		this.planCode = Globals.DEFAULT_VALUE_LONG;
		this.identifyKey = Globals.DEFAULT_VALUE_STRING;
		this.matchValues = queryConditions;
	}

	/**
	 * Dynamic match condition.
	 *
	 * @param planCode    the plan code
	 * @param identifyKey the identify key
	 * @return the match condition
	 */
	public static MatchCondition dynamic(long planCode, String identifyKey) {
		return new MatchCondition(planCode, identifyKey);
	}

	/**
	 * Condition match condition.
	 *
	 * @param matchValues the match values
	 * @return the match condition
	 */
	public static MatchCondition condition(Object... matchValues) {
		return new MatchCondition(matchValues);
	}

	/**
	 * Group match condition.
	 *
	 * @param queryConditions the query conditions
	 * @return the match condition
	 */
	public static MatchCondition group(QueryCondition... queryConditions) {
		return new MatchCondition(queryConditions);
	}

	/**
	 * Gets condition type.
	 *
	 * @return the condition type
	 */
	public QueryCondition.ConditionType getConditionType() {
		return conditionType;
	}

	/**
	 * Gets plan code.
	 *
	 * @return the plan code
	 */
	public long getPlanCode() {
		return planCode;
	}

	/**
	 * Gets identify key.
	 *
	 * @return the identify key
	 */
	public String getIdentifyKey() {
		return identifyKey;
	}

	/**
	 * Get match values object [ ].
	 *
	 * @return the object [ ]
	 */
	public Object[] getMatchValues() {
		return matchValues;
	}

	public String cacheKey() {
		Map<String, Object> cacheMap = new HashMap<>();

		cacheMap.put("ConditionType", this.conditionType.toString());
		cacheMap.put("IdentifyKey", this.identifyKey);
		if (QueryCondition.ConditionType.GROUP.equals(this.conditionType)) {
			List<String> conditionValues = new ArrayList<>();
			Arrays.asList(this.matchValues)
					.forEach(matchValue -> conditionValues.add(((MatchCondition) matchValue).cacheKey()));
			conditionValues.sort(String::compareTo);
			cacheMap.put("MatchValues", conditionValues);
		} else {
			cacheMap.put("MatchValues", this.matchValues);
		}

		return ConvertUtils.byteToHex(SecurityUtils.SHA256(cacheMap));
	}
}
