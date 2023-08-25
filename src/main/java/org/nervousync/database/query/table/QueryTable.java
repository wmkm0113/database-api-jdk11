/*
 * Copyright © 2003 Nervousync® Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of
 * Nervousync Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with Nervousync Studio.
 */
package org.nervousync.database.query.table;

import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.nervousync.beans.converter.impl.basic.ClassStringAdapter;
import org.nervousync.beans.core.BeanObject;
import org.nervousync.database.query.condition.QueryCondition;
import org.nervousync.database.query.item.QueryItem;
import org.nervousync.database.query.item.impl.QueryColumn;
import org.nervousync.database.query.item.impl.QueryConstant;
import org.nervousync.database.query.item.impl.QueryFunction;
import org.nervousync.database.query.join.JoinTable;

import java.util.ArrayList;
import java.util.List;

/**
 * <h2 class="en-US">Query table define</h2>
 * <h2 class="zh-CN">查询数据表信息定义</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Aug 2, 2021 13:25:47 $
 */
@XmlType(name = "query_table", namespace = "https://nervousync.org/schemas/query")
@XmlAccessorType(XmlAccessType.NONE)
public final class QueryTable extends BeanObject {
    /**
     * <span class="en-US">Serial version UID</span>
     * <span class="zh-CN">序列化UID</span>
     */
    private static final long serialVersionUID = 1917358665598504807L;
    /**
     * <span class="en-US">Table alias name</span>
     * <span class="zh-CN">数据表别名</span>
     */
    @XmlElement(name = "alias_name")
    private String aliasName;
    /**
     * <span class="en-US">Entity class</span>
     * <span class="zh-CN">实体类</span>
     */
    @XmlElement(name = "entity_class")
    @XmlJavaTypeAdapter(ClassStringAdapter.class)
    private Class<?> entityClass;
    /**
     * <span class="en-US">Query join instance list</span>
     * <span class="zh-CN">关联查询实例对象列表</span>
     */
    @XmlElement(name = "join_table")
    @XmlElementWrapper(name = "join_list")
    private List<JoinTable> joinList;
    /**
     * <span class="en-US">Query item instance list</span>
     * <span class="zh-CN">查询项目实例对象列表</span>
     */
    @XmlElementWrapper(name = "item_list")
	@XmlElementRefs({
			@XmlElementRef(name = "query_column", type = QueryColumn.class),
			@XmlElementRef(name = "query_constant", type = QueryConstant.class),
			@XmlElementRef(name = "query_function", type = QueryFunction.class)
	})
    private List<QueryItem> itemList;
    /**
     * <span class="en-US">Query condition instance list</span>
     * <span class="zh-CN">查询条件实例对象列表</span>
     */
    @XmlElement(name = "query_condition")
    @XmlElementWrapper(name = "condition_list")
    private List<QueryCondition> conditionList;

    /**
     * <h3 class="en-US">Constructor method for query table define</h3>
     * <h3 class="zh-CN">查询数据表信息的构造方法</h3>
     */
    public QueryTable() {
        this.joinList = new ArrayList<>();
        this.itemList = new ArrayList<>();
        this.conditionList = new ArrayList<>();
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
     * <h3 class="en-US">Getter method for entity class</h3>
     * <h3 class="zh-CN">实体类的Getter方法</h3>
     *
     * @return <span class="en-US">Entity class</span>
     * <span class="zh-CN">实体类</span>
     */
    public Class<?> getEntityClass() {
        return entityClass;
    }

    /**
     * <h3 class="en-US">Setter method for entity class</h3>
     * <h3 class="zh-CN">实体类的Setter方法</h3>
     *
     * @param entityClass <span class="en-US">Entity class</span>
     *                    <span class="zh-CN">实体类</span>
     */
    public void setEntityClass(Class<?> entityClass) {
        this.entityClass = entityClass;
    }

    /**
     * <h3 class="en-US">Getter method for query join instance list</h3>
     * <h3 class="zh-CN">关联查询实例对象列表的Getter方法</h3>
     *
     * @return <span class="en-US">Query join instance list</span>
     * <span class="zh-CN">关联查询实例对象列表</span>
     */
    public List<JoinTable> getJoinList() {
        return joinList;
    }

    /**
     * <h3 class="en-US">Setter method for query join instance list</h3>
     * <h3 class="zh-CN">关联查询实例对象列表的Setter方法</h3>
     *
     * @param joinList <span class="en-US">Query join instance list</span>
     *                 <span class="zh-CN">关联查询实例对象列表</span>
     */
    public void setJoinList(List<JoinTable> joinList) {
        this.joinList = joinList;
    }

    /**
     * <h3 class="en-US">Getter method for query item instance list</h3>
     * <h3 class="zh-CN">查询项目实例对象列表的Getter方法</h3>
     *
     * @return <span class="en-US">Query item instance list</span>
     * <span class="zh-CN">查询项目实例对象列表</span>
     */
    public List<QueryItem> getItemList() {
        return itemList;
    }

    /**
     * <h3 class="en-US">Setter method for query item instance list</h3>
     * <h3 class="zh-CN">查询项目实例对象列表的Setter方法</h3>
     *
     * @param itemList <span class="en-US">Query item instance list</span>
     *                 <span class="zh-CN">查询项目实例对象列表</span>
     */
    public void setItemList(List<QueryItem> itemList) {
        this.itemList = itemList;
    }

    /**
     * <h3 class="en-US">Getter method for query condition instance list</h3>
     * <h3 class="zh-CN">查询条件实例对象列表的Getter方法</h3>
     *
     * @return <span class="en-US">Query condition instance list</span>
     * <span class="zh-CN">查询条件实例对象列表</span>
     */
    public List<QueryCondition> getConditionList() {
        return conditionList;
    }

    /**
     * <h3 class="en-US">Setter method for query condition instance list</h3>
     * <h3 class="zh-CN">查询条件实例对象列表的Setter方法</h3>
     *
     * @param conditionList <span class="en-US">Query condition instance list</span>
     *                      <span class="zh-CN">查询条件实例对象列表</span>
     */
    public void setConditionList(List<QueryCondition> conditionList) {
        this.conditionList = conditionList;
    }
}
