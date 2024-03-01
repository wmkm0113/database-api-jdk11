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

package org.nervousync.database.query.param;

import jakarta.annotation.Nonnull;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlTransient;
import org.nervousync.commons.Globals;
import org.nervousync.database.enumerations.query.ItemType;
import org.nervousync.database.query.QueryInfo;
import org.nervousync.database.query.data.ArrayData;
import org.nervousync.database.query.data.RangesData;
import org.nervousync.database.query.core.AbstractItem;
import org.nervousync.database.query.core.SortedItem;
import org.nervousync.database.query.param.impl.*;
import org.nervousync.exceptions.builder.BuilderException;

/**
 * <h2 class="en-US">Abstract class for parameter information define</h2>
 * <h2 class="zh-CN">参数信息定义抽象类</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Oct 9, 2020 11:42:46 $
 */
@XmlTransient
@XmlSeeAlso({ArraysParameter.class, ColumnParameter.class, ConstantParameter.class, FunctionParameter.class, QueryParameter.class, RangesParameter.class})
public abstract class AbstractParameter<T> extends SortedItem {

    /**
     * <span class="en-US">Serial version UID</span>
     * <span class="zh-CN">序列化UID</span>
     */
    private static final long serialVersionUID = 3637722387304256057L;

    /**
     * <span class="en-US">Parameter type</span>
     * <span class="zh-CN">参数类型</span>
     */
    @XmlElement(name = "item_type")
    private final ItemType itemType;

    /**
     * <h3 class="en-US">Protect constructor method for abstract class for parameter information define</h3>
     * <h3 class="zh-CN">参数信息定义抽象类的构造方法</h3>
     *
     * @param itemType <span class="en-US">Parameter type</span>
     *                 <span class="zh-CN">参数类型</span>
     */
    protected AbstractParameter(final ItemType itemType) {
        this.itemType = itemType;
    }

    /**
     * <h3 class="en-US">Getter method for parameter type</h3>
     * <h3 class="zh-CN">参数类型的Getter方法</h3>
     *
     * @return <span class="en-US">Parameter type</span>
     * <span class="zh-CN">参数类型</span>
     */
    public final ItemType getItemType() {
        return itemType;
    }

    /**
     * <h3 class="en-US">Getter method for parameter value</h3>
     * <h3 class="zh-CN">参数值的Getter方法</h3>
     *
     * @return <span class="en-US">Parameter value</span>
     * <span class="zh-CN">参数值</span>
     */
    public abstract T getItemValue();

    /**
     * <h3 class="en-US">Setter method for parameter value</h3>
     * <h3 class="zh-CN">参数值的Setter方法</h3>
     *
     * @param itemValue <span class="en-US">Parameter value</span>
     *                  <span class="zh-CN">参数值</span>
     */
    public abstract void setItemValue(T itemValue);

    /**
     * <h3 class="en-US">Static method for generate function parameter instance</h3>
     * <h3 class="zh-CN">静态方法用于生成函数参数实例对象</h3>
     *
     * @param sqlFunction    <span class="en-US">Function name</span>
     *                       <span class="zh-CN">函数名称</span>
     * @param functionParams <span class="en-US">Function parameter values</span>
     *                       <span class="zh-CN">函数参数值</span>
     *
     * @return <span class="en-US">Generated object instance</span>
     * <span class="zh-CN">生成的对象实例</span>
     */
    public static FunctionParameter function(final String sqlFunction, final AbstractParameter<?>... functionParams) {
        return function(Globals.DEFAULT_VALUE_STRING, sqlFunction, functionParams);
    }

    /**
     * <h3 class="en-US">Static method for generate function parameter instance</h3>
     * <h3 class="zh-CN">静态方法用于生成函数参数实例对象</h3>
     *
     * @param aliasName      <span class="en-US">Item alias name</span>
     *                       <span class="zh-CN">查询项别名</span>
     * @param sqlFunction    <span class="en-US">Function name</span>
     *                       <span class="zh-CN">函数名称</span>
     * @param functionParams <span class="en-US">Function parameter values</span>
     *                       <span class="zh-CN">函数参数值</span>
     *
     * @return <span class="en-US">Generated object instance</span>
     * <span class="zh-CN">生成的对象实例</span>
     */
    public static FunctionParameter function(final String aliasName, final String sqlFunction,
                                             final AbstractParameter<?>... functionParams) {
        return function(aliasName, sqlFunction, Globals.DEFAULT_VALUE_INT, functionParams);
    }

