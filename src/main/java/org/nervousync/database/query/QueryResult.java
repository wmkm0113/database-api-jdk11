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
package org.nervousync.database.query;

import jakarta.annotation.Nonnull;
import jakarta.xml.bind.annotation.*;
import org.nervousync.beans.core.BeanObject;
import org.nervousync.commons.Globals;
import org.nervousync.database.commons.DatabaseCommons;
import org.nervousync.database.entity.core.BaseObject;
import org.nervousync.utils.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <h2 class="en-US">Query result partial collection define</h2>
 * <h2 class="zh-CN">查询结果部分集合定义</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Jan 13, 2010 16:07:14 $
 */
@XmlType(name = "query_result", namespace = "https://nervousync.org/schemas/query")
@XmlRootElement(name = "query_result", namespace = "https://nervousync.org/schemas/query")
@XmlAccessorType(XmlAccessType.NONE)
public final class QueryResult extends BeanObject {

	/**
	 * <span class="en-US">Serial version UID</span>
	 * <span class="zh-CN">序列化UID</span>
	 */
	private static final long serialVersionUID = 2086690645677391624L;
	private static final String RECORD_SPLIT_CHARACTER = "|";

	/**
	 * <span class="en-US">Record list</span>
	 * <span class="zh-CN">数据记录列表</span>
	 */
	@XmlElement(name = "record")
	@XmlElementWrapper(name = "record_list")
	private List<String> resultList = new ArrayList<>();
	/**
	 * <span class="en-US">Total record count</span>
	 * <span class="zh-CN">总记录数</span>
	 */
	@XmlElement(name = "total_count")
	private long totalCount = Globals.DEFAULT_VALUE_LONG;

	/**
	 * <h3 class="en-US">Default constructor method for query result partial collection define</h3>
	 * <h3 class="zh-CN">查询结果部分集合定义的默认构造方法</h3>
	 */
	public QueryResult() {
	}

	/**
	 * <h3 class="en-US">Getter method for record list</h3>
	 * <h3 class="zh-CN">数据记录列表的Getter方法</h3>
	 *
	 * @return <span class="en-US">Record list</span>
	 * <span class="zh-CN">数据记录列表</span>
	 */
	public List<String> getResultList() {
		return resultList;
	}

	/**
	 * <h3 class="en-US">Setter method for record list</h3>
	 * <h3 class="zh-CN">数据记录列表的Setter方法</h3>
	 *
	 * @param resultList <span class="en-US">Record list</span>
	 *                   <span class="zh-CN">数据记录列表</span>
	 */
	public void setResultList(final List<String> resultList) {
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

	public <T> List<T> asList(final Class<T> targetClass) {
		return this.resultList.stream()
				.map(string -> this.unmarshalRecord(targetClass, string))
				.collect(Collectors.toList());
	}

	/**
	 * <h3 class="en-US">Generate cache data</h3>
	 * <h3 class="zh-CN">生成缓存数据</h3>
	 *
	 * @return <span class="en-US">Generated cache data string</span>
	 * <span class="zh-CN">生成的缓存数据字符串</span>
	 */
	public String cacheData() {
		StringBuilder stringBuilder = new StringBuilder();
		this.resultList.stream()
				.map(recordObject -> StringUtils.objectToString(recordObject, StringUtils.StringType.JSON, Boolean.FALSE))
				.filter(StringUtils::notBlank)
				.map(record -> StringUtils.base64Encode(ConvertUtils.toByteArray(record)))
				.forEach(string -> stringBuilder.append(RECORD_SPLIT_CHARACTER).append(string));
		Map<String, String> convertMap = new HashMap<>();
		convertMap.put(DatabaseCommons.TOTAL_COUNT_KEY, Long.toHexString(this.totalCount));
		convertMap.put(DatabaseCommons.RESULT_LIST_KEY,
				(stringBuilder.length() > 0) ? stringBuilder.substring(1) : Globals.DEFAULT_VALUE_STRING);
		return StringUtils.objectToString(convertMap, StringUtils.StringType.JSON, Boolean.TRUE);
	}

	public void addResult(@Nonnull final BaseObject recordObject) {
		Optional.of(this.marshalRecord(recordObject))
				.filter(StringUtils::notBlank)
				.ifPresent(this.resultList::add);
	}

	private String marshalRecord(@Nonnull final BaseObject recordObject) {
		return Optional.of(StringUtils.objectToString(recordObject, StringUtils.StringType.JSON, Boolean.FALSE))
				.filter(StringUtils::notBlank)
				.map(string -> StringUtils.base64Encode(ConvertUtils.toByteArray(string)))
				.orElse(Globals.DEFAULT_VALUE_STRING);
	}

	private <T> T unmarshalRecord(@Nonnull Class<T> targetClass, @Nonnull final String string) {
		if (StringUtils.isEmpty(string)) {
			return null;
		}
		return Optional.of(StringUtils.base64Decode(string))
				.filter(dataBytes -> dataBytes.length > 0)
				.map(ConvertUtils::toString)
				.map(content -> StringUtils.dataToMap(content, StringUtils.StringType.JSON))
				.map(resultMap -> {
					T record = ObjectUtils.newInstance(targetClass);
					BeanUtils.copyData(resultMap, record);
					return record;
				})
				.orElse(null);
	}

	/**
	 * <h3 class="en-US">Static method for parse cache data string and generate PartialCollection instance</h3>
	 * <h3 class="zh-CN">静态方法用于解析缓存数据字符串并生成查询结果部分集合实例对象</h3>
	 *
	 * @param cacheData <span class="en-US">Cache data string</span>
	 *                  <span class="zh-CN">缓存数据字符串</span>
	 * @return <span class="en-US">Generated PartialCollection instance</span>
	 * <span class="zh-CN">生成的查询结果部分集合实例对象</span>
	 */
	public static QueryResult parse(final String cacheData) {
		if (StringUtils.isEmpty(cacheData)) {
			return null;
		}

		Map<String, Object> convertMap = StringUtils.dataToMap(cacheData, StringUtils.StringType.JSON);
		if (convertMap.isEmpty()) {
			return null;
		}
		String totalCount = (String) convertMap.get(DatabaseCommons.TOTAL_COUNT_KEY);
		String dataList = (String) convertMap.get(DatabaseCommons.RESULT_LIST_KEY);

		List<String> resultList = new ArrayList<>();
		Arrays.stream(StringUtils.tokenizeToStringArray(dataList, RECORD_SPLIT_CHARACTER))
				.map(record -> ConvertUtils.toString(StringUtils.base64Decode(record)))
				.filter(StringUtils::notBlank)
				.forEach(resultList::add);

		QueryResult queryResult = new QueryResult();
		queryResult.setTotalCount(Long.parseLong(totalCount, 16));
		queryResult.setResultList(resultList);

		return queryResult;
	}
}
