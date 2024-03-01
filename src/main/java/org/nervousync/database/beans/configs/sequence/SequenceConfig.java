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
package org.nervousync.database.beans.configs.sequence;

import jakarta.annotation.Nonnull;
import jakarta.xml.bind.annotation.*;
import org.nervousync.beans.core.BeanObject;
import org.nervousync.commons.Globals;
import org.nervousync.database.annotations.sequence.SequenceGenerator;

/**
 * <h2 class="en-US">Sequence configure information</h2>
 * <h2 class="zh-CN">序列生成器配置信息</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: May 15, 2012 17:54:44 $
 */
@XmlType(name = "sequence_config")
@XmlRootElement(name = "sequence_config")
@XmlAccessorType(XmlAccessType.NONE)
public final class SequenceConfig extends BeanObject {
    /**
     * <span class="en-US">Serial version UID</span>
     * <span class="zh-CN">序列化UID</span>
     */
    private static final long serialVersionUID = -79385184004859280L;
    /**
     * <span class="en-US">Sequence name</span>
     * <span class="zh-CN">序列名称</span>
     */
    @XmlElement(name = "sequence_name")
    private String sequenceName = Globals.DEFAULT_VALUE_STRING;
    /**
     * <span class="en-US">Sequence minimum value</span>
     * <span class="zh-CN">序列最小值</span>
     */
    @XmlElement(name = "minimum_value")
    private int minValue = Globals.DEFAULT_VALUE_INT;
    /**
     * <span class="en-US">Sequence maximum value</span>
     * <span class="zh-CN">序列最大值</span>
     */
    @XmlElement(name = "maximum_value")
    private int maxValue = Globals.DEFAULT_VALUE_INT;
    /**
     * <span class="en-US">Sequence step value</span>
     * <span class="zh-CN">序列步进值</span>
     */
    @XmlElement
    private int step = Globals.DEFAULT_VALUE_INT;
    /**
     * <span class="en-US">Sequence current value</span>
     * <span class="zh-CN">当前序列值</span>
     */
    @XmlElement
    private int current = Globals.DEFAULT_VALUE_INT;
    /**
     * <span class="en-US">Sequence value is cycle</span>
     * <span class="zh-CN">序列数据循环</span>
     */
    @XmlElement
    private boolean cycle = Boolean.FALSE;

    /**
     * <h3 class="en-US">Constructor method for sequence configure information</h3>
     * <h3 class="zh-CN">序列生成器配置信息的构造方法</h3>
     */
    public SequenceConfig() {
    }

    /**
     * <h3 class="en-US">Generate sequence configure information instance by given annotation instance</h3>
     * <h3 class="zh-CN">根据给定的注解实例对象生成序列生成器配置信息实例对象</h3>
     *
     * @param sequenceGenerator <span class="en-US">The annotation instance of SequenceGenerator</span>
     *                          <span class="zh-CN">注解 SequenceGenerator 的实例对象</span>
     * @return <span class="en-US">Generated sequence configure information instance</span>
     * <span class="zh-CN">生成的序列生成器配置信息实例对象</span>
     */
    public static SequenceConfig newInstance(@Nonnull final SequenceGenerator sequenceGenerator) {
        SequenceConfig sequenceConfig = new SequenceConfig();
        sequenceConfig.setMinValue(sequenceGenerator.min());
        sequenceConfig.setMaxValue(sequenceGenerator.max());
        sequenceConfig.setCurrent(sequenceGenerator.init());
        sequenceConfig.setStep(sequenceGenerator.step());
        sequenceConfig.setSequenceName(sequenceGenerator.name());
        sequenceConfig.setCycle(sequenceGenerator.cycle());
        return sequenceConfig;
    }

    /**
     * <h3 class="en-US">Getter method for sequence name</h3>
     * <h3 class="zh-CN">序列名称的Getter方法</h3>
     *
     * @return <span class="en-US">Sequence name</span>
     * <span class="zh-CN">序列名称</span>
     */
    public String getSequenceName() {
        return sequenceName;
    }

