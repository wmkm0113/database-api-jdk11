/*
 * Copyright © 2003 Nervousync® Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of
 * Nervousync Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with Nervousync Studio.
 */

package org.nervousync.database.operator.result;

import org.nervousync.commons.core.Globals;
import org.nervousync.utils.StringUtils;

import java.io.Serializable;

/**
 * The type Operate result.
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision : 1.0 $ $Date: 10/17/2020 2:19 PM $
 */
public abstract class OperateResult implements Serializable {

	private static final long serialVersionUID = 8837298183905444322L;

	/**
	 * Operate success flag
	 */
	private final boolean success;
	/**
	 * Error stack message
	 */
	private final String stackMsg;

	/**
	 * Constructor
	 *
	 * @param success  Success flag
	 * @param stackMsg the stack msg
	 */
	protected OperateResult(boolean success, String stackMsg) {
		this.success = success;
		this.stackMsg = StringUtils.notBlank(stackMsg) ? stackMsg : Globals.DEFAULT_VALUE_STRING;
	}

	/**
	 * Gets the value of serialVersionUID
	 *
	 * @return the value of serialVersionUID
	 */
	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	/**
	 * Gets the value of success
	 *
	 * @return the value of success
	 */
	public boolean isSuccess() {
		return success;
	}

	/**
	 * Gets stack msg.
	 *
	 * @return the stack msg
	 */
	public String getStackMsg() {
		return stackMsg;
	}

	/**
	 * Convert to target class object
	 *
	 * @param <T>   Template
	 * @param clazz Target class
	 * @return Converted object
	 */
	public <T> T unwrap(Class<T> clazz) throws ClassCastException {
		if (clazz.isInstance(this)) {
			return clazz.cast(this);
		}
		throw new ClassCastException("Can't convert from class "
				+ this.getClass().getName() + " to class " + clazz.getName());
	}
}
