/*
 * Copyright © 2003 Nervousync® Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of
 * Nervousync Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with Nervousync Studio.
 */
package org.nervousync.database.provider;

/**
 * The interface Provider.
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision : 1.0 $ $Date: Jul 12, 2018 $
 */
public interface DataProvider {

	/**
	 * Register record
	 *
	 * @param processCode process code
	 * @param objects the base objects
	 */
	void registerItems(final long processCode, final Object... objects);

	/**
	 * Register record
	 *
	 * @param processCode process code
	 * @param objects the base objects
	 */
	void updateItems(final long processCode, final Object... objects);

	/**
	 * Remove record
	 *
	 * @param processCode   process code
	 * @param identifiedKey record identify key
	 */
	void removeItems(final long processCode, final String... identifiedKey);

	/**
	 * Submit and finish process
	 *
	 * @param processCode process code
	 */
	void submitProcess(final long processCode);

	/**
	 * Rollback process
	 *
	 * @param processCode process code
	 */
	void rollbackProcess(final long processCode);

}
