/*
 * Copyright © 2003 Nervousync® Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of
 * Nervousync Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with Nervousync Studio.
 */

package org.nervousync.database.beans.configs.generator;

import jakarta.persistence.GeneratedValue;
import org.nervousync.commons.core.Globals;
import org.nervousync.database.annotations.sequence.SequenceGenerator;
import org.nervousync.database.beans.configs.sequence.SequenceConfig;
import org.nervousync.database.enumerations.generation.GenerationOption;

import java.io.Serializable;
import java.lang.reflect.Field;

/**
 * The type Generator config.
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision : 1.0 $ $Date: 11/4/2020 15:49 $
 */
public final class GeneratorConfig implements Serializable {

	private static final long serialVersionUID = -4086799829735527877L;

	/**
	 * Generation type
	 */
	private final GenerationOption generationOption;
	/**
	 * Column sequence name
	 */
	private final String generatorName;
	/**
	 * Sequence config
	 */
	private SequenceConfig sequenceConfig = null;

	/**
	 * Instantiates a new Generator config.
	 *
	 * @param field the field
	 */
	public GeneratorConfig(Field field) {
		if (!field.isAnnotationPresent(GeneratedValue.class)) {
			this.generationOption = GenerationOption.NONE;
			this.generatorName = Globals.DEFAULT_VALUE_STRING;
			return;
		}
		GeneratedValue generatedValue = field.getAnnotation(GeneratedValue.class);
		switch (generatedValue.strategy()) {
			case SEQUENCE:
				this.generationOption = GenerationOption.SEQUENCE;
				if (field.isAnnotationPresent(SequenceGenerator.class)) {
					SequenceGenerator sequenceGenerator = field.getAnnotation(SequenceGenerator.class);
					this.generatorName = sequenceGenerator.name();
					this.sequenceConfig = new SequenceConfig();
					this.sequenceConfig.setMinValue(sequenceGenerator.min());
					this.sequenceConfig.setMaxValue(sequenceGenerator.max());
					this.sequenceConfig.setCurrent(sequenceGenerator.init());
					this.sequenceConfig.setStep(sequenceGenerator.step());
					this.sequenceConfig.setSequenceName(sequenceGenerator.name());
					this.sequenceConfig.setCycle(sequenceGenerator.cycle());
				} else {
					this.generatorName = Globals.DEFAULT_VALUE_STRING;
				}
				break;
			case IDENTITY:
				this.generationOption = GenerationOption.INCREMENT;
				this.generatorName = Globals.DEFAULT_VALUE_STRING;
				break;
			case AUTO:
				this.generationOption = GenerationOption.GENERATOR;
				this.generatorName = generatedValue.generator();
				break;
			default:
				this.generationOption = GenerationOption.ASSIGNED;
				this.generatorName = Globals.DEFAULT_VALUE_STRING;
				break;
		}
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
	 * Gets generation option.
	 *
	 * @return the generation option
	 */
	public GenerationOption getGenerationOption() {
		return generationOption;
	}

	/**
	 * Gets generator name.
	 *
	 * @return the generator name
	 */
	public String getGeneratorName() {
		return generatorName;
	}

	/**
	 * Gets sequence config.
	 *
	 * @return the sequence config
	 */
	public SequenceConfig getSequenceConfig() {
		return sequenceConfig;
	}

	/**
	 * Sequence generator boolean.
	 *
	 * @return the boolean
	 */
	public boolean sequenceGenerator() {
		return GenerationOption.SEQUENCE.equals(this.generationOption);
	}
}
