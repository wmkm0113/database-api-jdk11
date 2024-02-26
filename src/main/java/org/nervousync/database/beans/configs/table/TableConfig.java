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
package org.nervousync.database.beans.configs.table;

import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.nervousync.beans.core.BeanObject;
import org.nervousync.beans.transfer.basic.ClassAdapter;
import org.nervousync.commons.Globals;
import org.nervousync.database.annotations.table.Options;
import org.nervousync.database.beans.configs.column.ColumnConfig;
import org.nervousync.database.beans.configs.index.IndexInfo;
import org.nervousync.database.beans.configs.reference.ReferenceConfig;
import org.nervousync.database.commons.DatabaseCommons;
import org.nervousync.database.commons.DatabaseUtils;
import org.nervousync.database.enumerations.drop.DropOption;
import org.nervousync.database.enumerations.lock.LockOption;
import org.nervousync.utils.ClassUtils;
import org.nervousync.utils.ObjectUtils;
import org.nervousync.utils.ReflectionUtils;
import org.nervousync.utils.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.util.*;

/**
 * <h2 class="en-US">Table configure information</h2>
 * <h2 class="zh-CN">数据表配置信息</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Mar 23, 2010 10:13:02 $
 */
@XmlType(name = "table_config")
@XmlRootElement(name = "table_config")
@XmlAccessorType(XmlAccessType.NONE)
public final class TableConfig extends BeanObject {
    /**
     * <span class="en-US">Serial version UID</span>
     * <span class="zh-CN">序列化UID</span>
     */
    private static final long serialVersionUID = -6261205588355266688L;
    /**
     * <span class="en-US">Private constant value of reference annotation list</span>
     * <span class="zh-CN">外键注解列表的私有常量值</span>
     */
    private static final List<Class<? extends Annotation>> REFERENCE_ANNOTATIONS =
            Arrays.asList(ManyToMany.class, ManyToOne.class, OneToMany.class, OneToOne.class);
    /**
     * <span class="en-US">Private constant value of join column annotation list</span>
     * <span class="zh-CN">关联注解列表的私有常量值</span>
     */
    private static final List<Class<? extends Annotation>> JOIN_ANNOTATIONS =
            Arrays.asList(JoinColumn.class, JoinColumns.class);
    /**
     * <span class="en-US">Database schema name</span>
     * <span class="zh-CN">数据库名称</span>
     */
    @XmlElement(name = "schema_name")
    private String schemaName;
    /**
     * <span class="en-US">Table name</span>
     * <span class="zh-CN">数据表名称</span>
     */
    @XmlElement(name = "table_name")
    private String tableName;
    /**
     * <span class="en-US">Cacheable data record</span>
     * <span class="zh-CN">缓存数据表记录</span>
     */
    @XmlElement
    private boolean cacheable;
    /**
     * <span class="en-US">Composite ID</span>
     * <span class="zh-CN">联合主键</span>
     */
    @XmlElement(name = "composite_id")
    private boolean compositeId;
    /**
     * <span class="en-US">Entity class</span>
     * <span class="zh-CN">实体类</span>
     */
    @XmlElement(name = "define_class")
    @XmlJavaTypeAdapter(ClassAdapter.class)
    private Class<?> defineClass;
    /**
     * <span class="en-US">Record lock option</span>
     * <span class="zh-CN">数据记录锁定选项</span>
     */
    @XmlElement(name = "lock_option")
    private LockOption lockOption;
    /**
     * <span class="en-US">Record drop option</span>
     * <span class="zh-CN">数据记录删除选项</span>
     */
    @XmlElement(name = "drop_option")
    private DropOption dropOption;
    /**
     * <span class="en-US">Column configure information list</span>
     * <span class="zh-CN">数据列配置信息列表</span>
     */
    @XmlElement(name = "column_config")
    @XmlElementWrapper(name = "column_config_list")
    private List<ColumnConfig> columnConfigs;
    /**
     * <span class="en-US">Table index information list</span>
     * <span class="zh-CN">数据表索引信息列表</span>
     */
    @XmlElement(name = "index_info")
    @XmlElementWrapper(name = "index_info")
    private List<IndexInfo> indexInfos;
    /**
     * <span class="en-US">Reference configure information list</span>
     * <span class="zh-CN">外键配置信息列表</span>
     */
    @XmlElement(name = "reference_config")
    @XmlElementWrapper(name = "reference_config_list")
    private List<ReferenceConfig<?>> referenceConfigs;

