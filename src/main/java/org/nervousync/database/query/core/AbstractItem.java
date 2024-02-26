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

package org.nervousync.database.query.core;

import jakarta.annotation.Nonnull;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlTransient;
import org.nervousync.commons.Globals;
import org.nervousync.database.annotations.query.ResultData;
import org.nervousync.database.beans.configs.column.ColumnConfig;
import org.nervousync.database.enumerations.query.ItemType;
import org.nervousync.database.query.QueryInfo;
import org.nervousync.database.query.item.FunctionItem;
import org.nervousync.database.query.item.ColumnItem;
import org.nervousync.database.query.item.QueryItem;
import org.nervousync.database.query.param.AbstractParameter;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Optional;

@XmlTransient
public abstract class AbstractItem extends SortedItem {

    /**
     * <span class="en-US">Serial version UID</span>
     * <span class="zh-CN">序列化UID</span>
     */
    private static final long serialVersionUID = -6138695356160544999L;

    /**
     * <span class="en-US">Query item type</span>
     * <span class="zh-CN">查询项类型</span>
     */
    @XmlElement(name = "item_type")
    private final ItemType itemType;
    /**
     * <span class="en-US">Item alias name</span>
     * <span class="zh-CN">查询项别名</span>
     */
    @XmlElement(name = "alias_name")
    private String aliasName;

    /**
     * <h3 class="en-US">Protect constructor method for abstract query item define</h3>
     * <h3 class="zh-CN">抽象查询项信息定义的构造方法</h3>
     *
     * @param itemType <span class="en-US">Query item type</span>
     *                 <span class="zh-CN">查询项类型</span>
     */
    protected AbstractItem(final ItemType itemType) {
        this.itemType = itemType;
    }

    /**
     * <h3 class="en-US">Static method for generate query column information instance</h3>
     * <h3 class="zh-CN">静态方法用于生成数据列查询对象实例</h3>
     *
     * @param field  <span class="en-US">Field instance object</span>
     *               <span class="zh-CN">属性对象</span>
     * @return <span class="en-US">Generated object instance</span>
     * <span class="zh-CN">生成的对象实例</span>
     */
    public static ColumnItem column(@Nonnull Field field) {
        return Optional.ofNullable(field.getAnnotation(ResultData.class))
                .map(resultData ->
                        column(resultData.entity(), resultData.identifyKey(), resultData.distinct(), field.getName()))
                .orElseThrow(null);
    }

    /**
     * <h3 class="en-US">Static method for generate query column information instance</h3>
     * <h3 class="zh-CN">静态方法用于生成数据列查询对象实例</h3>
     *
     * @param entityClass   <span class="en-US">The entity class to which the data column belongs</span>
     *                      <span class="zh-CN">数据列所属的实体类</span>
     * @param columnConfig  <span class="en-US">Column configure information</span>
     *                      <span class="zh-CN">数据列配置信息</span>
     * @return <span class="en-US">Generated object instance</span>
     * <span class="zh-CN">生成的对象实例</span>
     */
    public static ColumnItem column(@Nonnull final Class<?> entityClass, @Nonnull ColumnConfig columnConfig) {
        return column(entityClass, columnConfig.columnName(), columnConfig.getFieldName());
    }

    /**
     * <h3 class="en-US">Static method for generate query column information instance</h3>
     * <h3 class="zh-CN">静态方法用于生成数据列查询对象实例</h3>
     *
     * @param entityClass <span class="en-US">The entity class to which the data column belongs</span>
     *                    <span class="zh-CN">数据列所属的实体类</span>
     * @param identifyKey <span class="en-US">Identify key</span>
     *                    <span class="zh-CN">识别代码</span>
     * @param aliasName   <span class="en-US">Item alias name</span>
     *                    <span class="zh-CN">查询项别名</span>
     * @return <span class="en-US">Generated object instance</span>
     * <span class="zh-CN">生成的对象实例</span>
     */
    public static ColumnItem column(@Nonnull final Class<?> entityClass, final String identifyKey,
                                    final String aliasName) {
        return column(entityClass, identifyKey, Boolean.FALSE, aliasName);
    }

