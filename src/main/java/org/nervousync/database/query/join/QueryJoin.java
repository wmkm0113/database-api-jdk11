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

package org.nervousync.database.query.join;

import jakarta.annotation.Nonnull;
import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.nervousync.beans.transfer.basic.ClassAdapter;
import org.nervousync.database.enumerations.join.JoinType;
import org.nervousync.database.query.core.SortedItem;
import org.nervousync.utils.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * <h2 class="en-US">Query join information define</h2>
 * <h2 class="zh-CN">查询关联信息定义</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Jul 30, 2023 15:57:33 $
 */
@XmlType(name = "query_join", namespace = "https://nervousync.org/schemas/query")
@XmlRootElement(name = "query_join", namespace = "https://nervousync.org/schemas/query")
@XmlAccessorType(XmlAccessType.NONE)
public final class QueryJoin extends SortedItem {

	private static final long serialVersionUID = 8868119078098035574L;

	/**
	 * <span class="en-US">Driver table entity class</span>
	 * <span class="zh-CN">驱动表实体类</span>
	 */
	@XmlElement(name = "main_entity")
	@XmlJavaTypeAdapter(ClassAdapter.class)
	private Class<?> mainEntity;
	/**
	 * <span class="en-US">Reference table entity class</span>
	 * <span class="zh-CN">关联表实体类</span>
	 */
	@XmlElement(name = "join_entity")
	@XmlJavaTypeAdapter(ClassAdapter.class)
	private Class<?> joinEntity;
	/**
	 * <span class="en-US">Table join type</span>
	 * <span class="zh-CN">数据表关联类型</span>
	 */
	@XmlElement(name = "join_type")
	private JoinType joinType;
	/**
	 * <span class="en-US">Join columns list</span>
	 * <span class="zh-CN">关联列信息列表</span>
	 */
	@XmlElement(name = "join_info")
	@XmlElementWrapper(name = "join_info_list")
	private List<JoinInfo> joinInfos;

	/**
	 * <h3 class="en-US">Private constructor method for query join information define</h3>
	 * <h3 class="zh-CN">查询关联信息定义的私有构造方法</h3>
	 */
	public QueryJoin() {
		this.joinInfos = new ArrayList<>();
	}

	/**
	 * <h3 class="en-US">Private constructor method for query join information define</h3>
	 * <h3 class="zh-CN">查询关联信息定义的私有构造方法</h3>
	 *
	 * @param mainEntity <span class="en-US">Driver table entity class</span>
	 *                   <span class="zh-CN">驱动表实体类</span>
	 * @param joinEntity <span class="en-US">Reference table entity class</span>
	 *                   <span class="zh-CN">关联表实体类</span>
	 * @param joinType   <span class="en-US">Table join type</span>
	 *                   <span class="zh-CN">数据表关联类型</span>
	 * @param joinInfos  <span class="en-US">Join columns list</span>
	 *                   <span class="zh-CN">关联列信息列表</span>
	 */
	public QueryJoin(@Nonnull final Class<?> mainEntity, @Nonnull final Class<?> joinEntity,
	                 @Nonnull final JoinType joinType, @Nonnull final List<JoinInfo> joinInfos) {
		this.mainEntity = mainEntity;
		this.joinEntity = joinEntity;
		this.joinType = joinType;
		this.joinInfos = joinInfos;
	}

	/**
	 * <h3 class="en-US">Getter method for driver table entity class</h3>
	 * <h3 class="zh-CN">驱动表实体类的Getter方法</h3>
	 *
	 * @return <span class="en-US">Driver table entity class</span>
	 * <span class="zh-CN">驱动表实体类</span>
	 */
	public Class<?> getMainEntity() {
		return mainEntity;
	}

	/**
	 * <h3 class="en-US">Setter method for driver table entity class</h3>
	 * <h3 class="zh-CN">驱动表实体类的Setter方法</h3>
	 *
	 * @param mainEntity <span class="en-US">Driver table entity class</span>
	 *                   <span class="zh-CN">驱动表实体类</span>
	 */
	public void setMainEntity(Class<?> mainEntity) {
		this.mainEntity = mainEntity;
	}

	/**
	 * <h3 class="en-US">Getter method for reference table entity class</h3>
	 * <h3 class="zh-CN">关联表实体类的Getter方法</h3>
	 *
	 * @return <span class="en-US">Reference table entity class</span>
	 * <span class="zh-CN">关联表实体类</span>
	 */
	public Class<?> getJoinEntity() {
		return joinEntity;
	}

	/**
	 * <h3 class="en-US">Setter method for reference table entity class</h3>
	 * <h3 class="zh-CN">关联表实体类的Setter方法</h3>
	 *
	 * @param joinEntity <span class="en-US">Reference table entity class</span>
	 *                   <span class="zh-CN">关联表实体类</span>
	 */
	public void setJoinEntity(Class<?> joinEntity) {
		this.joinEntity = joinEntity;
	}

	/**
	 * <h3 class="en-US">Getter method for table join type</h3>
	 * <h3 class="zh-CN">数据表关联类型的Getter方法</h3>
	 *
	 * @return <span class="en-US">Table join type</span>
	 * <span class="zh-CN">数据表关联类型</span>
	 */
	public JoinType getJoinType() {
		return joinType;
	}

	/**
	 * <h3 class="en-US">Setter method for table join type</h3>
	 * <h3 class="zh-CN">数据表关联类型的Setter方法</h3>
	 *
	 * @param joinType <span class="en-US">Table join type</span>
	 *                 <span class="zh-CN">数据表关联类型</span>
	 */
	public void setJoinType(JoinType joinType) {
		this.joinType = joinType;
	}

	/**
	 * <h3 class="en-US">Getter method for join columns list</h3>
	 * <h3 class="zh-CN">关联列信息列表的Getter方法</h3>
	 *
	 * @return <span class="en-US">Join columns list</span>
	 * <span class="zh-CN">关联列信息列表</span>
	 */
	public List<JoinInfo> getJoinInfos() {
		return joinInfos;
	}

	/**
	 * <h3 class="en-US">Setter method for join columns list</h3>
	 * <h3 class="zh-CN">关联列信息列表的Setter方法</h3>
	 *
	 * @param joinInfos <span class="en-US">Join columns list</span>
	 *                  <span class="zh-CN">关联列信息列表</span>
	 */
	public void setJoinInfos(List<JoinInfo> joinInfos) {
		this.joinInfos = joinInfos;
	}

	/**
	 * <h3 class="en-US">Match the given join table is same as current information</h3>
	 * <h3 class="zh-CN">检查给定的关联表信息是否与当前信息一致</h3>
	 *
	 * @param mainEntity <span class="en-US">Driver table entity class</span>
	 *                   <span class="zh-CN">驱动表实体类</span>
	 * @param joinEntity <span class="en-US">Reference table entity class</span>
	 *                   <span class="zh-CN">关联表实体类</span>
	 * @return <span class="en-US">Match result</span>
	 * <span class="zh-CN">匹配结果</span>
	 */
	public boolean match(@Nonnull final Class<?> mainEntity, @Nonnull final Class<?> joinEntity) {
		return ObjectUtils.nullSafeEquals(mainEntity, this.mainEntity)
				&& ObjectUtils.nullSafeEquals(joinEntity, this.joinEntity);
	}
}
