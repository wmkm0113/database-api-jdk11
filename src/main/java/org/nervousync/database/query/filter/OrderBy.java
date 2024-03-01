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
package org.nervousync.database.query.filter;

import jakarta.annotation.Nonnull;
import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.nervousync.beans.transfer.basic.ClassAdapter;
import org.nervousync.database.enumerations.query.OrderType;
import org.nervousync.database.query.core.SortedItem;
import org.nervousync.utils.ObjectUtils;

/**
 * <h2 class="en-US">Query order by column define</h2>
 * <h2 class="zh-CN">查询排序列信息定义</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Sep 14， 2020 17:15:28 $
 */
@XmlType(name = "order_by", namespace = "https://nervousync.org/schemas/query")
@XmlRootElement(name = "order_by", namespace = "https://nervousync.org/schemas/query")
@XmlAccessorType(XmlAccessType.NONE)
public final class OrderBy extends SortedItem {
	/**
	 * <span class="en-US">Serial version UID</span>
	 * <span class="zh-CN">序列化UID</span>
	 */
	private static final long serialVersionUID = 297370462508507383L;
	/**
	 * <span class="en-US">Entity class</span>
	 * <span class="zh-CN">实体类</span>
	 */
	@XmlElement(name = "entity_class")
	@XmlJavaTypeAdapter(ClassAdapter.class)
	private Class<?> entityClass;
	/**
	 * <span class="en-US">Identify key</span>
	 * <span class="zh-CN">识别代码</span>
	 */
	@XmlElement(name = "identify_key")
	private String identifyKey;
	/**
	 * <span class="en-US">Query order type</span>
	 * <span class="zh-CN">查询结果集排序类型</span>
	 */
	@XmlElement(name = "order_type")
	private OrderType orderType;

	/**
	 * <h3 class="en-US">Constructor method for query order by column define</h3>
	 * <h3 class="zh-CN">查询排序列信息定义的构造方法</h3>
	 */
	public OrderBy() {
	}

	/**
	 * <h3 class="en-US">Constructor method for query order by column define</h3>
	 * <h3 class="zh-CN">查询排序列信息定义的构造方法</h3>
	 *
	 * @param entityClass <span class="en-US">Entity class</span>
	 *                    <span class="zh-CN">实体类</span>
	 * @param identifyKey <span class="en-US">Identify key</span>
	 *                    <span class="zh-CN">识别代码</span>
	 * @param orderType   <span class="en-US">Query order type</span>
	 *                    <span class="zh-CN">查询结果集排序类型</span>
	 * @param sortCode    <span class="en-US">Sort code</span>
	 *                    <span class="zh-CN">排序代码</span>
	 */
	public OrderBy(@Nonnull final Class<?> entityClass, @Nonnull final String identifyKey,
	               @Nonnull final OrderType orderType, final int sortCode) {
		this.entityClass = entityClass;
		this.identifyKey = identifyKey;
		this.orderType = orderType;
		super.setSortCode(sortCode);
	}

	/**
	 * <h3 class="en-US">Getter method for entity class</h3>
	 * <h3 class="zh-CN">实体类的Getter方法</h3>
	 *
	 * @return <span class="en-US">Entity class</span>
	 * <span class="zh-CN">实体类</span>
	 */
	public Class<?> getEntityClass() {
		return entityClass;
	}

	/**
	 * <h3 class="en-US">Setter method for entity class</h3>
	 * <h3 class="zh-CN">实体类的Setter方法</h3>
	 *
	 * @param entityClass <span class="en-US">Entity class</span>
	 *                    <span class="zh-CN">实体类</span>
	 */
	public void setEntityClass(Class<?> entityClass) {
		this.entityClass = entityClass;
	}

	/**
	 * <h3 class="en-US">Getter method for identify key</h3>
	 * <h3 class="zh-CN">识别代码的Getter方法</h3>
	 *
	 * @return <span class="en-US">Identify key</span>
	 * <span class="zh-CN">识别代码</span>
	 */
	public String getIdentifyKey() {
		return identifyKey;
	}

	/**
	 * <h3 class="en-US">Setter method for identify key</h3>
	 * <h3 class="zh-CN">识别代码的Setter方法</h3>
	 *
	 * @param identifyKey <span class="en-US">Identify key</span>
	 *                    <span class="zh-CN">识别代码</span>
	 */
	public void setIdentifyKey(String identifyKey) {
		this.identifyKey = identifyKey;
	}

	/**
	 * <h3 class="en-US">Getter method for query order type</h3>
	 * <h3 class="zh-CN">查询结果集排序类型的Getter方法</h3>
	 *
	 * @return <span class="en-US">Query order type</span>
	 * <span class="zh-CN">查询结果集排序类型</span>
	 */
	public OrderType getOrderType() {
		return orderType;
	}

	/**
	 * <h3 class="en-US">Setter method for query order type</h3>
	 * <h3 class="zh-CN">查询结果集排序类型的Setter方法</h3>
	 *
	 * @param orderType <span class="en-US">Query order type</span>
	 *                  <span class="zh-CN">查询结果集排序类型</span>
	 */
	public void setOrderType(OrderType orderType) {
		this.orderType = orderType;
	}

	/**
	 * <h3 class="en-US">Checks whether the given parameter value matches the current information</h3>
	 * <h3 class="zh-CN">检查给定的参数值是否与当前信息匹配</h3>
	 *
	 * @param entityClass <span class="en-US">Entity class</span>
	 *                    <span class="zh-CN">实体类</span>
	 * @param identifyKey <span class="en-US">Identify key</span>
	 *                    <span class="zh-CN">识别代码</span>
	 * @return <span class="en-US">Match result</span>
	 * <span class="zh-CN">匹配结果</span>
	 */
	public boolean match(final Class<?> entityClass, final String identifyKey) {
		return ObjectUtils.nullSafeEquals(entityClass, this.entityClass)
				&& ObjectUtils.nullSafeEquals(identifyKey, this.identifyKey);
	}

	/**
	 * <h3 class="en-US">Checks whether the given parameter value matches the current information</h3>
	 * <h3 class="zh-CN">检查给定的参数值是否与当前信息匹配</h3>
	 *
	 * @param orderBy <span class="en-US">Order by information</span>
	 *                <span class="zh-CN">排序信息</span>
	 * @return <span class="en-US">Match result</span>
	 * <span class="zh-CN">匹配结果</span>
	 */
	public boolean match(final OrderBy orderBy) {
		return this.match(orderBy.getEntityClass(), orderBy.getIdentifyKey());
	}
}