    /**
     * <h3 class="en-US">Constructor method for table configure information</h3>
     * <h3 class="zh-CN">数据表配置信息的构造方法</h3>
     */
    public TableConfig() {
        this.columnConfigs = new ArrayList<>();
        this.indexInfos = new ArrayList<>();
        this.referenceConfigs = new ArrayList<>();
    }

    /**
     * <h3 class="en-US">Generate table configure information instance by given entity class</h3>
     * <h3 class="zh-CN">根据给定的实体类生成数据表配置信息实例对象</h3>
     *
     * @param clazz <span class="en-US">Entity class</span>
     *              <span class="zh-CN">实体类</span>
     * @return <span class="en-US">Generated table configure information instance</span>
     * <span class="zh-CN">生成的数据表配置信息实例对象</span>
     */
    public static TableConfig newInstance(@Nonnull final Class<?> clazz) {
        if (!clazz.isAnnotationPresent(Table.class)) {
            return null;
        }

        LockOption lockOption;
        DropOption dropOption;
        if (clazz.isAnnotationPresent(Options.class)) {
            Options options = clazz.getAnnotation(Options.class);
            lockOption = options.lockOption();
            dropOption = options.dropOption();
        } else {
            lockOption = LockOption.NONE;
            dropOption = DropOption.NONE;
        }

        List<ColumnConfig> columnConfigs = new ArrayList<>();
        List<ReferenceConfig<?>> referenceConfigs = new ArrayList<>();

        final Object object = ObjectUtils.newInstance(clazz);
        ReflectionUtils.getAllDeclaredFields(clazz, Boolean.TRUE,
                        parentClass -> parentClass.isAnnotationPresent(MappedSuperclass.class),
                        DatabaseUtils::annotationMember)
                .forEach(field -> {
                    if (field.isAnnotationPresent(Column.class)) {
                        ColumnConfig.newInstance(field, ReflectionUtils.getFieldValue(field, object), lockOption)
                                .ifPresent(columnConfigs::add);
                    } else if (REFERENCE_ANNOTATIONS.stream().anyMatch(field::isAnnotationPresent)
                            && JOIN_ANNOTATIONS.stream().anyMatch(field::isAnnotationPresent)) {
                        String fieldName = field.getName();
                        Class<?> referenceClass = null;
                        CascadeType[] cascadeType;
                        boolean lazyLoad = Boolean.FALSE;

                        if (field.isAnnotationPresent(OneToMany.class)) {
                            OneToMany oneToMany = field.getAnnotation(OneToMany.class);
                            referenceClass = oneToMany.targetEntity();
                            lazyLoad = FetchType.LAZY.equals(oneToMany.fetch());
                            cascadeType = oneToMany.cascade();
                        } else if (field.isAnnotationPresent(ManyToOne.class)) {
                            ManyToOne manyToOne = field.getAnnotation(ManyToOne.class);
                            referenceClass = manyToOne.targetEntity();
                            lazyLoad = FetchType.LAZY.equals(manyToOne.fetch());
                            cascadeType = manyToOne.cascade();
                        } else if (field.isAnnotationPresent(OneToOne.class)) {
                            OneToOne oneToOne = field.getAnnotation(OneToOne.class);
                            referenceClass = oneToOne.targetEntity();
                            lazyLoad = FetchType.LAZY.equals(oneToOne.fetch());
                            cascadeType = oneToOne.cascade();
                        } else {
                            cascadeType = new CascadeType[0];
                        }

                        boolean returnArray = (List.class.isAssignableFrom(field.getType()) || field.getType().isArray());

                        if (void.class.equals(referenceClass)) {
                            if (returnArray) {
                                referenceClass = ClassUtils.componentType(field.getType());
                            } else {
                                referenceClass = field.getType();
                            }
                        }

                        if (referenceClass != null) {
                            Optional.ofNullable(ReferenceConfig.newInstance(referenceClass, fieldName,
                                            lazyLoad, returnArray, cascadeType, joinColumns(field)))
                                    .ifPresent(referenceConfigs::add);
                        }
                    }
                });

        ReflectionUtils.getAllDeclaredMethods(clazz, DatabaseUtils::annotationMember)
                .forEach(method -> {
                    Class<?> referenceClass = null;
                    CascadeType[] cascadeType;
                    boolean lazyLoad = Boolean.FALSE;

                    if (method.isAnnotationPresent(OneToMany.class)) {
                        OneToMany oneToMany = method.getAnnotation(OneToMany.class);
                        referenceClass = oneToMany.targetEntity();
                        lazyLoad = FetchType.LAZY.equals(oneToMany.fetch());
                        cascadeType = oneToMany.cascade();
                    } else if (method.isAnnotationPresent(ManyToOne.class)) {
                        ManyToOne manyToOne = method.getAnnotation(ManyToOne.class);
                        referenceClass = manyToOne.targetEntity();
                        lazyLoad = FetchType.LAZY.equals(manyToOne.fetch());
                        cascadeType = manyToOne.cascade();
                    } else {
                        cascadeType = new CascadeType[0];
                    }

                    if (referenceClass != null) {
                        boolean returnArray = List.class.isAssignableFrom(method.getReturnType())
                                || method.getReturnType().isArray();

                        Optional.ofNullable(ReferenceConfig.newInstance(referenceClass,
                                        ReflectionUtils.fieldName(method.getName()),
                                        lazyLoad, returnArray, cascadeType, joinColumns(method)))
                                .ifPresent(referenceConfigs::add);
                    }
                });

        Table table = clazz.getAnnotation(Table.class);

        List<IndexInfo> indexInfos = new ArrayList<>();
        Arrays.asList(table.indexes())
                .forEach(index ->
                        Optional.ofNullable(IndexInfo.newInstance(index, columnConfigs)).ifPresent(indexInfos::add));

        TableConfig tableConfig = new TableConfig();

        tableConfig.setSchemaName(StringUtils.isEmpty(table.schema())
                ? DatabaseCommons.DEFAULT_DATABASE_ALIAS
                : table.schema());
        tableConfig.setCacheable(Optional.ofNullable(clazz.getAnnotation(Cacheable.class))
                .map(Cacheable::value)
                .orElse(Boolean.FALSE));
        tableConfig.setLockOption(lockOption);
        tableConfig.setDropOption(dropOption);
        tableConfig.setTableName(StringUtils.notBlank(table.name()) ? table.name() : clazz.getSimpleName());
        tableConfig.setDefineClass(clazz);
        tableConfig.setColumnConfigs(columnConfigs);
        tableConfig.setIndexInfos(indexInfos);
        tableConfig.setReferenceConfigs(referenceConfigs);
        tableConfig.setCompositeId(columnConfigs.stream().filter(ColumnConfig::isPrimaryKey).count() > 1);

        return tableConfig;
    }

