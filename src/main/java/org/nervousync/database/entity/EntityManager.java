/*
 * Licensed to the Nervousync Studio (NSYC) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.nervousync.database.entity;

import jakarta.annotation.Nonnull;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Table;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.matcher.ElementMatchers;
import org.nervousync.commons.Globals;
import org.nervousync.database.beans.configs.column.ColumnConfig;
import org.nervousync.database.beans.configs.reference.JoinConfig;
import org.nervousync.database.beans.configs.reference.ReferenceConfig;
import org.nervousync.database.beans.configs.table.TableConfig;
import org.nervousync.database.beans.configs.transfer.TransferBean;
import org.nervousync.database.commons.DatabaseUtils;
import org.nervousync.database.entity.core.BaseObject;
import org.nervousync.database.interceptors.DataModifyInterceptor;
import org.nervousync.database.interceptors.LazyLoadInterceptor;
import org.nervousync.office.excel.ExcelWriter;
import org.nervousync.utils.*;

import java.lang.reflect.Member;
import java.util.*;

/**
 * <h2 class="en-US">Entity Class Manager</h2>
 * <h2 class="zh-CN">实体类管理器</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Jan 9, 2021 14:57:46 $
 */
public final class EntityManager {
    /**
     * <span class="en-US">Logger instance</span>
     * <span class="zh-CN">日志实例</span>
     */
    private static final LoggerUtils.Logger LOGGER = LoggerUtils.getLogger(EntityManager.class);
    /**
     * <span class="en-US">Registered table configure mapping</span>
     * <span class="zh-CN">已注册的数据表配置信息映射</span>
     */
    private static final Hashtable<String, TableConfig> REGISTERED_CONFIGS = new Hashtable<>();
    /**
     * <span class="en-US">Registered data transfer configure mapping</span>
     * <span class="zh-CN">已注册的数据传输配置信息映射</span>
     */
    private static final Hashtable<String, TransferBean<?>> REGISTERED_TRANSFERS = new Hashtable<>();
    private static final List<Class<?>> REDEFINED_CLASSES = new ArrayList<>();

    static {
        ByteBuddyAgent.install();
    }

    /**
     * <h3 class="en-US">Private constructor for EntityManager</h3>
     * <h3 class="zh-CN">实体类管理器的私有构造方法</h3>
     */
    private EntityManager() {
    }

    /**
     * <h3 class="en-US">Parse the given array of entity classes and write the mapping relationship into the mapping table</h3>
     * <h3 class="zh-CN">解析给定的实体类数组，并将映射关系写入映射表</h3>
     *
     * @param entityClasses <span class="en-US">Entity classes array</span>
     *                      <span class="zh-CN">实体类数组</span>
     * @return <span class="en-US">Registered TableConfig instance list</span>
     * <span class="zh-CN">注册的数据表配置信息实例对象列表</span>
     */
    public static List<TableConfig> registerTable(final Class<?>... entityClasses) {
        List<TableConfig> registeredTables = new ArrayList<>();
        Arrays.asList(entityClasses).forEach(entityClass ->
                Optional.ofNullable(TableConfig.newInstance(entityClass))
                        .ifPresent(EntityManager::registerTable));
        return registeredTables;
    }

