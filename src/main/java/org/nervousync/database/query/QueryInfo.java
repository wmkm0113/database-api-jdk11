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

import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.nervousync.beans.core.BeanObject;
import org.nervousync.beans.transfer.basic.ClassAdapter;
import org.nervousync.commons.Globals;
import org.nervousync.database.enumerations.lock.LockOption;
import org.nervousync.database.query.condition.Condition;
import org.nervousync.database.query.condition.impl.ColumnCondition;
import org.nervousync.database.query.condition.impl.GroupCondition;
import org.nervousync.database.query.core.AbstractItem;
import org.nervousync.database.query.core.SortedItem;
import org.nervousync.database.query.filter.GroupBy;
import org.nervousync.database.query.filter.OrderBy;
import org.nervousync.database.query.item.ColumnItem;
import org.nervousync.database.query.item.FunctionItem;
import org.nervousync.database.query.item.QueryItem;
import org.nervousync.database.query.join.QueryJoin;

import java.util.ArrayList;
import java.util.List;

/**
 * <h2 class="en-US">Query information define</h2>
 * <h2 class="zh-CN">查询信息定义</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Oct 28, 2020 11:46:08 $
 */
@XmlType(name = "query_info", namespace = "https://nervousync.org/schemas/query")
@XmlRootElement(name = "query_info", namespace = "https://nervousync.org/schemas/query")
@XmlAccessorType(XmlAccessType.NONE)
public final class QueryInfo extends BeanObject {

    /**
     * <span class="en-US">Serial version UID</span>
     * <span class="zh-CN">序列化UID</span>
     */
	private static final long serialVersionUID = 549973159743148887L;

	/**
	 * <span class="en-US">Query name</span>
	 * <span class="zh-CN">查询名称</span>
	 */
	@XmlElement(name = "identify_name")
	private String identifyName = Globals.DEFAULT_VALUE_STRING;
	/**
	 * <span class="en-US">Query driven table entity class</span>
	 * <span class="zh-CN">查询驱动表实体类</span>
	 */
	@XmlElement(name = "main_entity")
	@XmlJavaTypeAdapter(ClassAdapter.class)
	private Class<?> mainEntity;
	/**
	 * <span class="en-US">Related query information list</span>
	 * <span class="zh-CN">关联查询信息列表</span>
	 */
	@XmlElement(name = "query_join", namespace = "https://nervousync.org/schemas/query")
	@XmlElementWrapper(name = "join_list")
	private List<QueryJoin> queryJoins;
	/**
	 * <span class="en-US">Query item instance list</span>
	 * <span class="zh-CN">查询项目实例对象列表</span>
	 */
	@XmlElements({
			@XmlElement(name = "column_item", type = ColumnItem.class, namespace = "https://nervousync.org/schemas/query"),
			@XmlElement(name = "function_item", type = FunctionItem.class, namespace = "https://nervousync.org/schemas/query"),
			@XmlElement(name = "query_item", type = QueryItem.class, namespace = "https://nervousync.org/schemas/query")
	})
	@XmlElementWrapper(name = "item_list")
	private List<AbstractItem> itemList;
	/**
	 * <span class="en-US">Query condition instance list</span>
	 * <span class="zh-CN">查询条件实例对象列表</span>
	 */
	@XmlElements({
			@XmlElement(name = "column_condition", type = ColumnCondition.class, namespace = "https://nervousync.org/schemas/query"),
			@XmlElement(name = "group_condition", type = GroupCondition.class, namespace = "https://nervousync.org/schemas/query")
	})
	@XmlElementWrapper(name = "condition_list")
	private List<Condition> conditionList;
	/**
	 * <span class="en-US">Query order by columns list</span>
	 * <span class="zh-CN">查询排序数据列列表</span>
	 */
	@XmlElement(name = "order_by")
	@XmlElementWrapper(name = "order_list")
	private List<OrderBy> orderByList;
	/**
	 * <span class="en-US">Query group by columns list</span>
	 * <span class="zh-CN">查询分组数据列列表</span>
	 */
	@XmlElement(name = "group_by")
	@XmlElementWrapper(name = "group_list")
	private List<GroupBy> groupByList;
	/**
	 * <span class="en-US">Query result can cacheable</span>
	 * <span class="zh-CN">查询结果可以缓存</span>
	 */
	@XmlElement
	private boolean cacheables = Boolean.FALSE;
	/**
	 * <span class="en-US">Query result for update</span>
	 * <span class="zh-CN">查询结果用于批量更新记录</span>
	 */
	@XmlElement(name = "for_update")
	private boolean forUpdate = Boolean.FALSE;
	/**
	 * <span class="en-US">Query record lock option</span>
	 * <span class="zh-CN">查询记录锁定选项</span>
	 */
	@XmlElement(name = "lock_option")
	private LockOption lockOption = LockOption.NONE;
    /**
     * <span class="en-US">Current page number</span>
     * <span class="zh-CN">当前页数</span>
     */
	@XmlElement(name = "page_number")
    private int pageNo;
    /**
     * <span class="en-US">Page limit records count</span>
     * <span class="zh-CN">每页的记录数</span>
     */
	@XmlElement(name = "page_limit")
    private int pageLimit;