    public String identifyKey() {
        return ClassUtils.originalClassName(this.defineClass);
    }

    public boolean matchClass(@Nonnull final Class<?> entityClass) {
        return ObjectUtils.nullSafeEquals(this.defineClass, entityClass);
    }

    public boolean containsReference(@Nonnull final Class<?> referenceClass) {
        return this.referenceConfigs.stream().anyMatch(referenceConfig -> referenceConfig.match(referenceClass));
    }

    /**
     * <h3 class="en-US">Getter method for database schema name</h3>
     * <h3 class="zh-CN">数据库名称的Getter方法</h3>
     *
     * @return <span class="en-US">Database schema name</span>
     * <span class="zh-CN">数据库名称</span>
     */
    public String getSchemaName() {
        return schemaName;
    }

    /**
     * <h3 class="en-US">Setter method for database schema name</h3>
     * <h3 class="zh-CN">数据库名称的Setter方法</h3>
     *
     * @param schemaName <span class="en-US">Database schema name</span>
     *                   <span class="zh-CN">数据库名称</span>
     */
    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    /**
     * <h3 class="en-US">Getter method for table name</h3>
     * <h3 class="zh-CN">数据表名称的Getter方法</h3>
     *
     * @return <span class="en-US">Table name</span>
     * <span class="zh-CN">数据表名称</span>
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * <h3 class="en-US">Setter method for table name</h3>
     * <h3 class="zh-CN">数据表名称的Setter方法</h3>
     *
     * @param tableName <span class="en-US">Table name</span>
     *                  <span class="zh-CN">数据表名称</span>
     */
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    /**
     * <h3 class="en-US">Getter method for cacheable data record</h3>
     * <h3 class="zh-CN">缓存数据表记录的Getter方法</h3>
     *
     * @return <span class="en-US">Cacheable data record</span>
     * <span class="zh-CN">缓存数据表记录</span>
     */
    public boolean isCacheable() {
        return cacheable;
    }

