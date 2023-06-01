/*
 * Copyright © 2003 Nervousync® Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of
 * Nervousync Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with Nervousync Studio.
 */

package org.nervousync.database.query.core;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import org.nervousync.commons.core.Globals;
import org.nervousync.database.exceptions.record.QueryException;
import org.nervousync.utils.ConvertUtils;
import org.nervousync.utils.SecurityUtils;

import java.util.*;

/**
 * The type Query item.
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision : 1.0 $ $Date: 10/9/2020 11:30 AM $
 */
public abstract class QueryItem {

	private static final String CACHE_KEY_ITEM_TYPE = "ItemType";
	private static final String CACHE_KEY_ALIAS_NAME = "AliasName";

	/**
	 * Query item type
	 */
	private final ItemType itemType;
	/**
	 * Query item alias name
	 */
	private final String aliasName;

	/**
	 * Instantiates a new Query item.
	 *
	 * @param itemType  the item type
	 * @param aliasName the alias name
	 */
	private QueryItem(ItemType itemType, String aliasName) {
		this.itemType = itemType;
		this.aliasName = aliasName;
	}

	/**
	 * Create query column.
	 *
	 * @param identifyKey the identify key
	 * @param distinct    the distinct
	 * @param aliasName   the alias name
	 * @return the query column
	 */
	public static QueryColumn queryColumn(String identifyKey, boolean distinct, String aliasName) {
		return new QueryColumn(identifyKey, distinct, aliasName);
	}

	/**
	 * Query constant query constant.
	 *
	 * @param constantValue the constant value
	 * @return the query constant
	 */
	public static QueryConstant queryConstant(Object constantValue) {
		return new QueryConstant(constantValue);
	}

	/**
	 * Create query function.
	 *
	 * @param aliasName      the alias name
	 * @param sqlFunction    the sql function
	 * @param functionParams the function params
	 * @return the query function
	 * @throws QueryException the query exception
	 */
	public static QueryFunction queryFunction(String aliasName, String sqlFunction, QueryItem... functionParams) {
		return new QueryFunction(aliasName, sqlFunction, functionParams);
	}

	/**
	 * Gets the value of itemType
	 *
	 * @return the value of itemType
	 */
	public ItemType getItemType() {
		return itemType;
	}

	/**
	 * Gets the value of aliasName
	 *
	 * @return the value of aliasName
	 */
	public String getAliasName() {
		return aliasName;
	}

	/**
	 * Match boolean.
	 *
	 * @param queryItem the query item
	 * @return the boolean
	 */
	public abstract boolean match(QueryItem queryItem);

	public abstract String cacheKey();

	/**
	 * The enum Item type.
	 */
	public enum ItemType {
		/**
		 * Column item type.
		 */
		COLUMN,
		/**
		 * Function item type.
		 */
		FUNCTION,
		/**
		 * Constant item type.
		 */
		CONSTANT,
	}

	/**
	 * The type Query column.
	 */
	public static final class QueryColumn extends QueryItem {

		/**
		 * Column identify key, maybe field name or column name
		 */
		private final String identifyKey;
		/**
		 * Distinct column flag
		 */
		private final boolean distinct;

		/**
		 * Instantiates a new Query column.
		 *
		 * @param identifyKey the identify key
		 * @param distinct    the distinct
		 * @param aliasName   the alias name
		 */
		private QueryColumn(String identifyKey, boolean distinct, String aliasName) {
			super(ItemType.COLUMN, aliasName);
			this.identifyKey = identifyKey;
			this.distinct = distinct;
		}

		@Override
		public boolean match(QueryItem queryItem) {
			if (queryItem == null) {
				return Boolean.FALSE;
			}
			if (ItemType.COLUMN.equals(queryItem.getItemType())) {
				return this.identifyKey.equalsIgnoreCase(((QueryColumn)queryItem).getIdentifyKey())
						&& Objects.equals(this.distinct, ((QueryColumn)queryItem).isDistinct());
			}
			return Boolean.FALSE;
		}

		@Override
		public String cacheKey() {
			Map<String, Object> cacheMap = new HashMap<>();
			cacheMap.put(CACHE_KEY_ITEM_TYPE, ItemType.COLUMN.toString());
			cacheMap.put(CACHE_KEY_ALIAS_NAME, this.getAliasName());
			cacheMap.put("IdentifyKey", this.identifyKey);
			cacheMap.put("Distinct", this.distinct);
			return ConvertUtils.byteToHex(SecurityUtils.SHA256(cacheMap));
		}

