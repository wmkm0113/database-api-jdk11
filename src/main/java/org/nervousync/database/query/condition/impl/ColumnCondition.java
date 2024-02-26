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

package org.nervousync.database.query.condition.impl;

import jakarta.xml.bind.annotation.*;
import org.nervousync.database.query.condition.Condition;
import org.nervousync.database.query.param.AbstractParameter;
import org.nervousync.database.query.param.impl.*;

/**
 * <h2 class="en-US">Query column condition information define</h2>
 * <h2 class="zh-CN">数据列查询匹配条件定义</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Oct 9, 2020 19:12:02 $
 */
@XmlType(name = "column_condition", namespace = "https://nervousync.org/schemas/query")
@XmlRootElement(name = "column_condition", namespace = "https://nervousync.org/schemas/query")
@XmlAccessorType(XmlAccessType.NONE)
public final class ColumnCondition extends Condition {

    /**
     * <span class="en-US">Serial version UID</span>
     * <span class="zh-CN">序列化UID</span>
     */
    private static final long serialVersionUID = 7198216674051216778L;

    /**
     * <span class="en-US">The entity class to which the data column belongs</span>
     * <span class="zh-CN">数据列所属的实体类</span>
     */
    @XmlElement(name = "entity_class")
    private Class<?> entityClass;
    /**
     * <span class="en-US">Identify key</span>
     * <span class="zh-CN">识别代码</span>
     */
    @XmlElement(name = "identify_key")
    private String identifyKey;
    /**
     * <span class="en-US">Match condition</span>
     * <span class="zh-CN">匹配结果</span>
     */
    @XmlElements({
            @XmlElement(name = "arrays_parameter", type = ArraysParameter.class),
            @XmlElement(name = "column_parameter", type = ColumnParameter.class),
            @XmlElement(name = "constant_parameter", type = ConstantParameter.class),
            @XmlElement(name = "function_parameter", type = FunctionParameter.class),
            @XmlElement(name = "query_parameter", type = QueryParameter.class),
            @XmlElement(name = "ranges_parameter", type = RangesParameter.class)
    })
    private AbstractParameter<?> conditionParameter;

    /**
     * <h3 class="en-US">Constructor method for query column condition information define</h3>
     * <h3 class="zh-CN">数据列查询匹配条件定义的构造方法</h3>
     */
    public ColumnCondition() {
    }

    /**
     * <h3 class="en-US">Getter method for the entity class to which the data column belongs</h3>
     * <h3 class="zh-CN">数据列所属的实体类的Getter方法</h3>
     *
     * @return <span class="en-US">The entity class to which the data column belongs</span>
     * <span class="zh-CN">数据列所属的实体类</span>
     */
    public Class<?> getEntityClass() {
        return entityClass;
    }

    /**
     * <h3 class="en-US">Setter method for the entity class to which the data column belongs</h3>
     * <h3 class="zh-CN">数据列所属的实体类的Setter方法</h3>
     *
     * @param entityClass <span class="en-US">The entity class to which the data column belongs</span>
     *                    <span class="zh-CN">数据列所属的实体类</span>
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
     * <h3 class="en-US">Getter method for match condition</h3>
     * <h3 class="zh-CN">匹配结果的Getter方法</h3>
     *
     * @return <span class="en-US">Match condition</span>
     * <span class="zh-CN">匹配结果</span>
     */
    public AbstractParameter<?> getConditionParameter() {
        return conditionParameter;
    }

    /**
     * <h3 class="en-US">Setter method for match condition</h3>
     * <h3 class="zh-CN">匹配结果的Setter方法</h3>
     *
     * @param conditionParameter <span class="en-US">Match condition</span>
     *                           <span class="zh-CN">匹配结果</span>
     */
    public void setConditionParameter(AbstractParameter<?> conditionParameter) {
        this.conditionParameter = conditionParameter;
    }
}
