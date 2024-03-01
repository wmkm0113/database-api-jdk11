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
import org.nervousync.commons.Globals;
import org.nervousync.database.annotations.data.ExcelSheet;
import org.nervousync.exceptions.utils.DataInvalidException;
import org.nervousync.office.excel.ExcelWriter;
import org.nervousync.office.excel.SheetWriter;
import org.nervousync.utils.CollectionUtils;
import org.nervousync.utils.ObjectUtils;
import org.nervousync.utils.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.*;

/**
 * <h2 class="en-US">Data transfer configuration information</h2>
 * <h2 class="zh-CN">数据传输配置信息</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Oct 26, 2023 15:28:21 $
 */
public final class TransferBean<T> {

	/**
	 * <span class="en-US">Excel sheet name</span>
	 * <span class="zh-CN">Excel工作表名称</span>
	 */
	private final String sheetName;
	/**
	 * <span class="en-US">Data transmission class</span>
	 * <span class="zh-CN">数据传输类</span>
	 */
	private final Class<T> beanClass;
	/**
	 * <span class="en-US">Field transfer configuration information mapping table</span>
	 * <span class="zh-CN">属性配置信息映射表</span>
	 */
	private final Map<String, TransferField> fieldMaps;
	/**
	 * <span class="en-US">The maximum column index value defined</span>
	 * <span class="zh-CN">定义的最大列索引值</span>
	 */
	private final int maxIndex;

	/**
	 * <h4 class="en-US">Private constructor</h4>
	 * <h4 class="zh-CN">私有的构造方法</h4>
	 *
	 * @param beanClass <span class="en-US">Data transmission class</span>
	 *                  <span class="zh-CN">数据传输类</span>
	 */
	private TransferBean(@Nonnull final Class<T> beanClass) {
		this.sheetName =
				Optional.ofNullable(beanClass.getAnnotation(ExcelSheet.class))
						.map(ExcelSheet::value)
						.orElse(Globals.DEFAULT_VALUE_STRING);
		this.beanClass = beanClass;
		this.fieldMaps = new HashMap<>();
		ReflectionUtils.getAllDeclaredFields(beanClass, Boolean.TRUE).forEach(this::registerField);
		int maxIndex = Globals.DEFAULT_VALUE_INT;
		for (TransferField transferField : this.fieldMaps.values()) {
			if (maxIndex < transferField.getColumnIndex()) {
				maxIndex = transferField.getColumnIndex();
			}
		}
		this.maxIndex = maxIndex;
	}

	/**
	 * <h4 class="en-US">Register the given field instance</h4>
	 * <h4 class="zh-CN">注册给定的属性对象</h4>
	 *
	 * @param field <span class="en-US">Field instance</span>
	 *              <span class="zh-CN">属性对象</span>
	 */
    private void registerField(final Field field) {
        try {
            this.fieldMaps.put(field.getName(), TransferField.newInstance(field));
        } catch (DataInvalidException ignored) {
        }
    }

    /**
     * <h4 class="en-US">Static method is used to initialize data transfer configuration information</h4>
     * <h4 class="zh-CN">静态方法用于初始化数据传输配置信息</h4>
     *
     * @param <T>       <span class="en-US">Data transmission class</span>
     *                  <span class="zh-CN">数据传输类</span>
     * @param beanClass <span class="en-US">Data transmission class</span>
     *                  <span class="zh-CN">数据传输类</span>
     * @return <span class="en-US">Data transfer configuration information object instance</span>
     * <span class="zh-CN">数据传输配置信息实例对象</span>
     */
    public static <T> TransferBean<T> newInstance(final Class<T> beanClass) {
        return (beanClass == null) ? null : new TransferBean<>(beanClass);
    }

    /**
     * <h4 class="en-US">Populate data into the given Excel file writer</h4>
     * <h4 class="zh-CN">填充数据到给定的Excel文件写入器中</h4>
     *
     * @param excelWriter <span class="en-US">Excel file writer</span>
     *                    <span class="zh-CN">Excel文件写入器</span>
     * @param object      <span class="en-US">Data to be filled in</span>
     *                    <span class="zh-CN">需要填充的数据</span>
     */
    public void appendData(@Nonnull final ExcelWriter excelWriter, @Nonnull final Object object) {
        if (ObjectUtils.nullSafeEquals(this.beanClass, object.getClass())) {
            SheetWriter sheetWriter = excelWriter.sheetWriter(this.sheetName);
            List<Object> dataValues = new ArrayList<>();
            for (int i = 0; i <= this.maxIndex; i++) {
                dataValues.add(null);
            }
            this.fieldMaps.forEach((fieldName, fieldConfig) -> {
                String string =
                        Optional.ofNullable(fieldConfig.convert(ReflectionUtils.getFieldValue(fieldName, object)))
                                .filter(fieldValue -> fieldValue instanceof String)
                                .map(fieldValue -> (String) fieldValue)
                                .orElse(Globals.DEFAULT_VALUE_STRING);
                if (fieldConfig.getColumnIndex() < Globals.INITIALIZE_INT_VALUE) {
                    dataValues.add(string);
                } else {
                    dataValues.set(fieldConfig.getColumnIndex(), string);
                }
            });
            sheetWriter.appendData(dataValues);
        }
    }