    /**
     * <h3 class="en-US">Setter method for cacheable data record</h3>
     * <h3 class="zh-CN">缓存数据表记录的Setter方法</h3>
     *
     * @param cacheable <span class="en-US">Cacheable data record</span>
     *                  <span class="zh-CN">缓存数据表记录</span>
     */
    public void setCacheable(boolean cacheable) {
        this.cacheable = cacheable;
    }

    /**
     * <h3 class="en-US">Getter method for composite ID</h3>
     * <h3 class="zh-CN">联合主键的Getter方法</h3>
     *
     * @return <span class="en-US">Composite ID</span>
     * <span class="zh-CN">联合主键</span>
     */
    public boolean isCompositeId() {
        return compositeId;
    }

    /**
     * <h3 class="en-US">Setter method for composite ID</h3>
     * <h3 class="zh-CN">联合主键的Setter方法</h3>
     *
     * @param compositeId <span class="en-US">Composite ID</span>
     *                    <span class="zh-CN">联合主键</span>
     */
    public void setCompositeId(boolean compositeId) {
        this.compositeId = compositeId;
    }

    /**
     * <h3 class="en-US">Getter method for entity class</h3>
     * <h3 class="zh-CN">实体类的Getter方法</h3>
     *
     * @return <span class="en-US">Entity class</span>
     * <span class="zh-CN">实体类</span>
     */
    public Class<?> getDefineClass() {
        return defineClass;
    }

    /**
     * <h3 class="en-US">Setter method for entity class</h3>
     * <h3 class="zh-CN">实体类的Setter方法</h3>
     *
     * @param defineClass <span class="en-US">Entity class</span>
     *                    <span class="zh-CN">实体类</span>
     */
    public void setDefineClass(Class<?> defineClass) {
        this.defineClass = defineClass;
    }

    /**
     * <h3 class="en-US">Getter method for record lock option</h3>
     * <h3 class="zh-CN">数据记录锁定选项的Getter方法</h3>
     *
     * @return <span class="en-US">Record lock option</span>
     * <span class="zh-CN">数据记录锁定选项</span>
     */
    public LockOption getLockOption() {
        return lockOption;
    }

    /**
     * <h3 class="en-US">Setter method for record lock option</h3>
     * <h3 class="zh-CN">数据记录锁定选项的Setter方法</h3>
     *
     * @param lockOption <span class="en-US">Record lock option</span>
     *                   <span class="zh-CN">数据记录锁定选项</span>
     */
    public void setLockOption(LockOption lockOption) {
        this.lockOption = lockOption;
    }

