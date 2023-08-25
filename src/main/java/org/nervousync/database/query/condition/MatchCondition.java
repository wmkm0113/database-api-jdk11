/*
 * Copyright © 2003 Nervousync® Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of
 * Nervousync Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with Nervousync Studio.
 */
package org.nervousync.database.query.condition;

import jakarta.xml.bind.annotation.*;
import org.nervousync.beans.core.BeanObject;
import org.nervousync.commons.Globals;
import org.nervousync.database.enumerations.query.ConditionType;

/**
 * <h2 class="en-US">Match condition define</h2>
 * <h2 class="zh-CN">匹配条件信息定义</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Nov 1, 2020 17:37:44 $
 */
@XmlType(name = "match_condition", namespace = "https://nervousync.org/schemas/query")
@XmlAccessorType(XmlAccessType.NONE)
public final class MatchCondition extends BeanObject {
    /**
     * <span class="en-US">Serial version UID</span>
     * <span class="zh-CN">序列化UID</span>
     */
    private static final long serialVersionUID = -3911334746912919200L;
    /**
     * <span class="en-US">Condition type</span>
     * <span class="zh-CN">匹配类型</span>
     */
    @XmlElement(name = "condition_type")
    private ConditionType conditionType;
    /**
     * <span class="en-US">Current query code</span>
     * <span class="zh-CN">当前查询代码</span>
     */
    @XmlElement(name = "query_code")
    private long queryCode = Globals.DEFAULT_VALUE_LONG;
    /**
     * <span class="en-US">Identify key</span>
     * <span class="zh-CN">识别代码</span>
     */
    @XmlElement(name = "identify_key")
    private String identifyKey = Globals.DEFAULT_VALUE_STRING;
    /**
     * <span class="en-US">Match values array</span>
     * <span class="zh-CN">匹配值数组</span>
     */
    @XmlElement(name = "value")
    @XmlElementWrapper(name = "match_values")
    private Object[] matchValues;

    /**
     * <h3 class="en-US">Constructor method for match condition define</h3>
     * <h3 class="zh-CN">匹配条件信息定义的构造方法</h3>
     */
    public MatchCondition() {
    }

    /**
     * <h3 class="en-US">Static method for generate match condition instance which condition type is dynamic</h3>
     * <h3 class="zh-CN">静态方法用于生成动态类型的查询条件信息</h3>
     *
     * @param queryCode   <span class="en-US">Match value from query identify code</span>
     *                    <span class="zh-CN">匹配结果来自的查询识别代码</span>
     * @param identifyKey <span class="en-US">Identify key</span>
     *                    <span class="zh-CN">识别代码</span>
     * @return <span class="en-US">Generated object instance</span>
     * <span class="zh-CN">生成的对象实例</span>
     */
    public static MatchCondition dynamic(final long queryCode, final String identifyKey) {
        MatchCondition matchCondition = new MatchCondition();
        matchCondition.setConditionType(ConditionType.DYNAMIC);
        matchCondition.setQueryCode(queryCode);
        matchCondition.setIdentifyKey(identifyKey);
        return matchCondition;
    }

    /**
     * <h3 class="en-US">Static method for generate match condition instance which condition type is constant</h3>
     * <h3 class="zh-CN">静态方法用于生成静态值类型的查询条件信息</h3>
     *
     * @param matchValues <span class="en-US">Match values array</span>
     *                    <span class="zh-CN">匹配值数组</span>
     * @return <span class="en-US">Generated object instance</span>
     * <span class="zh-CN">生成的对象实例</span>
     */
    public static MatchCondition condition(final Object... matchValues) {
        MatchCondition matchCondition = new MatchCondition();
        matchCondition.setConditionType(ConditionType.CONSTANT);
        matchCondition.setMatchValues(matchValues);
        return matchCondition;
    }

    /**
     * <h3 class="en-US">Static method for generate match condition instance which condition type is group</h3>
     * <h3 class="zh-CN">静态方法用于生成条件组类型的查询条件信息</h3>
     *
     * @param queryConditions <span class="en-US">Query conditions instance array</span>
     *                        <span class="zh-CN">查询条件信息数组</span>
     * @return <span class="en-US">Generated object instance</span>
     * <span class="zh-CN">生成的对象实例</span>
     */
    public static MatchCondition group(final QueryCondition... queryConditions) {
        MatchCondition matchCondition = new MatchCondition();
        matchCondition.setConditionType(ConditionType.GROUP);
        matchCondition.setMatchValues(queryConditions);
        return matchCondition;
    }

    /**
     * <h3 class="en-US">Getter method for condition type</h3>
     * <h3 class="zh-CN">匹配类型的Getter方法</h3>
     *
     * @return <span class="en-US">Condition type</span>
     * <span class="zh-CN">匹配类型</span>
     */
    public ConditionType getConditionType() {
        return conditionType;
    }

    /**
     * <h3 class="en-US">Setter method for condition type</h3>
     * <h3 class="zh-CN">匹配类型的Setter方法</h3>
     *
     * @param conditionType <span class="en-US">Condition type</span>
     *                      <span class="zh-CN">匹配类型</span>
     */
    public void setConditionType(ConditionType conditionType) {
        this.conditionType = conditionType;
    }

    /**
     * <h3 class="en-US">Getter method for current query code</h3>
     * <h3 class="zh-CN">当前查询代码的Getter方法</h3>
     *
     * @return <span class="en-US">Current query code</span>
     * <span class="zh-CN">当前查询代码</span>
     */
    public long getQueryCode() {
        return queryCode;
    }

    /**
     * <h3 class="en-US">Setter method for current query code</h3>
     * <h3 class="zh-CN">当前查询代码的Setter方法</h3>
     *
     * @param queryCode <span class="en-US">Current query code</span>
     *                  <span class="zh-CN">当前查询代码</span>
     */
    public void setQueryCode(long queryCode) {
        this.queryCode = queryCode;
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
     * <h3 class="en-US">Getter method for match values array</h3>
     * <h3 class="zh-CN">匹配值数组的Getter方法</h3>
     *
     * @return <span class="en-US">Match values array</span>
     * <span class="zh-CN">匹配值数组</span>
     */
    public Object[] getMatchValues() {
        return matchValues;
    }

    /**
     * <h3 class="en-US">Setter method for match values array</h3>
     * <h3 class="zh-CN">匹配值数组的Setter方法</h3>
     *
     * @param matchValues <span class="en-US">Match values array</span>
     *                    <span class="zh-CN">匹配值数组</span>
     */
    public void setMatchValues(Object[] matchValues) {
        this.matchValues = matchValues;
    }
}
