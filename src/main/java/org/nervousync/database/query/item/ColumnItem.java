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
package org.nervousync.database.query.item;

import jakarta.xml.bind.annotation.*;
import org.nervousync.database.enumerations.query.ItemType;
import org.nervousync.database.query.core.AbstractItem;

/**
 * <h2 class="en-US">Query column information define</h2>
 * <h2 class="zh-CN">查询数据列信息定义</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Oct 9, 2020 11:42:19 $
 */
@XmlType(name = "column_item", namespace = "https://nervousync.org/schemas/query")
@XmlRootElement(name = "column_item", namespace = "https://nervousync.org/schemas/query")
@XmlAccessorType(XmlAccessType.NONE)
public final class ColumnItem extends AbstractItem {
    /**
     * <span class="en-US">Serial version UID</span>
     * <span class="zh-CN">序列化UID</span>
     */
    private static final long serialVersionUID = -9033998209945104277L;
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
     * <span class="en-US">Column distinct</span>
     * <span class="zh-CN">数据列去重</span>
     */
    @XmlElement
    private boolean distinct;

    /**
     * <h3 class="en-US">Constructor method for query column information define</h3>
     * <h3 class="zh-CN">查询数据列信息定义的构造方法</h3>
     */
    public ColumnItem() {
        super(ItemType.COLUMN);
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
     * <h3 class="en-US">Getter method for column distinct</h3>
     * <h3 class="zh-CN">数据列去重的Getter方法</h3>
     *
     * @return <span class="en-US">Column distinct</span>
     * <span class="zh-CN">数据列去重</span>
     */
    public boolean isDistinct() {
        return distinct;
    }

    /**
     * <h3 class="en-US">Setter method for column distinct</h3>
     * <h3 class="zh-CN">数据列去重的Setter方法</h3>
     *
     * @param distinct <span class="en-US">Column distinct</span>
     *                 <span class="zh-CN">数据列去重</span>
     */
    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }
}