    /**
     * <h3 class="en-US">Getter method for record drop option</h3>
     * <h3 class="zh-CN">数据记录删除选项的Getter方法</h3>
     *
     * @return <span class="en-US">Record drop option</span>
     * <span class="zh-CN">数据记录删除选项</span>
     */
    public DropOption getDropOption() {
        return dropOption;
    }

    /**
     * <h3 class="en-US">Setter method for record drop option</h3>
     * <h3 class="zh-CN">数据记录删除选项的Setter方法</h3>
     *
     * @param dropOption <span class="en-US">Record drop option</span>
     *                   <span class="zh-CN">数据记录删除选项</span>
     */
    public void setDropOption(DropOption dropOption) {
        this.dropOption = dropOption;
    }

    /**
     * <h3 class="en-US">Getter method for column configure information list</h3>
     * <h3 class="zh-CN">数据列配置信息列表的Getter方法</h3>
     *
     * @return <span class="en-US">Column configure information list</span>
     * <span class="zh-CN">数据列配置信息列表</span>
     */
    public List<ColumnConfig> getColumnConfigs() {
        return columnConfigs;
    }

    /**
     * <h3 class="en-US">Setter method for column configure information list</h3>
     * <h3 class="zh-CN">数据列配置信息列表的Setter方法</h3>
     *
     * @param columnConfigs <span class="en-US">Column configure information list</span>
     *                      <span class="zh-CN">数据列配置信息列表</span>
     */
    public void setColumnConfigs(List<ColumnConfig> columnConfigs) {
        this.columnConfigs = columnConfigs;
    }

    /**
     * <h3 class="en-US">Getter method for table index information list</h3>
     * <h3 class="zh-CN">数据表索引信息列表的Getter方法</h3>
     *
     * @return <span class="en-US">Table index information list</span>
     * <span class="zh-CN">数据表索引信息列表</span>
     */
    public List<IndexInfo> getIndexInfos() {
        return indexInfos;
    }

    /**
     * <h3 class="en-US">Setter method for table index information list</h3>
     * <h3 class="zh-CN">数据表索引信息列表的Setter方法</h3>
     *
     * @param indexInfos <span class="en-US">Table index information list</span>
     *                   <span class="zh-CN">数据表索引信息列表</span>
     */
    public void setIndexInfos(List<IndexInfo> indexInfos) {
        this.indexInfos = indexInfos;
    }

    /**
     * <h3 class="en-US">Getter method for reference configure information list</h3>
     * <h3 class="zh-CN">外键配置信息列表的Getter方法</h3>
     *
     * @return <span class="en-US">Reference configure information list</span>
     * <span class="zh-CN">外键配置信息列表</span>
     */
    public List<ReferenceConfig<?>> getReferenceConfigs() {
        return referenceConfigs;
    }

    /**
     * <h3 class="en-US">Setter method for reference configure information list</h3>
     * <h3 class="zh-CN">外键配置信息列表的Setter方法</h3>
     *
     * @param referenceConfigs <span class="en-US">Reference configure information list</span>
     *                         <span class="zh-CN">外键配置信息列表</span>
     */
    public void setReferenceConfigs(List<ReferenceConfig<?>> referenceConfigs) {
        this.referenceConfigs = referenceConfigs;
    }

    /**
     * <h3 class="en-US">Retrieve column name by given identify key</h3>
     * <h3 class="zh-CN">根据给定的识别代码查询列名称</h3>
     *
     * @param identifyKey <span class="en-US">Identify key</span>
     *                    <span class="zh-CN">识别代码</span>
     * @return <span class="en-US">Retrieved column name or empty string if not found</span>
     * <span class="zh-CN">查询到的列名称，如果未找到返回空字符串</span>
     */
    public String columnName(final String identifyKey) {
        return Optional.ofNullable(this.columnConfig(identifyKey))
                .map(ColumnConfig::columnName)
                .orElse(Globals.DEFAULT_VALUE_STRING);
    }