    /**
     * <h3 class="en-US">Setter method for sequence name</h3>
     * <h3 class="zh-CN">序列名称的Setter方法</h3>
     *
     * @param sequenceName <span class="en-US">Sequence name</span>
     *                     <span class="zh-CN">序列名称</span>
     */
    public void setSequenceName(String sequenceName) {
        this.sequenceName = sequenceName;
    }

    /**
     * <h3 class="en-US">Getter method for sequence minimum value</h3>
     * <h3 class="zh-CN">序列最小值的Getter方法</h3>
     *
     * @return <span class="en-US">Sequence minimum value</span>
     * <span class="zh-CN">序列最小值</span>
     */
    public int getMinValue() {
        return minValue;
    }

    /**
     * <h3 class="en-US">Setter method for sequence minimum value</h3>
     * <h3 class="zh-CN">序列最小值的Setter方法</h3>
     *
     * @param minValue <span class="en-US">Sequence minimum value</span>
     *                 <span class="zh-CN">序列最小值</span>
     */
    public void setMinValue(int minValue) {
        this.minValue = minValue;
    }

    /**
     * <h3 class="en-US">Getter method for sequence maximum value</h3>
     * <h3 class="zh-CN">序列最大值的Getter方法</h3>
     *
     * @return <span class="en-US">Sequence maximum value</span>
     * <span class="zh-CN">序列最大值</span>
     */
    public int getMaxValue() {
        return maxValue;
    }

    /**
     * <h3 class="en-US">Setter method for sequence maximum value</h3>
     * <h3 class="zh-CN">序列最大值的Setter方法</h3>
     *
     * @param maxValue <span class="en-US">Sequence maximum value</span>
     *                 <span class="zh-CN">序列最大值</span>
     */
    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    /**
     * <h3 class="en-US">Getter method for sequence step value</h3>
     * <h3 class="zh-CN">序列步进值的Getter方法</h3>
     *
     * @return <span class="en-US">Sequence step value</span>
     * <span class="zh-CN">序列步进值</span>
     */
    public int getStep() {
        return step;
    }

    /**
     * <h3 class="en-US">Setter method for sequence step value</h3>
     * <h3 class="zh-CN">序列步进值的Setter方法</h3>
     *
     * @param step <span class="en-US">Sequence step value</span>
     *             <span class="zh-CN">序列步进值</span>
     */
    public void setStep(int step) {
        this.step = step;
    }

    /**
     * <h3 class="en-US">Getter method for sequence current value</h3>
     * <h3 class="zh-CN">当前序列值的Getter方法</h3>
     *
     * @return <span class="en-US">Sequence current value</span>
     * <span class="zh-CN">当前序列值</span>
     */
    public int getCurrent() {
        return current;
    }

    /**
     * <h3 class="en-US">Setter method for sequence current value</h3>
     * <h3 class="zh-CN">当前序列值的Setter方法</h3>
     *
     * @param current <span class="en-US">Sequence current value</span>
     *                <span class="zh-CN">当前序列值</span>
     */
    public void setCurrent(int current) {
        this.current = current;
    }

    /**
     * <h3 class="en-US">Getter method for sequence value is cycle</h3>
     * <h3 class="zh-CN">序列数据循环的Getter方法</h3>
     *
     * @return <span class="en-US">Sequence value is cycle</span>
     * <span class="zh-CN">序列数据循环</span>
     */
    public boolean isCycle() {
        return cycle;
    }

    /**
     * <h3 class="en-US">Setter method for sequence value is cycle</h3>
     * <h3 class="zh-CN">序列数据循环的Setter方法</h3>
     *
     * @param cycle <span class="en-US">Sequence value is cycle</span>
     *              <span class="zh-CN">序列数据循环</span>
     */
    public void setCycle(boolean cycle) {
        this.cycle = cycle;
    }
}
