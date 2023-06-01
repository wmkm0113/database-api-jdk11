/*
 * Copyright © 2003 Nervousync® Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of
 * Nervousync Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with Nervousync Studio.
 */
package org.nervousync.database.enumerations.operator;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Feb 8, 2018 3:14:14 PM $
 */
public enum OperatorMode {
	//  Table operate mode
	Create, Alter, Truncate, Drop,
	//  Record operate mode
	Insert, Update, Retrieve, Query, Delete
}