    /**
     * <h3 class="en-US">Check the given identify key is column identify key</h3>
     * <h3 class="zh-CN">检查给定的识别代码是列识别代码</h3>
     *
     * @param identifyKey <span class="en-US">Identify key</span>
     *                    <span class="zh-CN">识别代码</span>
     * @return <span class="en-US">Check result</span>
     * <span class="zh-CN">检查结果</span>
     */
    public boolean isColumn(final String identifyKey) {
        return this.columnConfigs.stream().anyMatch(columnConfig -> columnConfig.matchKey(identifyKey));
    }

    /**
     * <h3 class="en-US">Checks that the column identified by the given identification code is sensitive data</h3>
     * <h3 class="zh-CN">检查给定的识别代码所标识的列是敏感数据</h3>
     *
     * @param identifyKey <span class="en-US">Identify key</span>
     *                    <span class="zh-CN">识别代码</span>
     * @return <span class="en-US">Check result</span>
     * <span class="zh-CN">检查结果</span>
     */
    public boolean isSensitive(final String identifyKey) {
        return this.columnConfigs.stream().anyMatch(columnConfig ->
                columnConfig.matchKey(identifyKey) && columnConfig.isSensitiveData());
    }

    /**
     * <h3 class="en-US">Check the given identify key is lazy load column or reference</h3>
     * <h3 class="zh-CN">检查给定的识别代码是懒加载列或外键</h3>
     *
     * @param identifyKey <span class="en-US">Identify key</span>
     *                    <span class="zh-CN">识别代码</span>
     * @return <span class="en-US">Check result</span>
     * <span class="zh-CN">检查结果</span>
     */
    public boolean lazyLoad(final String identifyKey) {
        if (StringUtils.isEmpty(identifyKey)) {
            return Boolean.FALSE;
        }
        return this.columnConfigs.stream().filter(columnConfig -> columnConfig.matchKey(identifyKey))
                .findFirst()
                .map(ColumnConfig::isLazyLoad)
                .orElseGet(() ->
                        this.referenceConfigs
                                .stream()
                                .filter(referenceConfig -> match(referenceConfig, identifyKey))
                                .findFirst()
                                .map(ReferenceConfig::isLazyLoad)
                                .orElse(Boolean.FALSE));
    }

    /**
     * <h3 class="en-US">Generate iterator of current reference configure list</h3>
     * <h3 class="zh-CN">生成当前外键配置列表的遍历器</h3>
     *
     * @return <span class="en-US">Generated iterator instance</span>
     * <span class="zh-CN">生成的遍历器实例对象</span>
     */
    public Iterator<ReferenceConfig<?>> referenceIterator() {
        return this.referenceConfigs.iterator();
    }

    /**
     * <h3 class="en-US">Retrieve column configure instance by given identify key</h3>
     * <h3 class="zh-CN">根据给定的识别代码查询列配置信息实例</h3>
     *
     * @param identifyKey <span class="en-US">Identify key</span>
     *                    <span class="zh-CN">识别代码</span>
     * @return <span class="en-US">Retrieved column configure instance or <code>null</code> if not found</span>
     * <span class="zh-CN">查询到的列配置信息实例，如果未找到返回 <code>null</code></span>
     */
    public ColumnConfig columnConfig(final String identifyKey) {
        if (StringUtils.isEmpty(identifyKey)) {
            return null;
        }
        return this.columnConfigs.stream()
                .filter(columnConfig -> columnConfig.matchKey(identifyKey))
                .findFirst()
                .orElse(null);
    }

