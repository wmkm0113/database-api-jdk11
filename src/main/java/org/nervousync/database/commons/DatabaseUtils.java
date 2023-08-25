package org.nervousync.database.commons;

import jakarta.annotation.Nonnull;
import org.nervousync.commons.Globals;
import org.nervousync.database.beans.configs.column.ColumnConfig;
import org.nervousync.database.beans.configs.table.TableConfig;
import org.nervousync.database.dialects.Converter;
import org.nervousync.database.entity.EntityManager;
import org.nervousync.database.entity.core.BaseObject;
import org.nervousync.database.enumerations.lock.LockOption;
import org.nervousync.database.exceptions.entity.TableConfigException;
import org.nervousync.utils.*;

import java.math.BigDecimal;
import java.sql.Types;
import java.util.*;

/**
 * <h2 class="en-US">Database utilities define</h2>
 * <h2 class="zh-CN">数据库基本工具定义</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Mar 30, 2016 17:05:12 $
 */
public final class DatabaseUtils {
    /**
     * <span class="en-US">Logger instance</span>
     * <span class="zh-CN">日志实例</span>
     */
    private static final LoggerUtils.Logger LOGGER = LoggerUtils.getLogger(DatabaseUtils.class);

    /**
     * <span class="en-US">The constant mapping between Java type and JDBC type code</span>
     * <span class="zh-CN">常量映射表，用于映射Java类型和JDBC类型代码</span>
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

    /**
     * <h3 class="en-US">Register the mapping relationship between Java type and JDBC type code</h3>
     * <h3 class="zh-CN">注册Java类型和JDBC类型代码的映射关系</h3>
     *
     * @param typeClass <span class="en-US">Java type class</span>
     *                  <span class="zh-CN">Java类型</span>
     * @param jdbcType  <span class="en-US">JDBC type code</span>
     *                  <span class="zh-CN">JDBC类型代码</span>
     */
    public static void registerDataType(final Class<?> typeClass, final int jdbcType) {
        if (DATA_CONVERT_MAPPING.containsKey(typeClass)) {
            LOGGER.warn("Override type mapping: {}", typeClass.getName());
        }
        DATA_CONVERT_MAPPING.put(typeClass, jdbcType);
    }

    /**
     * <h3 class="en-US">Generate a unique identification string</h3>
     * <span class="en-US">Based on the given entity class information and query the primary key mapping table</span>
     * <h3 class="zh-CN">生成唯一识别字符串</h3>
     * <span class="zh-CN">根据给定的实体类信息和查询主键映射表</span>
     *
     * @param defineClass     <span class="en-US">Entity define class</span>
     *                        <span class="zh-CN">实体类定义</span>
     * @param queryParameters <span class="en-US">Primary key mapping table</span>
     *                        <span class="zh-CN">查询主键映射表</span>
     * @return <span class="en-US">Generated cache key string</span>
     * <span class="zh-CN">生成的缓存键值</span>
     * @throws TableConfigException <span class="en-US">If table entity class not registered</span>
     * <span class="zh-CN">如果数据表实体类未注册</span>
     */
    public static String identifyKey(final Class<?> defineClass, final SortedMap<String, Object> queryParameters)
            throws TableConfigException {
        return identifyKey(defineClass, queryParameters, Globals.DEFAULT_VALUE_STRING);
    }

    /**
     * <h3 class="en-US">Generate a unique identification string</h3>
     * <span class="en-US">Based on the given entity class information, query the primary key mapping table and lazy load item identify key</span>
     * <h3 class="zh-CN">生成唯一识别字符串</h3>
     * <span class="zh-CN">根据给定的实体类信息，查询主键映射表和懒加载项识别代码</span>
     *
     * @param defineClass     <span class="en-US">Entity define class</span>
     *                        <span class="zh-CN">实体类定义</span>
     * @param queryParameters <span class="en-US">Primary key mapping table</span>
     *                        <span class="zh-CN">查询主键映射表</span>
     * @param identifyKey     <span class="en-US">Lazy load item identify key</span>
     *                        <span class="zh-CN">懒加载项识别代码</span>
     * @return <span class="en-US">Generated cache key string</span>
     * <span class="zh-CN">生成的缓存键值</span>
     * @throws TableConfigException <span class="en-US">If table entity class not registered</span>
     * <span class="zh-CN">如果数据表实体类未注册</span>
     */
    public static String identifyKey(final Class<?> defineClass, final SortedMap<String, Object> queryParameters,
                                     final String identifyKey)
            throws TableConfigException {
        if (defineClass == null || queryParameters == null || queryParameters.isEmpty()) {
            throw new TableConfigException(0x00DB00000001L, "Table_Not_Found_Error");
        }
        TableConfig tableConfig =
                EntityManager.tableConfig(defineClass)
                        .orElseThrow(() -> new TableConfigException(0x00DB00000001L, "Table_Not_Found_Error"));
        return identifyKey(new TreeMap<>(queryParameters), tableConfig, identifyKey);
    }

