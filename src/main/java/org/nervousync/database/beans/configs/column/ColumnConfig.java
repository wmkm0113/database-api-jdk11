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
package org.nervousync.database.beans.configs.column;

import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.nervousync.beans.converter.Adapter;
import org.nervousync.beans.converter.impl.basic.ClassStringAdapter;
import org.nervousync.beans.core.BeanObject;
import org.nervousync.commons.Globals;
import org.nervousync.database.annotations.sequence.SequenceGenerator;
import org.nervousync.database.annotations.table.Transfer;
import org.nervousync.database.beans.configs.generator.GeneratorConfig;
import org.nervousync.database.enumerations.lock.LockOption;
import org.nervousync.utils.ConvertUtils;
import org.nervousync.utils.SecurityUtils;
import org.nervousync.utils.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * <h2 class="en-US">Column configure information</h2>
 * <h2 class="zh-CN">列配置信息</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Jun 27, 2018 22:18:46 $
 */
@XmlType(name = "column_config")
@XmlRootElement(name = "column_config")
@XmlAccessorType(XmlAccessType.NONE)
public final class ColumnConfig extends BeanObject {
    /**
     * <span class="en-US">Serial version UID</span>
     * <span class="zh-CN">序列化UID</span>
     */
    private static final long serialVersionUID = -6062009105869563215L;
    /**
     * <span class="en-US">Column information</span>
     * <span class="zh-CN">列基本信息</span>
     */
    @XmlElement(name = "column_info")
    private ColumnInfo columnInfo;
    /**
     * <span class="en-US">Column mapping field name</span>
     * <span class="zh-CN">列映射的属性名</span>
     */
    @XmlElement(name = "field_name")
    private String fieldName;
    /**
     * <span class="en-US">Column mapping field type class</span>
     * <span class="zh-CN">列映射的属性类型</span>
     */
    @XmlElement(name = "field_type")
    @XmlJavaTypeAdapter(ClassStringAdapter.class)
    private Class<?> fieldType;
    /**
     * <span class="en-US">Column is primary key</span>
     * <span class="zh-CN">列是主键</span>
     */
    @XmlElement(name = "primary_key")
    private boolean primaryKey;
    /**
     * <span class="en-US">Column value can updatable</span>
     * <span class="zh-CN">列值允许更新</span>
     */
    @XmlElement
    private boolean updatable;
    /**
     * <span class="en-US">Column value is unique</span>
     * <span class="zh-CN">列值是唯一的</span>
     */
    @XmlElement
    private boolean unique;
    /**
     * <span class="en-US">Column value is lazy load</span>
     * <span class="zh-CN">列值懒加载</span>
     */
    @XmlElement
    private boolean lazyLoad;
    /**
     * <span class="en-US">Column is version identify</span>
     * <span class="zh-CN">列值是版本识别</span>
     */
    @XmlElement(name = "identify_version")
    private boolean identifyVersion;
    /**
     * <span class="en-US">Column order number in Excel</span>
     * <span class="zh-CN">Excel表中的列排列序号</span>
     */
    @XmlElement(name = "sort_order")
    private int sortOrder;
    /**
     * <span class="en-US">Data converter class</span>
     * <span class="zh-CN">数据转换类</span>
     */
    @XmlElement(name = "converter_class")
    @XmlJavaTypeAdapter(ClassStringAdapter.class)
    private Class<?> converterClass;
    /**
     * <span class="en-US">Column value generator configure</span>
     * <span class="zh-CN">数据生成器配置</span>
     */
    @XmlElement(name = "generator_config")
    private GeneratorConfig generatorConfig;
    /**
     * <span class="en-US">Column identify key list</span>
     * <span class="zh-CN">列识别值列表</span>
     */
    private final List<String> identifyKeys;

    /**
     * <h3 class="en-US">Constructor method for column configure information</h3>
     * <h3 class="zh-CN">列配置信息的构造方法</h3>
     */
    public ColumnConfig() {
        this.identifyKeys = new ArrayList<>();
    }

