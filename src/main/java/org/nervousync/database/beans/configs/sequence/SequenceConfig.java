/*
 * Copyright © 2003 Nervousync® Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of
 * Nervousync Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with Nervousync Studio.
 */
package org.nervousync.database.beans.configs.sequence;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import org.nervousync.beans.core.BeanObject;
import org.nervousync.commons.core.Globals;

/**
 * Sequence config
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: May 15, 2012 5:54:44 PM $
 */
@XmlRootElement
public final class SequenceConfig extends BeanObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -79385184004859280L;
	
	/**
	 * Sequence name
	 */
	@XmlElement
	private String sequenceName = Globals.DEFAULT_VALUE_STRING;
	/**
	 * Sequence minimum value
	 */
	@XmlElement
	private int minValue = Globals.DEFAULT_VALUE_INT;
	/**
	 * Sequence maximum value
	 */
	@XmlElement
	private int maxValue = Globals.DEFAULT_VALUE_INT;
	/**
	 * Sequence step value
	 */
	@XmlElement
	private int step = Globals.DEFAULT_VALUE_INT;
	/**
	 * Sequence current value
	 */
	@XmlElement
	private int current = Globals.DEFAULT_VALUE_INT;
	/**
	 * Sequence value is cycle
	 */
	@XmlElement
	private boolean cycle = Boolean.FALSE;
	
	public SequenceConfig() {
		
	}

	/**
	 * @return the sequenceName
	 */
	public String getSequenceName() {
		return sequenceName;
	}

	/**
	 * @param sequenceName the sequenceName to set
	 */
	public void setSequenceName(String sequenceName) {
		this.sequenceName = sequenceName;
	}

	/**
	 * @return the minValue
	 */
	public int getMinValue() {
		return minValue;
	}

	/**
	 * @param minValue the minValue to set
	 */
	public void setMinValue(int minValue) {
		this.minValue = minValue;
	}

	/**
	 * @return the maxValue
	 */
	public int getMaxValue() {
		return maxValue;
	}

	/**
	 * @param maxValue the maxValue to set
	 */
	public void setMaxValue(int maxValue) {
		this.maxValue = maxValue;
	}

	/**
	 * @return the step
	 */
	public int getStep() {
		return step;
	}

	/**
	 * @param step the step to set
	 */
	public void setStep(int step) {
		this.step = step;
	}

	/**
	 * @return the current
	 */
	public int getCurrent() {
		return current;
	}

	/**
	 * @param current the current to set
	 */
	public void setCurrent(int current) {
		this.current = current;
	}

	/**
	 * @return the cycle
	 */
	public boolean isCycle() {
		return cycle;
	}

	/**
	 * @param cycle the cycle to set
	 */
	public void setCycle(boolean cycle) {
		this.cycle = cycle;
	}

	/**
	 * @return the serialVersionUID
	 */
	public static long getSerialVersionUID() {
		return serialVersionUID;
	}
}
