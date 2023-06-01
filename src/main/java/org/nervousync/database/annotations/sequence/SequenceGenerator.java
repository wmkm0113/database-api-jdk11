/*
 * Copyright © 2003 Nervousync® Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of
 * Nervousync Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with Nervousync Studio.
 */
package org.nervousync.database.annotations.sequence;

import org.nervousync.commons.core.Globals;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Sequence generator annotation
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Apr 11, 2018 4:12:43 PM $
 */
@Target({TYPE, METHOD, FIELD}) 
@Retention(RUNTIME)
@Documented
public @interface SequenceGenerator {
	
	/**
	 * Generator name
	 * @return	Sequence name
	 */
	String name();
	
	/**
	 * Minimum value
	 * @return	sequence minimum value
	 */
	int min() default Globals.INITIALIZE_INT_VALUE;
	
	/**
	 * Maximum value
	 * @return	sequence maximum value
	 */
	int max() default Globals.DEFAULT_VALUE_INT;
	
	/**
	 * Step value
	 * @return	sequence step value
	 */
	int step();
	
	/**
	 * Initialize value
	 * @return	sequence initialize value
	 */
	int init() default Globals.INITIALIZE_INT_VALUE;
	
	/**
	 * Sequence is cycle, default value is false
	 * @return	sequence is cycle status
	 */
	boolean cycle() default false;
	
}
