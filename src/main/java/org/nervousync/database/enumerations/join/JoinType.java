/*
 * Copyright © 2003 Nervousync® Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of
 * Nervousync Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with Nervousync Studio.
 */

package org.nervousync.database.enumerations.join;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision : 1.0 $ $Date: 2/17/2021 09:49 AM $
 */
public enum JoinType {
	/**
	 * Left join type.
	 */
	LEFT,
	/**
	 * Right join type.
	 */
	RIGHT,
	/**
	 * Full join type.
	 */
	FULL,
	/**
	 * Inner join type.
	 */
	INNER,
	/**
	 * Cross join type.
	 */
	CROSS
}
