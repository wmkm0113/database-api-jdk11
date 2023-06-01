/*
 * Copyright © 2003 Nervousync® Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of
 * Nervousync Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with Nervousync Studio.
 */
package org.nervousync.database.enumerations.transactional;

import java.sql.Connection;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Mar 30, 2016 3:52:00 PM $
 */
public enum Isolation {
	
	DEFAULT(Connection.TRANSACTION_NONE),
	
	ISOLATION_READ_UNCOMMITTED(Connection.TRANSACTION_READ_UNCOMMITTED), 

	ISOLATION_READ_COMMITTED(Connection.TRANSACTION_READ_COMMITTED), 

	ISOLATION_REPEATABLE_READ(Connection.TRANSACTION_REPEATABLE_READ), 

	ISOLATION_SERIALIZABLE(Connection.TRANSACTION_SERIALIZABLE);
	
	private final int transactionLevel;
	
	Isolation(int transactionLevel) {
		this.transactionLevel = transactionLevel;
	}
	
	public int value() {
		return this.transactionLevel;
	}
}
