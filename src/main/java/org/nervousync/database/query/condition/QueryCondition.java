/*
 * Copyright © 2003 Nervousync® Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of
 * Nervousync Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with Nervousync Studio.
 */
package org.nervousync.database.query.condition;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.nervousync.beans.converter.impl.basic.ClassStringAdapter;
import org.nervousync.beans.core.BeanObject;
import org.nervousync.database.enumerations.query.operate.ConditionCode;
import org.nervousync.database.enumerations.query.operate.ConnectionCode;
import org.nervousync.utils.ObjectUtils;

/**
 * <h2 class="en-US">Query condition define</h2>
 * <h2 class="zh-CN">查询条件信息定义</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Oct 28, 2020 17:35:46 $
 */
@XmlType(name = "query_condition", namespace = "https://nervousync.org/schemas/query")
@XmlAccessorType(XmlAccessType.NONE)
public final class QueryCondition extends BeanObject {
    /**
     * <span class="en-US">Serial version UID</span>
     * <span class="zh-CN">序列化UID</span>
     */
    private static final long serialVersionUID = 335859744880763915L;
    /**
     * <span class="en-US">Query connection code</span>
     * <span class="zh-CN">查询条件连接代码</span>
     */
    @XmlElement(name = "connection_code")
    private ConnectionCode connectionCode;
    /**
     * <span class="en-US">Query condition code</span>
     * <span class="zh-CN">查询条件运算代码</span>
     */
    @XmlElement(name = "condition_code")
    private ConditionCode conditionCode;
    /**
     * <span class="en-US">Entity class</span>
     * <span class="zh-CN">实体类</span>
     */
    @XmlElement(name = "entity_class")
    @XmlJavaTypeAdapter(ClassStringAdapter.class)
    private Class<?> entityClass;
    /**
     * <span class="en-US">Identify key</span>
     * <span class="zh-CN">识别代码</span>
     */
    @XmlElement(name = "identify_key")
    private String identifyKey;
    /**
     * <span class="en-US">Query match condition</span>
     * <span class="zh-CN">查询条件匹配结果</span>
     */
    @XmlElement(name = "match_condition")
    private MatchCondition matchCondition;

    /**
     * <h3 class="en-US">Constructor method for query condition define</h3>
     * <h3 class="zh-CN">查询条件信息定义的构造方法</h3>
     */
    public QueryCondition() {
    }

    /**
     * <h3 class="en-US">Getter method for query connection code</h3>
     * <h3 class="zh-CN">查询条件连接代码的Getter方法</h3>
     *
     * @return <span class="en-US">Query connection code</span>
     * <span class="zh-CN">查询条件连接代码</span>
     */
    public ConnectionCode getConnectionCode() {
        return connectionCode;
    }

    /**
     * <h3 class="en-US">Setter method for query connection code</h3>
     * <h3 class="zh-CN">查询条件连接代码的Setter方法</h3>
     *
     * @param connectionCode <span class="en-US">Query connection code</span>
     *                       <span class="zh-CN">查询条件连接代码</span>
     */
    public void setConnectionCode(ConnectionCode connectionCode) {
        this.connectionCode = connectionCode;
    }

    /**
     * <h3 class="en-US">Getter method for query condition code</h3>
     * <h3 class="zh-CN">查询条件运算代码的Getter方法</h3>
     *
     * @return <span class="en-US">Query condition code</span>
     * <span class="zh-CN">查询条件运算代码</span>
     */
    public ConditionCode getConditionCode() {
        return conditionCode;
    }

    /**
     * <h3 class="en-US">Setter method for query condition code</h3>
     * <h3 class="zh-CN">查询条件运算代码的Setter方法</h3>
     *
     * @param conditionCode <span class="en-US">Query condition code</span>
     *                      <span class="zh-CN">查询条件运算代码</span>
     */
    public void setConditionCode(ConditionCode conditionCode) {
        this.conditionCode = conditionCode;
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
     * <h3 class="en-US">Getter method for identify key</h3>
     * <h3 class="zh-CN">识别代码的Getter方法</h3>
     *
     * @return <span class="en-US">Identify key</span>
     * <span class="zh-CN">识别代码</span>
     */
    public String getIdentifyKey() {
        return identifyKey;
    }

    /**
     * <h3 class="en-US">Setter method for identify key</h3>
     * <h3 class="zh-CN">识别代码的Setter方法</h3>
     *
     * @param identifyKey <span class="en-US">Identify key</span>
     *                    <span class="zh-CN">识别代码</span>
     */
    public void setIdentifyKey(String identifyKey) {
        this.identifyKey = identifyKey;
    }

    /**
     * <h3 class="en-US">Getter method for query match condition</h3>
     * <h3 class="zh-CN">查询条件匹配结果的Getter方法</h3>
     *
     * @return <span class="en-US">Query match condition</span>
     * <span class="zh-CN">查询条件匹配结果</span>
     */
    public MatchCondition getMatchCondition() {
        return matchCondition;
    }

    /**
     * <h3 class="en-US">Setter method for query match condition</h3>
     * <h3 class="zh-CN">查询条件匹配结果的Setter方法</h3>
     *
     * @param matchCondition <span class="en-US">Query match condition</span>
     *                       <span class="zh-CN">查询条件匹配结果</span>
     */
    public void setMatchCondition(MatchCondition matchCondition) {
        this.matchCondition = matchCondition;
    }

    /**
     * <h3 class="en-US">Checks whether the given parameter information matches the current information</h3>
     * <h3 class="zh-CN">检查给定的参数信息是否与当前信息匹配</h3>
     *
     * @param connectionCode <span class="en-US">Query connection code</span>
     *                       <span class="zh-CN">查询条件连接代码</span>
     * @param conditionCode  <span class="en-US">Query condition code</span>
     *                       <span class="zh-CN">查询条件运算代码</span>
     * @param identifyKey    <span class="en-US">Identify key</span>
     *                       <span class="zh-CN">识别代码</span>
     * @param matchCondition <span class="en-US">Query match condition</span>
     *                       <span class="zh-CN">查询条件匹配结果</span>
     * @return <span class="en-US">Match result</span>
     * <span class="zh-CN">匹配结果</span>
     */
    public boolean match(final ConnectionCode connectionCode, final ConditionCode conditionCode,
                         final String identifyKey, final MatchCondition matchCondition) {
        return ObjectUtils.nullSafeEquals(this.connectionCode, connectionCode)
                && ObjectUtils.nullSafeEquals(this.conditionCode, conditionCode)
                && ObjectUtils.nullSafeEquals(this.identifyKey, identifyKey)
                && ObjectUtils.nullSafeEquals(this.matchCondition, matchCondition);
    }
}