    /**
     * <h3 class="en-US">Generate a unique identification string based on the given entity object instance</h3>
     * <h3 class="zh-CN">根据给定的实体对象实例生成唯一识别字符串</h3>
     *
     * @param object <span class="en-US">Entity object instance</span>
     *               <span class="zh-CN">实体对象实例</span>
     * @return <span class="en-US">Generated cache key string</span>
     * <span class="zh-CN">生成的缓存键值</span>
     * @throws TableConfigException <span class="en-US">If table entity class not registered</span>
     * <span class="zh-CN">如果数据表实体类未注册</span>
     */
    public static String identifyKey(Object object) throws TableConfigException {
        return identifyKey(object, Globals.DEFAULT_VALUE_STRING);
    }

    /**
     * <h3 class="en-US">Generate a unique identification string</h3>
     * <span class="en-US">Based on the given entity object instance and lazy load item identify key</span>
     * <h3 class="zh-CN">生成唯一识别字符串</h3>
     * <span class="zh-CN">根据给定的实体对象实例和懒加载项识别代码</span>
     *
     * @param object      <span class="en-US">Entity object instance</span>
     *                    <span class="zh-CN">实体对象实例</span>
     * @param identifyKey <span class="en-US">Lazy load item identify key</span>
     *                    <span class="zh-CN">懒加载项识别代码</span>
     * @return <span class="en-US">Generated cache key string</span>
     * <span class="zh-CN">生成的缓存键值</span>
     * @throws TableConfigException <span class="en-US">If table entity class not registered</span>
     * <span class="zh-CN">如果数据表实体类未注册</span>
     */
    public static String identifyKey(final Object object, final String identifyKey) throws TableConfigException {
        if (object == null) {
            throw new TableConfigException(0x00DB00000001L, "Table_Not_Found_Error");
        }
        TableConfig tableConfig =
                EntityManager.tableConfig(object.getClass())
                        .orElseThrow(() -> new TableConfigException(0x00DB00000001L, "Table_Not_Found_Error"));

        TreeMap<String, Object> dataMap = new TreeMap<>();
        tableConfig.getColumnConfigs().stream()
                .filter(ColumnConfig::isPrimaryKey)
                .forEach(columnConfig ->
                        dataMap.put(columnConfig.columnName().toUpperCase(),
                                ReflectionUtils.getFieldValue(columnConfig.getFieldName(), object)));
        return identifyKey(dataMap, tableConfig, identifyKey);
    }

    /**
     * <h3 class="en-US">Generate a primary key data mapping table based on a given entity object instance</h3>
     * <h3 class="zh-CN">根据给定的实体对象实例生成主键数据映射表</h3>
     *
     * @param object <span class="en-US">Entity object instance</span>
     *               <span class="zh-CN">实体对象实例</span>
     * @return <span class="en-US">Generated primary key data mapping table</span>
     * <span class="zh-CN">生成的主键数据映射表</span>
     */
    public static SortedMap<String, Object> primaryKeyMap(final Object object) {
        return primaryKeyMap(object, Boolean.FALSE);
    }

    /**
     * <h3 class="en-US">Generate a primary key data mapping table based on a given entity object instance</h3>
     * <h3 class="zh-CN">根据给定的实体对象实例生成主键数据映射表</h3>
     *
     * @param object    <span class="en-US">Entity object instance</span>
     *                  <span class="zh-CN">实体对象实例</span>
     * @param forUpdate <span class="en-US">The primary key data mapping table is used to update records</span>
     *                  <span class="zh-CN">主键数据映射表用于更新记录</span>
     * @return <span class="en-US">Generated primary key data mapping table</span>
     * <span class="zh-CN">生成的主键数据映射表</span>
     */
    public static SortedMap<String, Object> primaryKeyMap(final Object object, final boolean forUpdate) {
        final SortedMap<String, Object> parameterMap = new TreeMap<>();
        EntityManager.tableConfig(ClassUtils.originalClassName(object.getClass()))
                .ifPresent(tableConfig -> {
                    tableConfig.getColumnConfigs()
                            .stream()
                            .filter(ColumnConfig::isPrimaryKey)
                            .forEach(columnConfig ->
                                    parameterMap.put(columnConfig.columnName().toUpperCase(),
                                            ReflectionUtils.getFieldValue(columnConfig.getFieldName(), object)));
                    if (forUpdate && LockOption.OPTIMISTIC_UPGRADE.equals(tableConfig.getLockOption())) {
                        tableConfig.versionColumn()
                                .ifPresent(columnConfig -> parameterMap.put(columnConfig.columnName().toUpperCase(),
                                        ReflectionUtils.getFieldValue(columnConfig.getFieldName(), object)));
                    }
                });
        return parameterMap;
    }

