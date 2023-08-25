/*
 * Licensed to the Nervousync Studio (NSYC) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
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
import org.nervousync.database.annotations.result.ItemKey;
import org.nervousync.database.annotations.result.QueryResult;
import org.nervousync.database.beans.configs.table.TableConfig;
import org.nervousync.utils.ClassUtils;
import org.nervousync.utils.ObjectUtils;
import org.nervousync.utils.ReflectionUtils;
import org.nervousync.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static final Logger LOGGER = LoggerFactory.getLogger(EntityManager.class);
    /**
     * <span class="en-US">Registered table configure mapping</span>
     * <span class="zh-CN">已注册的数据表配置信息映射</span>
     */
    private static final Hashtable<String, TableConfig> REGISTERED_CONFIGS = new Hashtable<>();
    private static final Hashtable<String, Map<String, String>> REGISTERED_QUERY_RESULTS = new Hashtable<>();

    /**
     * <h3 class="en-US">Private constructor for EntityManager</h3>
     * <h3 class="zh-CN">实体类管理器的私有构造方法</h3>
     */
    private EntityManager() {
    }

    /**
     * <h3 class="en-US">According to the given query result class, retrieve the mapping table between the result set and the field name</h3>
     * <h3 class="zh-CN">根据给定的查询结果类，检索结果集与属性名的映射表</h3>
     *
     * @param targetClass <span class="en-US">Query result class</span>
     *                    <span class="zh-CN">查询结果类</span>
     * @return <span class="en-US">The retrieved mapping table</span>
     * <span class="zh-CN">检索到的映射表</span>
     */
    public static Map<String, String> queryResultMap(@Nonnull final Class<?> targetClass) {
        String className = ClassUtils.originalClassName(targetClass);
        Map<String, String> queryResultMap = REGISTERED_QUERY_RESULTS.get(className);
        if (queryResultMap == null) {
            Map<String, String> itemKeyMap = new HashMap<>();
            if (targetClass.isAnnotationPresent(QueryResult.class)) {
                ReflectionUtils.getAllDeclaredFields(targetClass, Boolean.TRUE, MappedSuperclass.class)
                        .stream()
                        .filter(field -> field.isAnnotationPresent(ItemKey.class))
                        .forEach(field -> {
                            String mapKey = field.getAnnotation(ItemKey.class).value();
                            if (StringUtils.isEmpty(mapKey)) {
                                mapKey = field.getName();
                            }
                            itemKeyMap.put(mapKey, field.getName());
                        });
            }
            queryResultMap = new HashMap<>(itemKeyMap);
        }
        return queryResultMap;
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
                        .ifPresent(tableConfig -> {
                            if (tableExists(entityClass)) {
                                LOGGER.warn("Override table config, entity class: {} table name: {}",
                                        entityClass.getName(), tableConfig.getTableName());
                            }
                            String className = ClassUtils.originalClassName(entityClass);
                            REGISTERED_CONFIGS.put(tableConfig.getTableName(), tableConfig);
                            REGISTERED_CONFIGS.put(className, tableConfig);
                            registeredTables.add(tableConfig);
                            if (LOGGER.isDebugEnabled()) {
                                LOGGER.debug("Register class {} mapping to table {}",
                                        className, tableConfig.getTableName());
                            }
                            Map<String, String> queryResultMap = new HashMap<>();
                            tableConfig.getColumnConfigs()
                                    .stream()
                                    .filter(columnConfig -> !columnConfig.isLazyLoad())
                                    .forEach(columnConfig ->
                                            queryResultMap.put(columnConfig.columnName().toUpperCase(),
                                                    columnConfig.getFieldName()));
                            REGISTERED_QUERY_RESULTS.put(className, queryResultMap);
                        }));
        return registeredTables;
    }

    /**
     * <h3 class="en-US">Remove the corresponding data table configuration information according to the given entity class array</h3>
     * <h3 class="zh-CN">根据给定的实体类数组移除对应的数据表配置信息</h3>
     *
     * @param entityClasses <span class="en-US">Entity classes array</span>
     *                      <span class="zh-CN">实体类数组</span>
     */
    public static void removeTable(final Class<?>... entityClasses) {
        Arrays.stream(entityClasses)
                .filter(entityClass -> REGISTERED_CONFIGS.containsKey(ClassUtils.originalClassName(entityClass)))
                .forEach(entityClass -> {
                    String className = ClassUtils.originalClassName(entityClass);
                    TableConfig tableConfig = REGISTERED_CONFIGS.get(className);
                    REGISTERED_CONFIGS.remove(tableConfig.getTableName());
                    REGISTERED_CONFIGS.remove(className);
                });
    }

    /**
     * <h3 class="en-US">Check for registration against a given entity class</h3>
     * <h3 class="zh-CN">根据给定的实体类检查是否注册</h3>
     *
     * @param defineClass <span class="en-US">Entity class</span>
     *                    <span class="zh-CN">实体类</span>
     * @return <span class="en-US">Registered returns <code>true</code>, unregistered returns <code>false</code></span>
     * <span class="zh-CN">已注册返回 <code>true</code>，未注册返回 <code>false</code></span>
     */
    public static boolean tableExists(final Class<?> defineClass) {
        return tableExists(ClassUtils.originalClassName(defineClass));
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
     * <h3 class="en-US">Retrieve the data table configuration information instance object according to the given entity class</h3>
     * <h3 class="zh-CN">根据给定的实体类检索数据表配置信息实例对象</h3>
     *
     * @param defineClass <span class="en-US">Entity class</span>
     *                    <span class="zh-CN">实体类</span>
     * @return <span class="en-US">Retrieved TableConfig instance</span>
     * <span class="zh-CN">检索到的数据表配置信息实例对象</span>
     */
    public static Optional<TableConfig> tableConfig(final Class<?> defineClass) {
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
    public static Optional<TableConfig> tableConfig(final String identifyKey) {
        if (StringUtils.isEmpty(identifyKey)) {
            return Optional.empty();
        }
        return Optional.ofNullable(REGISTERED_CONFIGS.get(identifyKey));
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
        return tableConfig(ClassUtils.originalClassName(object.getClass()))
                .map(tableConfig -> ObjectUtils.nullSafeEquals(tableConfig.getSchemaName(), schemaName))
                .orElse(Boolean.FALSE);
    }

    /**
     * <h3 class="en-US">Unregister all registered data table configuration information</h3>
     * <h3 class="zh-CN">注销所有已注册的数据表配置信息</h3>
     */
    public static void destroy() {
        REGISTERED_CONFIGS.clear();
    }
}