    /**
     * <h3 class="en-US">Register the data table configuration information of the remote data source</h3>
     * <h3 class="zh-CN">注册远程数据源的数据表配置信息</h3>
     *
     * @param tableConfig <span class="en-US">TableConfig instance</span>
     *                    <span class="zh-CN">数据表配置信息实例对象</span>
     */
    public static void registerTable(@Nonnull final TableConfig tableConfig) {
        if (StringUtils.isEmpty(tableConfig.getSchemaName()) || StringUtils.isEmpty(tableConfig.getTableName())) {
            return;
        }
        String identifyKey = tableConfig.identifyKey();
        if (tableExists(identifyKey)) {
            LOGGER.warn("Table_Config_Override",
                    tableConfig.getDefineClass().getName(), tableConfig.getTableName());
        }
        REGISTERED_CONFIGS.put(tableConfig.getTableName(), tableConfig);
        TransferBean<?> transferBean = TransferBean.newInstance(tableConfig.getDefineClass());
        REGISTERED_TRANSFERS.put(identifyKey, transferBean);
        Optional.of(DatabaseUtils.tableKey(tableConfig.getTableName()))
                .filter(StringUtils::notBlank)
                .ifPresent(tableKey -> {
                    REGISTERED_CONFIGS.put(tableKey, tableConfig);
                    REGISTERED_TRANSFERS.put(tableKey, transferBean);
                });
        redefineClass(tableConfig.getDefineClass());
        REGISTERED_CONFIGS.put(identifyKey, tableConfig);
        Optional.of(DatabaseUtils.tableKey(identifyKey))
                .filter(StringUtils::notBlank)
                .ifPresent(tableKey -> REGISTERED_CONFIGS.put(tableKey, tableConfig));
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Table_Config_Info", tableConfig.getDefineClass().getName(),
                    tableConfig.getTableName());
        }
    }

    /**
     * <h3 class="en-US">Remove the corresponding data table configuration information according to the given entity class array</h3>
     * <h3 class="zh-CN">根据给定的实体类数组移除对应的数据表配置信息</h3>
     *
     * @param entityClasses <span class="en-US">Entity classes array</span>
     *                      <span class="zh-CN">实体类数组</span>
     * @return <span class="en-US">List of deleted data table configuration information</span>
     * <span class="zh-CN">删除的数据表配置信息列表</span>
     */
    public static List<TableConfig> removeTable(final Class<?>... entityClasses) {
        List<TableConfig> removedTables = new ArrayList<>();
        Arrays.stream(entityClasses)
                .filter(entityClass -> REGISTERED_CONFIGS.containsKey(ClassUtils.originalClassName(entityClass)))
                .forEach(entityClass -> {
                    String className = ClassUtils.originalClassName(entityClass);
                    TableConfig tableConfig = REGISTERED_CONFIGS.get(className);
                    REGISTERED_CONFIGS.remove(tableConfig.getTableName());
                    REGISTERED_CONFIGS.remove(className);
                    removedTables.add(tableConfig);
                });
        return removedTables;
    }

    /**
     * <h3 class="en-US">Check if it is registered according to the given identification code</h3>
     * <h3 class="zh-CN">根据给定的识别代码检查是否注册</h3>
     *
     * @param identifyKey <span class="en-US">The identification code</span>
     *                    <span class="zh-CN">识别代码</span>
     * @return <span class="en-US">Registered returns <code>true</code>, unregistered returns <code>false</code></span>
     * <span class="zh-CN">已注册返回 <code>true</code>，未注册返回 <code>false</code></span>
     */
    public static boolean tableExists(final String identifyKey) {
        if (StringUtils.isEmpty(identifyKey)) {
            return Boolean.FALSE;
        }
        return REGISTERED_CONFIGS.containsKey(identifyKey);
    }

    /**
     * <h3 class="en-US">Check if it is registered according to the given identification code</h3>
     * <h3 class="zh-CN">根据给定的识别代码检查是否注册</h3>
     *
     * @param defineClass <span class="en-US">Entity class</span>
     *                    <span class="zh-CN">实体类</span>
     * @return <span class="en-US">Registered returns <code>true</code>, unregistered returns <code>false</code></span>
     * <span class="zh-CN">已注册返回 <code>true</code>，未注册返回 <code>false</code></span>
     */
    public static boolean tableExists(final Class<?> defineClass) {
        if (defineClass == null) {
            return Boolean.FALSE;
        }
        return REGISTERED_CONFIGS.containsKey(ClassUtils.originalClassName(defineClass));
    }

    /**
     * <h3 class="en-US">Retrieve the data table configuration information instance object according to the given entity class</h3>
     * <h3 class="zh-CN">根据给定的实体类检索数据表配置信息实例对象</h3>
     *
     * @param defineClass <span class="en-US">Entity class</span>
     *                    <span class="zh-CN">实体类</span>
     * @return <span class="en-US">Retrieved TableConfig instance</span>
     * <span class="zh-CN">检索到的数据表配置信息实例对象</span>
     */
    public static TableConfig tableConfig(final Class<?> defineClass) {
        return tableConfig(ClassUtils.originalClassName(defineClass));
    }

    /**
     * <h3 class="en-US">Retrieve the data table configuration information instance object according to the given identification code</h3>
     * <h3 class="zh-CN">根据给定的识别代码检索数据表配置信息实例对象</h3>
     *
     * @param identifyKey <span class="en-US">The identification code</span>
     *                    <span class="zh-CN">识别代码</span>
     * @return <span class="en-US">Retrieved TableConfig instance</span>
     * <span class="zh-CN">检索到的数据表配置信息实例对象</span>
     */
    public static TableConfig tableConfig(final String identifyKey) {
        if (StringUtils.isEmpty(identifyKey)) {
            return null;
        }
        return REGISTERED_CONFIGS.get(identifyKey);
    }

    /**
     * <h3 class="en-US">Retrieve the data table name according to the given identification code</h3>
     * <h3 class="zh-CN">根据给定的识别代码检索数据表名</h3>
     *
     * @param defineClass <span class="en-US">Entity class</span>
     *                    <span class="zh-CN">实体类</span>
     * @return <span class="en-US">Retrieved table name</span>
     * <span class="zh-CN">检索到的数据表名</span>
     */
    public static String tableName(final Class<?> defineClass) {
        return tableName(ClassUtils.originalClassName(defineClass));
    }

    /**
     * <h3 class="en-US">Retrieve the data table name according to the given identification code</h3>
     * <h3 class="zh-CN">根据给定的识别代码检索数据表名</h3>
     *
     * @param identifyKey <span class="en-US">The identification code</span>
     *                    <span class="zh-CN">识别代码</span>
     * @return <span class="en-US">Retrieved table name</span>
     * <span class="zh-CN">检索到的数据表名</span>
     */
    public static String tableName(final String identifyKey) {
        if (StringUtils.isEmpty(identifyKey)) {
            return Globals.DEFAULT_VALUE_STRING;
        }
        return Optional.ofNullable(REGISTERED_CONFIGS.get(identifyKey))
                .map(TableConfig::getTableName)
                .orElse(Globals.DEFAULT_VALUE_STRING);
    }

    public static List<JoinConfig> joinConfigs(final Class<?> mainEntity, final Class<?> joinEntity) {
        return Optional.ofNullable(tableConfig(mainEntity))
                .map(tableConfig -> tableConfig.referenceConfig(joinEntity))
                .map(ReferenceConfig::getJoinColumnList)
                .orElse(new ArrayList<>());
    }

    /**
     * <h3 class="en-US">Retrieve the database schema name according to the given identification code</h3>
     * <h3 class="zh-CN">根据给定的识别代码检索数据库名称</h3>
     *
     * @param defineClass <span class="en-US">Entity class</span>
     *                    <span class="zh-CN">实体类</span>
     * @return <span class="en-US">Retrieved table name</span>
     * <span class="zh-CN">检索到的数据库名称</span>
     */
    public static String schemaName(final Class<?> defineClass) {
        return schemaName(ClassUtils.originalClassName(defineClass));
    }

    /**
     * <h3 class="en-US">Retrieve the database schema name according to the given identification code</h3>
     * <h3 class="zh-CN">根据给定的识别代码检索数据库名称</h3>
     *
     * @param identifyKey <span class="en-US">The identification code</span>
     *                    <span class="zh-CN">识别代码</span>
     * @return <span class="en-US">Retrieved table name</span>
     * <span class="zh-CN">检索到的数据库名称</span>
     */
    public static String schemaName(final String identifyKey) {
        if (StringUtils.isEmpty(identifyKey)) {
            return Globals.DEFAULT_VALUE_STRING;
        }
        return Optional.ofNullable(REGISTERED_CONFIGS.get(identifyKey))
                .map(TableConfig::getSchemaName)
                .orElse(Globals.DEFAULT_VALUE_STRING);
    }

    /**
     * <h3 class="en-US">Check if the given entity class is in the same database</h3>
     * <h3 class="zh-CN">检查给定的实体类是否在同一个数据库中</h3>
     *
     * @param entityClass    <span class="en-US">Entity class</span>
     *                       <span class="zh-CN">实体类</span>
     * @param referenceClass <span class="en-US">Reference entity class</span>
     *                       <span class="zh-CN">关联实体类</span>
     * @return <span class="en-US">Match result</span>
     * <span class="zh-CN">匹配结果</span>
     */
    public static boolean schemaMatch(final Class<?> entityClass, final Class<?> referenceClass) {
        return ObjectUtils.nullSafeEquals(EntityManager.schemaName(entityClass),
                EntityManager.schemaName(referenceClass));
    }

    /**
     * <h3 class="en-US">Generate corresponding entity class instance objects according to the given data mapping table</h3>
     * <h3 class="zh-CN">根据给定的数据映射表生成对应实体类实例对象</h3>
     *
     * @param <T>         <span class="en-US">Entity define class</span>
     *                    <span class="zh-CN">实体类定义</span>
     * @param entityClass <span class="en-US">Entity define class</span>
     *                    <span class="zh-CN">实体类定义</span>
     * @param dataMap     <span class="en-US">data mapping table</span>
     *                    <span class="zh-CN">数据映射表</span>
     * @return <span class="en-US">Generated entity class instance object</span>
     * <span class="zh-CN">生成的实体类实例对象</span>
     */
    public static <T> T dataMapToObject(final Class<T> entityClass, final Map<String, String> dataMap) {
        return Optional.ofNullable(REGISTERED_TRANSFERS.get(ClassUtils.originalClassName(entityClass)))
                .map(transferBean -> entityClass.cast(transferBean.convert(dataMap)))
                .orElse(null);
    }

    public static Map<String, String> parseList(@Nonnull final Class<?> entityClass,
                                                @Nonnull final List<String> dataValues) {
        return Optional.ofNullable(REGISTERED_TRANSFERS.get(ClassUtils.originalClassName(entityClass)))
                .map(transferBean -> transferBean.parseList(dataValues))
                .orElse(new HashMap<>());
    }

    public static Map<String, Object> unmarshalMap(@Nonnull final Class<?> entityClass,
                                                   @Nonnull final Map<String, String> transferMap) {
        return Optional.ofNullable(REGISTERED_TRANSFERS.get(ClassUtils.originalClassName(entityClass)))
                .map(transferBean -> transferBean.unmarshalMap(transferMap))
                .orElse(new HashMap<>());
    }

    public static Map<String, String> objectToMap(final boolean removeRecord, @Nonnull final BaseObject baseObject) {
        Map<String, String> dataMap = new HashMap<>();
        if (tableExists(ClassUtils.originalClassName(baseObject.getClass()))) {
            Optional.ofNullable(REGISTERED_TRANSFERS.get(ClassUtils.originalClassName(baseObject.getClass())))
                    .ifPresent(transferBean ->
                            Optional.ofNullable(tableConfig(baseObject.getClass()))
                                    .ifPresent(tableConfig -> {
                                        List<String> fieldNames = new ArrayList<>();
                                        if (removeRecord) {
                                            tableConfig.getColumnConfigs()
                                                    .stream()
                                                    .filter(ColumnConfig::isPrimaryKey)
                                                    .forEach(columnConfig -> fieldNames.add(columnConfig.getFieldName()));
                                        } else {
                                            fieldNames.addAll(baseObject.modifiedColumns());
                                        }
                                        transferBean.transferMap(baseObject, fieldNames)
                                                .forEach((fieldName, value) ->
                                                        Optional.ofNullable(tableConfig.columnConfig(fieldName))
                                                                .ifPresent(columnConfig ->
                                                                        dataMap.put(columnConfig.columnName(), value)));
                                    }));
        }
        return dataMap;
    }

    public static void appendToExcel(@Nonnull final ExcelWriter excelWriter, @Nonnull final Object object) {
        Optional.ofNullable(REGISTERED_TRANSFERS.get(ClassUtils.originalClassName(object.getClass())))
                .ifPresent(transferBean -> transferBean.appendData(excelWriter, object));
    }

    /**
     * <h3 class="en-US">Checks if the given data record object instance matches the given database name</h3>
     * <h3 class="zh-CN">检查给定的数据记录对象实例是否匹配给定的数据库名称</h3>
     *
     * @param object     <span class="en-US">Entity object instance</span>
     *                   <span class="zh-CN">实体对象实例</span>
     * @param schemaName <span class="en-US">Database schema name</span>
     *                   <span class="zh-CN">数据库名称</span>
     * @return <span class="en-US">Match result</span>
     * <span class="zh-CN">匹配结果</span>
     */
    public static boolean matchSchema(final Object object, final String schemaName) {
        if (StringUtils.isEmpty(schemaName)) {
            return Boolean.FALSE;
        }
        return Optional.ofNullable(tableConfig(ClassUtils.originalClassName(object.getClass())))
                .map(tableConfig -> ObjectUtils.nullSafeEquals(tableConfig.getSchemaName(), schemaName))
                .orElse(Boolean.FALSE);
    }

    /**
     * <h3 class="en-US">Unregister all registered data table configuration information</h3>
     * <h3 class="zh-CN">注销所有已注册的数据表配置信息</h3>
     */
    public static void destroy() {
        REGISTERED_CONFIGS.clear();
        REGISTERED_TRANSFERS.clear();
    }

    /**
     * <h3 class="en-US">Checks whether the given entity class contains lazy loading annotation</h3>
     * <h3 class="zh-CN">检查给定的实体类是否包含懒加载注解</h3>
     *
     * @param entityClass <span class="en-US">Entity define class</span>
     *                    <span class="zh-CN">实体类定义</span>
     * @return <span class="en-US">Check result</span>
     * <span class="zh-CN">检查结果</span>
     */
    public static boolean containsLazyLoad(final Class<?> entityClass) {
        List<Member> annotationMembers = new ArrayList<>();
        annotationMembers.addAll(ReflectionUtils.getAllDeclaredFields(entityClass, Boolean.FALSE,
                parentClass -> parentClass.isAnnotationPresent(MappedSuperclass.class),
                DatabaseUtils::annotationMember));
        annotationMembers.addAll(ReflectionUtils.getAllDeclaredMethods(entityClass, Boolean.FALSE,
                parentClass -> parentClass.isAnnotationPresent(MappedSuperclass.class),
                DatabaseUtils::annotationMember));
        return !annotationMembers.isEmpty();
    }

    /**
     * <h3 class="en-US">Use Bytebuddy to modify entity classes to implement lazy loading function</h3>
     * <h3 class="zh-CN">使用Bytebuddy对实体类进行修改，以实现懒加载功能</h3>
     *
     * @param entityClass <span class="en-US">Entity define class</span>
     *                    <span class="zh-CN">实体类定义</span>
     */
    private static void redefineClass(final Class<?> entityClass) {
        if (REDEFINED_CLASSES.contains(entityClass)) {
            return;
        }
        Optional.ofNullable(entityClass.getSuperclass()).ifPresent(EntityManager::redefineClass);
        if (entityClass.isAnnotationPresent(MappedSuperclass.class) || entityClass.isAnnotationPresent(Table.class)) {
            DynamicType.Builder<?> byteBuddy = new ByteBuddy().redefine(entityClass)
                    .visit(Advice.to(DataModifyInterceptor.class)
                            .on(ElementMatchers.isSetter().and(ElementMatchers.not(ElementMatchers.isStatic()))));
            final DynamicType.Unloaded<?> unloaded;
            if (EntityManager.containsLazyLoad(entityClass)) {
                unloaded = byteBuddy.visit(Advice.to(LazyLoadInterceptor.class)
                                .on(ElementMatchers.isGetter().and(ElementMatchers.not(ElementMatchers.isStatic()))))
                        .make();
            } else {
                unloaded = byteBuddy.make();
            }
            try {
                unloaded.load(entityClass.getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
            } finally {
                IOUtils.closeStream(unloaded);
            }
        }
        REDEFINED_CLASSES.add(entityClass);
    }
}
