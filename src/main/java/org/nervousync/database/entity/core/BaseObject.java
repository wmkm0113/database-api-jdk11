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
package org.nervousync.database.entity.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.MappedSuperclass;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import org.nervousync.beans.core.BeanObject;

import java.util.ArrayList;
import java.util.List;

/**
 * <h2 class="en-US">Abstract Entity Class</h2>
 * <h2 class="zh-CN">实体类抽象父类</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Jan 9, 2018 10:21:06 $
 */
@MappedSuperclass
@XmlAccessorType(XmlAccessType.NONE)
public abstract class BaseObject extends BeanObject {
    /**
     * <span class="en-US">Serial version UID</span>
     * <span class="zh-CN">序列化UID</span>
     */
    private static final long serialVersionUID = -1860456342847902560L;
    /**
     * <span class="en-US">New record status</span>
     * <span class="zh-CN">新纪录状态</span>
     */
    @JsonIgnore
    private boolean newObject = Boolean.TRUE;
    /**
     * <span class="en-US">Update record status</span>
     * <span class="zh-CN">更新纪录状态</span>
     */
    @JsonIgnore
    private Boolean forUpdate = null;
    /**
     * <span class="en-US">Transactional identify code</span>
     * <span class="zh-CN">事务识别代码</span>
     */
    @JsonIgnore
    private Long transactionalCode = null;
    /**
     * <span class="en-US">Modified column name list</span>
     * <span class="zh-CN">修改的列名列表</span>
     */
    @JsonIgnore
    private final List<String> modifiedColumns = new ArrayList<>();
    /**
     * <span class="en-US">Loaded field list</span>
     * <span class="zh-CN">已加载属性名列表</span>
     */
    @JsonIgnore
    private final List<String> loadedFields = new ArrayList<>();

    /**
     * <h3 class="en-US">Getter method for new record status</h3>
     * <h3 class="zh-CN">新纪录状态的Getter方法</h3>
     *
     * @return <span class="en-US">New record status</span>
     * <span class="zh-CN">新纪录状态</span>
     */
    public final boolean isNewObject() {
        return this.newObject;
    }

    /**
     * <h3 class="en-US">Getter method for update record status</h3>
     * <h3 class="zh-CN">更新纪录状态的Getter方法</h3>
     *
     * @return <span class="en-US">Update record status</span>
     * <span class="zh-CN">更新纪录状态</span>
     */
    public final boolean getForUpdate() {
        return this.forUpdate != null && this.forUpdate;
    }

    /**
     * <h3 class="en-US">Setter method for update record status</h3>
     * <h3 class="zh-CN">更新纪录状态的Setter方法</h3>
     *
     * @param forUpdate <span class="en-US">Update record status</span>
     *                  <span class="zh-CN">更新纪录状态</span>
     */
    public final void setForUpdate(final Boolean forUpdate) {
        if (this.forUpdate == null) {
            this.forUpdate = forUpdate;
            this.newObject = Boolean.FALSE;
        }
    }

    /**
     * <h3 class="en-US">Getter method for transactional identify code</h3>
     * <h3 class="zh-CN">事务识别代码的Getter方法</h3>
     *
     * @return <span class="en-US">Transactional identify code</span>
     * <span class="zh-CN">事务识别代码</span>
     */
    public final Long getTransactionalCode() {
        return transactionalCode;
    }

    /**
     * <h3 class="en-US">Setter method for transactional identify code</h3>
     * <h3 class="zh-CN">事务识别代码的Setter方法</h3>
     *
     * @param transactionalCode <span class="en-US">Transactional identify code</span>
     *                          <span class="zh-CN">事务识别代码</span>
     */
    public final void setTransactionalCode(final Long transactionalCode) {
        if (this.transactionalCode == null) {
            this.transactionalCode = transactionalCode;
        }
    }

    /**
     * <h3 class="en-US">Retrieve the modified columns identify code list</h3>
     * <h3 class="zh-CN">检索已修改的列识别代码列表</h3>
     *
     * @return <span class="en-US">Modified column name list</span>
     * <span class="zh-CN">修改的列名列表</span>
     */
    public final List<String> modifiedColumns() {
        return modifiedColumns;
    }

    /**
     * <h3 class="en-US">Check if the data of the current record has been modified</h3>
     * <h3 class="zh-CN">检查当前记录的数据是否被修改</h3>
     *
     * @return <span class="en-US">Check result</span>
     * <span class="zh-CN">检查结果</span>
     */
    public final boolean dataModified() {
        return this.isNewObject() || (this.getForUpdate() && !this.modifiedColumns.isEmpty());
    }

    /**
     * <h3 class="en-US">Checks if the given field identification code has been loaded</h3>
     * <h3 class="zh-CN">检查给定的字段识别代码是否已经加载</h3>
     *
     * @param fieldName <span class="en-US">Field identification code</span>
     *                  <span class="zh-CN">字段识别代码</span>
     * @return <span class="en-US">Check result</span>
     * <span class="zh-CN">检查结果</span>
     */
    public final boolean loadedField(final String fieldName) {
        return this.loadedFields.contains(fieldName);
    }

    /**
     * <h3 class="en-US">Load the field value according to the given field identification code</h3>
     * <h3 class="zh-CN">根据给定的字段识别代码加载字段值</h3>
     *
     * @param fieldName <span class="en-US">Field identification code</span>
     *                  <span class="zh-CN">字段识别代码</span>
     */
    public final void loadField(final String fieldName) {
        if (this.loadedFields.contains(fieldName)) {
            return;
        }
        this.loadedFields.add(fieldName);
    }

    /**
     * <h3 class="en-US">Adds the given field identification code to the list of modified fields</h3>
     * <h3 class="zh-CN">添加给定的字段识别代码到已修改字段列表中</h3>
     *
     * @param fieldName <span class="en-US">Field identification code</span>
     *                  <span class="zh-CN">字段识别代码</span>
     */
    public final void modifyField(final String fieldName) {
        if (this.modifiedColumns.contains(fieldName)) {
            return;
        }
        this.modifiedColumns.add(fieldName);
    }
}
