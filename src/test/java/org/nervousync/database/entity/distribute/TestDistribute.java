/*
 * Copyright © 2003 Nervousync® Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of
 * Nervousync Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with Nervousync Studio.
 */

package org.nervousync.database.entity.distribute;

import jakarta.persistence.*;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.nervousync.annotations.beans.OutputConfig;
import org.nervousync.beans.transfer.basic.BigDecimalAdapter;
import org.nervousync.beans.transfer.basic.DateTimeAdapter;
import org.nervousync.beans.transfer.blob.Base64Adapter;
import org.nervousync.commons.Globals;
import org.nervousync.database.entity.core.BaseObject;
import org.nervousync.utils.IDUtils;
import org.nervousync.utils.StringUtils;

import java.math.BigDecimal;
import java.util.Date;

/**
 * The type Test entity.
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: 11/30/2020 1:05 PM $
 */
@XmlRootElement(name = "test_distribute")
@XmlAccessorType(XmlAccessType.NONE)
@OutputConfig(type = StringUtils.StringType.JSON)
@Table(name = "Test_Distribute", schema = "Distribute")
public final class TestDistribute extends BaseObject {

	/**
	 * The constant serialVersionUID.
	 */
	private static final long serialVersionUID = -1136316002800055959L;

    /**
     * Identify code.
     */
    @Id
    @Column(nullable = false)
    @GeneratedValue(generator = IDUtils.UUIDv4)
    @XmlElement(name = "identify_code")
    private String identifyCode;
    /**
     * The Msg title.
     */
    @Column(nullable = false, length = 200)
    @XmlElement(name = "msg_title")
    private String msgTitle;
    /**
     * The Msg content.
     */
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column
    @XmlElement(name = "msg_bytes")
    @XmlJavaTypeAdapter(Base64Adapter.class)
    private byte[] msgBytes;
    /**
     * The Msg content.
     */
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column
    @XmlElement(name = "msg_content")
    private String msgContent;
    /**
     * The Test int.
     */
    @Column
    @XmlElement(name = "test_int")
    private int testInt = Globals.DEFAULT_VALUE_INT;
    /**
     * The Test short.
     */
    @Column
    @XmlElement(name = "test_short")
    private short testShort = Globals.DEFAULT_VALUE_SHORT;
    /**
     * The Test double.
     */
    @Column(precision = 53)
    @XmlElement(name = "test_double")
    private double testDouble = Globals.DEFAULT_VALUE_DOUBLE;
    /**
     * The Test float.
     */
    @Column(precision = 53)
    @XmlElement(name = "test_float")
    private float testFloat = Globals.DEFAULT_VALUE_FLOAT;
    /**
     * The Test byte.
     */
    @Column
    @XmlElement(name = "test_byte")
    private byte testByte;
    /**
     * The Test boolean.
     */
    @Column
    @XmlElement(name = "test_boolean")
    private boolean testBoolean = Boolean.FALSE;
    /**
     * The Test date.
     */
    @Column
    @Temporal(TemporalType.DATE)
    @XmlElement(name = "test_date")
    @XmlJavaTypeAdapter(DateTimeAdapter.class)
    private Date testDate;
    /**
     * The Test time.
     */
    @Column
    @Temporal(TemporalType.TIME)
    @XmlElement(name = "test_time")
    @XmlJavaTypeAdapter(DateTimeAdapter.class)
    private Date testTime;
    /**
     * The Test timestamp.
     */
    @Column
    @Temporal(TemporalType.TIMESTAMP)
    @XmlElement(name = "test_timestamp")
    @XmlJavaTypeAdapter(DateTimeAdapter.class)
    private Date testTimestamp;
    /**
     * The Test big decimal.
     */
    @Column(precision = 31, scale = 14)
    @XmlElement(name = "test_decimal")
    @XmlJavaTypeAdapter(BigDecimalAdapter.class)
    private BigDecimal testBigDecimal;
	/**
	 * Distribute reference.
	 */
	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinColumn(columnDefinition = "identifyCode", referencedColumnName = "identifyCode")
	private DistributeReference distributeReference;

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
	 * Gets msg title.
	 *
	 * @return the msg title
	 */
	public String getMsgTitle() {
		return msgTitle;
	}

	/**
	 * Sets msg title.
	 *
	 * @param msgTitle the msg title
	 */
	public void setMsgTitle(String msgTitle) {
		this.msgTitle = msgTitle;
	}

