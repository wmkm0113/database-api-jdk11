/*
 * Copyright © 2003 Nervousync® Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of
 * Nervousync Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with Nervousync Studio.
 */
package org.nervousync.database.annotations.transactional;

import org.nervousync.commons.core.Globals;
import org.nervousync.database.enumerations.transactional.Isolation;

import java.lang.annotation.*;

/**
 * Transactional annotation, if class or method was annotated with this annotation, system will execute method in transactional mode
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Mar 30, 2016 3:45:19 PM $
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Documented
public @interface Transactional {

	/**
	 * Setting for transactional timeout value
	 * @return		Transactional timeout value
	 */
	int timeout() default Globals.DEFAULT_VALUE_INT;
	
	/**
	 * Transactional isolation
	 * @see Isolation
	 * @return		Transactional isolation
	 */
	Isolation isolation() default Isolation.DEFAULT;
	
	/**
	 * System will process rollback automatic when catch these exceptions
	 * @return		Rollback class list
	 */
	Class<?>[] rollbackFor() default {};
}
