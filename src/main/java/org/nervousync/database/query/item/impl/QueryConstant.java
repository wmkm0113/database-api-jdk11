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
package org.nervousync.database.query.item.impl;

import jakarta.xml.bind.annotation.*;
import org.nervousync.database.enumerations.query.ItemType;
import org.nervousync.database.query.item.QueryItem;

import java.util.Objects;

/**
 * <h2 class="en-US">Query constant information define</h2>
 * <h2 class="zh-CN">查询数据常量信息定义</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Oct 9, 2020 11:42:19 $
 */
@XmlRootElement(name = "query_constant", namespace = "https://nervousync.org/schemas/query")
@XmlAccessorType(XmlAccessType.NONE)
public class QueryConstant extends QueryItem {
    /**
     * <span class="en-US">Serial version UID</span>
     * <span class="zh-CN">序列化UID</span>
     */
    private static final long serialVersionUID = -3349412013945003287L;
    /**
     * <span class="en-US">Constant value</span>
     * <span class="zh-CN">常量值</span>
     */
    @XmlElement(name = "constant_value")
    private Object constantValue = null;

    /**
     * <h3 class="en-US">Constructor method for query constant information define</h3>
     * <h3 class="zh-CN">查询数据常量信息定义的构造方法</h3>
     */
    public QueryConstant() {
        super(ItemType.CONSTANT);
    }

    /**
     * (Non-javadoc)
     *
     * @see QueryItem#match(QueryItem)
     */
    @Override
    public boolean match(QueryItem queryItem) {
        if (queryItem == null) {
            return Boolean.FALSE;
        }
        if (ItemType.CONSTANT.equals(queryItem.getItemType())) {
            return Objects.equals(this.constantValue, ((QueryConstant) queryItem).getConstantValue());
        }
        return Boolean.FALSE;
    }

    /**
     * <h3 class="en-US">Getter method for constant value</h3>
     * <h3 class="zh-CN">常量值的Getter方法</h3>
     *
     * @return <span class="en-US">Constant value</span>
     * <span class="zh-CN">常量值</span>
     */
    public Object getConstantValue() {
        return constantValue;
    }

    /**
     * <h3 class="en-US">Setter method for constant value</h3>
     * <h3 class="zh-CN">常量值的Setter方法</h3>
     *
     * @param constantValue <span class="en-US">Constant value</span>
     *                      <span class="zh-CN">常量值</span>
     */
    public void setConstantValue(Object constantValue) {
        this.constantValue = constantValue;
    }
}
