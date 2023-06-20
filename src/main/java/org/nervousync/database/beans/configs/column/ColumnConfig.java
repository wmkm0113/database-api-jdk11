/*
 * Copyright © 2003 Nervousync® Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of
 * Nervousync Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with Nervousync Studio.
 */
package org.nervousync.database.beans.configs.column;

import jakarta.persistence.*;
import org.nervousync.commons.core.Globals;
import org.nervousync.database.annotations.table.DataParser;
import org.nervousync.database.beans.configs.generator.GeneratorConfig;
import org.nervousync.database.commons.DatabaseCommons;
import org.nervousync.database.beans.parser.AbstractDataParser;
import org.nervousync.utils.ConvertUtils;
import org.nervousync.utils.ReflectionUtils;
import org.nervousync.utils.SecurityUtils;
import org.nervousync.utils.StringUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Column config
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision : 1.0 $ $Date: Jun 27, 2018 $
 */
public final class ColumnConfig implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -6062009105869563215L;

	/**
	 * Column Length
	 */
	private final int length;
	/**
	 * Column precision
	 */
	private final int precision;
	/**
	 * Column scale
	 */
	private final int scale;
	/**
	 * Column name
	 */
	private final String columnName;
	/**
	 * Class field name
	 */
	private final String fieldName;
	/**
	 * Class field type class
	 */
	private final Class<?> fieldType;
	/**
	 * JDBC data type
	 */
	private final int jdbcType;
	/**
	 * Column default value
	 */
	private final Object defaultValue;
	/**
	 * Column is item of primary key
	 */
	private final boolean primaryKeyColumn;
	/**
	 * Column null status: Y/N
	 */
	private final boolean nullable;
	/**
	 * Column update status: Y/N
	 */
	private final boolean updatable;
	/**
	 * Column unique status: Y/N
	 */
	private final boolean unique;
	/**
	 * Lazy load status: Y/N
	 */
	private final boolean lazyLoad;
	/**
	 * Identify version column
	 */
	private final boolean identifyVersion;
	private final Class<? extends AbstractDataParser> parserClass;
	/**
	 * Column value generator config
	 */
	private final GeneratorConfig generatorConfig;
	private final List<String> identifyKeys = new ArrayList<>();

	/**
	 * Instantiates a new Column config.
	 *
	 * @param field            the field
	 * @param object           the object
	 * @param primaryKeyColumn the primary key column
	 */
	private ColumnConfig(final Field field, final Object object, final boolean primaryKeyColumn) {
		Column column = field.getAnnotation(Column.class);
		this.columnName = column.name().length() == 0 ? field.getName() : column.name();
		this.identifyKeys.add(this.columnName);
		this.identifyKeys.add(ConvertUtils.byteToHex(SecurityUtils.SHA256(this.columnName)));
		this.precision = column.precision();
		this.scale = column.scale();
		this.fieldName = field.getName();
		this.identifyKeys.add(this.fieldName);
		this.identifyKeys.add(ConvertUtils.byteToHex(SecurityUtils.SHA256(this.fieldName)));
		this.fieldType = field.getType();
		if (Date.class.equals(this.fieldType) && field.isAnnotationPresent(Temporal.class)) {
			switch (field.getAnnotation(Temporal.class).value()) {
				case DATE:
					this.jdbcType = Types.DATE;
					break;
				case TIME:
					this.jdbcType = Types.TIME;
					break;
				default:
					this.jdbcType = Types.TIMESTAMP;
					break;
			}
		} else if (field.isAnnotationPresent(Lob.class)) {
			if (String.class.equals(this.fieldType)
					|| char[].class.equals(this.fieldType)
					|| Character[].class.equals(this.fieldType)) {
				this.jdbcType = Types.CLOB;
			} else {
				this.jdbcType = Types.BLOB;
			}
		} else {
			this.jdbcType = DatabaseCommons.retrieveJdbcType(this.fieldType);
		}
		switch (this.jdbcType) {
			case Types.CHAR:
			case Types.NCHAR:
			case Types.VARCHAR:
			case Types.NVARCHAR:
				this.length = column.length();
				break;
			default:
				this.length = Globals.DEFAULT_VALUE_INT;
				break;
		}
		this.defaultValue = ReflectionUtils.getFieldValue(field.getName(), object);
		this.primaryKeyColumn = primaryKeyColumn;
		if (this.primaryKeyColumn) {
			this.nullable = Boolean.FALSE;
			this.updatable = Boolean.FALSE;
			this.identifyVersion = Boolean.FALSE;
		} else {
			this.nullable = (this.jdbcType == Types.TIMESTAMP) ? Boolean.FALSE : column.nullable();
			this.updatable = column.updatable();
			this.identifyVersion = field.isAnnotationPresent(Version.class);
		}
		this.parserClass = field.isAnnotationPresent(DataParser.class)
				? field.getAnnotation(DataParser.class).value()
				: AbstractDataParser.class;
		this.unique = column.unique();
		if (field.isAnnotationPresent(Basic.class)) {
			this.lazyLoad = FetchType.LAZY.equals(field.getAnnotation(Basic.class).fetch());
		} else {
			this.lazyLoad = Boolean.FALSE;
		}
		this.generatorConfig = new GeneratorConfig(field);
	}

	public static ColumnConfig newInstance(final Field field, final Object object, final boolean primaryKeyColumn) {
		if (field != null && object != null && field.isAnnotationPresent(Column.class)) {
			return new ColumnConfig(field, object, primaryKeyColumn);
		}
		return null;
	}

	/**
	 * Match identify key boolean.
	 *
	 * @param identifyName identify key to match
	 * @return the boolean
	 */
	public boolean matchIdentifyKey(String identifyName) {
		if (StringUtils.isEmpty(identifyName)) {
			return Boolean.FALSE;
		}
		return this.identifyKeys.stream().anyMatch(identifyKey -> identifyKey.equalsIgnoreCase(identifyName));
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
	 * Gets length.
	 *
	 * @return the length
	 */
	public int getLength() {
		return length;
	}

	/**
	 * Gets precision.
	 *
	 * @return the precision
	 */
	public int getPrecision() {
		return precision;
	}

	/**
	 * Gets scale.
	 *
	 * @return the scale
	 */
	public int getScale() {
		return scale;
	}

	/**
	 * Gets column name.
	 *
	 * @return the column name
	 */
	public String getColumnName() {
		return columnName;
	}

	/**
	 * Gets field name.
	 *
	 * @return the field name
	 */
	public String getFieldName() {
		return fieldName;
	}

	/**
	 * Gets field type.
	 *
	 * @return the field type
	 */
	public Class<?> getFieldType() {
		return fieldType;
	}

	/**
	 * Gets jdbc type.
	 *
	 * @return the jdbc type
	 */
	public int getJdbcType() {
		return jdbcType;
	}

	/**
	 * Gets default value.
	 *
	 * @return the default value
	 */
	public Object getDefaultValue() {
		return defaultValue;
	}

	/**
	 * Is primary key column boolean.
	 *
	 * @return the boolean
	 */
	public boolean isPrimaryKeyColumn() {
		return primaryKeyColumn;
	}

	/**
	 * Is nullable boolean.
	 *
	 * @return the boolean
	 */
	public boolean isNullable() {
		return nullable;
	}

	/**
	 * Is updatable boolean.
	 *
	 * @return the boolean
	 */
	public boolean isUpdatable() {
		return updatable;
	}

	/**
	 * Is unique boolean.
	 *
	 * @return the boolean
	 */
	public boolean isUnique() {
		return unique;
	}

	/**
	 * Is lazy load boolean.
	 *
	 * @return the boolean
	 */
	public boolean isLazyLoad() {
		return lazyLoad;
	}

	/**
	 * Is identify version boolean.
	 *
	 * @return the boolean
	 */
	public boolean isIdentifyVersion() {
		return identifyVersion;
	}

	public Class<? extends AbstractDataParser> getParserClass() {
		return parserClass;
	}

	/**
	 * Gets generator config.
	 *
	 * @return the generator config
	 */
	public GeneratorConfig getGeneratorConfig() {
		return generatorConfig;
	}
}
