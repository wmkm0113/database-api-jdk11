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
package org.nervousync.database.beans.configs.index;

import jakarta.persistence.Index;
import org.nervousync.beans.core.BeanObject;
import org.nervousync.commons.Globals;
import org.nervousync.database.beans.configs.column.ColumnConfig;
import org.nervousync.utils.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <h2 class="en-US">Column index configure information</h2>
 * <h2 class="zh-CN">列索引配置信息</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Nov 4, 2020 16:33:28 $
 */
public final class IndexInfo extends BeanObject {
    /**
     * <span class="en-US">Serial version UID</span>
     * <span class="zh-CN">序列化UID</span>
     */
    private static final long serialVersionUID = -5516465655300980659L;
    /**
     * <span class="en-US">Index name</span>
     * <span class="zh-CN">索引名称</span>
     */
    private String indexName;
    /**
     * <span class="en-US">Index contains column name list</span>
     * <span class="zh-CN">索引包含的列名列表</span>
     */
    private List<String> columnList;
    /**
     * <span class="en-US">Index is unique</span>
     * <span class="zh-CN">索引是唯一索引</span>
     */
    private boolean unique;

    /**
     * <h3 class="en-US">Constructor method for column index configure information</h3>
     * <h3 class="zh-CN">列索引配置信息的构造方法</h3>
     */
    public IndexInfo() {
    }

    /**
     * <h3 class="en-US">Generate column index configure information instance by given annotation instance and column info list</h3>
     * <h3 class="zh-CN">根据给定的注解实例对象和列基本信息列表生成列索引配置信息实例对象</h3>
     *
     * @param index         <span class="en-US">The annotation instance of Index</span>
     *                      <span class="zh-CN">注解 Index 的实例对象</span>
     * @param columnConfigs <span class="en-US">Column info list</span>
     *                      <span class="zh-CN">列基本信息列表</span>
     * @return <span class="en-US">Generated column index configure information instance</span>
     * <span class="zh-CN">生成的列索引配置信息实例对象</span>
     */
    public static IndexInfo newInstance(final Index index, final List<ColumnConfig> columnConfigs) {
        if (index == null || StringUtils.isEmpty(index.columnList()) || columnConfigs == null) {
            return null;
        }
        List<String> columnList = new ArrayList<>();
        Arrays.asList(StringUtils.tokenizeToStringArray(index.columnList(), Globals.DEFAULT_SPLIT_SEPARATOR))
                .forEach(identifyKey ->
                        columnConfigs.stream()
                                .filter(columnConfig -> columnConfig.matchKey(identifyKey))
                                .findFirst()
                                .ifPresent(columnConfig ->
                                        columnList.add(columnConfig.getColumnInfo().getColumnName())));
        if (columnList.isEmpty()) {
            return null;
        }
        IndexInfo indexInfo = new IndexInfo();
        indexInfo.setIndexName(index.name());
        indexInfo.setUnique(index.unique());
        indexInfo.setColumnList(columnList);
        return indexInfo;
    }

    /**
     * <h3 class="en-US">Generate column index information instance by given result set</h3>
     * <h3 class="zh-CN">根据给定的查询结果集生成列索引配置信息实例对象</h3>
     *
     * @param resultSet <span class="en-US">result set instance</span>
     *                  <span class="zh-CN">查询结果集实例对象</span>
     * @return <span class="en-US">Generated column index configure information instance</span>
     * <span class="zh-CN">生成的列索引配置信息实例对象</span>
     * @throws SQLException <span class="en-US">If an error occurs when parse result set instance</span>
     *                      <span class="zh-CN">如果解析查询结果集时出现异常</span>
     */
    public static IndexInfo newInstance(final ResultSet resultSet) throws SQLException {
        if (resultSet == null) {
            return null;
        }
        String indexName = resultSet.getString("INDEX_NAME");
        boolean unique = !resultSet.getBoolean("NON_UNIQUE");
        List<String> columnList =
                Arrays.asList(StringUtils.tokenizeToStringArray(
                        resultSet.getString("COLUMN_NAME"), Globals.DEFAULT_SPLIT_SEPARATOR));
        IndexInfo indexInfo = null;
        if (StringUtils.notBlank(indexName)) {
            indexInfo = new IndexInfo();
            indexInfo.setIndexName(indexName);
            indexInfo.setUnique(unique);
            indexInfo.setColumnList(columnList);
        }
        return indexInfo;
    }

    /**
     * <h3 class="en-US">Getter method for index name</h3>
     * <h3 class="zh-CN">索引名称的Getter方法</h3>
     *
     * @return <span class="en-US">Index name</span>
     * <span class="zh-CN">索引名称</span>
     */
    public String getIndexName() {
        return indexName;
    }

    /**
     * <h3 class="en-US">Setter method for index name</h3>
     * <h3 class="zh-CN">索引名称的Setter方法</h3>
     *
     * @param indexName <span class="en-US">Index name</span>
     *                  <span class="zh-CN">索引名称</span>
     */
    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    /**
     * <h3 class="en-US">Getter method for index contains column list</h3>
     * <h3 class="zh-CN">索引包含的列基本信息列表的Getter方法</h3>
     *
     * @return <span class="en-US">Index contains column list</span>
     * <span class="zh-CN">索引包含的列基本信息列表</span>
     */
    public List<String> getColumnList() {
        return columnList;
    }

    /**
     * <h3 class="en-US">Setter method for index contains column list</h3>
     * <h3 class="zh-CN">索引包含的列基本信息列表的Setter方法</h3>
     *
     * @param columnList <span class="en-US">Index contains column list</span>
     *                   <span class="zh-CN">索引包含的列基本信息列表</span>
     */
    public void setColumnList(List<String> columnList) {
        this.columnList = columnList;
    }

    /**
     * <h3 class="en-US">Getter method for index is unique</h3>
     * <h3 class="zh-CN">索引是唯一索引的Getter方法</h3>
     *
     * @return <span class="en-US">Index is unique</span>
     * <span class="zh-CN">索引是唯一索引</span>
     */
    public boolean isUnique() {
        return unique;
    }

    /**
     * <h3 class="en-US">Setter method for index is unique</h3>
     * <h3 class="zh-CN">索引是唯一索引的Setter方法</h3>
     *
     * @param unique <span class="en-US">Index is unique</span>
     *               <span class="zh-CN">索引是唯一索引</span>
     */
    public void setUnique(boolean unique) {
        this.unique = unique;
    }
}