    /**
     * <h4 class="en-US">Convert the given transport data mapping table to a standard data mapping table</h4>
     * <h4 class="zh-CN">将给定的传输数据映射表转换为标准数据映射表</h4>
     *
     * @param transferMap <span class="en-US">Data mapping table</span>
     *                    <span class="zh-CN">传输数据映射表</span>
     * @return <span class="en-US">Standard data mapping table</span>
     * <span class="zh-CN">标准数据映射表</span>
     */
    public Map<String, Object> unmarshalMap(@Nonnull final Map<String, String> transferMap) {
        Map<String, Object> dataMap = new HashMap<>();
        this.fieldMaps.entrySet()
                .stream()
                .filter(entry -> transferMap.containsKey(entry.getKey()))
                .forEach(entry ->
                        dataMap.put(entry.getKey(), entry.getValue().convert(transferMap.get(entry.getKey()))));
        return dataMap;
    }

    /**
     * <h4 class="en-US">Converts the given instance object to a transport data mapping table</h4>
     * <h4 class="zh-CN">将给定的实例对象转换为传输数据映射表</h4>
     *
     * @param object     <span class="en-US">Data transfer object instance</span>
     *                   <span class="zh-CN">数据传输类实例对象</span>
     * @param fieldNames <span class="en-US">List of field names that need to be transferred</span>
     *                   <span class="zh-CN">需要传输的属性名称列表</span>
     * @return <span class="en-US">Data mapping table</span>
     * <span class="zh-CN">传输数据映射表</span>
     */
    public Map<String, String> transferMap(final Object object, @Nonnull final List<String> fieldNames) {
        Map<String, String> transferMap = new HashMap<>();
        if (ObjectUtils.nullSafeEquals(this.beanClass, object.getClass())) {
            this.fieldMaps.forEach((fieldName, fieldConfig) -> {
                if (CollectionUtils.contains(fieldNames, fieldName)) {
                    transferMap.put(fieldName,
                            (String) fieldConfig.convert(ReflectionUtils.getFieldValue(fieldName, object)));
                }
            });
        }
        return transferMap;
    }

	/**
	 * <h4 class="en-US">Parse data list to data map which data list read from excel file</h4>
	 * <h4 class="zh-CN">解析Excel读取的数据列表为数据映射表</h4>
	 *
	 * @param dataValues <span class="en-US">Data list which read from excel file</span>
	 *                   <span class="zh-CN">Excel读取的数据列表</span>
	 * @return <span class="en-US">Parsed data map</span>
	 * <span class="zh-CN">解析的数据映射表</span>
	 */
    public Map<String, String> parseList(@Nonnull final List<String> dataValues) {
        Map<String, String> transferMap = new HashMap<>();
        this.fieldMaps.forEach((fieldName, fieldConfig) -> {
            if (fieldConfig.getColumnIndex() >= Globals.INITIALIZE_INT_VALUE
                    && fieldConfig.getColumnIndex() < dataValues.size()) {
                transferMap.put(fieldName, dataValues.get(fieldConfig.getColumnIndex()));
            }
        });
        return transferMap;
    }

    /**
     * <h4 class="en-US">Convert the given data mapping table into a data transfer class instance object</h4>
     * <h4 class="zh-CN">将给定的数据映射表转换为数据传输类实例对象</h4>
     *
     * @param dataMap <span class="en-US">Data mapping table</span>
     *                <span class="zh-CN">数据映射表</span>
     * @return <span class="en-US">Data transfer object instance</span>
     * <span class="zh-CN">数据传输类实例对象</span>
     */
    public T convert(final Map<String, String> dataMap) {
        if (dataMap == null || dataMap.isEmpty()) {
            return null;
        }
        T object = ObjectUtils.newInstance(this.beanClass);
        dataMap.forEach((key, value) ->
                Optional.ofNullable(this.fieldMaps.get(key))
                        .ifPresent(transferField -> ReflectionUtils.setField(key, object, transferField.convert(value))));
        return object;
    }
}
