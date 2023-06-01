/*
 * Copyright © 2003 Nervousync® Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of
 * Nervousync Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with Nervousync Studio.
 */
package org.nervousync.database.beans.configs.transactional;

import org.nervousync.database.annotations.transactional.Transactional;
import org.nervousync.database.enumerations.transactional.Isolation;
import org.nervousync.utils.IDUtils;

import java.io.Serializable;

/**
 * Transactional config
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision : 1.0 $ $Date: Mar 30, 2016 4:07:44 PM $
 */
public final class TransactionalConfig implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5195470765056859725L;

	/** Transactional identify code */
	private final long transactionalCode;
	/** Transactional timeout */
	private final int timeout;
	/** Transactional isolation */
	private final Isolation isolation;
	/** System will process rollback automatic when catch these exceptions */
	private final Class<?>[] rollBackForClasses;
	
	private TransactionalConfig(Transactional transactional) {
		this.transactionalCode = IDUtils.snowflake();
		this.timeout = transactional.timeout();
		this.isolation = transactional.isolation();
		this.rollBackForClasses = transactional.rollbackFor();
	}

	/**
	 * Initialize transactional config
	 *
	 * @param transactional Transactional annotation
	 * @return TransactionalConfig transactional config
	 * @see Transactional
	 * @see TransactionalConfig
	 */
	public static TransactionalConfig initialize(Transactional transactional) {
		if (transactional != null) {
			return new TransactionalConfig(transactional);
		}
		return null;
	}

	/**
	 * Gets serial version uid.
	 *
	 * @return the serialVersionUID
	 */
	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	/**
	 * Gets transactional code.
	 *
	 * @return the transactional code
	 */
	public long getTransactionalCode() {
		return transactionalCode;
	}

	/**
	 * Gets timeout.
	 *
	 * @return the timeout
	 */
	public int getTimeout() {
		return timeout;
	}

	/**
	 * Gets isolation.
	 *
	 * @return the isolation
	 */
	public Isolation getIsolation() {
		return isolation;
	}

	/**
	 * Gets roll back for class names.
	 *
	 * @return the rollBackForClassNames
	 */
	public Class<?>[] getRollBackForClasses() {
		return rollBackForClasses;
	}
}
