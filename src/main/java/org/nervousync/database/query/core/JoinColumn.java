/*
 * Copyright © 2003 Nervousync® Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of
 * Nervousync Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with Nervousync Studio.
 */

package org.nervousync.database.query.core;

/**
 * The type Join column.
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision : 1.0 $ $Date: 2/17/2021 04:35 PM $
 */
public final class JoinColumn {

	/**
	 * The Join key.
	 */
	private final String joinKey;
	/**
	 * The Reference key.
	 */
	private final String referenceKey;

	/**
	 * Instantiates a new Join column.
	 *
	 * @param joinKey      the join key
	 * @param referenceKey the reference key
	 */
	JoinColumn(String joinKey, String referenceKey) {
		this.joinKey = joinKey;
		this.referenceKey = referenceKey;
	}

	/**
	 * Gets join key.
	 *
	 * @return the join key
	 */
	public String getJoinKey() {
		return joinKey;
	}

	/**
	 * Gets reference key.
	 *
	 * @return the reference key
	 */
	public String getReferenceKey() {
		return referenceKey;
	}
}
