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
 * @version $Revision: 1.0 $ $Date: Jul 5, 2018 $
 */
public final class EntityStatusException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7165012923102792737L;

	/**
	 * Creates a new instance of EntityStatusException without detail message.
	 * @param errorMessage		Error message
	 */
	public EntityStatusException(String errorMessage) {
		super(errorMessage);
	}
}