    /**
     * <h3 class="en-US">Generate column configure information instance by given field instance and default value</h3>
     * <h3 class="zh-CN">根据给定的反射获得的属性实例对象和默认值生成列配置信息实例对象</h3>
     *
     * @param field        <span class="en-US">Field instance</span>
     *                     <span class="zh-CN">反射获得的属性实例对象</span>
     * @param defaultValue <span class="en-US">Default value of current field</span>
     *                     <span class="zh-CN">当前属性的默认值</span>
     * @param lockOption   <span class="en-US">Record lock option</span>
     *                     <span class="zh-CN">数据记录锁定选项</span>
     * @return <span class="en-US">Generated column configure information instance</span>
     * <span class="zh-CN">生成的列配置信息实例对象</span>
     */
    public static Optional<ColumnConfig> newInstance(@Nonnull final Field field, final Object defaultValue,
                                                     final LockOption lockOption) {
        ColumnConfig columnConfig = null;
        if (field.isAnnotationPresent(Column.class)) {
            columnConfig = Optional.ofNullable(ColumnInfo.newInstance(field, defaultValue))
                    .map(columnInfo -> {
                        ColumnConfig config = new ColumnConfig();
                        boolean primaryKey = field.isAnnotationPresent(Id.class);
                        Column column = field.getAnnotation(Column.class);
                        config.setColumnInfo(columnInfo);
                        config.setFieldName(field.getName());
                        config.setFieldType(field.getType());
                        config.setPrimaryKey(primaryKey);
                        config.setUnique(column.unique());
                        if (primaryKey) {
                            config.setUpdatable(Boolean.FALSE);
                            config.setIdentifyVersion(Boolean.FALSE);
                        } else {
                            config.setUpdatable(column.updatable());
                            if (LockOption.OPTIMISTIC_UPGRADE.equals(lockOption)) {
                                config.setIdentifyVersion(field.isAnnotationPresent(Version.class));
                            } else {
                                config.setIdentifyVersion(Boolean.FALSE);
                            }
                        }
                        if (field.isAnnotationPresent(Transfer.class)) {
                            Transfer transfer = field.getAnnotation(Transfer.class);
                            config.setSortOrder(transfer.order());
                            config.setConverterClass(transfer.converter());
                        } else {
                            config.setSortOrder(Globals.DEFAULT_VALUE_INT);
                            config.setConverterClass(Adapter.class);
                        }
                        if (field.isAnnotationPresent(Basic.class)) {
                            config.setLazyLoad(FetchType.LAZY.equals(field.getAnnotation(Basic.class).fetch()));
                        } else {
                            config.setLazyLoad(Boolean.FALSE);
                        }
                        config.setGeneratorConfig(GeneratorConfig.newInstance(field.getAnnotation(GeneratedValue.class),
                                field.getAnnotation(SequenceGenerator.class)));
                        return config;
                    })
                    .orElse(null);
        }
        return Optional.ofNullable(columnConfig);
    }

    /**
     * <h3 class="en-US">Match given identify key contains in identify key list</h3>
     * <h3 class="zh-CN">检查给定的识别码包含在列识别值列表中</h3>
     *
     * @param identifyKey <span class="en-US">Identify key</span>
     *                    <span class="zh-CN">识别码</span>
     * @return <span class="en-US">Match result</span>
     * <span class="zh-CN">匹配结果</span>
     */
    public boolean matchKey(final String identifyKey) {
        if (StringUtils.isEmpty(identifyKey)) {
            return Boolean.FALSE;
        }
        return this.identifyKeys.stream().anyMatch(identifyKey::equalsIgnoreCase);
    }

    /**
     * <h3 class="en-US">Getter method for column information</h3>
     * <h3 class="zh-CN">列基本信息的Getter方法</h3>
     *
     * @return <span class="en-US">Column information</span>
     * <span class="zh-CN">列基本信息</span>
     */
    public ColumnInfo getColumnInfo() {
        return columnInfo;
    }

    /**
     * <h3 class="en-US">Setter method for column information</h3>
     * <h3 class="zh-CN">列基本信息的Setter方法</h3>
     *
     * @param columnInfo <span class="en-US">Column information</span>
     *                   <span class="zh-CN">列基本信息</span>
     */
    public void setColumnInfo(ColumnInfo columnInfo) {
        if (this.columnInfo != null) {
            this.identifyKeys.remove(this.columnInfo.getColumnName());
            this.identifyKeys.remove(ConvertUtils.toHex(SecurityUtils.SHA256(this.columnInfo.getColumnName())));
        }
        this.columnInfo = columnInfo;
        this.identifyKeys.add(this.columnInfo.getColumnName());
        this.identifyKeys.add(ConvertUtils.toHex(SecurityUtils.SHA256(this.columnInfo.getColumnName())));
    }