    /**
     * <h3 class="en-US">Static method for generate function parameter instance</h3>
     * <h3 class="zh-CN">静态方法用于生成函数参数实例对象</h3>
     *
     * @param aliasName      <span class="en-US">Item alias name</span>
     *                       <span class="zh-CN">查询项别名</span>
     * @param sqlFunction    <span class="en-US">Function name</span>
     *                       <span class="zh-CN">函数名称</span>
     * @param sortCode       <span class="en-US">Sort code</span>
     *                       <span class="zh-CN">排序代码</span>
     * @param functionParams <span class="en-US">Function parameter values</span>
     *                       <span class="zh-CN">函数参数值</span>
     *
     * @return <span class="en-US">Generated object instance</span>
     * <span class="zh-CN">生成的对象实例</span>
     */
    public static FunctionParameter function(final String aliasName, final String sqlFunction, final int sortCode,
                                             final AbstractParameter<?>... functionParams) {
        FunctionParameter functionParameter = new FunctionParameter();
        functionParameter.setItemValue(AbstractItem.function(aliasName, sqlFunction, functionParams));
        functionParameter.setSortCode(sortCode);
        return functionParameter;
    }

    /**
     * <h3 class="en-US">Static method for generate column parameter instance</h3>
     * <h3 class="zh-CN">静态方法用于生成数据列参数实例对象</h3>
     *
     * @param entityClass <span class="en-US">The entity class to which the data column belongs</span>
     *                    <span class="zh-CN">数据列所属的实体类</span>
     * @param identifyKey <span class="en-US">Identify key</span>
     *                    <span class="zh-CN">识别代码</span>
     * @param aliasName   <span class="en-US">Item alias name</span>
     *                    <span class="zh-CN">查询项别名</span>
     *
     * @return <span class="en-US">Generated object instance</span>
     * <span class="zh-CN">生成的对象实例</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
     */
    public static ColumnParameter column(final Class<?> entityClass, final String identifyKey, final String aliasName)
            throws BuilderException {
        return column(entityClass, identifyKey, aliasName, Globals.DEFAULT_VALUE_INT);
    }

    /**
     * <h3 class="en-US">Static method for generate column parameter instance</h3>
     * <h3 class="zh-CN">静态方法用于生成数据列参数实例对象</h3>
     *
     * @param entityClass <span class="en-US">The entity class to which the data column belongs</span>
     *                    <span class="zh-CN">数据列所属的实体类</span>
     * @param identifyKey <span class="en-US">Identify key</span>
     *                    <span class="zh-CN">识别代码</span>
     * @param aliasName   <span class="en-US">Item alias name</span>
     *                    <span class="zh-CN">查询项别名</span>
     * @param sortCode    <span class="en-US">Sort code</span>
     *                    <span class="zh-CN">排序代码</span>
     * @return <span class="en-US">Generated object instance</span>
     * <span class="zh-CN">生成的对象实例</span>
	 * @throws BuilderException <span class="en-US">If the driver table entity class is not registered or column not found</span>
	 *                          <span class="zh-CN">如果驱动表实体类未注册或数据列未找到</span>
     */
    public static ColumnParameter column(final Class<?> entityClass, final String identifyKey,
                                         final String aliasName, final int sortCode) throws BuilderException {
        ColumnParameter columnParameter = new ColumnParameter();
        columnParameter.setItemValue(AbstractItem.column(entityClass, identifyKey, aliasName));
        columnParameter.setSortCode(sortCode);
        return columnParameter;
    }

    /**
     * <h3 class="en-US">Static method for generate constant parameter instance</h3>
     * <h3 class="zh-CN">静态方法用于生成常量参数实例对象</h3>
     *
     * @param object <span class="en-US">Constant value</span>
     *               <span class="zh-CN">常量值</span>
     * @return <span class="en-US">Generated object instance</span>
     * <span class="zh-CN">生成的对象实例</span>
     */
    public static ConstantParameter constant(@Nonnull final Object object) {
        return constant(object, Globals.DEFAULT_VALUE_INT);
    }

