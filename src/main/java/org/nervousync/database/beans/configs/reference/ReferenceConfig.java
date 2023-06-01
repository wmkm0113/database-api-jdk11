/*
 * Copyright © 2003 Nervousync® Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of
 * Nervousync Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with Nervousync Studio.
 */
package org.nervousync.database.beans.configs.reference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.JoinColumn;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import org.nervousync.beans.core.BeanObject;
import org.nervousync.utils.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Table reference config
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision : 1.0 $ $Date: Mar 30, 2016 5:37:59 PM $
 */
public final class ReferenceConfig implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 352330256621053556L;

	/**
	 * Field value is lazy load, default value is false
	 */
	private final boolean lazyLoad;
	/**
	 * Return value is array
	 */
	private final boolean returnArray;
	/**
	 * Reference table define class
	 */
	private final Class<?> referenceClass;
	/**
	 * Reference field name
	 */
	private final String fieldName;
	/**
	 * Reference cascade types
	 */
	private final CascadeType[] cascadeTypes;
	/**
	 * Reference table column mappings
	 */
	private final List<JoinColumnConfig> referenceColumnList;
	
	/**
	 * Constructor
	 * @param referenceClass        reference entity class
	 * @param lazyLoad              lazy load status
	 * @param returnArray           data is array
	 * @param cascadeTypes          cascade type array
	 * @param joinColumns           join column array
	 */
	private ReferenceConfig(final Class<?> referenceClass, final String fieldName, final boolean lazyLoad,
	                        final boolean returnArray, final CascadeType[] cascadeTypes,
	                        final JoinColumn[] joinColumns) {
		this.referenceColumnList = new ArrayList<>();

		this.lazyLoad = lazyLoad;
		this.returnArray = returnArray;
		this.cascadeTypes = cascadeTypes;
		this.fieldName = fieldName;
		this.referenceClass = referenceClass;

		for (JoinColumn joinColumn : joinColumns) {
			this.referenceColumnList.add(
					new JoinColumnConfig(joinColumn.columnDefinition(),
							joinColumn.referencedColumnName()));
		}
	}

	/**
	 * Initialize ReferenceConfig object
	 *
	 * @param referenceClass the reference class
	 * @param fieldName      the field name
	 * @param lazyLoad       Field value is lazy load
	 * @param returnArray    Return value is array
	 * @param cascadeTypes   Cascade types
	 * @param joinColumns    Mapping columns
	 * @return ReferenceConfig reference config
	 * @see ReferenceConfig
	 */
	public static ReferenceConfig initialize(final Class<?> referenceClass, final String fieldName,
	                                         final boolean lazyLoad, final boolean returnArray,
	                                         final CascadeType[] cascadeTypes, final JoinColumn[] joinColumns) {
		if (joinColumns == null || cascadeTypes == null || StringUtils.isEmpty(fieldName) || joinColumns.length == 0) {
			return null;
		}
		return new ReferenceConfig(referenceClass, fieldName, lazyLoad, returnArray, cascadeTypes, joinColumns);
	}

	/**
	 * Gets serial version uid.
	 *
	 * @return the serialVersionUID
	 */
	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	/**
	 * Gets reference class.
	 *
	 * @return the reference class
	 */
	public Class<?> getReferenceClass() {
		return referenceClass;
	}

	/**
	 * Gets field name.
	 *
	 * @return the fieldName
	 */
	public String getFieldName() {
		return fieldName;
	}

	/**
	 * Is return array boolean.
	 *
	 * @return the returnArray
	 */
	public boolean isReturnArray() {
		return returnArray;
	}

	/**
	 * Get cascade types cascade type [ ].
	 *
	 * @return the cascadeType
	 */
	public CascadeType[] getCascadeTypes() {
		return cascadeTypes == null ? new CascadeType[]{} : cascadeTypes.clone();
	}

	/**
	 * Is lazy load boolean.
	 *
	 * @return the lazyLoad
	 */
	public boolean isLazyLoad() {
		return lazyLoad;
	}

	/**
	 * Gets reference column list.
	 *
	 * @return the referenceColumnList
	 */
	public List<JoinColumnConfig> getReferenceColumnList() {
		return referenceColumnList;
	}

	/**
	 * The type Join column config.
	 */
	@XmlRootElement
	public static final class JoinColumnConfig extends BeanObject {

		/**
		 * 
		 */
		private static final long serialVersionUID = -5091778742481427204L;

		@XmlElement
		private final String currentField;
		@XmlElement
		private final String referenceField;

		/**
		 * Instantiates a new Join column config.
		 *
		 * @param currentField   the current field name
		 * @param referenceField the reference field name
		 */
		JoinColumnConfig(String currentField, String referenceField) {
			this.currentField = currentField;
			this.referenceField = referenceField;
		}

		/**
		 * Gets serial version uid.
		 *
		 * @return the serialVersionUID
		 */
		public static long getSerialVersionUID() {
			return serialVersionUID;
		}

		/**
		 * Gets current field name.
		 *
		 * @return the currentField
		 */
		public String getCurrentField() {
			return currentField;
		}

		/**
		 * Gets reference field name.
		 *
		 * @return the referenceFieldName
		 */
		public String getReferenceField() {
			return referenceField;
		}
	}
}
