/*
 * Copyright © 2003 Nervousync® Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of
 * Nervousync Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with Nervousync Studio.
 */
package org.nervousync.database.query.join;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import org.nervousync.beans.core.BeanObject;

/**
 * <h2 class="en-US">Join column define</h2>
 * <h2 class="zh-CN">关联列信息定义</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Feb 17, 2021 16:35:51 $
 */
@XmlType(name = "join_column", namespace = "https://nervousync.org/schemas/query")
@XmlAccessorType(XmlAccessType.NONE)
public final class JoinColumn extends BeanObject {
    /**
     * <span class="en-US">Serial version UID</span>
     * <span class="zh-CN">序列化UID</span>
     */
    private static final long serialVersionUID = 8163690027798389179L;
    /**
     * <span class="en-US">Join column identify code</span>
     * <span class="zh-CN">关联列识别代码</span>
     */
    @XmlElement(name = "join_key")
    private String joinKey;
    /**
     * <span class="en-US">Reference column identify code</span>
     * <span class="zh-CN">目标列识别代码</span>
     */
    @XmlElement(name = "reference_key")
    private String referenceKey;

    /**
     * <h3 class="en-US">Constructor method for join column define</h3>
     * <h3 class="zh-CN">关联列信息定义的构造方法</h3>
     */
    public JoinColumn() {
    }

    /**
     * <h3 class="en-US">Getter method for join column identify code</h3>
     * <h3 class="zh-CN">关联列识别代码的Getter方法</h3>
     *
     * @return <span class="en-US">Join column identify code</span>
     * <span class="zh-CN">关联列识别代码</span>
     */
    public String getJoinKey() {
        return joinKey;
    }

    /**
     * <h3 class="en-US">Setter method for join column identify code</h3>
     * <h3 class="zh-CN">关联列识别代码的Setter方法</h3>
     *
     * @param joinKey <span class="en-US">Join column identify code</span>
     *                <span class="zh-CN">关联列识别代码</span>
     */
    public void setJoinKey(String joinKey) {
        this.joinKey = joinKey;
    }

    /**
     * <h3 class="en-US">Getter method for reference column identify code</h3>
     * <h3 class="zh-CN">目标列识别代码的Getter方法</h3>
     *
     * @return <span class="en-US">Reference column identify code</span>
     * <span class="zh-CN">目标列识别代码</span>
     */
    public String getReferenceKey() {
        return referenceKey;
    }

    /**
     * <h3 class="en-US">Setter method for reference column identify code</h3>
     * <h3 class="zh-CN">目标列识别代码的Setter方法</h3>
     *
     * @param referenceKey <span class="en-US">Reference column identify code</span>
     *                     <span class="zh-CN">目标列识别代码</span>
     */
    public void setReferenceKey(String referenceKey) {
        this.referenceKey = referenceKey;
    }
}