		/**
		 * Gets the value of identifyKey
		 *
		 * @return the value of identifyKey
		 */
		public String getIdentifyKey() {
			return identifyKey;
		}

		/**
		 * Is distinct boolean.
		 *
		 * @return the boolean
		 */
		public boolean isDistinct() {
			return distinct;
		}
	}

	/**
	 * The type Query constant.
	 */
	public static final class QueryConstant extends QueryItem {

		/**
		 * The Constant value.
		 */
		private final Object constantValue;

		/**
		 * Instantiates a new Query constant.
		 *
		 * @param constantValue the constant value
		 */
		private QueryConstant(Object constantValue) {
			super(ItemType.CONSTANT, Globals.DEFAULT_VALUE_STRING);
			this.constantValue = constantValue;
		}

		@Override
		public boolean match(QueryItem queryItem) {
			if (queryItem == null) {
				return Boolean.FALSE;
			}
			if (ItemType.CONSTANT.equals(queryItem.getItemType())) {
				return Objects.equals(this.constantValue, ((QueryConstant)queryItem).getConstantValue());
			}
			return Boolean.FALSE;
		}

		@Override
		public String cacheKey() {
			Map<String, Object> cacheMap = new HashMap<>();
			cacheMap.put(CACHE_KEY_ITEM_TYPE, ItemType.CONSTANT.toString());
			cacheMap.put(CACHE_KEY_ALIAS_NAME, this.getAliasName());
			cacheMap.put("ItemValue", this.constantValue);
			return ConvertUtils.byteToHex(SecurityUtils.SHA256(cacheMap));
		}

		/**
		 * Gets the value of constantValue
		 *
		 * @return the value of constantValue
		 */
		public Object getConstantValue() {
			return constantValue;
		}
	}

	/**
	 * The type Query function.
	 */
	@XmlRootElement
	public static final class QueryFunction extends QueryItem {

		/**
		 * The constant serialVersionUID.
		 */
		private static final long serialVersionUID = 4463684389449026498L;

		/**
		 * The Sql function.
		 */
		@XmlElement
		private final String sqlFunction;
		/**
		 * The Function params.
		 */
		@XmlElementWrapper
		private final List<QueryItem> functionParams;

		/**
		 * Instantiates a new Query function.
		 *
		 * @param aliasName      the alias name
		 * @param sqlFunction    the sql function
		 * @param functionParams the function params
		 */
		private QueryFunction(String aliasName, String sqlFunction, QueryItem... functionParams) {
			super(ItemType.FUNCTION, aliasName);
			this.sqlFunction = sqlFunction;
			this.functionParams = Arrays.asList(functionParams);
		}

		/**
		 * Gets the value of serialVersionUID
		 *
		 * @return the value of serialVersionUID
		 */
		public static long getSerialVersionUID() {
			return serialVersionUID;
		}

		@Override
		public boolean match(QueryItem queryItem) {
			if (queryItem == null) {
				return Boolean.FALSE;
			}
			if (ItemType.FUNCTION.equals(queryItem.getItemType())) {
				return this.sqlFunction.equalsIgnoreCase(((QueryFunction)queryItem).getSQLFunction()) &&
						Objects.deepEquals(this.functionParams, ((QueryFunction)queryItem).getFunctionParams());
			}
			return Boolean.FALSE;
		}

		@Override
		public String cacheKey() {
			List<String> itemValues = new ArrayList<>();
			this.functionParams.forEach(queryItem -> itemValues.add(queryItem.cacheKey()));
			itemValues.sort(String::compareTo);
			Map<String, Object> cacheMap = new HashMap<>();
			cacheMap.put(CACHE_KEY_ITEM_TYPE, ItemType.FUNCTION.toString());
			cacheMap.put(CACHE_KEY_ALIAS_NAME, this.getAliasName());
			cacheMap.put("Function", this.sqlFunction);
			cacheMap.put("ItemValue", itemValues);
			return ConvertUtils.byteToHex(SecurityUtils.SHA256(cacheMap));
		}

		/**
		 * Gets the value of standardSQLFunction
		 *
		 * @return the value of standardSQLFunction
		 */
		public String getSQLFunction() {
			return sqlFunction;
		}

		/**
		 * Gets the value of functionParams
		 *
		 * @return the value of functionParams
		 */
		public List<QueryItem> getFunctionParams() {
			return functionParams;
		}
	}
}
