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
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import org.nervousync.annotations.beans.OutputConfig;
import org.nervousync.database.entity.core.BaseObject;
import org.nervousync.utils.StringUtils;

/**
 * The type Distribute reference.
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: 3/16/2021 02:30 PM $
 */
@XmlRootElement(name = "distribute_reference")
@XmlAccessorType(XmlAccessType.NONE)
@OutputConfig(type = StringUtils.StringType.JSON)
@Table(name = "Distribute_Reference", schema = "Distribute")
public final class DistributeReference extends BaseObject {

	/**
	 * The constant serialVersionUID.
	 */
	private static final long serialVersionUID = -4424333396905164733L;

	/**
	 * Identify code.
	 */
	@Id
	@Column(nullable = false)
    @XmlElement(name = "identify_code")
	private String identifyCode;
	/**
	 * The Ref statue.
	 */
	@Column(nullable = false)
    @XmlElement(name = "ref_status")
	private int refStatue;

	/**
	 * Instantiates a new Distribute reference.
	 */
	public DistributeReference() {
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