	/**
	 * <h4 class="en-US">Constructor method for query information define</h4>
	 * <h4 class="zh-CN">查询条件信息的构造方法</h4>
	 */
	public QueryInfo() {
		this.queryJoins = new ArrayList<>();
		this.itemList = new ArrayList<>();
		this.conditionList = new ArrayList<>();
	}

	/**
	 * <h4 class="en-US">Getter method for query name</h4>
	 * <h4 class="zh-CN">查询名称的Getter方法</h4>
	 *
	 * @return <span class="en-US">Query name</span>
	 * <span class="zh-CN">查询名称</span>
	 */
	public String getIdentifyName() {
		return identifyName;
	}

	/**
	 * <h4 class="en-US">Setter method for query name</h4>
	 * <h4 class="zh-CN">查询名称的Setter方法</h4>
	 *
	 * @param identifyName <span class="en-US">Query name</span>
	 *                     <span class="zh-CN">查询名称</span>
	 */
	public void setIdentifyName(String identifyName) {
		this.identifyName = identifyName;
	}

	/**
	 * <h4 class="en-US">Getter method for query driven table entity class</h4>
	 * <h4 class="zh-CN">查询驱动表实体类的Getter方法</h4>
	 *
	 * @return <span class="en-US">Query driven table entity class</span>
	 * <span class="zh-CN">查询驱动表实体类</span>
	 */
	public Class<?> getMainEntity() {
		return mainEntity;
	}

	/**
	 * <h4 class="en-US">Setter method for query driven table entity class</h4>
	 * <h4 class="zh-CN">查询驱动表实体类的Setter方法</h4>
	 *
	 * @param mainEntity <span class="en-US">Query driven table entity class</span>
	 *                   <span class="zh-CN">查询驱动表实体类</span>
	 */
	public void setMainEntity(Class<?> mainEntity) {
		this.mainEntity = mainEntity;
	}

	/**
	 * <h4 class="en-US">Getter method for related query information list</h4>
	 * <h4 class="zh-CN">关联查询信息列表的Getter方法</h4>
	 *
	 * @return <span class="en-US">Related query information list</span>
	 * <span class="zh-CN">关联查询信息列表</span>
	 */
	public List<QueryJoin> getQueryJoins() {
		return queryJoins;
	}

	/**
	 * <h4 class="en-US">Setter method for related query information list</h4>
	 * <h4 class="zh-CN">关联查询信息列表的Setter方法</h4>
	 *
	 * @param queryJoins <span class="en-US">Related query information list</span>
	 *                   <span class="zh-CN">关联查询信息列表</span>
	 */
	public void setQueryJoins(List<QueryJoin> queryJoins) {
		this.queryJoins = queryJoins;
	}

	/**
	 * <h4 class="en-US">Getter method for query item instance list</h4>
	 * <h4 class="zh-CN">查询项目实例对象列表的Getter方法</h4>
	 *
	 * @return <span class="en-US">Query item instance list</span>
	 * <span class="zh-CN">查询项目实例对象列表</span>
	 */
	public List<AbstractItem> getItemList() {
		return itemList;
	}

	/**
	 * <h4 class="en-US">Setter method for query item instance list</h4>
	 * <h4 class="zh-CN">查询项目实例对象列表的Setter方法</h4>
	 *
	 * @param itemList <span class="en-US">Query item instance list</span>
	 *                 <span class="zh-CN">查询项目实例对象列表</span>
	 */
	public void setItemList(List<AbstractItem> itemList) {
		this.itemList = itemList;
		this.itemList.sort(SortedItem.desc());

	}

	/**
	 * <h4 class="en-US">Getter method for query condition instance list</h4>
	 * <h4 class="zh-CN">查询条件实例对象列表的Getter方法</h4>
	 *
	 * @return <span class="en-US">Query condition instance list</span>
	 * <span class="zh-CN">查询条件实例对象列表</span>
	 */
	public List<Condition> getConditionList() {
		return conditionList;
	}

	/**
	 * <h4 class="en-US">Setter method for query condition instance list</h4>
	 * <h4 class="zh-CN">查询条件实例对象列表的Setter方法</h4>
	 *
	 * @param conditionList <span class="en-US">Query condition instance list</span>
	 *                      <span class="zh-CN">查询条件实例对象列表</span>
	 */
	public void setConditionList(List<Condition> conditionList) {
		this.conditionList = conditionList;
		this.conditionList.sort(SortedItem.desc());
	}

	/**
	 * <h4 class="en-US">Getter method for query order by columns list</h4>
	 * <h4 class="zh-CN">查询排序数据列列表的Getter方法</h4>
	 *
	 * @return <span class="en-US">Query order by columns list</span>
	 * <span class="zh-CN">查询排序数据列列表</span>
	 */
	public List<OrderBy> getOrderByList() {
		return orderByList;
	}