    /**
     * <h3 class="en-US">Generate a data mapping table based on a given entity object instance</h3>
     * <h3 class="zh-CN">根据给定的实体对象实例生成数据映射表</h3>
     *
     * @param object    <span class="en-US">Entity object instance</span>
     *                  <span class="zh-CN">实体对象实例</span>
     * @param converter <span class="en-US">Data converter instance</span>
     *                  <span class="zh-CN">数据转换器实例对象</span>
     * @return <span class="en-US">Generated data mapping table</span>
     * <span class="zh-CN">生成的数据映射表</span>
     */
    public SortedMap<String, Object> dataMap(final Object object, final Converter converter) {
        final SortedMap<String, Object> parameterMap = new TreeMap<>();
        EntityManager.tableConfig(ClassUtils.originalClassName(object.getClass()))
                .ifPresent(tableConfig ->
                        tableConfig.getColumnConfigs()
                                .forEach(columnConfig ->
                                        Optional.ofNullable(retrieveValue(object, columnConfig, converter))
                                                .ifPresent(fieldValue ->
                                                        parameterMap.put(columnConfig.columnName(), fieldValue))));
        return parameterMap;
    }

    /**
     * <h3 class="en-US">Generate a update data mapping table based on a given entity object instance</h3>
     * <h3 class="zh-CN">根据给定的实体对象实例生成更新数据映射表</h3>
     *
     * @param object    <span class="en-US">Entity object instance</span>
     *                  <span class="zh-CN">实体对象实例</span>
     * @param converter <span class="en-US">Data converter instance</span>
     *                  <span class="zh-CN">数据转换器实例对象</span>
     * @return <span class="en-US">Generated update data mapping table</span>
     * <span class="zh-CN">生成的更新数据映射表</span>
     */
    public SortedMap<String, Object> updateMap(final Object object, final Converter converter) {
        final TreeMap<String, Object> parameterMap = new TreeMap<>();
        EntityManager.tableConfig(ClassUtils.originalClassName(object.getClass()))
                .ifPresent(tableConfig -> {
                    if (object instanceof BaseObject) {
                        ((BaseObject) object).modifiedColumns()
                                .forEach(identifyKey ->
                                        Optional.ofNullable(tableConfig.columnConfig(identifyKey))
                                                .ifPresent(columnConfig ->
                                                        parameterMap.put(columnConfig.getColumnInfo().getColumnName(),
                                                                retrieveValue(object, columnConfig, converter))));
                    } else {
                        tableConfig.getColumnConfigs().stream()
                                .filter(columnConfig -> !columnConfig.isPrimaryKey() && columnConfig.isUpdatable())
                                .forEach(columnConfig ->
                                        parameterMap.put(columnConfig.columnName(),
                                                retrieveValue(object, columnConfig, converter)));
                    }
                });
        return parameterMap;
    }

    /**
     * <h3 class="en-US">Get a list of identification codes for lazy load items</h3>
     * <h3 class="zh-CN">获取懒加载项的识别代码列表</h3>
     *
     * @param object <span class="en-US">Entity object instance</span>
     *               <span class="zh-CN">实体对象实例</span>
     * @return <span class="en-US">The list of identification codes</span>
     * <span class="zh-CN">识别码列表</span>
     * @throws TableConfigException <span class="en-US">If table entity class not registered</span>
     * <span class="zh-CN">如果数据表实体类未注册</span>
     */
    public static List<String> lazyLoadKeys(final Object object) throws TableConfigException {
        final List<String> cacheKeys = new ArrayList<>();
        final SortedMap<String, Object> primaryKeyMap = DatabaseUtils.primaryKeyMap(object);
        TableConfig tableConfig =
                EntityManager.tableConfig(ClassUtils.originalClassName(object.getClass()))
                        .orElseThrow(() -> new TableConfigException(0x00DB00000001L, "Table_Not_Found_Error"));
        for (ColumnConfig columnConfig : tableConfig.getColumnConfigs()) {
            if (columnConfig.isLazyLoad()) {
                cacheKeys.add(DatabaseUtils.identifyKey(tableConfig.getDefineClass(), primaryKeyMap,
                        columnConfig.columnName()));
            }
        }
        return cacheKeys;
    }