    /**
     * <h3 class="en-US">Static method for generate constant parameter instance</h3>
     * <h3 class="zh-CN">静态方法用于生成常量参数实例对象</h3>
     *
     * @param object   <span class="en-US">Constant value</span>
     *                 <span class="zh-CN">常量值</span>
     * @param sortCode <span class="en-US">Sort code</span>
     *                 <span class="zh-CN">排序代码</span>
     * @return <span class="en-US">Generated object instance</span>
     * <span class="zh-CN">生成的对象实例</span>
     */
    public static ConstantParameter constant(@Nonnull final Object object, final int sortCode) {
        ConstantParameter constantParameter = new ConstantParameter();
        constantParameter.setItemValue(object);
        constantParameter.setSortCode(sortCode);
        return constantParameter;
    }

    /**
     * <h3 class="en-US">Static method for generate sub-query parameter instance</h3>
     * <h3 class="zh-CN">静态方法用于生成子查询参数实例对象</h3>
     *
     * @param queryInfo     <span class="en-US">Sub-query information</span>
     *                      <span class="zh-CN">子查询信息</span>
     * @return <span class="en-US">Generated object instance</span>
     * <span class="zh-CN">生成的对象实例</span>
     */
    public static QueryParameter subQuery(final QueryInfo queryInfo) {
        return subQuery(queryInfo, Globals.DEFAULT_VALUE_INT);
    }

    /**
     * <h3 class="en-US">Static method for generate sub-query parameter instance</h3>
     * <h3 class="zh-CN">静态方法用于生成子查询参数实例对象</h3>
     *
     * @param queryInfo     <span class="en-US">Sub-query information</span>
     *                      <span class="zh-CN">子查询信息</span>
     * @param sortCode      <span class="en-US">Sort code</span>
     *                      <span class="zh-CN">排序代码</span>
     * @return <span class="en-US">Generated object instance</span>
     * <span class="zh-CN">生成的对象实例</span>
     */
    public static QueryParameter subQuery(final QueryInfo queryInfo, final int sortCode) {
        QueryParameter queryParameter = new QueryParameter();
        queryParameter.setItemValue(queryInfo);
        queryParameter.setSortCode(sortCode);
        return queryParameter;
    }

    /**
     * <h3 class="en-US">Static method for generate ranges data parameter instance</h3>
     * <h3 class="zh-CN">静态方法用于生成范围值查询参数实例对象</h3>
     *
     * @param beginValue <span class="en-US">Begin value</span>
     *                   <span class="zh-CN">起始值</span>
     * @param endValue   <span class="en-US">End value</span>
     *                   <span class="zh-CN">终止值</span>
     * @return <span class="en-US">Generated object instance</span>
     * <span class="zh-CN">生成的对象实例</span>
     */
    public static RangesParameter ranges(@Nonnull final Object beginValue, @Nonnull final Object endValue) {
        return ranges(beginValue, endValue, Globals.DEFAULT_VALUE_INT);
    }

    /**
     * <h3 class="en-US">Static method for generate condition data array parameter instance</h3>
     * <h3 class="zh-CN">静态方法用于生成匹配值数组查询参数实例对象</h3>
     *
     * @param matchValues <span class="en-US">Condition data array</span>
     *                    <span class="zh-CN">匹配值数组</span>
     * @return <span class="en-US">Generated object instance</span>
     * <span class="zh-CN">生成的对象实例</span>
     */
    public static ArraysParameter arrays(final Object... matchValues) {
        ArraysParameter arraysParameter = new ArraysParameter();
        arraysParameter.setItemValue(new ArrayData(matchValues));
        return arraysParameter;
    }

    /**
     * <h3 class="en-US">Static method for generate ranges data parameter instance</h3>
     * <h3 class="zh-CN">静态方法用于生成范围值查询参数实例对象</h3>
     *
     * @param beginValue <span class="en-US">Begin value</span>
     *                   <span class="zh-CN">起始值</span>
     * @param endValue   <span class="en-US">End value</span>
     *                   <span class="zh-CN">终止值</span>
     * @param sortCode   <span class="en-US">Sort code</span>
     *                   <span class="zh-CN">排序代码</span>
     * @return <span class="en-US">Generated object instance</span>
     * <span class="zh-CN">生成的对象实例</span>
     */
    public static RangesParameter ranges(@Nonnull final Object beginValue, @Nonnull final Object endValue,
                                         final int sortCode) {
        RangesParameter rangesParameter = new RangesParameter();
        rangesParameter.setItemValue(new RangesData(beginValue, endValue));
        rangesParameter.setSortCode(sortCode);
        return rangesParameter;
    }
}
