/*
 * Copyright © 2003 Nervousync® Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of
 * Nervousync Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with Nervousync Studio.
 */
package org.nervousync.database.exceptions.security;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: May 24, 2016 4:56:06 PM $
 */
public final class DataModifiedException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 257967587883015245L;

	/**
	 * Creates a new instance of DataModifiedException without detail message.
	 * @param errorMessage		Error message
	 */
	public DataModifiedException(String errorMessage) {
		super(errorMessage);
	}
}
