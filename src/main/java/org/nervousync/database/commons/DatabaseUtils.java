package org.nervousync.database.commons;

import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import org.nervousync.annotations.provider.Provider;
import org.nervousync.commons.Globals;
import org.nervousync.database.annotations.query.ResultData;
import org.nervousync.database.annotations.transactional.Transactional;
import org.nervousync.database.api.DatabaseClient;
import org.nervousync.database.api.DatabaseManager;
import org.nervousync.database.beans.configs.column.ColumnConfig;
import org.nervousync.database.beans.configs.transactional.TransactionalConfig;
import org.nervousync.database.dialects.Converter;
import org.nervousync.database.entity.EntityManager;
import org.nervousync.database.entity.core.BaseObject;
import org.nervousync.database.enumerations.join.JoinType;
import org.nervousync.database.enumerations.lock.LockOption;
import org.nervousync.database.enumerations.query.OrderType;
import org.nervousync.database.exceptions.core.DatabaseException;
import org.nervousync.database.query.filter.GroupBy;
import org.nervousync.database.query.filter.OrderBy;
import org.nervousync.database.query.join.JoinInfo;
import org.nervousync.utils.*;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Types;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

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
	/**
	 * <span class="en-US">Registered database manager name and implementation class mapping table</span>
	 * <span class="zh-CN">注册的数据库管理器名称和实现类映射表</span>
	 */
	private static final Hashtable<String, Class<?>> REGISTERED_DATABASE_MANAGER_PROVIDERS = new Hashtable<>();
	private static DatabaseManager DATABASE_MANAGER = null;

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

		ServiceLoader.load(DatabaseManager.class)
				.forEach(databaseManager ->
						Optional.ofNullable(databaseManager.getClass().getAnnotation(Provider.class))
								.ifPresent(provider ->
										REGISTERED_DATABASE_MANAGER_PROVIDERS.put(provider.name(),
												databaseManager.getClass())));
	}

	/**
	 * <h3 class="en-US">Initializes the database manager based on the given provider name</h3>
	 * <h3 class="zh-CN">根据给定的适配器名称进行数据库管理器的初始化</h3>
	 *
	 * @param managerName <span class="en-US">Provider name</span>
	 *                    <span class="zh-CN">适配器名称</span>
	 * @throws DatabaseException <span class="en-US">An error occurred while initializing the database manager instance</span>
	 *                           <span class="zh-CN">初始化数据库管理器实例时出错</span>
	 */
	public static void initialize(final String managerName) throws DatabaseException {
		DatabaseManager databaseManager =
				Optional.ofNullable(REGISTERED_DATABASE_MANAGER_PROVIDERS.get(managerName))
						.map(managerClass -> (DatabaseManager) ObjectUtils.newInstance(managerClass))
						.orElseThrow(() -> new DatabaseException(0x00DB00000004L));
		if (databaseManager.initialize()) {
			if (DATABASE_MANAGER == null) {
				Runtime.getRuntime().addShutdownHook(new Thread(DatabaseUtils::destroy));
			} else {
				LOGGER.warn("Manager_Override");
				DATABASE_MANAGER.destroy();
			}
			DATABASE_MANAGER = databaseManager;
			return;
		}
		throw new DatabaseException(0x00DB00000005L);
	}

	public static String tableKey(final String string) {
		if (StringUtils.isEmpty(string)) {
			return Globals.DEFAULT_VALUE_STRING;
		}
		return ConvertUtils.toHex(SecurityUtils.SHA256(string));
	}

	/**
	 * <h3 class="en-US">Register entity class array</h3>
	 * <h3 class="zh-CN">注册实体类数组</h3>
	 *
	 * @param entityClasses <span class="en-US">Entity class array</span>
	 *                      <span class="zh-CN">实体类数组</span>
	 * @return <span class="en-US">Process success count</span>
	 * <span class="zh-CN">操作成功计数</span>
	 */
	public static int registerTable(final Class<?>... entityClasses) {
		AtomicInteger successCount = new AtomicInteger(Globals.INITIALIZE_INT_VALUE);
		Optional.ofNullable(DATABASE_MANAGER)
				.ifPresent(databaseManager ->
						EntityManager.registerTable(entityClasses).forEach(tableConfig -> {
							if (databaseManager.initTable(tableConfig)) {
								successCount.incrementAndGet();
							} else {
								//  Remove register if initialize table error
								EntityManager.removeTable(tableConfig.getDefineClass());
							}
						}));
		return successCount.get();
	}

	/**
	 * <h3 class="en-US">Truncate entity class array</h3>
	 * <h3 class="zh-CN">清空实体类数组的数据记录</h3>
	 *
	 * @param entityClasses <span class="en-US">Entity class array</span>
	 *                      <span class="zh-CN">实体类数组</span>
	 */
	public static void truncateTable(final Class<?>... entityClasses) {
		Optional.ofNullable(DATABASE_MANAGER)
				.ifPresent(databaseManager -> databaseManager.truncateTable(entityClasses));
	}

	/**
	 * <h3 class="en-US">Delete the data table corresponding to the entity class based on the given entity class array</h3>
	 * <h3 class="zh-CN">根据给定的实体类数组删除实体类对应的数据表</h3>
	 *
	 * @param entityClasses <span class="en-US">Entity class array</span>
	 *                      <span class="zh-CN">实体类数组</span>
	 * @return <span class="en-US">Process success count</span>
	 * <span class="zh-CN">操作成功计数</span>
	 */
	public static int dropTable(final Class<?>... entityClasses) {
		AtomicInteger successCount = new AtomicInteger(Globals.INITIALIZE_INT_VALUE);
		Optional.ofNullable(DATABASE_MANAGER)
				.ifPresent(databaseManager ->
						EntityManager.removeTable(entityClasses).forEach(tableConfig -> {
							if (databaseManager.dropTable(tableConfig)) {
								successCount.incrementAndGet();
							}
						}));
		return successCount.get();
	}

	/**
	 * <h3 class="en-US">Generate database client in data restore mode</h3>
	 * <h3 class="zh-CN">生成数据恢复模式的数据操作客户端实例对象</h3>
	 *
	 * @param txConfig <span class="en-US">Transactional configure information object instance</span>
	 *                 <span class="zh-CN">事务配置信息实例对象</span>
	 * @return <span class="en-US">Generated database client instance</span>
	 * <span class="zh-CN">生成的数据操作客户端实例对象</span>
	 */
	public static DatabaseClient restoreClient(final TransactionalConfig txConfig) {
		return Optional.ofNullable(DATABASE_MANAGER)
				.map(databaseManager -> {
					if (txConfig == null) {
						return databaseManager.restoreClient();
					} else {
						return databaseManager.generateClient(txConfig, Boolean.TRUE);
					}
				})
				.orElse(null);
	}

	/**
	 * <h3 class="en-US">Generate database client in read only mode</h3>
	 * <h3 class="zh-CN">生成只读模式的数据操作客户端实例对象</h3>
	 *
	 * @return <span class="en-US">Generated database client instance</span>
	 * <span class="zh-CN">生成的数据操作客户端实例对象</span>
	 */
	public static DatabaseClient readOnlyClient() {
		return Optional.ofNullable(DATABASE_MANAGER).map(DatabaseManager::readOnlyClient).orElse(null);
	}

	/**
	 * <h3 class="en-US">Generate database client</h3>
	 * <h3 class="zh-CN">生成数据操作客户端实例对象</h3>
	 *
	 * @return <span class="en-US">Generated database client instance</span>
	 * <span class="zh-CN">生成的数据操作客户端实例对象</span>
	 */
	public static DatabaseClient retrieveClient() {
		return Optional.ofNullable(DATABASE_MANAGER).map(DatabaseManager::generateClient).orElse(null);
	}

	/**
	 * <h3 class="en-US">Generate database client in transactional mode</h3>
	 * <h3 class="zh-CN">生成事务模式的数据操作客户端实例对象</h3>
	 *
	 * @param clazz      <span class="en-US">The database client using for class</span>
	 *                   <span class="zh-CN">使用数据操作客户端的类</span>
	 * @param methodName <span class="en-US">The database client using for method name</span>
	 *                   <span class="zh-CN">使用数据操作客户端的方法名</span>
	 * @return <span class="en-US">Generated database client instance</span>
	 * <span class="zh-CN">生成的数据操作客户端实例对象</span>
	 */
	public static DatabaseClient retrieveClient(final Class<?> clazz, final String methodName) {
		return retrieveClient(transactionalConfig(clazz, methodName));
	}

	/**
	 * <h3 class="en-US">Generate transaction mode data operation client instance objects based on given transaction configuration information</h3>
	 * <h3 class="zh-CN">根据给定的事务配置信息生成事务模式的数据操作客户端实例对象</h3>
	 *
	 * @param txConfig <span class="en-US">Transactional configure information</span>
	 *                 <span class="zh-CN">事务配置信息</span>
	 * @return <span class="en-US">Generated database client instance</span>
	 * <span class="zh-CN">生成的数据操作客户端实例对象</span>
	 */
	public static DatabaseClient retrieveClient(final TransactionalConfig txConfig) {
		return Optional.ofNullable(DATABASE_MANAGER)
				.map(databaseManager -> databaseManager.generateClient(txConfig))
				.orElse(null);
	}

	/**
	 * <h3 class="en-US">Find the corresponding client instance object based on the given transaction identification code</h3>
	 * <h3 class="zh-CN">根据给定的事务识别代码查找对应的客户端实例对象</h3>
	 *
	 * @param transactionalCode <span class="en-US">transaction identification code</span>
	 *                          <span class="zh-CN">事务识别代码</span>
	 * @return <span class="en-US">Retrieved database client instance, return <code>null</code> if not found</span>
	 * <span class="zh-CN">找到的数据操作客户端实例对象，如果未找到则返回<code>null</code></span>
	 */
	public static DatabaseClient retrieveClient(final long transactionalCode) {
		if (transactionalCode == Globals.DEFAULT_VALUE_LONG) {
			return null;
		}
		return Optional.ofNullable(DATABASE_MANAGER)
				.map(databaseManager -> databaseManager.retrieveClient(transactionalCode))
				.orElse(null);
	}

	/**
	 * <h3 class="en-US">
	 * Destroys the initialized database manager and clears all registered
	 * manager adapters and data conversion mappings.
	 * </h3>
	 * <h3 class="zh-CN">销毁已初始化的数据库管理器并清除所有注册的管理器适配器和数据转换映射</h3>
	 */
	public static void destroy() {
		if (DATABASE_MANAGER != null) {
			DATABASE_MANAGER.destroy();
			DATABASE_MANAGER = null;
		}
		REGISTERED_DATABASE_MANAGER_PROVIDERS.clear();
		DATA_CONVERT_MAPPING.clear();
		EntityManager.destroy();
		DataUtils.destroy();
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
	 * <h3 class="en-US">Generates an immutable information identification string based on a given entity object instance</h3>
	 * <h3 class="zh-CN">根据给定的实体对象实例生成不可变信息识别字符串</h3>
	 *
	 * @param object <span class="en-US">Entity object instance</span>
	 *               <span class="zh-CN">实体对象实例</span>
	 * @return <span class="en-US">Generated cache key string</span>
	 * <span class="zh-CN">生成的缓存键值</span>
	 */
	public static String uniqueKey(@Nonnull final Object object) {
		return identifyKey(primaryKeyMap(object), object.getClass(), DataType.UNIQUE, Globals.DEFAULT_VALUE_STRING);
	}

	/**
	 * <h3 class="en-US">Generate an immutable information identification string based on the given data mapping table and entity class information</h3>
	 * <h3 class="zh-CN">根据给定的数据映射表和实体类信息生成不可变信息识别字符串</h3>
	 *
	 * @param dataMap     <span class="en-US">Primary key data mapping table</span>
	 *                    <span class="zh-CN">主键数据映射表</span>
	 * @param defineClass <span class="en-US">Entity define class</span>
	 *                    <span class="zh-CN">实体类定义</span>
	 * @return <span class="en-US">Generated cache key string</span>
	 * <span class="zh-CN">生成的缓存键值</span>
	 */
	public static String uniqueKey(@Nonnull final Map<String, Object> dataMap, final Class<?> defineClass) {
		return identifyKey(dataMap, defineClass, DataType.UNIQUE, Globals.DEFAULT_VALUE_STRING);
	}

	/**
	 * <h3 class="en-US">Generate an updatable information identification string based on a given entity object instance</h3>
	 * <h3 class="zh-CN">根据给定的实体对象实例生成可更新信息识别字符串</h3>
	 *
	 * @param object <span class="en-US">Entity object instance</span>
	 *               <span class="zh-CN">实体对象实例</span>
	 * @return <span class="en-US">Generated cache key string</span>
	 * <span class="zh-CN">生成的缓存键值</span>
	 */
	public static String updatableKey(@Nonnull final Object object) {
		return identifyKey(primaryKeyMap(object), object.getClass(), DataType.UPDATABLE, Globals.DEFAULT_VALUE_STRING);
	}

	/**
	 * <h3 class="en-US">Generate an updatable information identification string based on the given data mapping table and entity class information</h3>
	 * <h3 class="zh-CN">根据给定的数据映射表和实体类信息生成可更新信息识别字符串</h3>
	 *
	 * @param dataMap     <span class="en-US">Primary key data mapping table</span>
	 *                    <span class="zh-CN">主键数据映射表</span>
	 * @param defineClass <span class="en-US">Entity define class</span>
	 *                    <span class="zh-CN">实体类定义</span>
	 * @return <span class="en-US">Generated cache key string</span>
	 * <span class="zh-CN">生成的缓存键值</span>
	 */
	public static String updatableKey(@Nonnull final Map<String, Object> dataMap, final Class<?> defineClass) {
		return identifyKey(dataMap, defineClass, DataType.UPDATABLE, Globals.DEFAULT_VALUE_STRING);
	}

	/**
	 * <h3 class="en-US">Generate lazy loading information identification string based on the given entity object instance and lazy loading identification code</h3>
	 * <h3 class="zh-CN">根据给定的实体对象实例和懒加载识别代码生成懒加载信息识别字符串</h3>
	 *
	 * @param object      <span class="en-US">Entity object instance</span>
	 *                    <span class="zh-CN">实体对象实例</span>
	 * @param identifyKey <span class="en-US">Lazy load item identify key</span>
	 *                    <span class="zh-CN">懒加载项识别代码</span>
	 * @return <span class="en-US">Generated cache key string</span>
	 * <span class="zh-CN">生成的缓存键值</span>
	 */
	public static String lazyLoadKey(@Nonnull final Object object, @Nonnull final String identifyKey) {
		return identifyKey(primaryKeyMap(object), object.getClass(), DataType.LAZY_LOAD, identifyKey);
	}

	/**
	 * <h3 class="en-US">Generate lazy loading information identification string based on the given data mapping table, entity class information and lazy loading identification code</h3>
	 * <h3 class="zh-CN">根据给定的数据映射表、实体类信息和懒加载识别代码生成懒加载信息识别字符串</h3>
	 *
	 * @param dataMap     <span class="en-US">Primary key data mapping table</span>
	 *                    <span class="zh-CN">主键数据映射表</span>
	 * @param defineClass <span class="en-US">Entity define class</span>
	 *                    <span class="zh-CN">实体类定义</span>
	 * @param identifyKey <span class="en-US">Lazy load item identify key</span>
	 *                    <span class="zh-CN">懒加载项识别代码</span>
	 * @return <span class="en-US">Generated cache key string</span>
	 * <span class="zh-CN">生成的缓存键值</span>
	 */
	public static String lazyLoadKey(@Nonnull final Map<String, Object> dataMap, final Class<?> defineClass,
	                                 @Nonnull final String identifyKey) {
		return identifyKey(dataMap, defineClass, DataType.LAZY_LOAD, identifyKey);
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
		Optional.ofNullable(EntityManager.tableConfig(ClassUtils.originalClassName(object.getClass())))
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
	 * <h3 class="en-US">Generate a can't updatable data mapping table based on the given entity object instance</h3>
	 * <h3 class="zh-CN">根据给定的实体对象实例生成不可更新数据映射表</h3>
	 *
	 * @param object <span class="en-US">Entity object instance</span>
	 *               <span class="zh-CN">实体对象实例</span>
	 * @return <span class="en-US">Generated data mapping table</span>
	 * <span class="zh-CN">生成的数据映射表</span>
	 */
	public static Map<String, Object> uniqueFieldMap(final Object object) {
		return fieldDataMap(object, columnConfig -> !columnConfig.isLazyLoad() && !columnConfig.isUpdatable());
	}

	/**
	 * <h3 class="en-US">Generate a updatable data mapping table based on the given entity object instance</h3>
	 * <h3 class="zh-CN">根据给定的实体对象实例生成可更新数据映射表</h3>
	 *
	 * @param object <span class="en-US">Entity object instance</span>
	 *               <span class="zh-CN">实体对象实例</span>
	 * @return <span class="en-US">Generated data mapping table</span>
	 * <span class="zh-CN">生成的数据映射表</span>
	 */
	public static Map<String, Object> updatableFieldMap(final Object object) {
		return fieldDataMap(object, columnConfig -> !columnConfig.isLazyLoad() && columnConfig.isUpdatable());
	}

	/**
	 * <h3 class="en-US">Generate a lazy load data mapping table based on the given entity object instance</h3>
	 * <h3 class="zh-CN">根据给定的实体对象实例生成懒加载数据映射表</h3>
	 *
	 * @param object <span class="en-US">Entity object instance</span>
	 *               <span class="zh-CN">实体对象实例</span>
	 * @return <span class="en-US">Generated data mapping table</span>
	 * <span class="zh-CN">生成的数据映射表</span>
	 */
	public static Map<String, Object> lazyLoadFieldMap(final Object object) {
		return fieldDataMap(object, ColumnConfig::isLazyLoad);
	}

	/**
	 * <h3 class="en-US">Generate a data mapping table based on a given entity object instance</h3>
	 * <h3 class="zh-CN">根据给定的实体对象实例生成数据映射表</h3>
	 *
	 * @param object <span class="en-US">Entity object instance</span>
	 *               <span class="zh-CN">实体对象实例</span>
	 * @return <span class="en-US">Generated data mapping table</span>
	 * <span class="zh-CN">生成的数据映射表</span>
	 */
	public static SortedMap<String, Object> dataMap(final Object object) {
		return dataMap(object, null);
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
	public static SortedMap<String, Object> dataMap(final Object object, final Converter converter) {
		final SortedMap<String, Object> parameterMap = new TreeMap<>();
		Optional.ofNullable(EntityManager.tableConfig(ClassUtils.originalClassName(object.getClass())))
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
	public static SortedMap<String, Object> modifiedDataMap(final Object object, final Converter converter) {
		final TreeMap<String, Object> parameterMap = new TreeMap<>();
		Optional.ofNullable(EntityManager.tableConfig(ClassUtils.originalClassName(object.getClass())))
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
	 * <h3 class="en-US">Check the given member instance is contains annotation</h3>
	 * <h3 class="zh-CN">检查给定的成员对象实例包含标注信息</h3>
	 *
	 * @param member <span class="en-US">Member instance</span>
	 *               <span class="zh-CN">成员对象实例</span>
	 * @return <span class="en-US">Check result</span>
	 * <span class="zh-CN">检查结果</span>
	 */
	public static boolean annotationMember(final Member member) {
		if (member == null) {
			return Boolean.FALSE;
		}
		if (member instanceof Field) {
			Field field = (Field) member;
			return field.isAnnotationPresent(Column.class) || field.isAnnotationPresent(EmbeddedId.class)
					|| (field.isAnnotationPresent(OneToMany.class) || field.isAnnotationPresent(ManyToOne.class)
					|| field.isAnnotationPresent(OneToOne.class)) &&
					(field.isAnnotationPresent(JoinColumns.class) || field.isAnnotationPresent(JoinColumn.class));
		}
		if (member instanceof Method) {
			Method method = (Method) member;
			return (method.isAnnotationPresent(OneToMany.class) || method.isAnnotationPresent(ManyToOne.class))
					&& (method.isAnnotationPresent(JoinColumns.class) || method.isAnnotationPresent(JoinColumn.class))
					&& (method.getName().startsWith("get") || method.getName().startsWith("is"));
		}
		return Boolean.FALSE;
	}

	/**
	 * <h3 class="en-US">Check the given member instance is contains annotation</h3>
	 * <h3 class="zh-CN">检查给定的成员对象实例包含标注信息</h3>
	 *
	 * @param member <span class="en-US">Member instance</span>
	 *               <span class="zh-CN">成员对象实例</span>
	 * @return <span class="en-US">Check result</span>
	 * <span class="zh-CN">检查结果</span>
	 */
	public static boolean resultDataMember(final Member member) {
		if (member == null) {
			return Boolean.FALSE;
		}
		if (member instanceof Field) {
			Field field = (Field) member;
			return field.isAnnotationPresent(ResultData.class);
		}
		return Boolean.FALSE;
	}

	/**
	 * <h3 class="en-US">Calculate query offset value</h3>
	 * <h3 class="zh-CN">计算查询起始记录数</h3>
	 *
	 * @param pageNo    <span class="en-US">Page number</span>
	 *                  <span class="zh-CN">页数</span>
	 * @param pageLimit <span class="en-US">Page limit</span>
	 *                  <span class="zh-CN">每页记录数</span>
	 * @return <span class="en-US">Calculate result</span>
	 * <span class="zh-CN">计算结果</span>
	 */
	public static int queryOffset(final int pageNo, final int pageLimit) {
		if (pageNo == Globals.DEFAULT_VALUE_INT) {
			return Globals.DEFAULT_VALUE_INT;
		}
		int currentPage = (pageNo <= 0) ? DatabaseCommons.DEFAULT_PAGE_NO : pageNo;
		return (currentPage - 1) * queryLimit(pageLimit);
	}

	/**
	 * <h3 class="en-US">Calculate query limit value</h3>
	 * <h3 class="zh-CN">计算查询分页记录数</h3>
	 *
	 * @param pageLimit <span class="en-US">Page limit</span>
	 *                  <span class="zh-CN">每页记录数</span>
	 * @return <span class="en-US">Calculate result</span>
	 * <span class="zh-CN">计算结果</span>
	 */
	public static int queryLimit(final int pageLimit) {
		return pageLimit <= 0 ? DatabaseCommons.DEFAULT_PAGE_LIMIT : pageLimit;
	}

	/**
	 * <h3 class="en-US">Private construction methods defined by database basic tools</h3>
	 * <h3 class="zh-CN">数据库基本工具定义的私有构造方法</h3>
	 */
	private DatabaseUtils() {
	}

	/**
	 * <h3 class="en-US">Merge matching result sets</h3>
	 * <h3 class="zh-CN">合并匹配的结果集</h3>
	 *
	 * @param recordMap  <span class="en-US">Driver table record map</span>
	 *                   <span class="zh-CN">驱动表记录</span>
	 * @param recordList <span class="en-US">Join result list</span>
	 *                   <span class="zh-CN">关联信息列表</span>
	 * @return <span class="en-US">Merged result set</span>
	 * <span class="zh-CN">合并后的结果集</span>
	 */
	private static List<Map<String, Object>> mergeRecords(@Nonnull final Map<String, Object> recordMap,
	                                                      @Nonnull final List<Map<String, Object>> recordList) {
		List<Map<String, Object>> mergeRecords = new ArrayList<>();
		if (recordList.isEmpty()) {
			mergeRecords.add(recordMap);
		} else {
			recordList.forEach(filterRecord -> {
				Map<String, Object> dataMap = new HashMap<>(recordMap);
				dataMap.putAll(filterRecord);
				mergeRecords.add(dataMap);
			});
		}
		return mergeRecords;
	}

	/**
	 * <h2 class="en-US">Recordset sorter</h2>
	 * <h2 class="zh-CN">记录集排序器</h2>
	 *
	 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
	 * @version $Revision: 1.0.0 $ $Date: Feb 27, 2021 14:28:19 $
	 */
	private static final class RecordComparator implements Comparator<Map<String, Object>> {

		/**
		 * <span class="en-US">Query order by columns list</span>
		 * <span class="zh-CN">查询排序数据列列表</span>
		 */
		private final List<OrderBy> orderByList;

		/**
		 * <h3 class="en-US">Private construction methods for recordset sorter</h3>
		 * <h3 class="zh-CN">记录集排序器的私有构造方法</h3>
		 *
		 * @param orderByList <span class="en-US">Query order by columns list</span>
		 *                    <span class="zh-CN">查询排序数据列列表</span>
		 */
		private RecordComparator(final List<OrderBy> orderByList) {
			this.orderByList = (orderByList == null) ? new ArrayList<>() : orderByList;
		}

		@Override
		@SuppressWarnings({"unchecked", "rawtypes"})
		public int compare(final Map<String, Object> o1, final Map<String, Object> o2) {
			if (!this.orderByList.isEmpty()) {
				for (OrderBy orderBy : orderByList) {
					Object data1 = o1.get(orderBy.getIdentifyKey());
					Object data2 = o2.get(orderBy.getIdentifyKey());
					if (data1 instanceof Comparable) {
						int compare = ((Comparable) data1).compareTo(data2);
						if (compare != 0) {
							return OrderType.DESC.equals(orderBy.getOrderType()) ? compare * -1 : compare;
						}
					}
				}
			}
			return Globals.INITIALIZE_INT_VALUE;
		}
	}

	/**
	 * <h3 class="en-US">Filter the result set based on the given information</h3>
	 * <h3 class="zh-CN">根据给定的信息过滤结果集</h3>
	 *
	 * @param joinType    <span class="en-US">Table join type</span>
	 *                    <span class="zh-CN">数据表关联类型</span>
	 * @param recordMap   <span class="en-US">Driver table record map</span>
	 *                    <span class="zh-CN">驱动表记录</span>
	 * @param recordList  <span class="en-US">Join result list</span>
	 *                    <span class="zh-CN">关联信息列表</span>
	 * @param joinInfos   <span class="en-US">Join columns list</span>
	 *                    <span class="zh-CN">关联列信息列表</span>
	 * @param groupByList <span class="en-US">Query group by columns list</span>
	 *                    <span class="zh-CN">查询分组数据列列表</span>
	 * @param orderByList <span class="en-US">Query order by columns list</span>
	 *                    <span class="zh-CN">查询排序数据列列表</span>
	 * @return <span class="en-US">Filter the result set</span>
	 * <span class="zh-CN">过滤结果集</span>
	 */
	private static List<Map<String, Object>> filterRecords(final JoinType joinType, final Map<String, Object> recordMap,
	                                                       final List<Map<String, Object>> recordList,
	                                                       final List<JoinInfo> joinInfos,
	                                                       final List<GroupBy> groupByList,
	                                                       final List<OrderBy> orderByList) {
		List<Map<String, Object>> matchRecords = new ArrayList<>();
		recordList.stream()
				.filter(joinRecord -> matchJoin(joinType, recordMap, joinRecord, joinInfos))
				.forEach(matchRecords::add);

		matchRecords.sort(new RecordComparator(orderByList));

		List<Map<String, Object>> filterRecords = new ArrayList<>();
		if (!groupByList.isEmpty()) {
			matchRecords.forEach(filterRecord -> {
				if (filterRecords.stream().noneMatch(existRecord ->
						groupByList.stream().allMatch(groupBy ->
								ObjectUtils.nullSafeEquals(existRecord.get(groupBy.getIdentifyKey()),
										filterRecord.get(groupBy.getIdentifyKey()))))) {
					filterRecords.add(filterRecord);
				}
			});
		} else {
			filterRecords.addAll(matchRecords);
		}

		return filterRecords;
	}

	/**
	 * <h3 class="en-US">Checks whether two result sets match associated information</h3>
	 * <h3 class="zh-CN">检查两个结果集是否匹配关联信息</h3>
	 *
	 * @param joinType   <span class="en-US">Table join type</span>
	 *                   <span class="zh-CN">数据表关联类型</span>
	 * @param mainRecord <span class="en-US">Driver table record map</span>
	 *                   <span class="zh-CN">驱动表记录</span>
	 * @param joinRecord <span class="en-US">Reference table record map</span>
	 *                   <span class="zh-CN">关联表记录</span>
	 * @param joinInfos  <span class="en-US">Join columns list</span>
	 *                   <span class="zh-CN">关联列信息列表</span>
	 * @return <span class="en-US">Check result</span>
	 * <span class="zh-CN">检查结果</span>
	 */
	private static boolean matchJoin(final JoinType joinType, final Map<String, Object> mainRecord,
	                                 final Map<String, Object> joinRecord, final List<JoinInfo> joinInfos) {
		Boolean matchResult = null;
		for (JoinInfo joinInfo : joinInfos) {
			Object mainObject, joinObject;
			switch (joinType) {
				case LEFT:
				case INNER:
					mainObject = mainRecord.get(joinInfo.getJoinKey());
					joinObject = joinRecord.get(joinInfo.getReferenceKey());
					break;
				case RIGHT:
				case FULL:
					mainObject = mainRecord.get(joinInfo.getReferenceKey());
					joinObject = joinRecord.get(joinInfo.getJoinKey());
					break;
				default:
					return Boolean.FALSE;
			}
			if (matchResult == null) {
				matchResult = ObjectUtils.nullSafeEquals(mainObject, joinObject);
			} else {
				switch (joinInfo.getConnectionCode()) {
					case AND:
						matchResult &= ObjectUtils.nullSafeEquals(mainObject, joinObject);
						break;
					case OR:
						matchResult |= ObjectUtils.nullSafeEquals(mainObject, joinObject);
						break;
				}
			}
		}
		return Optional.ofNullable(matchResult).orElse(Boolean.FALSE);
	}

	/**
	 * <h3 class="en-US">Generate a data mapping table based on the given entity object instance and data column information filter</h3>
	 * <h3 class="zh-CN">根据给定的实体对象实例和数据列信息过滤器生成数据映射表</h3>
	 *
	 * @param object    <span class="en-US">Entity object instance</span>
	 *                  <span class="zh-CN">实体对象实例</span>
	 * @param predicate <span class="en-US">Data column information filter</span>
	 *                  <span class="zh-CN">数据列信息过滤器</span>
	 * @return <span class="en-US">Generated data mapping table</span>
	 * <span class="zh-CN">生成的数据映射表</span>
	 */
	private static Map<String, Object> fieldDataMap(final Object object, final Predicate<ColumnConfig> predicate) {
		final Map<String, Object> dataMap = new HashMap<>();
		Optional.ofNullable(EntityManager.tableConfig(ClassUtils.originalClassName(object.getClass())))
				.ifPresent(tableConfig ->
						tableConfig.getColumnConfigs()
								.stream()
								.filter(predicate)
								.forEach(columnConfig ->
										Optional.ofNullable(ReflectionUtils.getFieldValue(columnConfig.getFieldName(), object))
												.ifPresent(fieldValue -> dataMap.put(columnConfig.getFieldName(), fieldValue))));
		return dataMap;
	}

	/**
	 * <h3 class="en-US">Based on the given class name and method name, check the transaction annotation and generate the transaction configuration information instance object</h3>
	 * <h3 class="zh-CN">根据给定的类名和方法名，检查事务注解，并生成事务配置信息实例对象</h3>
	 *
	 * @param clazz      <span class="en-US">The database client using for class</span>
	 *                   <span class="zh-CN">使用数据操作客户端的类</span>
	 * @param methodName <span class="en-US">The database client using for method name</span>
	 *                   <span class="zh-CN">使用数据操作客户端的方法名</span>
	 * @return <span class="en-US">Generated transaction configuration information instance object</span>
	 * <span class="zh-CN">生成的事务配置信息实例对象</span>
	 */
	private static TransactionalConfig transactionalConfig(final Class<?> clazz, final String methodName) {
		if (clazz == null || StringUtils.isEmpty(methodName)) {
			return null;
		}
		return Optional.ofNullable(ReflectionUtils.findMethod(clazz, methodName))
				.map(method -> TransactionalConfig.newInstance(method.getAnnotation(Transactional.class)))
				.orElse(null);
	}

	/**
	 * <h3 class="en-US">Fill in the basic information and calculate the identification code</h3>
	 * <h3 class="zh-CN">填充基本信息并计算识别代码</h3>
	 *
	 * @param dataMap        <span class="en-US">Primary key data mapping table</span>
	 *                       <span class="zh-CN">主键数据映射表</span>
	 * @param defineClass    <span class="en-US">Entity define class</span>
	 *                       <span class="zh-CN">实体类定义</span>
	 * @param dataType       <span class="en-US">Data type enumeration value</span>
	 *                       <span class="zh-CN">数据类型枚举值</span>
	 * @param columnIdentify <span class="en-US">Lazy load item identify key</span>
	 *                       <span class="zh-CN">懒加载项识别代码</span>
	 * @return <span class="en-US">Generated identification code</span>
	 * <span class="zh-CN">生成的识别码</span>
	 */
	private static String identifyKey(@Nonnull final Map<String, Object> dataMap, @Nonnull final Class<?> defineClass,
	                                  @Nonnull final DataType dataType, @Nonnull final String columnIdentify) {
		return Optional.ofNullable(EntityManager.tableConfig(defineClass))
				.map(tableConfig -> {
					TreeMap<String, Object> keyMap = new TreeMap<>();
					keyMap.put(DatabaseCommons.CONTENT_MAP_KEY_DATABASE_NAME.toUpperCase(),
							tableConfig.getSchemaName());
					keyMap.put(DatabaseCommons.CONTENT_MAP_KEY_TABLE_NAME.toUpperCase(),
							tableConfig.getTableName());
					keyMap.put(DatabaseCommons.CONTENT_MAP_KEY_DATA_TYPE.toUpperCase(), dataType.toString());
					dataMap.forEach((key, value) ->
							Optional.ofNullable(tableConfig.columnConfig(key))
									.ifPresent(columnConfig -> keyMap.put(columnConfig.columnName().toUpperCase(), value)));
					if (DataType.LAZY_LOAD.equals(dataType)) {
						ColumnConfig columnConfig = tableConfig.columnConfig(columnIdentify);
						if (columnConfig == null || !columnConfig.isLazyLoad()) {
							return Globals.DEFAULT_VALUE_STRING;
						}
						keyMap.put(DatabaseCommons.CONTENT_MAP_KEY_ITEM.toUpperCase(),
								columnConfig.columnName().toUpperCase());
					}
					return ConvertUtils.toHex(SecurityUtils.SHA256(keyMap));
				})
				.orElse(Globals.DEFAULT_VALUE_STRING);
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

	private enum DataType {
		UNIQUE, UPDATABLE, LAZY_LOAD
	}
}
