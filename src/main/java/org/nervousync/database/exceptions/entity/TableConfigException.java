/*
 * Copyright © 2003 Nervousync® Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of
 * Nervousync Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with Nervousync Studio.
 */

package org.nervousync.database.exceptions.entity;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: 8/25/2020 3:27 PM $
 */
public final class TableConfigException extends RuntimeException {

	private static final long serialVersionUID = 5937753553304837818L;

	/**
	 * Creates a new instance of TableConfigException without detail message.
	 * @param errorMessage		Error message
	 */
	public TableConfigException(String errorMessage) {
		super(errorMessage);
	}

	/**
	 * Creates an instance of TableConfigException with nested exception
	 * @param errorMessage		Error message
	 * @param e 				Nested exception
	 */
	public TableConfigException(String errorMessage, Exception e) {
		super(errorMessage, e);
	}
}
