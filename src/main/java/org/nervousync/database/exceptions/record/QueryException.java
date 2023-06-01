/*
 * Copyright © 2003 Nervousync® Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of
 * Nervousync Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with Nervousync Studio.
 */
package org.nervousync.database.exceptions.record;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Mar 3, 2016 5:26:36 PM $
 */
public final class QueryException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6208059064429827947L;

	/**
	 * Creates a new instance of QueryException without detail message.
	 * @param errorMessage		Error message
	 */
	public QueryException(String errorMessage) {
		super(errorMessage);
	}

	/**
	 * Creates an instance of QueryException with nested exception
	 * @param errorMessage		Error message
	 * @param e 				Nested exception
	 */
	public QueryException(String errorMessage, Exception e) {
		super(errorMessage, e);
	}
}
