/*
 * Copyright © 2003 Nervousync® Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of
 * Nervousync Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with Nervousync Studio.
 */

package org.nervousync.database.operator.process.impl;

import org.nervousync.database.beans.configs.transactional.TransactionalConfig;
import org.nervousync.database.enumerations.operator.OperatorMode;
import org.nervousync.database.operator.process.Operator;

/**
 * The type Record operator.
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision : 1.0 $ $Date: 10/17/2020 3:43 PM $
 */
public final class RecordOperator extends Operator {

	private static final long serialVersionUID = -5030143609634957788L;

	private final Object[] objects;

	private RecordOperator(final OperatorMode operatorMode, final Object... objects) {
		super(operatorMode, Boolean.FALSE);
		this.objects = objects;
	}

	private RecordOperator(final OperatorMode operatorMode, final TransactionalConfig transactionalConfig,
	                       final Object... objects) {
		super(operatorMode, transactionalConfig);
		this.objects = objects;
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
	 * Gets the value of baseObjects
	 *
	 * @return the value of baseObjects
	 */
	public Object[] getObjects() {
		return this.objects;
	}

	/**
	 * Insert records to database
	 *
	 * @param transactionalConfig the transactional config
	 * @param objects             the objects
	 * @return RecordOperator instance
	 */
	public static RecordOperator insertRecords(final TransactionalConfig transactionalConfig,
	                                           final Object... objects) {
		return (transactionalConfig == null)
				? new RecordOperator(OperatorMode.Insert, objects)
				: new RecordOperator(OperatorMode.Insert, transactionalConfig, objects);
	}

	/**
	 * Update records to database
	 *
	 * @param transactionalConfig the transactional config
	 * @param objects             the objects
	 * @return RecordOperator instance
	 */
	public static RecordOperator updateRecords(final TransactionalConfig transactionalConfig,
	                                           final Object... objects) {
		return (transactionalConfig == null)
				? new RecordOperator(OperatorMode.Update, objects)
				: new RecordOperator(OperatorMode.Update, transactionalConfig, objects);
	}

	/**
	 * Delete records from database
	 *
	 * @param transactionalConfig the transactional config
	 * @param objects             the objects
	 * @return RecordOperator instance
	 */
	public static RecordOperator deleteRecords(final TransactionalConfig transactionalConfig,
	                                           final Object... objects) {
		return (transactionalConfig == null)
				? new RecordOperator(OperatorMode.Delete, objects)
				: new RecordOperator(OperatorMode.Delete, transactionalConfig, objects);
	}
}