    /**
     * <h3 class="en-US">Retrieve the mapping JDBC type code by given Java type class</h3>
     * <h3 class="zh-CN">通过给定的 Java 类型类检索映射 JDBC 类型代码</h3>
     *
     * @param typeClass <span class="en-US">Java type class</span>
     *                  <span class="zh-CN">Java类型</span>
     * @return <span class="en-US">Retrieved JDBC type code</span>
     * <span class="zh-CN">检索到的JDBC类型代码</span>
     */
    public static int jdbcType(final Class<?> typeClass) {
        if (DATA_CONVERT_MAPPING.containsKey(typeClass)) {
            return DATA_CONVERT_MAPPING.get(typeClass);
        }
        return Types.OTHER;
    }

    /**
     * <h3 class="en-US">Fill in the basic information and calculate the identification code</h3>
     * <h3 class="zh-CN">填充基本信息并计算识别代码</h3>
     *
     * @param primaryKeyMap <span class="en-US">Primary key data mapping table</span>
     *                      <span class="zh-CN">主键数据映射表</span>
     * @param tableConfig   <span class="en-US">Table configure information instance</span>
     *                      <span class="zh-CN">数据表配置信息实例对象</span>
     * @param identifyKey   <span class="en-US">Lazy load item identify key</span>
     *                      <span class="zh-CN">懒加载项识别代码</span>
     * @return <span class="en-US">Generated identification code</span>
     * <span class="zh-CN">生成的识别码</span>
     */
    private static String identifyKey(@Nonnull final SortedMap<String, Object> primaryKeyMap,
                                      @Nonnull final TableConfig tableConfig,
                                      final String identifyKey)
            throws TableConfigException {
        primaryKeyMap.put(DatabaseCommons.CONTENT_MAP_KEY_DATABASE_NAME.toUpperCase(),
                tableConfig.getSchemaName());
        primaryKeyMap.put(DatabaseCommons.CONTENT_MAP_KEY_TABLE_NAME.toUpperCase(),
                tableConfig.getTableName());
        if (StringUtils.notBlank(identifyKey)) {
            primaryKeyMap.put(DatabaseCommons.CONTENT_MAP_KEY_ITEM.toUpperCase(),
                    Optional.ofNullable(tableConfig.columnConfig(identifyKey))
                            .filter(ColumnConfig::isLazyLoad)
                            .map(columnConfig -> columnConfig.columnName().toUpperCase())
                            .orElseThrow(() ->
                                    new TableConfigException(0x00DB00000002L, "Column_Not_Found_Error", identifyKey)));
        }
        return ConvertUtils.toHex(SecurityUtils.SHA256(primaryKeyMap));
    }

    /**
     * <h3 class="en-US">Read the value of the corresponding field of the data column from the instance of the entity class object</h3>
     * <span class="en-US">If the data converter exists, the data is transformed using the converter</span>
     * <h3 class="zh-CN">从实体类对象实例中读取数据列对应字段的值</h3>
     * <span class="zh-CN">如果数据转换器存在，则使用转换器对数据进行转换</span>
     *
     * @param object       <span class="en-US">Entity object instance</span>
     *                     <span class="zh-CN">实体对象实例</span>
     * @param columnConfig <span class="en-US">Column configure information instance</span>
     *                     <span class="zh-CN">数据列配置信息实例对象</span>
     * @param converter    <span class="en-US">Data converter instance</span>
     *                     <span class="zh-CN">数据转换器实例对象</span>
     * @return <span class="en-US">Read value</span>
     * <span class="zh-CN">读取的字段值</span>
     */
    private static Object retrieveValue(final Object object, final ColumnConfig columnConfig,
                                        final Converter converter) {
        Object columnValue = ReflectionUtils.getFieldValue(columnConfig.getFieldName(), object);
        if (columnValue == null || converter == null) {
            return columnValue;
        }
        return converter.convertValue(columnConfig, columnValue);
    }
}
