/*
 * Copyright © 2003 Nervousync® Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of
 * Nervousync Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with Nervousync Studio.
 */

package org.nervousync.database.query.result;

import org.nervousync.enumerations.xml.DataType;
import org.nervousync.database.beans.configs.column.ColumnConfig;
import org.nervousync.database.beans.parser.AbstractDataParser;
import org.nervousync.database.beans.parser.impl.Base64DataParser;
import org.nervousync.database.beans.parser.impl.BooleanDataParser;
import org.nervousync.database.beans.parser.impl.DatetimeDataParser;
import org.nervousync.database.beans.parser.impl.NumberDataParser;
import org.nervousync.database.entity.EntityManager;
import org.nervousync.database.entity.core.BaseObject;
import org.nervousync.database.exceptions.entity.TableConfigException;
import org.nervousync.database.exceptions.security.DataModifiedException;
import org.nervousync.utils.ObjectUtils;
import org.nervousync.utils.ReflectionUtils;
import org.nervousync.utils.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * The type Result map.
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision : 1.0 $ $Date: 10/17/2020 2:58 PM $
 */
public final class ResultMap extends HashMap<String, String> {

	/**
	 * Instantiates a new Result map.
	 */
	public ResultMap() {
		super();
	}

	/**
	 * Instantiates a new Result map.
	 *
	 * @param map the map
	 */
	public ResultMap(final Map<String, String> map) {
		this();
		this.putAll(map);
	}

	/**
	 * Convert the result map to JavaBean
	 *
	 * @param <T>               Template
	 * @param clazz             JavaBean class
	 * @param forUpdate         the for update
	 * @param identifyCode      patch identify code
	 * @param transactionalCode the transactional code
	 * @return JavaBean instance
	 * @throws TableConfigException  the table config exception
	 * @throws DataModifiedException the data modified exception
	 */
	public <T> T unwrap(final Class<T> clazz, final boolean forUpdate, final long identifyCode,
	                    final long transactionalCode) throws TableConfigException, DataModifiedException {
		if (ResultMap.class.equals(clazz)) {
			return clazz.cast(this);
		}
		return Optional.ofNullable(EntityManager.getInstance().retrieveTableConfig(clazz))
				.map(tableConfig -> {
					T returnObject;
					try {
						returnObject = ObjectUtils.newInstance(clazz);

						tableConfig.getColumnConfigList()
								.stream()
								.filter(columnConfig ->
										this.containsKey(columnConfig.getColumnName().toUpperCase()))
								.forEach(columnConfig ->
										this.processFieldValue(columnConfig, returnObject, returnObject));

						((BaseObject) returnObject).setForUpdate(forUpdate);
						((BaseObject) returnObject).setTransactionalCode(transactionalCode);
					} catch (Exception e) {
						throw new TableConfigException("Convert result map to object error! ", e);
					}
					tableConfig.verify(returnObject, identifyCode);
					return returnObject;
				})
				.orElseThrow(() -> new TableConfigException("Can't found table define! "));
	}

	private void processFieldValue(final ColumnConfig columnConfig, final Object returnObject,
								   final Object compositeId) {
		final AbstractDataParser dataParser;
		if (AbstractDataParser.class.equals(columnConfig.getParserClass())) {
			if (DataType.NUMBER.equals(ObjectUtils.retrieveSimpleDataType(columnConfig.getFieldType()))) {
				dataParser = new NumberDataParser();
			} else if (boolean.class.equals(columnConfig.getFieldType())
					|| Boolean.class.equals(columnConfig.getFieldType())) {
				dataParser = new BooleanDataParser();
			} else if (Date.class.equals(columnConfig.getFieldType())) {
				dataParser = new DatetimeDataParser();
			} else if (byte[].class.equals(columnConfig.getFieldType())) {
				dataParser = new Base64DataParser();
			} else {
				dataParser = null;
			}
		} else {
			dataParser = ObjectUtils.newInstance(columnConfig.getParserClass());
		}
		Object fieldValue = this.get(columnConfig.getColumnName().toUpperCase());
		if (dataParser != null) {
			fieldValue = dataParser.parse((String) fieldValue, columnConfig.getFieldType());
		}
		if (fieldValue == null) {
			return;
		}
		if (columnConfig.isPrimaryKeyColumn()) {
			ReflectionUtils.setField(columnConfig.getFieldName(), compositeId, fieldValue);
		} else {
			ReflectionUtils.setField(columnConfig.getFieldName(), returnObject, fieldValue);
		}
	}

	/**
	 * Convert the result map to JSON string for cache data
	 *
	 * @return Converted JSON string
	 */
	public String cacheData() {
		return StringUtils.objectToString(this, StringUtils.StringType.JSON, Boolean.TRUE);
	}
}
