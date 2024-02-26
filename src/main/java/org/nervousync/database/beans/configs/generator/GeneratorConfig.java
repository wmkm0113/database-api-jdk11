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
package org.nervousync.database.beans.configs.generator;

import jakarta.xml.bind.annotation.*;
import org.nervousync.beans.core.BeanObject;
import org.nervousync.commons.Globals;
import org.nervousync.database.annotations.sequence.SequenceGenerator;
import org.nervousync.database.annotations.table.GeneratedValue;
import org.nervousync.database.beans.configs.sequence.SequenceConfig;
import org.nervousync.database.enumerations.table.GenerationType;

/**
 * <h2 class="en-US">Column data generator configure information</h2>
 * <h2 class="zh-CN">列数据生成器配置信息</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Nov 4, 2020 15:49:52 $
 */
@XmlType(name = "generator_config")
@XmlRootElement(name = "generator_config")
@XmlAccessorType(XmlAccessType.NONE)
public final class GeneratorConfig extends BeanObject {
    /**
     * <span class="en-US">Serial version UID</span>
     * <span class="zh-CN">序列化UID</span>
     */
    private static final long serialVersionUID = -4086799829735527877L;
    /**
     * <span class="en-US">Generation type</span>
     * <span class="zh-CN">生成器类型</span>
     */
    @XmlElement(name = "generation_type")
    private GenerationType generationType;
    /**
     * <span class="en-US">Generator name</span>
     * <span class="zh-CN">生成器名称</span>
     */
    @XmlElement(name = "generator_name")
    private String generatorName;
    /**
     * <span class="en-US">Sequence configure information</span>
     * <span class="zh-CN">序列生成器配置信息</span>
     */
    @XmlElement(name = "sequence_config")
    private SequenceConfig sequenceConfig = null;

    /**
     * <h3 class="en-US">Constructor method for column data generator configure information</h3>
     * <h3 class="zh-CN">列数据生成器配置信息的构造方法</h3>
     */
    public GeneratorConfig() {
    }

    /**
     * <h3 class="en-US">Generate column data generator configure information instance by given annotation instance</h3>
     * <h3 class="zh-CN">根据给定的注解实例对象生成列数据生成器配置信息实例对象</h3>
     *
     * @param generatedValue    <span class="en-US">The annotation instance of GeneratedValue</span>
     *                          <span class="zh-CN">注解 GeneratedValue 的实例对象</span>
     * @param sequenceGenerator <span class="en-US">The annotation instance of SequenceGenerator</span>
     *                          <span class="zh-CN">注解 SequenceGenerator 的实例对象</span>
     * @return <span class="en-US">Generated data generator configure information instance</span>
     * <span class="zh-CN">生成的列数据生成器配置信息实例对象</span>
     */
    public static GeneratorConfig newInstance(final GeneratedValue generatedValue,
                                              final SequenceGenerator sequenceGenerator) {
        GeneratorConfig generatorConfig = new GeneratorConfig();
        if (generatedValue == null) {
            generatorConfig.setGenerationType(GenerationType.ASSIGNED);
            generatorConfig.setGeneratorName(Globals.DEFAULT_VALUE_STRING);
        } else {
            GenerationType generationType = generatedValue.type();
            generatorConfig.setGenerationType(generationType);
            switch (generationType) {
                case SEQUENCE:
                    if (sequenceGenerator == null) {
                        generatorConfig.setGeneratorName(generatedValue.generator());
                    } else {
                        generatorConfig.setGeneratorName(sequenceGenerator.name());
                        generatorConfig.setSequenceConfig(SequenceConfig.newInstance(sequenceGenerator));
                    }
                    break;
                case GENERATE:
                    generatorConfig.setGeneratorName(generatedValue.generator());
                    break;
                default:
                    generatorConfig.setGeneratorName(Globals.DEFAULT_VALUE_STRING);
                    break;
            }
        }
        return generatorConfig;
    }

    /**
     * <h3 class="en-US">Getter method for generation type</h3>
     * <h3 class="zh-CN">生成器类型的Getter方法</h3>
     *
     * @return <span class="en-US">Generation type</span>
     * <span class="zh-CN">生成器类型</span>
     */
    public GenerationType getGenerationType() {
        return generationType;
    }

    /**
     * <h3 class="en-US">Setter method for generation type</h3>
     * <h3 class="zh-CN">生成器类型的Setter方法</h3>
     *
     * @param generationType <span class="en-US">Generation type</span>
     *                       <span class="zh-CN">生成器类型</span>
     */
    public void setGenerationType(GenerationType generationType) {
        this.generationType = generationType;
    }

    /**
     * <h3 class="en-US">Getter method for generator name</h3>
     * <h3 class="zh-CN">生成器名称的Getter方法</h3>
     *
     * @return <span class="en-US">Generator name</span>
     * <span class="zh-CN">生成器名称</span>
     */
    public String getGeneratorName() {
        return generatorName;
    }

    /**
     * <h3 class="en-US">Setter method for generator name</h3>
     * <h3 class="zh-CN">生成器名称的Setter方法</h3>
     *
     * @param generatorName <span class="en-US">Generator name</span>
     *                      <span class="zh-CN">生成器名称</span>
     */
    public void setGeneratorName(String generatorName) {
        this.generatorName = generatorName;
    }

    /**
     * <h3 class="en-US">Getter method for sequence configure information</h3>
     * <h3 class="zh-CN">序列生成器配置信息的Getter方法</h3>
     *
     * @return <span class="en-US">Sequence configure information</span>
     * <span class="zh-CN">序列生成器配置信息</span>
     */
    public SequenceConfig getSequenceConfig() {
        return sequenceConfig;
    }

    /**
     * <h3 class="en-US">Setter method for sequence configure information</h3>
     * <h3 class="zh-CN">序列生成器配置信息的Setter方法</h3>
     *
     * @param sequenceConfig <span class="en-US">Sequence configure information</span>
     *                       <span class="zh-CN">序列生成器配置信息</span>
     */
    public void setSequenceConfig(SequenceConfig sequenceConfig) {
        this.sequenceConfig = sequenceConfig;
    }
}