    /**
     * <h3 class="en-US">Getter method for column mapping field name</h3>
     * <h3 class="zh-CN">列映射的属性名的Getter方法</h3>
     *
     * @return <span class="en-US">Column mapping field name</span>
     * <span class="zh-CN">列映射的属性名</span>
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * <h3 class="en-US">Setter method for column mapping field name</h3>
     * <h3 class="zh-CN">列映射的属性名的Setter方法</h3>
     *
     * @param fieldName <span class="en-US">Column mapping field name</span>
     *                  <span class="zh-CN">列映射的属性名</span>
     */
    public void setFieldName(String fieldName) {
        if (StringUtils.notBlank(this.fieldName)) {
            this.identifyKeys.remove(this.fieldName);
            this.identifyKeys.remove(ConvertUtils.toHex(SecurityUtils.SHA256(this.fieldName)));
        }
        this.fieldName = fieldName;
        this.identifyKeys.add(this.fieldName);
        this.identifyKeys.add(ConvertUtils.toHex(SecurityUtils.SHA256(this.fieldName)));
    }

    /**
     * <h3 class="en-US">Getter method for column mapping field type class</h3>
     * <h3 class="zh-CN">列映射的属性类型的Getter方法</h3>
     *
     * @return <span class="en-US">Column mapping field type class</span>
     * <span class="zh-CN">列映射的属性类型</span>
     */
    public Class<?> getFieldType() {
        return fieldType;
    }

    /**
     * <h3 class="en-US">Setter method for column mapping field type class</h3>
     * <h3 class="zh-CN">列映射的属性类型的Setter方法</h3>
     *
     * @param fieldType <span class="en-US">Column mapping field type class</span>
     *                  <span class="zh-CN">列映射的属性类型</span>
     */
    public void setFieldType(Class<?> fieldType) {
        this.fieldType = fieldType;
    }

    /**
     * <h3 class="en-US">Getter method for column is primary key</h3>
     * <h3 class="zh-CN">列是主键的Getter方法</h3>
     *
     * @return <span class="en-US">Column is primary key</span>
     * <span class="zh-CN">列是主键</span>
     */
    public boolean isPrimaryKey() {
        return primaryKey;
    }

    /**
     * <h3 class="en-US">Setter method for column is primary key</h3>
     * <h3 class="zh-CN">列是主键的Setter方法</h3>
     *
     * @param primaryKey <span class="en-US">Column is primary key</span>
     *                   <span class="zh-CN">列是主键</span>
     */
    public void setPrimaryKey(boolean primaryKey) {
        this.primaryKey = primaryKey;
    }

    /**
     * <h3 class="en-US">Getter method for column value can updatable</h3>
     * <h3 class="zh-CN">列值允许更新的Getter方法</h3>
     *
     * @return <span class="en-US">Column value can updatable</span>
     * <span class="zh-CN">列值允许更新</span>
     */
    public boolean isUpdatable() {
        return updatable;
    }

    /**
     * <h3 class="en-US">Setter method for column value can updatable</h3>
     * <h3 class="zh-CN">列值允许更新的Setter方法</h3>
     *
     * @param updatable <span class="en-US">Column value can updatable</span>
     *                  <span class="zh-CN">列值允许更新</span>
     */
    public void setUpdatable(boolean updatable) {
        this.updatable = updatable;
    }

    /**
     * <h3 class="en-US">Getter method for column value is unique</h3>
     * <h3 class="zh-CN">列值是唯一的Getter方法</h3>
     *
     * @return <span class="en-US">Column value is unique</span>
     * <span class="zh-CN">列值是唯一的</span>
     */
    public boolean isUnique() {
        return unique;
    }

    /**
     * <h3 class="en-US">Setter method for column value is unique</h3>
     * <h3 class="zh-CN">列值是唯一的Setter方法</h3>
     *
     * @param unique <span class="en-US">Column value is unique</span>
     *               <span class="zh-CN">列值是唯一的</span>
     */
    public void setUnique(boolean unique) {
        this.unique = unique;
    }

