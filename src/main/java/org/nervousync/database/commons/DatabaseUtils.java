package org.nervousync.database.commons;

import jakarta.persistence.*;
import org.nervousync.annotations.provider.Provider;
import org.nervousync.commons.Globals;
import org.nervousync.database.annotations.query.ResultData;
import org.nervousync.database.annotations.transactional.Transactional;
import org.nervousync.database.api.DatabaseClient;
import org.nervousync.database.api.DatabaseManager;
import org.nervousync.database.beans.configs.column.ColumnConfig;
import org.nervousync.database.beans.configs.transactional.TransactionalConfig;
import org.nervousync.database.entity.EntityManager;
import org.nervousync.database.enumerations.lock.LockOption;
import org.nervousync.database.exceptions.core.DatabaseException;
import org.nervousync.utils.*;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Types;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <h2 class="en-US">Database utilities define</h2>
 * <h2 class="zh-CN">数据库基本工具定义</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
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
	 * <h4 class="en-US">Initializes the database manager based on the given provider name</h4>
	 * <h4 class="zh-CN">根据给定的适配器名称进行数据库管理器的初始化</h4>
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
						.orElseThrow(() -> new DatabaseException(0x00DB00000003L));
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
		throw new DatabaseException(0x00DB00000004L);
	}

	/**
	 * <h4 class="en-US">Data table identification code</h4>
	 * <h4 class="zh-CN">数据表识别代码</h4>
	 *
	 * @param string <span class="en-US">Data table name or entity class name</span>
	 *               <span class="zh-CN">数据表名或实体类名</span>
	 * @return <span class="en-US">Identification code</span>
	 * <span class="zh-CN">识别代码</span>
	 */
	public static String tableKey(final String string) {
		if (StringUtils.isEmpty(string)) {
			return Globals.DEFAULT_VALUE_STRING;
		}
		return ConvertUtils.toHex(SecurityUtils.SHA256(string));
	}

	/**
	 * <h4 class="en-US">Register entity class array</h4>
	 * <h4 class="zh-CN">注册实体类数组</h4>
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
	 * <h4 class="en-US">Truncate entity class array</h4>
	 * <h4 class="zh-CN">清空实体类数组的数据记录</h4>
	 *
	 * @param entityClasses <span class="en-US">Entity class array</span>
	 *                      <span class="zh-CN">实体类数组</span>
	 */
	public static void truncateTable(final Class<?>... entityClasses) {
		Optional.ofNullable(DATABASE_MANAGER)
				.ifPresent(databaseManager -> databaseManager.truncateTable(entityClasses));
	}

	/**
	 * <h4 class="en-US">Delete the data table corresponding to the entity class based on the given entity class array</h4>
	 * <h4 class="zh-CN">根据给定的实体类数组删除实体类对应的数据表</h4>
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
	 * <h4 class="en-US">Generate database client in data restore mode</h4>
	 * <h4 class="zh-CN">生成数据恢复模式的数据操作客户端实例对象</h4>
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
	 * <h4 class="en-US">Generate database client in read only mode</h4>
	 * <h4 class="zh-CN">生成只读模式的数据操作客户端实例对象</h4>
	 *
	 * @return <span class="en-US">Generated database client instance</span>
	 * <span class="zh-CN">生成的数据操作客户端实例对象</span>
	 */
	public static DatabaseClient readOnlyClient() {
		return Optional.ofNullable(DATABASE_MANAGER).map(DatabaseManager::readOnlyClient).orElse(null);
	}

	/**
	 * <h4 class="en-US">Generate database client</h4>
	 * <h4 class="zh-CN">生成数据操作客户端实例对象</h4>
	 *
	 * @return <span class="en-US">Generated database client instance</span>
	 * <span class="zh-CN">生成的数据操作客户端实例对象</span>
	 */
	public static DatabaseClient retrieveClient() {
		return Optional.ofNullable(DATABASE_MANAGER).map(DatabaseManager::generateClient).orElse(null);
	}

	/**
	 * <h4 class="en-US">Generate database client in transactional mode</h4>
	 * <h4 class="zh-CN">生成事务模式的数据操作客户端实例对象</h4>
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
	 * <h4 class="en-US">Generate transaction mode data operation client instance objects based on given transaction configuration information</h4>
	 * <h4 class="zh-CN">根据给定的事务配置信息生成事务模式的数据操作客户端实例对象</h4>
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
	 * <h4 class="en-US">Find the corresponding client instance object based on the given transaction identification code</h4>
	 * <h4 class="zh-CN">根据给定的事务识别代码查找对应的客户端实例对象</h4>
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
	 * <h4 class="en-US">
	 * Destroys the initialized database manager and clears all registered
	 * manager adapters and data conversion mappings.
	 * </h4>
	 * <h4 class="zh-CN">销毁已初始化的数据库管理器并清除所有注册的管理器适配器和数据转换映射</h4>
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
	 * <h4 class="en-US">Register the mapping relationship between Java type and JDBC type code</h4>
	 * <h4 class="zh-CN">注册Java类型和JDBC类型代码的映射关系</h4>
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
	 * <h4 class="en-US">Generate a primary key data mapping table based on a given entity object instance</h4>
	 * <h4 class="zh-CN">根据给定的实体对象实例生成主键数据映射表</h4>
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
	 * <h4 class="en-US">Generate a primary key data mapping table based on a given entity object instance</h4>
	 * <h4 class="zh-CN">根据给定的实体对象实例生成主键数据映射表</h4>
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
	 * <h4 class="en-US">Retrieve the mapping JDBC type code by given Java type class</h4>
	 * <h4 class="zh-CN">通过给定的 Java 类型类检索映射 JDBC 类型代码</h4>
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
	 * <h4 class="en-US">Check the given member instance is contains annotation</h4>
	 * <h4 class="zh-CN">检查给定的成员对象实例包含标注信息</h4>
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
	 * <h4 class="en-US">Check the given member instance is contains annotation</h4>
	 * <h4 class="zh-CN">检查给定的成员对象实例包含标注信息</h4>
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
	 * <h4 class="en-US">Private construction methods defined by database basic tools</h4>
	 * <h4 class="zh-CN">数据库基本工具定义的私有构造方法</h4>
	 */
	private DatabaseUtils() {
	}

	/**
	 * <h4 class="en-US">Based on the given class name and method name, check the transaction annotation and generate the transaction configuration information instance object</h4>
	 * <h4 class="zh-CN">根据给定的类名和方法名，检查事务注解，并生成事务配置信息实例对象</h4>
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
}
