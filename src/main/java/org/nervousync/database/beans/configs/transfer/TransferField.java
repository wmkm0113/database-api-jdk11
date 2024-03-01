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

package org.nervousync.database.beans.configs.transfer;

import jakarta.annotation.Nonnull;
import org.nervousync.annotations.beans.DataTransfer;
import org.nervousync.beans.config.TransferConfig;
import org.nervousync.commons.Globals;
import org.nervousync.database.annotations.data.ExcelColumn;
import org.nervousync.exceptions.utils.DataInvalidException;

import java.lang.reflect.Field;
import java.util.Optional;

/**
 * <h2 class="en-US">Data field transfer configuration information</h2>
 * <h2 class="zh-CN">数据传输属性配置信息</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Oct 26, 2023 15:25:10 $
 */
public final class TransferField {
    /**
     * <span class="en-US">Column order number in Excel</span>
     * <span class="zh-CN">Excel表中的列排列序号</span>
     */
    private final int columnIndex;
    /**
     * <span class="en-US">Data convert configure</span>
     * <span class="zh-CN">数据转换配置信息</span>
     */
    private final TransferConfig<?, ?> transferConfig;

    /**
     * <h3 class="en-US">Private constructor</h3>
     * <h3 class="zh-CN">私有的构造方法</h3>
     *
     * @param columnIndex    <span class="en-US">Column order number in Excel</span>
     *                       <span class="zh-CN">Excel表中的列排列序号</span>
     * @param transferConfig <span class="en-US">The object instance of data transfer configure</span>
     *                       <span class="zh-CN">数据传输配置实例对象</span>
     */
    private TransferField(final int columnIndex, final TransferConfig<?, ?> transferConfig) {
        this.columnIndex = columnIndex;
        this.transferConfig = transferConfig;
    }

    /**
     * <h3 class="en-US">Static method is used to initialize data transmission attribute configuration information</h3>
     * <h3 class="zh-CN">静态方法用于初始化数据传输属性配置信息</h3>
     *
     * @param field <span class="en-US">Field instance object obtained by reflection</span>
     *              <span class="zh-CN">反射获取的属性实例对象</span>
     * @return <span class="en-US">Data field transfer configuration information object instance</span>
     * <span class="zh-CN">数据传输属性配置信息实例对象</span>
     * @throws DataInvalidException <span class="en-US">Getting wrong number of types for generic</span>
     *                              <span class="zh-CN">获取泛型的类型数量错误</span>
     */
    public static TransferField newInstance(@Nonnull final Field field) throws DataInvalidException {
        return new TransferField(
                Optional.ofNullable(field.getAnnotation(ExcelColumn.class))
                        .map(ExcelColumn::value)
                        .orElse(Globals.DEFAULT_VALUE_INT),
                new TransferConfig<>(field.getAnnotation(DataTransfer.class)));
    }

    /**
     * <h3 class="en-US">Getter method for column order number in Excel</h3>
     * <h3 class="zh-CN">Excel表中的列排列序号的Getter方法</h3>
     *
     * @return <span class="en-US">Column order number in Excel</span>
     * <span class="zh-CN">Excel表中的列排列序号</span>
     */
    public int getColumnIndex() {
        return this.columnIndex;
    }

    public Object convert(final Object object) {
        return this.transferConfig.convert(object);
    }
}