    /**
     * <h3 class="en-US">Getter method for column value is lazy load</h3>
     * <h3 class="zh-CN">列值懒加载的Getter方法</h3>
     *
     * @return <span class="en-US">Column value is lazy load</span>
     * <span class="zh-CN">列值懒加载</span>
     */
    public boolean isLazyLoad() {
        return lazyLoad;
    }

    /**
     * <h3 class="en-US">Setter method for column value is lazy load</h3>
     * <h3 class="zh-CN">列值懒加载的Setter方法</h3>
     *
     * @param lazyLoad <span class="en-US">Column value is lazy load</span>
     *                 <span class="zh-CN">列值懒加载</span>
     */
    public void setLazyLoad(boolean lazyLoad) {
        this.lazyLoad = lazyLoad;
    }

    /**
     * <h3 class="en-US">Getter method for column is version identify</h3>
     * <h3 class="zh-CN">列值是版本识别的Getter方法</h3>
     *
     * @return <span class="en-US">Column is version identify</span>
     * <span class="zh-CN">列值是版本识别</span>
     */
    public boolean isIdentifyVersion() {
        return identifyVersion;
    }

    /**
     * <h3 class="en-US">Setter method for column is version identify</h3>
     * <h3 class="zh-CN">列值是版本识别的Setter方法</h3>
     *
     * @param identifyVersion <span class="en-US">Column is version identify</span>
     *                        <span class="zh-CN">列值是版本识别</span>
     */
    public void setIdentifyVersion(boolean identifyVersion) {
        this.identifyVersion = identifyVersion;
    }

    /**
     * <h3 class="en-US">Getter method for column order number in Excel</h3>
     * <h3 class="zh-CN">Excel表中的列排列序号的Getter方法</h3>
     *
     * @return <span class="en-US">Column order number in Excel</span>
     * <span class="zh-CN">Excel表中的列排列序号</span>
     */
    public int getSortOrder() {
        return sortOrder;
    }

    /**
     * <h3 class="en-US">Setter method for column order number in Excel</h3>
     * <h3 class="zh-CN">Excel表中的列排列序号的Setter方法</h3>
     *
     * @param sortOrder <span class="en-US">Column order number in Excel</span>
     *                  <span class="zh-CN">Excel表中的列排列序号</span>
     */
    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    /**
     * <h3 class="en-US">Getter method for data converter class</h3>
     * <h3 class="zh-CN">数据转换类的Getter方法</h3>
     *
     * @return <span class="en-US">Data converter class</span>
     * <span class="zh-CN">数据转换类</span>
     */
    public Class<?> getConverterClass() {
        return converterClass;
    }

    /**
     * <h3 class="en-US">Setter method for data converter class</h3>
     * <h3 class="zh-CN">数据转换类的Setter方法</h3>
     *
     * @param converterClass <span class="en-US">Data converter class</span>
     *                       <span class="zh-CN">数据转换类</span>
     */
    public void setConverterClass(Class<?> converterClass) {
        this.converterClass = converterClass;
    }

    /**
     * <h3 class="en-US">Getter method for column value generator configure</h3>
     * <h3 class="zh-CN">数据生成器配置的Getter方法</h3>
     *
     * @return <span class="en-US">Column value generator configure</span>
     * <span class="zh-CN">数据生成器配置</span>
     */
    public GeneratorConfig getGeneratorConfig() {
        return generatorConfig;
    }

    /**
     * <h3 class="en-US">Setter method for column value generator configure</h3>
     * <h3 class="zh-CN">数据生成器配置的Setter方法</h3>
     *
     * @param generatorConfig <span class="en-US">Column value generator configure</span>
     *                        <span class="zh-CN">数据生成器配置</span>
     */
    public void setGeneratorConfig(GeneratorConfig generatorConfig) {
        this.generatorConfig = generatorConfig;
    }

    /**
     * <span class="en-US">Current column name</span>
     * <span class="zh-CN">当前列名</span>
     *
     * @return <span class="en-US">The column name</span>
     * <span class="zh-CN">列名</span>
     */
    public String columnName() {
        return this.columnInfo.getColumnName();
    }
}
