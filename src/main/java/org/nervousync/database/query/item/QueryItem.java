/*
 * Copyright © 2003 Nervousync® Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of
 * Nervousync Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with Nervousync Studio.
 */

package org.nervousync.database.query.item;

import jakarta.xml.bind.annotation.*;
import org.nervousync.beans.core.BeanObject;
import org.nervousync.commons.Globals;
import org.nervousync.database.entity.EntityManager;
import org.nervousync.database.enumerations.query.ItemType;
import org.nervousync.database.query.item.impl.QueryColumn;
import org.nervousync.database.query.item.impl.QueryConstant;
import org.nervousync.database.query.item.impl.QueryFunction;
import org.nervousync.utils.StringUtils;

import java.util.Arrays;

/**
 * <h2 class="en-US">Abstract query item define</h2>
 * <h2 class="zh-CN">抽象查询项信息定义</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Oct 9, 2020 11:30:54 $
 */
@XmlType(namespace = "https://nervousync.org/schemas/query")
@XmlSeeAlso({QueryColumn.class, QueryConstant.class, QueryFunction.class})
@XmlAccessorType(XmlAccessType.NONE)
public abstract class QueryItem extends BeanObject {
    /**
     * <span class="en-US">Serial version UID</span>
     * <span class="zh-CN">序列化UID</span>
     */
    private static final long serialVersionUID = -8808799678096056072L;

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
     * <span class="en-US">Sort code</span>
     * <span class="zh-CN">排序代码</span>
     */
    @XmlElement(name = "sort_code")
    private int sortCode;

    /**
     * <h3 class="en-US">Protect constructor method for abstract query item define</h3>
     * <h3 class="zh-CN">抽象查询项信息定义的构造方法</h3>
     *
     * @param itemType <span class="en-US">Query item type</span>
     *                 <span class="zh-CN">查询项类型</span>
     */
    protected QueryItem(ItemType itemType) {
        this.itemType = itemType;
    }

    /**
     * <h3 class="en-US">Static method for generate query column information instance</h3>
     * <h3 class="zh-CN">静态方法用于生成数据列查询对象实例</h3>
     *
     * @param entityClass <span class="en-US">Entity class</span>
     *                    <span class="zh-CN">实体类</span>
     * @param identifyKey <span class="en-US">Identify key</span>
     *                    <span class="zh-CN">识别代码</span>
     * @return <span class="en-US">Generated object instance</span>
     * <span class="zh-CN">生成的对象实例</span>
     */
    public static QueryColumn queryColumn(final Class<?> entityClass, final String identifyKey) {
        return queryColumn(entityClass, identifyKey, Globals.DEFAULT_VALUE_STRING);
    }

    /**
     * <h3 class="en-US">Static method for generate query column information instance</h3>
     * <h3 class="zh-CN">静态方法用于生成数据列查询对象实例</h3>
     *
     * @param entityClass <span class="en-US">Entity class</span>
     *                    <span class="zh-CN">实体类</span>
     * @param identifyKey <span class="en-US">Identify key</span>
     *                    <span class="zh-CN">识别代码</span>
     * @param aliasName   <span class="en-US">Item alias name</span>
     *                    <span class="zh-CN">查询项别名</span>
     * @return <span class="en-US">Generated object instance</span>
     * <span class="zh-CN">生成的对象实例</span>
     */
    public static QueryColumn queryColumn(final Class<?> entityClass, final String identifyKey,
                                          final String aliasName) {
        return queryColumn(entityClass, identifyKey, Boolean.FALSE, aliasName);
    }

    /**
     * <h3 class="en-US">Static method for generate query column information instance</h3>
     * <h3 class="zh-CN">静态方法用于生成数据列查询对象实例</h3>
     *
     * @param entityClass <span class="en-US">Entity class</span>
     *                    <span class="zh-CN">实体类</span>
     * @param identifyKey <span class="en-US">Identify key</span>
     *                    <span class="zh-CN">识别代码</span>
     * @param distinct    <span class="en-US">Column distinct</span>
     *                    <span class="zh-CN">数据列去重</span>
     * @param aliasName   <span class="en-US">Item alias name</span>
     *                    <span class="zh-CN">查询项别名</span>
     * @return <span class="en-US">Generated object instance</span>
     * <span class="zh-CN">生成的对象实例</span>
     */
    public static QueryColumn queryColumn(final Class<?> entityClass, final String identifyKey,
                                          final boolean distinct, final String aliasName) {
        return queryColumn(entityClass, identifyKey, distinct, aliasName, Globals.DEFAULT_VALUE_INT);
    }

