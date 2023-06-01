/*
 * Copyright © 2003 Nervousync® Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of
 * Nervousync Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with Nervousync Studio.
 */
package org.nervousync.database.operator.process;

import org.nervousync.database.beans.configs.transactional.TransactionalConfig;
import org.nervousync.database.enumerations.operator.OperatorMode;
import org.nervousync.database.operator.result.OperateResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * The type Operator.
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision : 1.0 $ $Date: 10/17/2020 2:14 PM $
 */
public abstract class Operator implements Serializable {

	/**
	 * The Logger.
	 */
	protected transient final Logger logger = LoggerFactory.getLogger(this.getClass());

	private static final long serialVersionUID = 4108130472521792519L;

	private final OperatorMode operatorMode;
	private final TransactionalConfig transactionalConfig;
	private final boolean readOnly;
	private OperateResult operateResult;

	/**
	 * Instantiates a new Operator.
	 *
	 * @param operatorMode the operator mode
	 * @param readOnly     the read only
	 */
	protected Operator(OperatorMode operatorMode, boolean readOnly) {
		this.operatorMode = operatorMode;
		this.transactionalConfig = null;
		if (OperatorMode.Retrieve.equals(operatorMode) || OperatorMode.Query.equals(operatorMode)) {
			this.readOnly = readOnly;
		} else {
			if (readOnly) {
				this.logger.warn("Operator mode doesn't support read only mode, ignore read only config! ");
			}
			this.readOnly = Boolean.FALSE;
		}
	}

	/**
	 * Instantiates a new Operator.
	 *
	 * @param operatorMode        the operator mode
	 * @param transactionalConfig the transactional config
	 */
	protected Operator(OperatorMode operatorMode, TransactionalConfig transactionalConfig) {
		this.operatorMode = operatorMode;
		this.transactionalConfig = transactionalConfig;
		this.readOnly = Boolean.FALSE;
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
	 * Gets the value of operatorMode
	 *
	 * @return the value of operatorMode
	 */
	public OperatorMode getOperatorMode() {
		return operatorMode;
	}

	/**
	 * Is read only boolean.
	 *
	 * @return the boolean
	 */
	public boolean isReadOnly() {
		return readOnly;
	}

	/**
	 * Gets transactional config.
	 *
	 * @return the transactional config
	 */
	public TransactionalConfig getTransactionalConfig() {
		return transactionalConfig;
	}

	/**
	 * Gets operate result.
	 *
	 * @return operate result
	 */
	public OperateResult getOperateResult() {
		return operateResult;
	}

	/**
	 * Sets operate result.
	 *
	 * @param operateResult operate result
	 */
	public void setOperateResult(OperateResult operateResult) {
		this.operateResult = operateResult;
	}

	/**
	 * Convert to target class object
	 *
	 * @param <T>   Template
	 * @param clazz Target class
	 * @return Converted object
	 */
	public <T> T unwrap(Class<T> clazz) {
		if (clazz.isInstance(this)) {
			return clazz.cast(this);
		}
		throw new ClassCastException("Can't convert from class "
				+ this.getClass().getName() + " to class " + clazz.getName());
	}
}
