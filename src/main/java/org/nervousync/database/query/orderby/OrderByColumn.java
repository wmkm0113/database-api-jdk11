/*
 * Copyright © 2003 Nervousync® Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of
 * Nervousync Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with Nervousync Studio.
 */
package org.nervousync.database.query.orderby;

import org.nervousync.commons.core.Globals;

/**
 * The type Order by column.
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision : 1.0 $ $Date: 9/14/2020 5:15 PM $
 */
public final class OrderByColumn {

	/**
	 * The Alias name.
	 */
	private final Class<?> entityClass;
	/**
	 * The Identify key.
	 */
	private final String identifyKey;
	/**
	 * The Order by type.
	 */
	private final OrderByType orderByType;

	/**
	 * Instantiates a new Order by column.
	 *
	 * @param orderByType the order by type
	 * @param entityClass the entity class
	 * @param identifyKey the identify key
	 */
	public OrderByColumn(OrderByType orderByType, Class<?> entityClass, String identifyKey) {
		this.orderByType = orderByType;
		this.entityClass = entityClass;
		this.identifyKey = identifyKey;
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
	 * Gets entity class.
	 *
	 * @return the entity class
	 */
	public Class<?> getEntityClass() {
		return entityClass;
	}

	/**
	 * Gets order by type.
	 *
	 * @return the orderByType
	 */
	public OrderByType getOrderByType() {
		return orderByType;
	}

	public String cacheKey() {
		return this.entityClass.getName() + Globals.EXTENSION_SEPARATOR + this.identifyKey;
	}

	/**
	 * The enum Order by type.
	 */
	public enum OrderByType {
		/**
		 * Asc order by type.
		 */
		ASC,
		/**
		 * Desc order by type.
		 */
		DESC
	}
}