	/**
	 * <h4 class="en-US">Setter method for query order by columns list</h4>
	 * <h4 class="zh-CN">查询排序数据列列表的Setter方法</h4>
	 *
	 * @param orderByList <span class="en-US">Query order by columns list</span>
	 *                    <span class="zh-CN">查询排序数据列列表</span>
	 */
	public void setOrderByList(List<OrderBy> orderByList) {
		this.orderByList = orderByList;
		this.orderByList.sort(SortedItem.desc());
	}

	/**
	 * <h4 class="en-US">Getter method for query group by columns list</h4>
	 * <h4 class="zh-CN">查询分组数据列列表的Getter方法</h4>
	 *
	 * @return <span class="en-US">Query group by columns list</span>
	 * <span class="zh-CN">查询分组数据列列表</span>
	 */
	public List<GroupBy> getGroupByList() {
		return groupByList;
	}

	/**
	 * <h4 class="en-US">Setter method for query group by columns list</h4>
	 * <h4 class="zh-CN">查询分组数据列列表的Setter方法</h4>
	 *
	 * @param groupByList <span class="en-US">Query group by columns list</span>
	 *                    <span class="zh-CN">查询分组数据列列表</span>
	 */
	public void setGroupByList(List<GroupBy> groupByList) {
		this.groupByList = groupByList;
		this.groupByList.sort(SortedItem.desc());
	}

	/**
	 * <h4 class="en-US">Getter method for query result can cacheable</h4>
	 * <h4 class="zh-CN">查询结果可以缓存的Getter方法</h4>
	 *
	 * @return <span class="en-US">Query result can cacheable</span>
	 * <span class="zh-CN">查询结果可以缓存</span>
	 */
	public boolean isCacheables() {
		return cacheables;
	}

	/**
	 * <h4 class="en-US">Setter method for query result can cacheable</h4>
	 * <h4 class="zh-CN">查询结果可以缓存的Setter方法</h4>
	 *
	 * @param cacheables <span class="en-US">Query result can cacheable</span>
	 *                   <span class="zh-CN">查询结果可以缓存</span>
	 */
	public void setCacheables(boolean cacheables) {
		this.cacheables = cacheables;
	}

	/**
	 * <h4 class="en-US">Getter method for query result for update</h4>
	 * <h4 class="zh-CN">查询结果用于批量更新记录的Getter方法</h4>
	 *
	 * @return <span class="en-US">Query result for update</span>
	 * <span class="zh-CN">查询结果用于批量更新记录</span>
	 */
	public boolean isForUpdate() {
		return forUpdate;
	}

	/**
	 * <h4 class="en-US">Setter method for query result for update</h4>
	 * <h4 class="zh-CN">查询结果用于批量更新记录的Setter方法</h4>
	 *
	 * @param forUpdate <span class="en-US">Query result for update</span>
	 *                  <span class="zh-CN">查询结果用于批量更新记录</span>
	 */
	public void setForUpdate(boolean forUpdate) {
		this.forUpdate = forUpdate;
	}

	/**
	 * <h4 class="en-US">Getter method for query record lock option</h4>
	 * <h4 class="zh-CN">查询记录锁定选项的Getter方法</h4>
	 *
	 * @return <span class="en-US">Query record lock option</span>
	 * <span class="zh-CN">查询记录锁定选项</span>
	 */
	public LockOption getLockOption() {
		return lockOption;
	}

	/**
	 * <h4 class="en-US">Setter method for query record lock option</h4>
	 * <h4 class="zh-CN">查询记录锁定选项的Setter方法</h4>
	 *
	 * @param lockOption <span class="en-US">Query record lock option</span>
	 *                   <span class="zh-CN">查询记录锁定选项</span>
	 */
	public void setLockOption(LockOption lockOption) {
		this.lockOption = lockOption;
	}

    /**
     * <h4 class="en-US">Getter method for current page number</h4>
     * <h4 class="zh-CN">当前页数的Getter方法</h4>
     *
     * @return <span class="en-US">Current page number</span>
     * <span class="zh-CN">当前页数</span>
     */
    public int getPageNo() {
        return pageNo;
    }

    /**
     * <h4 class="en-US">Setter method for current page number</h4>
     * <h4 class="zh-CN">当前页数的Setter方法</h4>
     *
     * @param pageNo <span class="en-US">Current page number</span>
     *               <span class="zh-CN">当前页数</span>
     */
    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    /**
     * <h4 class="en-US">Getter method for query page limit</h4>
     * <h4 class="zh-CN">查询分页记录数的Getter方法</h4>
     *
     * @return <span class="en-US">Query page limit</span>
     * <span class="zh-CN">查询分页记录数</span>
     */
    public int getPageLimit() {
        return pageLimit;
    }

    /**
     * <h4 class="en-US">Setter method for query page limit</h4>
     * <h4 class="zh-CN">查询分页记录数的Setter方法</h4>
     *
     * @param pageLimit <span class="en-US">Query page limit</span>
     *                  <span class="zh-CN">查询分页记录数</span>
     */
    public void setPageLimit(int pageLimit) {
        this.pageLimit = pageLimit;
    }
}