    /**
     * <h3 class="en-US">Static method for generate query column information instance</h3>
     * <h3 class="zh-CN">静态方法用于生成数据列查询对象实例</h3>
     *
     * @param entityClass <span class="en-US">The entity class to which the data column belongs</span>
     *                    <span class="zh-CN">数据列所属的实体类</span>
     * @param identifyKey <span class="en-US">Identify key</span>
     *                    <span class="zh-CN">识别代码</span>
     * @param distinct    <span class="en-US">Column distinct</span>
     *                    <span class="zh-CN">数据列去重</span>
     * @param aliasName   <span class="en-US">Item alias name</span>
     *                    <span class="zh-CN">查询项别名</span>
     * @return <span class="en-US">Generated object instance</span>
     * <span class="zh-CN">生成的对象实例</span>
     */
    public static ColumnItem column(@Nonnull final Class<?> entityClass, final String identifyKey,
                                    final boolean distinct, final String aliasName) {
        return column(entityClass, identifyKey, distinct, aliasName, Globals.DEFAULT_VALUE_INT);
    }

    /**
     * <h3 class="en-US">Static method for generate query column information instance</h3>
     * <h3 class="zh-CN">静态方法用于生成数据列查询对象实例</h3>
     *
     * @param entityClass <span class="en-US">The entity class to which the data column belongs</span>
     *                    <span class="zh-CN">数据列所属的实体类</span>
     * @param identifyKey <span class="en-US">Identify key</span>
     *                    <span class="zh-CN">识别代码</span>
     * @param distinct    <span class="en-US">Column distinct</span>
     *                    <span class="zh-CN">数据列去重</span>
     * @param aliasName   <span class="en-US">Item alias name</span>
     *                    <span class="zh-CN">查询项别名</span>
     * @param sortCode    <span class="en-US">Sort code</span>
     *                    <span class="zh-CN">排序代码</span>
     * @return <span class="en-US">Generated object instance</span>
     * <span class="zh-CN">生成的对象实例</span>
     */
    public static ColumnItem column(@Nonnull final Class<?> entityClass, final String identifyKey,
                                    final boolean distinct, final String aliasName, final int sortCode) {
        ColumnItem queryColumn = new ColumnItem();
        queryColumn.setEntityClass(entityClass);
        queryColumn.setAliasName(aliasName);
        queryColumn.setIdentifyKey(identifyKey);
        queryColumn.setDistinct(distinct);
        queryColumn.setSortCode(sortCode);
        return queryColumn;
    }

    /**
     * <h3 class="en-US">Static method for generate query function information instance</h3>
     * <h3 class="zh-CN">静态方法用于生成数据列函数查询对象实例</h3>
     *
     * @param aliasName      <span class="en-US">Item alias name</span>
     *                       <span class="zh-CN">查询项别名</span>
     * @param sqlFunction    <span class="en-US">Function name</span>
     *                       <span class="zh-CN">函数名</span>
     * @param functionParams <span class="en-US">Function arguments array</span>
     *                       <span class="zh-CN">函数参数数组</span>
     * @return <span class="en-US">Generated object instance</span>
     * <span class="zh-CN">生成的对象实例</span>
     */
    public static FunctionItem function(final String aliasName, final String sqlFunction,
                                        final AbstractParameter<?>... functionParams) {
        return function(aliasName, Globals.DEFAULT_VALUE_INT, sqlFunction, functionParams);
    }

