package org.nervousync.database.commons;

import org.nervousync.commons.core.Globals;
import org.nervousync.database.entity.EntityManager;
import org.nervousync.utils.ConvertUtils;
import org.nervousync.utils.SecurityUtils;
import org.nervousync.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.Types;
import java.util.*;

public final class CerebrateCommons {

	/**
	 * The constant LOGGER.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(CerebrateCommons.class);

	/**
	 * The constant DATA_CONVERT_MAPPING.
	 */
	private static final Map<Class<?>, Integer> DATA_CONVERT_MAPPING = new HashMap<>();

	static {
		registerDataType(String.class, Types.VARCHAR);
		registerDataType(Integer.class, Types.INTEGER);
		registerDataType(int.class, Types.INTEGER);
		registerDataType(Short.class, Types.SMALLINT);
		registerDataType(short.class, Types.SMALLINT);
		registerDataType(Long.class, Types.BIGINT);
		registerDataType(long.class, Types.BIGINT);
		registerDataType(Byte.class, Types.TINYINT);
		registerDataType(byte.class, Types.TINYINT);
		registerDataType(Float.class, Types.REAL);
		registerDataType(float.class, Types.REAL);
		registerDataType(Double.class, Types.DOUBLE);
		registerDataType(double.class, Types.DOUBLE);
		registerDataType(Boolean.class, Types.BOOLEAN);
		registerDataType(boolean.class, Types.BOOLEAN);
		registerDataType(Date.class, Types.TIMESTAMP);
		registerDataType(Calendar.class, Types.TIMESTAMP);
		registerDataType(Byte[].class, Types.BLOB);
		registerDataType(byte[].class, Types.BLOB);
		registerDataType(Character[].class, Types.CLOB);
		registerDataType(char[].class, Types.CLOB);
		registerDataType(BigDecimal.class, Types.DECIMAL);
	}

	public static final int DEFAULT_PAGE_NO = 1;
	public static final int DEFAULT_PAGE_LIMIT = 20;

	public static final String DEFAULT_DATABASE_ALIAS = "DefaultDatabase";
	public static final String CONTENT_MAP_KEY_DATABASE_NAME = "NSYC_DATABASE_NAME";
	public static final String CONTENT_MAP_KEY_TABLE_NAME = "NSYC_TABLE_NAME";
	public static final String CONTENT_MAP_KEY_ITEM = "NSYC_CONTENT_ITEM";

	/**
	 * Retrieve jdbc type int.
	 *
	 * @param typeClass the type class
	 * @return the int
	 */
	public static int retrieveJdbcType(Class<?> typeClass) {
		if (DATA_CONVERT_MAPPING.containsKey(typeClass)) {
			return DATA_CONVERT_MAPPING.get(typeClass);
		}
		return Types.OTHER;
	}

	/**
	 * Register data type.
	 *
	 * @param fieldType the field type
	 * @param jdbcType  the jdbc type
	 */
	public static void registerDataType(Class<?> fieldType, int jdbcType) {
		if (DATA_CONVERT_MAPPING.containsKey(fieldType)) {
			LOGGER.warn("Override type mapping: {}", fieldType.getName());
		}
		DATA_CONVERT_MAPPING.put(fieldType, jdbcType);
	}

	public static Map<String, String> cacheDataToMap(final String cacheData) {
		Map<String, String> cacheMap = new HashMap<>();
		for (Map.Entry<String, Object> entry
				: StringUtils.dataToMap(cacheData, StringUtils.StringType.JSON).entrySet()) {
			if (entry.getValue() instanceof String) {
				cacheMap.put(entry.getKey(), (String) entry.getValue());
			}
		}
		return cacheMap;
	}

	/**
	 * Check parameters.
	 *
	 * @param defineClass define class
	 * @throws SecurityException the security exception
	 */
	public static void checkParameters(String defineClass) throws SecurityException {
		if (defineClass == null) {
			throw new SecurityException("Entity class is unknown");
		}

		if (!EntityManager.getInstance().tableExists(defineClass)) {
			throw new SecurityException("Can't found table define");
		}
	}

	/**
	 * Cache key string.
	 *
	 * @param defineClass     define class
	 * @param queryParameters the query parameters
	 * @return the string
	 */
	public static String cacheKey(String defineClass, SortedMap<String, Object> queryParameters) {
		if (queryParameters == null || queryParameters.isEmpty()) {
			return null;
		}
		checkParameters(defineClass);
		return Optional.ofNullable(EntityManager.getInstance().retrieveTableConfig(defineClass))
				.map(tableConfig -> {
					TreeMap<String, Object> cacheKeyMap = new TreeMap<>(queryParameters);
					cacheKeyMap.put(CerebrateCommons.CONTENT_MAP_KEY_TABLE_NAME.toUpperCase(),
							tableConfig.getTableName().toUpperCase());
					return ConvertUtils.byteToHex(SecurityUtils.SHA256(cacheKeyMap));
				})
				.orElse(Globals.DEFAULT_VALUE_STRING);
	}
}
