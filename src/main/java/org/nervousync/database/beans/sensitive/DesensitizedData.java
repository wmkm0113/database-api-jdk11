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

package org.nervousync.database.beans.sensitive;

import org.nervousync.commons.Globals;
import org.nervousync.database.beans.configs.column.ColumnConfig;
import org.nervousync.database.beans.configs.sensitive.SensitiveConfig;
import org.nervousync.database.enumerations.sensitive.SensitiveType;
import org.nervousync.security.factory.SecureFactory;
import org.nervousync.utils.StringUtils;

import java.util.Optional;

/**
 * <h2 class="en-US">Desensitized data definition</h2>
 * <h2 class="zh-CN">脱敏数据定义</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Sep 13, 2023 12:35:12 $
 */
public final class DesensitizedData {

    /**
     * <span class="en-US">Desensitized data</span>
     * <span class="zh-CN">脱敏后的数据</span>
     */
    private final String desensitizationValue;
    /**
     * <span class="en-US">Encrypted hidden data</span>
     * <span class="zh-CN">加密后的隐藏数据</span>
     */
    private final String encryptedValue;

    /**
     * <h2 class="en-US">Private constructor for desensitized data definition</h2>
     * <h2 class="zh-CN">脱敏数据定义的私有构造方法</h2>
     *
     * @param desensitizationValue <span class="en-US">Desensitized data</span>
     *                             <span class="zh-CN">脱敏后的数据</span>
     * @param encryptedValue       <span class="en-US">Encrypted hidden data</span>
     *                             <span class="zh-CN">加密后的隐藏数据</span>
     */
    private DesensitizedData(final String desensitizationValue, final String encryptedValue) {
        this.desensitizationValue = desensitizationValue;
        this.encryptedValue = encryptedValue;
    }

    /**
     * <h2 class="en-US">Static methods are used to generate desensitized data definition instance objects</h2>
     * <h2 class="zh-CN">静态方法用于生成脱敏数据定义实例对象</h2>
     *
     * @param columnConfig  <span class="en-US">Data column definition</span>
     *                      <span class="zh-CN">数据列定义</span>
     * @param sensitiveData <span class="en-US">Sensitive information data content</span>
     *                      <span class="zh-CN">敏感信息数据内容</span>
     * @return <span class="en-US">Generated sensitive information handling configuration</span>
     * <span class="zh-CN">生成的敏感信息处理配置实例对象</span>
     */
    public static DesensitizedData desensitization(final ColumnConfig columnConfig, final String sensitiveData) {
        if (columnConfig == null || StringUtils.isEmpty(sensitiveData) || sensitiveData.indexOf("*") > 0
                || !SecureFactory.registeredConfig(columnConfig.getSecureName())) {
            return null;
        }
        SensitiveType sensitiveType;
        try {
            sensitiveType = SensitiveType.valueOf(columnConfig.getSensitiveType());
        } catch (Exception e) {
            return null;
        }
        return Optional.ofNullable(SensitiveConfig.newInstance(sensitiveType, sensitiveData))
                .map(sensitiveConfig -> {
                    String encData = Globals.DEFAULT_VALUE_STRING;
                    if (StringUtils.notBlank(columnConfig.getEncField())
                            && StringUtils.notBlank(columnConfig.getSecureName())) {
                        encData = SecureFactory.encrypt(columnConfig.getSecureName(),
                                sensitiveData.substring(sensitiveConfig.getPrefixLength(),
                                        sensitiveData.length() - sensitiveConfig.getSuffixLength()));
                    }
                    return new DesensitizedData(desensitize(sensitiveData, sensitiveConfig), encData);
                })
                .orElse(null);
    }

    /**
     * <h3 class="en-US">Getter method for desensitized data</h3>
     * <h3 class="zh-CN">脱敏后的数据的Getter方法</h3>
     *
     * @return <span class="en-US">Desensitized data</span>
     * <span class="zh-CN">脱敏后的数据</span>
     */
    public String getDesensitizationValue() {
        return desensitizationValue;
    }

    /**
     * <h3 class="en-US">Getter method for encrypted hidden data</h3>
     * <h3 class="zh-CN">加密后的隐藏数据的Getter方法</h3>
     *
     * @return <span class="en-US">Encrypted hidden data</span>
     * <span class="zh-CN">加密后的隐藏数据</span>
     */
    public String getEncryptedValue() {
        return encryptedValue;
    }

    /**
     * <h3 class="en-US">Handle sensitive information based on given prefix and suffix lengths</h3>
     * <h3 class="zh-CN">根据给定的前缀和后缀长度处理敏感信息</h3>
     *
     * @param sensitiveData   <span class="en-US">Sensitive information string</span>
     *                        <span class="zh-CN">敏感信息字符串</span>
     * @param sensitiveConfig <span class="en-US">Information desensitization configuration</span>
     *                        <span class="zh-CN">信息脱敏配置</span>
     * @return <span class="en-US">Desensitized information</span>
     * <span class="zh-CN">脱敏后的敏感信息</span>
     */
    private static String desensitize(final String sensitiveData, final SensitiveConfig sensitiveConfig) {
        if (StringUtils.isEmpty(sensitiveData) || sensitiveConfig == null
                || sensitiveData.length() < sensitiveConfig.getPrefixLength()
                || sensitiveData.length() < sensitiveConfig.getSuffixLength()) {
            return sensitiveData;
        }
        String prefix = sensitiveData.substring(0, sensitiveConfig.getPrefixLength());
        String suffix = sensitiveData.substring(sensitiveData.length() - sensitiveConfig.getSuffixLength());
        StringBuilder hidden = new StringBuilder();
        int hiddenCount = sensitiveData.length() - sensitiveConfig.getPrefixLength() - sensitiveConfig.getSuffixLength();
        do {
            hidden.append("*");
            hiddenCount--;
        } while (hiddenCount > 0);
        return prefix + hidden + suffix;
    }
}