    /**
     * <h3 class="en-US">Static method for generate query function information instance</h3>
     * <h3 class="zh-CN">静态方法用于生成数据列函数查询对象实例</h3>
     *
     * @param aliasName      <span class="en-US">Item alias name</span>
     *                       <span class="zh-CN">查询项别名</span>
     * @param sortCode       <span class="en-US">Sort code</span>
     *                       <span class="zh-CN">排序代码</span>
     * @param sqlFunction    <span class="en-US">Function name</span>
     *                       <span class="zh-CN">函数名</span>
     * @param functionParams <span class="en-US">Function arguments array</span>
     *                       <span class="zh-CN">函数参数数组</span>
     * @return <span class="en-US">Generated object instance</span>
     * <span class="zh-CN">生成的对象实例</span>
     */
    public static FunctionItem function(final String aliasName, final int sortCode, final String sqlFunction,
                                        final AbstractParameter<?>... functionParams) {
        FunctionItem queryFunction = new FunctionItem();
        queryFunction.setAliasName(aliasName);
        queryFunction.setSqlFunction(sqlFunction);
        queryFunction.setSortCode(sortCode);
        queryFunction.setFunctionParams(Arrays.asList(functionParams));
        return queryFunction;
    }

    /**
     * <h3 class="en-US">Static method for generate sub-query information instance</h3>
     * <h3 class="zh-CN">静态方法用于生成子查询对象实例</h3>
     *
     * @param queryInfo <span class="en-US">Query information</span>
     *                  <span class="zh-CN">查询信息</span>
     * @return <span class="en-US">Generated object instance</span>
     * <span class="zh-CN">生成的对象实例</span>
     */
    public static QueryItem query(final QueryInfo queryInfo) {
        return query(Globals.DEFAULT_VALUE_STRING, queryInfo);
    }

    /**
     * <h3 class="en-US">Static method for generate sub-query information instance</h3>
     * <h3 class="zh-CN">静态方法用于生成子查询对象实例</h3>
     *
     * @param aliasName <span class="en-US">Item alias name</span>
     *                  <span class="zh-CN">查询项别名</span>
     * @param queryInfo <span class="en-US">Query information</span>
     *                  <span class="zh-CN">查询信息</span>
     * @return <span class="en-US">Generated object instance</span>
     * <span class="zh-CN">生成的对象实例</span>
     */
    public static QueryItem query(final String aliasName, final QueryInfo queryInfo) {
        return query(aliasName, Globals.DEFAULT_VALUE_INT, queryInfo);
    }

    /**
     * <h3 class="en-US">Static method for generate sub-query information instance</h3>
     * <h3 class="zh-CN">静态方法用于生成子查询对象实例</h3>
     *
     * @param aliasName <span class="en-US">Item alias name</span>
     *                  <span class="zh-CN">查询项别名</span>
     * @param sortCode  <span class="en-US">Sort code</span>
     *                  <span class="zh-CN">排序代码</span>
     * @param queryInfo <span class="en-US">Query information</span>
     *                  <span class="zh-CN">查询信息</span>
     * @return <span class="en-US">Generated object instance</span>
     * <span class="zh-CN">生成的对象实例</span>
     */
    public static QueryItem query(final String aliasName, final int sortCode, final QueryInfo queryInfo) {
        QueryItem queryItem = new QueryItem();

        queryItem.setAliasName(aliasName);
        queryItem.setQueryInfo(queryInfo);
        queryItem.setSortCode(sortCode);

        return queryItem;
    }

    /**
     * <h3 class="en-US">Getter method for query item type</h3>
     * <h3 class="zh-CN">查询项类型的Getter方法</h3>
     *
     * @return <span class="en-US">Query item type</span>
     * <span class="zh-CN">查询项类型</span>
     */
    public ItemType getItemType() {
        return itemType;
    }

    /**
     * <h3 class="en-US">Getter method for alias name</h3>
     * <h3 class="zh-CN">别名的Getter方法</h3>
     *
     * @return <span class="en-US">Alias name</span>
     * <span class="zh-CN">别名</span>
     */
    public String getAliasName() {
        return aliasName;
    }

    /**
     * <h3 class="en-US">Setter method for alias name</h3>
     * <h3 class="zh-CN">别名的Setter方法</h3>
     *
     * @param aliasName <span class="en-US">Alias name</span>
     *                  <span class="zh-CN">别名</span>
     */
    public void setAliasName(String aliasName) {
        this.aliasName = aliasName;
    }
}
