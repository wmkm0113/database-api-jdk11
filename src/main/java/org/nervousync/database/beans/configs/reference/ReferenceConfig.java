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
package org.nervousync.database.beans.configs.reference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.JoinColumn;
import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.nervousync.beans.converter.impl.basic.ClassStringAdapter;
import org.nervousync.beans.core.BeanObject;
import org.nervousync.utils.ObjectUtils;
import org.nervousync.utils.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <h2 class="en-US">Reference configure information</h2>
 * <h2 class="zh-CN">外键引用配置信息</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Mar 30, 2016 17:48:56 $
 */
@XmlType(name = "reference_config")
@XmlRootElement(name = "reference_config")
@XmlAccessorType(XmlAccessType.NONE)
public final class ReferenceConfig extends BeanObject {
    /**
     * <span class="en-US">Serial version UID</span>
     * <span class="zh-CN">序列化UID</span>
     */
    private static final long serialVersionUID = 352330256621053556L;
    /**
     * <span class="en-US">Reference is lazy load</span>
     * <span class="zh-CN">外键懒加载</span>
     */
    @XmlElement(name = "lazy_load")
    private boolean lazyLoad;
    /**
     * <span class="en-US">Return value is array</span>
     * <span class="zh-CN">返回值是数组或列表</span>
     */
    @XmlElement(name = "return_array")
    private boolean returnArray;
    /**
     * <span class="en-US">Target reference entity class</span>
     * <span class="zh-CN">目标外键实体类</span>
     */
    @XmlElement(name = "reference_class")
    @XmlJavaTypeAdapter(ClassStringAdapter.class)
    private Class<?> referenceClass;
    /**
     * <span class="en-US">Column mapping field name</span>
     * <span class="zh-CN">列映射的属性名</span>
     */
    @XmlElement(name = "field_name")
    private String fieldName;
    /**
     * <span class="en-US">Reference cascade type array</span>
     * <span class="zh-CN">外键级联状态数组</span>
     */
    @XmlElement(name = "cascade_type")
    @XmlElementWrapper(name = "cascade_types")
    private CascadeType[] cascadeTypes;
    /**
     * <span class="en-US">Reference join column configure list</span>
     * <span class="zh-CN">外键关联列配置信息列表</span>
     */
    @XmlElement(name = "join_column")
    @XmlElementWrapper(name = "join_column_list")
    private List<JoinConfig> joinColumnList;

    /**
     * <h3 class="en-US">Constructor method for reference configure information</h3>
     * <h3 class="zh-CN">外键引用配置信息的构造方法</h3>
     */
    public ReferenceConfig() {
    }

