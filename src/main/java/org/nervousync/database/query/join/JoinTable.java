/*
 * Copyright © 2003 Nervousync® Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of
 * Nervousync Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with Nervousync Studio.
 */

package org.nervousync.database.query.join;

import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.nervousync.beans.converter.impl.basic.ClassStringAdapter;
import org.nervousync.beans.core.BeanObject;
import org.nervousync.database.enumerations.join.JoinType;

import java.util.ArrayList;
import java.util.List;

/**
 * <h2 class="en-US">Query join define</h2>
 * <h2 class="zh-CN">查询关联信息定义</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: 2/8/2021 01:33 PM $
 */
@XmlType(name = "join_table", namespace = "https://nervousync.org/schemas/query")
@XmlAccessorType(XmlAccessType.NONE)
public final class JoinTable extends BeanObject {
    /**
     * <span class="en-US">Serial version UID</span>
     * <span class="zh-CN">序列化UID</span>
     */
    private static final long serialVersionUID = -2242693422943333570L;
    /**
     * <span class="en-US">Table join type</span>
     * <span class="zh-CN">数据表关联类型</span>
     */
    @XmlElement(name = "join_type")
    private JoinType joinType;
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
     * <span class="en-US">Join columns list</span>
     * <span class="zh-CN">关联列信息列表</span>
     */
    @XmlElement(name = "join_column")
    @XmlElementWrapper(name = "join_column_list")
    private List<JoinColumn> joinColumns;
    /**
     * <span class="en-US">Join tables list</span>
     * <span class="zh-CN">关联表信息列表</span>
     */
    @XmlElement(name = "join_table")
    @XmlElementWrapper(name = "join_list")
    private List<JoinTable> joinTables;

    /**
     * <h3 class="en-US">Constructor method for query join define</h3>
     * <h3 class="zh-CN">查询关联信息定义的构造方法</h3>
     */
    public JoinTable() {
        this.joinColumns = new ArrayList<>();
        this.joinTables = new ArrayList<>();
    }

    /**
     * <h3 class="en-US">Getter method for table join type</h3>
     * <h3 class="zh-CN">数据表关联类型的Getter方法</h3>
     *
     * @return <span class="en-US">Table join type</span>
     * <span class="zh-CN">数据表关联类型</span>
     */
    public JoinType getJoinType() {
        return joinType;
    }

    /**
     * <h3 class="en-US">Setter method for table join type</h3>
     * <h3 class="zh-CN">数据表关联类型的Setter方法</h3>
     *
     * @param joinType <span class="en-US">Table join type</span>
     *                 <span class="zh-CN">数据表关联类型</span>
     */
    public void setJoinType(JoinType joinType) {
        this.joinType = joinType;
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
     * <h3 class="en-US">Getter method for join columns list</h3>
     * <h3 class="zh-CN">关联列信息列表的Getter方法</h3>
     *
     * @return <span class="en-US">Join columns list</span>
     * <span class="zh-CN">关联列信息列表</span>
     */
    public List<JoinColumn> getJoinColumns() {
        return joinColumns;
    }

    /**
     * <h3 class="en-US">Setter method for join columns list</h3>
     * <h3 class="zh-CN">关联列信息列表的Setter方法</h3>
     *
     * @param joinColumns <span class="en-US">Join columns list</span>
     *                    <span class="zh-CN">关联列信息列表</span>
     */
    public void setJoinColumns(List<JoinColumn> joinColumns) {
        this.joinColumns = joinColumns;
    }

    /**
     * <h3 class="en-US">Getter method for join tables list</h3>
     * <h3 class="zh-CN">关联表信息列表的Getter方法</h3>
     *
     * @return <span class="en-US">Join tables list</span>
     * <span class="zh-CN">关联表信息列表</span>
     */
    public List<JoinTable> getJoinTables() {
        return joinTables;
    }

    /**
     * <h3 class="en-US">Setter method for join tables list</h3>
     * <h3 class="zh-CN">关联表信息列表的Setter方法</h3>
     *
     * @param joinTables <span class="en-US">Join tables list</span>
     *                   <span class="zh-CN">关联表信息列表</span>
     */
    public void setJoinTables(List<JoinTable> joinTables) {
        this.joinTables = joinTables;
    }
}
