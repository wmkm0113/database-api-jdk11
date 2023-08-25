/*
 * Copyright © 2003 Nervousync® Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of
 * Nervousync Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with Nervousync Studio.
 */

package org.nervousync.database.entity.distribute;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.nervousync.beans.core.BeanObject;

/**
 * The type Distribute reference.
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision : 1.0 $ $Date: 3/16/2021 02:30 PM $
 */
@Table(name = "Distribute_Reference", catalog = "Distribute")
public final class DistributeReference extends BeanObject {

	/**
	 * The constant serialVersionUID.
	 */
	private static final long serialVersionUID = -4424333396905164733L;

	/**
	 * Identify code.
	 */
	@Id
	@Column(nullable = false)
	private String identifyCode;
	/**
	 * The Ref statue.
	 */
	@Column(nullable = false)
	private int refStatue;

	/**
	 * Instantiates a new Distribute reference.
	 */
	public DistributeReference() {
	}

	/**
	 * Gets serial version uid.
	 *
	 * @return the serial version uid
	 */
	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	/**
	 * Gets identify code.
	 *
	 * @return identify code
	 */
	public String getIdentifyCode() {
		return identifyCode;
	}

	/**
	 * Sets identify code.
	 *
	 * @param identifyCode identify code
	 */
	public void setIdentifyCode(String identifyCode) {
		this.identifyCode = identifyCode;
	}

	/**
	 * Gets ref statue.
	 *
	 * @return the ref statue
	 */
	public int getRefStatue() {
		return refStatue;
	}

	/**
	 * Sets ref statue.
	 *
	 * @param refStatue the ref statue
	 */
	public void setRefStatue(int refStatue) {
		this.refStatue = refStatue;
	}
}