    /**
     * <h3 class="en-US">Retrieve identify version column configure instance</h3>
     * <h3 class="zh-CN">查询版本识别列配置信息实例</h3>
     *
     * @return <span class="en-US">Retrieved column configure instance or <code>null</code> if not found</span>
     * <span class="zh-CN">查询到的列配置信息实例，如果未找到返回 <code>null</code></span>
     */
    public Optional<ColumnConfig> versionColumn() {
        ColumnConfig columnConfig = null;
        for (ColumnConfig currentColumn : this.columnConfigs) {
            if (currentColumn.isIdentifyVersion()) {
                columnConfig = currentColumn;
            }
        }
        return Optional.ofNullable(columnConfig);
    }

    /**
     * <h3 class="en-US">Retrieve reference configure instance by given identify key</h3>
     * <h3 class="zh-CN">根据给定的识别代码查询外键配置信息实例</h3>
     *
     * @param referenceClass <span class="en-US">Foreign key target entity class</span>
     *                       <span class="zh-CN">外键目标实体类</span>
     * @return <span class="en-US">Retrieved reference configure instance or <code>null</code> if not found</span>
     * <span class="zh-CN">查询到的外键配置信息实例，如果未找到返回 <code>null</code></span>
     */
    public ReferenceConfig<?> referenceConfig(final Class<?> referenceClass) {
        return this.referenceConfigs.stream()
                .filter(referenceConfig -> referenceConfig.match(referenceClass))
                .findFirst()
                .orElse(null);
    }

    /**
     * <h3 class="en-US">Retrieve reference configure instance by given identify key</h3>
     * <h3 class="zh-CN">根据给定的识别代码查询外键配置信息实例</h3>
     *
     * @param identifyKey <span class="en-US">Identify key</span>
     *                    <span class="zh-CN">识别代码</span>
     * @return <span class="en-US">Retrieved reference configure instance or <code>null</code> if not found</span>
     * <span class="zh-CN">查询到的外键配置信息实例，如果未找到返回 <code>null</code></span>
     */
    public ReferenceConfig<?> referenceConfig(final String identifyKey) {
        return this.referenceConfigs.stream()
                .filter(referenceConfig -> match(referenceConfig, identifyKey))
                .findFirst()
                .orElse(null);
    }

    /**
     * <h3 class="en-US">Check the given identify key is matched with given reference configure information instance</h3>
     * <h3 class="zh-CN">检查给定的识别代码是否匹配给定的外键配置信息实例对象</h3>
     *
     * @param referenceConfig <span class="en-US">Reference configure information</span>
     *                        <span class="zh-CN">外键配置信息</span>
     * @param identifyKey     <span class="en-US">Identify key</span>
     *                        <span class="zh-CN">识别代码</span>
     * @return <span class="en-US">Check result</span>
     * <span class="zh-CN">检查结果</span>
     */
    private static boolean match(final ReferenceConfig<?> referenceConfig, final String identifyKey) {
        if (referenceConfig == null || StringUtils.isEmpty(identifyKey)) {
            return Boolean.FALSE;
        }
        return referenceConfig.getFieldName().equalsIgnoreCase(identifyKey)
                || referenceConfig.getReferenceClass().getName().equalsIgnoreCase(identifyKey);
    }

    /**
     * <h3 class="en-US">Retrieve the given accessible object contains join column annotation</h3>
     * <h3 class="zh-CN">检查给定的访问对象实例包含标注信息</h3>
     *
     * @param obj <span class="en-US">AccessibleObject instance</span>
     *            <span class="zh-CN">注解对象实例</span>
     * @return <span class="en-US">JoinColumn instance array</span>
     * <span class="zh-CN">注解 JoinColumn 实例对象数组</span>
     */
    private static JoinColumn[] joinColumns(final AccessibleObject obj) {
        if (obj == null) {
            return new JoinColumn[0];
        }
        if (obj.isAnnotationPresent(JoinColumns.class)) {
            JoinColumns annotationColumns = obj.getAnnotation(JoinColumns.class);
            return annotationColumns.value();
        } else if (obj.isAnnotationPresent(JoinColumn.class)) {
            return new JoinColumn[]{obj.getAnnotation(JoinColumn.class)};
        }
        return new JoinColumn[0];
    }
}