    /**
     * <h3 class="en-US">Generate reference configure information instance by given arguments</h3>
     * <h3 class="zh-CN">根据给定的参数信息生成外键引用配置信息实例对象</h3>
     *
     * @param referenceClass <span class="en-US">Target reference entity class</span>
     *                       <span class="zh-CN">目标外键实体类</span>
     * @param fieldName      <span class="en-US">Column mapping field name</span>
     *                       <span class="zh-CN">列映射的属性名</span>
     * @param lazyLoad       <span class="en-US">Reference is lazy load</span>
     *                       <span class="zh-CN">外键懒加载</span>
     * @param returnArray    <span class="en-US">Return value is array</span>
     *                       <span class="zh-CN">返回值是数组或列表</span>
     * @param cascadeTypes   <span class="en-US">Reference cascade type array</span>
     *                       <span class="zh-CN">外键级联状态数组</span>
     * @param joinColumns    <span class="en-US">The annotation instance array of JoinColumn</span>
     *                       <span class="zh-CN">注解 JoinColumn 的实例对象数组</span>
     * @return <span class="en-US">Generated reference configure information instance</span>
     * <span class="zh-CN">生成的外键引用配置信息实例对象</span>
     */
    public static ReferenceConfig newInstance(final Class<?> referenceClass, final String fieldName,
                                              final boolean lazyLoad, final boolean returnArray,
                                              final CascadeType[] cascadeTypes, final JoinColumn[] joinColumns) {
        if (joinColumns == null || cascadeTypes == null || StringUtils.isEmpty(fieldName) || joinColumns.length == 0) {
            return null;
        }

        List<JoinConfig> referenceColumns = new ArrayList<>();
        Arrays.asList(joinColumns)
                .forEach(joinColumn -> {
                    JoinConfig joinConfig = new JoinConfig();
                    joinConfig.setCurrentField(joinColumn.columnDefinition());
                    joinConfig.setReferenceField(joinColumn.referencedColumnName());
                    referenceColumns.add(joinConfig);
                });
        ReferenceConfig referenceConfig = new ReferenceConfig();
        referenceConfig.setReferenceClass(referenceClass);
        referenceConfig.setFieldName(fieldName);
        referenceConfig.setLazyLoad(lazyLoad);
        referenceConfig.setReturnArray(returnArray);
        referenceConfig.setCascadeTypes(cascadeTypes);
        referenceConfig.setJoinColumnList(referenceColumns);
        return referenceConfig;
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
     * <h3 class="en-US">Getter method for return value is array</h3>
     * <h3 class="zh-CN">返回值是数组或列表的Getter方法</h3>
     *
     * @return <span class="en-US">Return value is array</span>
     * <span class="zh-CN">返回值是数组或列表</span>
     */
    public boolean isReturnArray() {
        return returnArray;
    }

    /**
     * <h3 class="en-US">Setter method for return value is array</h3>
     * <h3 class="zh-CN">返回值是数组或列表的Setter方法</h3>
     *
     * @param returnArray <span class="en-US">Return value is array</span>
     *                    <span class="zh-CN">返回值是数组或列表</span>
     */
    public void setReturnArray(boolean returnArray) {
        this.returnArray = returnArray;
    }

    /**
     * <h3 class="en-US">Getter method for target reference entity class</h3>
     * <h3 class="zh-CN">目标外键实体类的Getter方法</h3>
     *
     * @return <span class="en-US">Target reference entity class</span>
     * <span class="zh-CN">目标外键实体类</span>
     */
    public Class<?> getReferenceClass() {
        return referenceClass;
    }

    /**
     * <h3 class="en-US">Setter method for target reference entity class</h3>
     * <h3 class="zh-CN">目标外键实体类的Setter方法</h3>
     *
     * @param referenceClass <span class="en-US">Target reference entity class</span>
     *                       <span class="zh-CN">目标外键实体类</span>
     */
    public void setReferenceClass(Class<?> referenceClass) {
        this.referenceClass = referenceClass;
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
        this.fieldName = fieldName;
    }

    /**
     * <h3 class="en-US">Getter method for reference cascade type array</h3>
     * <h3 class="zh-CN">外键级联状态数组的Getter方法</h3>
     *
     * @return <span class="en-US">Reference cascade type array</span>
     * <span class="zh-CN">外键级联状态数组</span>
     */
    public CascadeType[] getCascadeTypes() {
        return cascadeTypes;
    }

    /**
     * <h3 class="en-US">Setter method for reference cascade type array</h3>
     * <h3 class="zh-CN">外键级联状态数组的Setter方法</h3>
     *
     * @param cascadeTypes <span class="en-US">Reference cascade type array</span>
     *                     <span class="zh-CN">外键级联状态数组</span>
     */
    public void setCascadeTypes(CascadeType[] cascadeTypes) {
        this.cascadeTypes = cascadeTypes;
    }

    /**
     * <h3 class="en-US">Getter method for reference join column configure list</h3>
     * <h3 class="zh-CN">外键关联列配置信息列表的Getter方法</h3>
     *
     * @return <span class="en-US">Reference join column configure list</span>
     * <span class="zh-CN">外键关联列配置信息列表</span>
     */
    public List<JoinConfig> getJoinColumnList() {
        return joinColumnList;
    }

    /**
     * <h3 class="en-US">Setter method for reference join column configure list</h3>
     * <h3 class="zh-CN">外键关联列配置信息列表的Setter方法</h3>
     *
     * @param joinColumnList <span class="en-US">Reference join column configure list</span>
     *                       <span class="zh-CN">外键关联列配置信息列表</span>
     */
    public void setJoinColumnList(List<JoinConfig> joinColumnList) {
        this.joinColumnList = joinColumnList;
    }

    /**
     * <h3 class="en-US">Match the given entity class was same as current target reference entity class</h3>
     * <h3 class="zh-CN">匹配给定的实体类对象是否与当前目标外键实体类信息一致</h3>
     *
     * @param entityClass   <span class="en-US">Given entity class</span>
     *                      <span class="zh-CN">给定的实体类</span>
     * @return <span class="en-US">Match result</span>
     * <span class="zh-CN">匹配结果</span>
     */
    public boolean match(final Class<?> entityClass) {
        return ObjectUtils.nullSafeEquals(this.referenceClass, entityClass);
    }
}
