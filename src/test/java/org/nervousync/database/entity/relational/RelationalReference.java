/*
 * Copyright © 2003 Nervousync® Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of
 * Nervousync Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with Nervousync Studio.
 */

package org.nervousync.database.entity.relational;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import org.nervousync.annotations.beans.OutputConfig;
import org.nervousync.database.commons.DatabaseCommons;
import org.nervousync.database.entity.core.BaseObject;
import org.nervousync.utils.DateTimeUtils;
import org.nervousync.utils.StringUtils;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: 3/16/2021 03:12 PM $
 */
@XmlRootElement(name = "relational_reference")
@XmlAccessorType(XmlAccessType.NONE)
@OutputConfig(type = StringUtils.StringType.JSON)
@Table(name = "Relational_Reference", catalog = DatabaseCommons.DEFAULT_DATABASE_ALIAS)
public class RelationalReference extends BaseObject {

    private static final long serialVersionUID = 5529322451235666123L;

    @Id
    @Column(nullable = false)
    @XmlElement(name = "identify_code")
    private String identifyCode;
    @Id
    @Column(nullable = false, name = "identifyTime")
    @XmlElement(name = "current_time")
    private long currentTime = DateTimeUtils.currentUTCTimeMillis();
    /**
     * The Ref statue.
     */
    @Column(nullable = false)
    @XmlElement(name = "ref_status")
    private int refStatue;

    /**
     * Instantiates a new Distribute reference.
     */
    public RelationalReference() {
    }

    /**
     * Gets serial version uid.
     *
     * @return the serial version uid
     */
    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getIdentifyCode() {
        return identifyCode;
    }

    public void setIdentifyCode(String identifyCode) {
        this.identifyCode = identifyCode;
    }

    public long getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(long currentTime) {
        this.currentTime = currentTime;
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