	/**
	 * Get msg bytes byte [ ].
	 *
	 * @return the byte [ ]
	 */
	public byte[] getMsgBytes() {
		return msgBytes;
	}

	/**
	 * Sets msg bytes.
	 *
	 * @param msgBytes the msg bytes
	 */
	public void setMsgBytes(byte[] msgBytes) {
		this.msgBytes = msgBytes;
	}

	/**
	 * Get msg content char [ ].
	 *
	 * @return the char [ ]
	 */
	public String getMsgContent() {
		return msgContent;
	}

	/**
	 * Sets msg content.
	 *
	 * @param msgContent the msg content
	 */
	public void setMsgContent(String msgContent) {
		this.msgContent = msgContent;
	}

	/**
	 * Gets test int.
	 *
	 * @return the test int
	 */
	public int getTestInt() {
		return testInt;
	}

	/**
	 * Sets test int.
	 *
	 * @param testInt the test int
	 */
	public void setTestInt(int testInt) {
		this.testInt = testInt;
	}

	/**
	 * Gets test short.
	 *
	 * @return the test short
	 */
	public short getTestShort() {
		return testShort;
	}

	/**
	 * Sets test short.
	 *
	 * @param testShort the test short
	 */
	public void setTestShort(short testShort) {
		this.testShort = testShort;
	}

	/**
	 * Gets test double.
	 *
	 * @return the test double
	 */
	public double getTestDouble() {
		return testDouble;
	}

	/**
	 * Sets test double.
	 *
	 * @param testDouble the test double
	 */
	public void setTestDouble(double testDouble) {
		this.testDouble = testDouble;
	}

	/**
	 * Gets test float.
	 *
	 * @return the test float
	 */
	public float getTestFloat() {
		return testFloat;
	}

	/**
	 * Sets test float.
	 *
	 * @param testFloat the test float
	 */
	public void setTestFloat(float testFloat) {
		this.testFloat = testFloat;
	}

	/**
	 * Gets test byte.
	 *
	 * @return the test byte
	 */
	public byte getTestByte() {
		return testByte;
	}

	/**
	 * Sets test byte.
	 *
	 * @param testByte the test byte
	 */
	public void setTestByte(byte testByte) {
		this.testByte = testByte;
	}

	/**
	 * Is test boolean.
	 *
	 * @return the boolean
	 */
	public boolean isTestBoolean() {
		return testBoolean;
	}

	/**
	 * Sets test boolean.
	 *
	 * @param testBoolean the test boolean
	 */
	public void setTestBoolean(boolean testBoolean) {
		this.testBoolean = testBoolean;
	}

	/**
	 * Gets test date.
	 *
	 * @return the test date
	 */
	public Date getTestDate() {
		return testDate;
	}

	/**
	 * Sets test date.
	 *
	 * @param testDate the test date
	 */
	public void setTestDate(Date testDate) {
		this.testDate = testDate;
	}

	/**
	 * Gets test time.
	 *
	 * @return the test time
	 */
	public Date getTestTime() {
		return testTime;
	}

	/**
	 * Sets test time.
	 *
	 * @param testTime the test time
	 */
	public void setTestTime(Date testTime) {
		this.testTime = testTime;
	}

	/**
	 * Gets test timestamp.
	 *
	 * @return the test timestamp
	 */
	public Date getTestTimestamp() {
		return testTimestamp;
	}

	/**
	 * Sets test timestamp.
	 *
	 * @param testTimestamp the test timestamp
	 */
	public void setTestTimestamp(Date testTimestamp) {
		this.testTimestamp = testTimestamp;
	}

	/**
	 * Gets test big decimal.
	 *
	 * @return the test big decimal
	 */
	public BigDecimal getTestBigDecimal() {
		return testBigDecimal;
	}

	/**
	 * Sets test big decimal.
	 *
	 * @param testBigDecimal the test big decimal
	 */
	public void setTestBigDecimal(BigDecimal testBigDecimal) {
		this.testBigDecimal = testBigDecimal;
	}

	/**
	 * Gets distribute reference.
	 *
	 * @return distribute reference
	 */
	public DistributeReference getDistributeReference() {
		return distributeReference;
	}

	/**
	 * Sets distribute reference.
	 *
	 * @param distributeReference distribute reference
	 */
	public void setDistributeReference(DistributeReference distributeReference) {
		this.distributeReference = distributeReference;
	}
}