    /**
     * <h3 class="en-US">Static method for generate query column information instance</h3>
     * <h3 class="zh-CN">静态方法用于生成数据列查询对象实例</h3>
     *
     * @param entityClass <span class="en-US">Entity class</span>
     *                    <span class="zh-CN">实体类</span>
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
    public static QueryColumn queryColumn(final Class<?> entityClass, final String identifyKey, final boolean distinct,
                                          final String aliasName, final int sortCode) {
        return EntityManager.tableConfig(entityClass)
                .map(tableConfig -> tableConfig.columnConfig(identifyKey))
                .map(columnConfig -> {
                    QueryColumn queryColumn = new QueryColumn();
                    queryColumn.setEntityClass(entityClass);
                    queryColumn.setAliasName(StringUtils.isEmpty(aliasName) ? columnConfig.getFieldName() : aliasName);
                    queryColumn.setIdentifyKey(identifyKey);
                    queryColumn.setDistinct(distinct);
                    queryColumn.setSortCode(sortCode);
                    return queryColumn;
                })
                .orElse(null);
    }

    /**
     * <h3 class="en-US">Static method for generate query constant information instance</h3>
     * <h3 class="zh-CN">静态方法用于生成数据列常量查询对象实例</h3>
     *
     * @param constantValue <span class="en-US">Constant value</span>
     *                      <span class="zh-CN">常量值</span>
     * @return <span class="en-US">Generated object instance</span>
     * <span class="zh-CN">生成的对象实例</span>
     */
    public static QueryConstant queryConstant(final Object constantValue) {
        return queryConstant(constantValue, Globals.DEFAULT_VALUE_INT);
    }

    /**
     * <h3 class="en-US">Static method for generate query constant information instance</h3>
     * <h3 class="zh-CN">静态方法用于生成数据列常量查询对象实例</h3>
     *
     * @param constantValue <span class="en-US">Constant value</span>
     *                      <span class="zh-CN">常量值</span>
     * @param sortCode      <span class="en-US">Sort code</span>
     *                      <span class="zh-CN">排序代码</span>
     * @return <span class="en-US">Generated object instance</span>
     * <span class="zh-CN">生成的对象实例</span>
     */
    public static QueryConstant queryConstant(final Object constantValue, final int sortCode) {
        QueryConstant queryConstant = new QueryConstant();
        queryConstant.setConstantValue(constantValue);
        queryConstant.setSortCode(sortCode);
        return queryConstant;
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
    public static QueryFunction queryFunction(final String aliasName, final String sqlFunction,
                                              final QueryItem... functionParams) {
        QueryFunction queryFunction = new QueryFunction();
        queryFunction.setAliasName(aliasName);
        queryFunction.setSqlFunction(sqlFunction);
        queryFunction.setSortCode(Globals.DEFAULT_VALUE_INT);
        queryFunction.setFunctionParams(Arrays.asList(functionParams));
        return queryFunction;
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
     * <h3 class="en-US">Getter method for table alias name</h3>
     * <h3 class="zh-CN">数据表别名的Getter方法</h3>
     *
     * @return <span class="en-US">Table alias name</span>
     * <span class="zh-CN">数据表别名</span>
     */
    public String getAliasName() {
        return aliasName;
    }

    /**
     * <h3 class="en-US">Setter method for table alias name</h3>
     * <h3 class="zh-CN">数据表别名的Setter方法</h3>
     *
     * @param aliasName <span class="en-US">Table alias name</span>
     *                  <span class="zh-CN">数据表别名</span>
     */
    public void setAliasName(String aliasName) {
        this.aliasName = aliasName;
    }

    /**
     * <h3 class="en-US">Getter method for sort code</h3>
     * <h3 class="zh-CN">排序代码的Getter方法</h3>
     *
     * @return <span class="en-US">Sort code</span>
     * <span class="zh-CN">排序代码</span>
     */
    public int getSortCode() {
        return sortCode;
    }

    /**
     * <h3 class="en-US">Setter method for sort code</h3>
     * <h3 class="zh-CN">排序代码的Setter方法</h3>
     *
     * @param sortCode <span class="en-US">Sort code</span>
     *                 <span class="zh-CN">排序代码</span>
     */
    public void setSortCode(int sortCode) {
        this.sortCode = sortCode;
    }

    /**
     * <h3 class="en-US">Checks whether the given parameter information matches the current information</h3>
     * <h3 class="zh-CN">检查给定的参数信息是否与当前信息匹配</h3>
     *
     * @param queryItem <span class="en-US">Query item instance</span>
     *                  <span class="zh-CN">查询项实例对象</span>
     * @return <span class="en-US">Match result</span>
     * <span class="zh-CN">匹配结果</span>
     */
    public abstract boolean match(final QueryItem queryItem);
}
