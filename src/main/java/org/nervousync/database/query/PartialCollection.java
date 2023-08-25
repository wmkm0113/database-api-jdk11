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
package org.nervousync.database.query;

import jakarta.xml.bind.annotation.*;
import org.nervousync.beans.core.BeanObject;
import org.nervousync.commons.Globals;
import org.nervousync.database.commons.DatabaseCommons;
import org.nervousync.utils.StringUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * <h2 class="en-US">Query result partial collection define</h2>
 * <h2 class="zh-CN">查询结果部分集合定义</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Jan 13, 2010 16:07:14 $
 */
@XmlType(name = "partial_collection", namespace = "https://nervousync.org/schemas/query")
@XmlRootElement(name = "partial_collection", namespace = "https://nervousync.org/query")
@XmlAccessorType(XmlAccessType.NONE)
public final class PartialCollection<T> extends BeanObject {
    /**
     * <span class="en-US">Serial version UID</span>
     * <span class="zh-CN">序列化UID</span>
     */
    private static final long serialVersionUID = 2086690645677391624L;
    /**
     * <span class="en-US">Record list</span>
     * <span class="zh-CN">数据记录列表</span>
     */
    @XmlElement(name = "record")
    @XmlElementWrapper(name = "result_list")
    private List<T> resultList;
    /**
     * <span class="en-US">Total record count</span>
     * <span class="zh-CN">总记录数</span>
     */
    @XmlElement(name = "total_count")
    private long totalCount;

    /**
     * <h3 class="en-US">Default constructor method for query result partial collection define</h3>
     * <h3 class="zh-CN">查询结果部分集合定义的默认构造方法</h3>
     */
    public PartialCollection() {
    }

    /**
     * <h3 class="en-US">Getter method for record list</h3>
     * <h3 class="zh-CN">数据记录列表的Getter方法</h3>
     *
     * @return <span class="en-US">Record list</span>
     * <span class="zh-CN">数据记录列表</span>
     */
    public List<T> getResultList() {
        return resultList;
    }

    /**
     * <h3 class="en-US">Setter method for record list</h3>
     * <h3 class="zh-CN">数据记录列表的Setter方法</h3>
     *
     * @param resultList <span class="en-US">Record list</span>
     *                   <span class="zh-CN">数据记录列表</span>
     */
    public void setResultList(List<T> resultList) {
        this.resultList = resultList;
    }

    /**
     * <h3 class="en-US">Getter method for total record count</h3>
     * <h3 class="zh-CN">总记录数的Getter方法</h3>
     *
     * @return <span class="en-US">Total record count</span>
     * <span class="zh-CN">总记录数</span>
     */
    public long getTotalCount() {
        return this.totalCount;
    }

    /**
     * <h3 class="en-US">Setter method for total record count</h3>
     * <h3 class="zh-CN">总记录数的Setter方法</h3>
     *
     * @param totalCount <span class="en-US">Total record count</span>
     *                   <span class="zh-CN">总记录数</span>
     */
    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    /**
     * <h3 class="en-US">Retrieve current record list size</h3>
     * <h3 class="zh-CN">检索当前结果集记录数</h3>
     *
     * @return <span class="en-US">current record list count</span>
     * <span class="zh-CN">当前结果集记录数</span>
     */
    public int size() {
        return this.resultList.size();
    }

    /**
     * <h3 class="en-US">Check current record list is empty</h3>
     * <h3 class="zh-CN">检查当前的结果集列表是否为空</h3>
     *
     * @return <span class="en-US"><code>true</code> if this collection is empty or <code>false</code> for otherwise</span>
     * <span class="zh-CN">当前的结果集列表是否为空返回<code>true</code>，其他情况返回 <code>false</code></span>
     */
    public boolean isEmpty() {
        return this.resultList.isEmpty();
    }

    /**
     * <h3 class="en-US">Add record instance to current record list</h3>
     * <h3 class="zh-CN">添加数据记录实例对象到当前数据集列表中</h3>
     *
     * @param record <span class="en-US">Record instance</span>
     *               <span class="zh-CN">数据记录实例对象</span>
     */
    public void add(T record) {
        this.resultList.add(record);
    }

    /**
     * <h3 class="en-US">Generate iterator instance by current record list</h3>
     * <h3 class="zh-CN">生成当前数据集列表的遍历器</h3>
     *
     * @return <span class="en-US">Generated iterator instance</span>
     * <span class="zh-CN">生成的遍历器实例对象</span>
     */
    public Iterator<T> iterator() {
        return this.resultList.iterator();
    }

    /**
     * <h3 class="en-US">Generate cache data</h3>
     * <h3 class="zh-CN">生成缓存数据</h3>
     *
     * @return <span class="en-US">Generated cache data string</span>
     * <span class="zh-CN">生成的缓存数据字符串</span>
     */
    public String cacheData() {
        Map<String, String> convertMap = new HashMap<>();
        convertMap.put(DatabaseCommons.TOTAL_COUNT_KEY, Long.toHexString(this.totalCount));
        convertMap.put(DatabaseCommons.RESULT_LIST_KEY,
                StringUtils.objectToString(this.resultList, StringUtils.StringType.JSON, Boolean.FALSE));
        return StringUtils.objectToString(convertMap, StringUtils.StringType.JSON, Boolean.TRUE);
    }

    /**
     * <h3 class="en-US">Static method for parse cache data string and generate PartialCollection instance</h3>
     * <h3 class="zh-CN">静态方法用于解析缓存数据字符串并生成查询结果部分集合实例对象</h3>
     *
     * @param <T>         <span class="en-US">Result entity class</span>
     *                    <span class="zh-CN">数据结果实体类</span>
     * @param entityClass <span class="en-US">Result entity class</span>
     *                    <span class="zh-CN">数据结果实体类</span>
     * @param cacheData   <span class="en-US">Cache data string</span>
     *                    <span class="zh-CN">缓存数据字符串</span>
     * @return <span class="en-US">Generated PartialCollection instance</span>
     * <span class="zh-CN">生成的查询结果部分集合实例对象</span>
     */
    public static <T> PartialCollection<T> parse(final Class<T> entityClass, final String cacheData) {
        if (StringUtils.isEmpty(cacheData)) {
            return null;
        }

        Map<String, Object> convertMap = StringUtils.dataToMap(cacheData, StringUtils.StringType.JSON);
        if (convertMap.isEmpty()) {
            return null;
        }
        String totalCount = (String) convertMap.get(DatabaseCommons.TOTAL_COUNT_KEY);
        String resultList = (String) convertMap.get(DatabaseCommons.RESULT_LIST_KEY);

        PartialCollection<T> partialCollection = new PartialCollection<>();
        partialCollection.setTotalCount(Long.parseLong(totalCount, 16));
        partialCollection.setResultList(StringUtils.stringToList(resultList, Globals.DEFAULT_ENCODING, entityClass));

        return partialCollection;
    }
}
