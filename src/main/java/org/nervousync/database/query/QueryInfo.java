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
import org.nervousync.database.enumerations.lock.LockOption;
import org.nervousync.database.query.table.QueryTable;
import org.nervousync.database.query.group.GroupByColumn;
import org.nervousync.database.query.orderby.OrderByColumn;

import java.util.List;

/**
 * <h2 class="en-US">Query information define</h2>
 * <h2 class="zh-CN">查询条件信息定义</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
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
    private static final long serialVersionUID = 2260856815709263794L;
    /**
     * <span class="en-US">Query table define</span>
     * <span class="zh-CN">查询数据表信息定义</span>
     */
    @XmlElement(name = "query_table")
    private QueryTable queryTable;
    /**
     * <span class="en-US">Query order by columns list</span>
     * <span class="zh-CN">查询排序数据列列表</span>
     */
    @XmlElement(name = "order_by_column")
    @XmlElementWrapper(name = "order_by_column_list")
    private List<OrderByColumn> orderByColumns;
    /**
     * <span class="en-US">Query group by columns list</span>
     * <span class="zh-CN">查询分组数据列列表</span>
     */
    @XmlElement(name = "group_by_column")
    @XmlElementWrapper(name = "group_by_column_list")
    private List<GroupByColumn> groupByColumns;
    /**
     * <span class="en-US">Query page limit</span>
     * <span class="zh-CN">查询分页记录数</span>
     */
    @XmlElement(name = "page_limit")
    private int pageLimit;
    /**
     * <span class="en-US">Query offset value</span>
     * <span class="zh-CN">查询起始记录数</span>
     */
    @XmlElement
    private int offset;
    /**
     * <span class="en-US">Query result can cacheable</span>
     * <span class="zh-CN">查询结果可以缓存</span>
     */
    @XmlElement
    private boolean cacheables;
    /**
     * <span class="en-US">Query result for update</span>
     * <span class="zh-CN">查询结果用于批量更新记录</span>
     */
    @XmlElement(name = "for_update")
    private boolean forUpdate;
    /**
     * <span class="en-US">Query record lock option</span>
     * <span class="zh-CN">查询记录锁定选项</span>
     */
    @XmlElement(name = "lock_option")
    private LockOption lockOption;

    /**
     * <h3 class="en-US">Constructor method for query information define</h3>
     * <h3 class="zh-CN">查询条件信息的构造方法</h3>
     */
    public QueryInfo() {
    }

    /**
     * <h3 class="en-US">Getter method for query table define</h3>
     * <h3 class="zh-CN">查询数据表信息定义的Getter方法</h3>
     *
     * @return <span class="en-US">Query table define</span>
     * <span class="zh-CN">查询数据表信息定义</span>
     */
    public QueryTable getQueryTable() {
        return queryTable;
    }

    /**
     * <h3 class="en-US">Setter method for query table define</h3>
     * <h3 class="zh-CN">查询数据表信息定义的Setter方法</h3>
     *
     * @param queryTable <span class="en-US">Query table define</span>
     *                   <span class="zh-CN">查询数据表信息定义</span>
     */
    public void setQueryTable(QueryTable queryTable) {
        this.queryTable = queryTable;
    }

    /**
     * <h3 class="en-US">Getter method for query order by columns list</h3>
     * <h3 class="zh-CN">查询排序数据列列表的Getter方法</h3>
     *
     * @return <span class="en-US">Query order by columns list</span>
     * <span class="zh-CN">查询排序数据列列表</span>
     */
    public List<OrderByColumn> getOrderByColumns() {
        return orderByColumns;
    }

    /**
     * <h3 class="en-US">Setter method for query order by columns list</h3>
     * <h3 class="zh-CN">查询排序数据列列表的Setter方法</h3>
     *
     * @param orderByColumns <span class="en-US">Query order by columns list</span>
     *                       <span class="zh-CN">查询排序数据列列表</span>
     */
    public void setOrderByColumns(List<OrderByColumn> orderByColumns) {
        this.orderByColumns = orderByColumns;
    }

    /**
     * <h3 class="en-US">Getter method for query group by columns list</h3>
     * <h3 class="zh-CN">查询分组数据列列表的Getter方法</h3>
     *
     * @return <span class="en-US">Query group by columns list</span>
     * <span class="zh-CN">查询分组数据列列表</span>
     */
    public List<GroupByColumn> getGroupByColumns() {
        return groupByColumns;
    }

    /**
     * <h3 class="en-US">Setter method for query group by columns list</h3>
     * <h3 class="zh-CN">查询分组数据列列表的Setter方法</h3>
     *
     * @param groupByColumns <span class="en-US">Query group by columns list</span>
     *                       <span class="zh-CN">查询分组数据列列表</span>
     */
    public void setGroupByColumns(List<GroupByColumn> groupByColumns) {
        this.groupByColumns = groupByColumns;
    }

    /**
     * <h3 class="en-US">Getter method for query page limit</h3>
     * <h3 class="zh-CN">查询分页记录数的Getter方法</h3>
     *
     * @return <span class="en-US">Query page limit</span>
     * <span class="zh-CN">查询分页记录数</span>
     */
    public int getPageLimit() {
        return pageLimit;
    }

    /**
     * <h3 class="en-US">Setter method for query page limit</h3>
     * <h3 class="zh-CN">查询分页记录数的Setter方法</h3>
     *
     * @param pageLimit <span class="en-US">Query page limit</span>
     *                  <span class="zh-CN">查询分页记录数</span>
     */
    public void setPageLimit(int pageLimit) {
        this.pageLimit = pageLimit;
    }

    /**
     * <h3 class="en-US">Getter method for query offset value</h3>
     * <h3 class="zh-CN">查询起始记录数的Getter方法</h3>
     *
     * @return <span class="en-US">Query offset value</span>
     * <span class="zh-CN">查询起始记录数</span>
     */
    public int getOffset() {
        return offset;
    }

    /**
     * <h3 class="en-US">Setter method for query offset value</h3>
     * <h3 class="zh-CN">查询起始记录数的Setter方法</h3>
     *
     * @param offset <span class="en-US">Query offset value</span>
     *               <span class="zh-CN">查询起始记录数</span>
     */
    public void setOffset(int offset) {
        this.offset = offset;
    }

    /**
     * <h3 class="en-US">Getter method for query result can cacheable</h3>
     * <h3 class="zh-CN">查询结果可以缓存的Getter方法</h3>
     *
     * @return <span class="en-US">Query result can cacheable</span>
     * <span class="zh-CN">查询结果可以缓存</span>
     */
    public boolean isCacheables() {
        return cacheables;
    }

    /**
     * <h3 class="en-US">Setter method for query result can cacheable</h3>
     * <h3 class="zh-CN">查询结果可以缓存的Setter方法</h3>
     *
     * @param cacheables <span class="en-US">Query result can cacheable</span>
     *                   <span class="zh-CN">查询结果可以缓存</span>
     */
    public void setCacheables(boolean cacheables) {
        this.cacheables = cacheables;
    }

    /**
     * <h3 class="en-US">Getter method for query result for update</h3>
     * <h3 class="zh-CN">查询结果用于批量更新记录的Getter方法</h3>
     *
     * @return <span class="en-US">Query result for update</span>
     * <span class="zh-CN">查询结果用于批量更新记录</span>
     */
    public boolean isForUpdate() {
        return forUpdate;
    }

    /**
     * <h3 class="en-US">Setter method for query result for update</h3>
     * <h3 class="zh-CN">查询结果用于批量更新记录的Setter方法</h3>
     *
     * @param forUpdate <span class="en-US">Query result for update</span>
     *                  <span class="zh-CN">查询结果用于批量更新记录</span>
     */
    public void setForUpdate(boolean forUpdate) {
        this.forUpdate = forUpdate;
    }

    /**
     * <h3 class="en-US">Getter method for query record lock option</h3>
     * <h3 class="zh-CN">查询记录锁定选项的Getter方法</h3>
     *
     * @return <span class="en-US">Query record lock option</span>
     * <span class="zh-CN">查询记录锁定选项</span>
     */
    public LockOption getLockOption() {
        return lockOption;
    }

    /**
     * <h3 class="en-US">Setter method for query record lock option</h3>
     * <h3 class="zh-CN">查询记录锁定选项的Setter方法</h3>
     *
     * @param lockOption <span class="en-US">Query record lock option</span>
     *                   <span class="zh-CN">查询记录锁定选项</span>
     */
    public void setLockOption(LockOption lockOption) {
        this.lockOption = lockOption;
    }

    /**
     * <h3 class="en-US">Check current query information is pager query</h3>
     * <h3 class="zh-CN">检查当前的查询信息是分页查询</h3>
     *
     * @return <span class="en-US">Check result</span>
     * <span class="zh-CN">检查结果</span>
     */
    public boolean pagerQuery() {
        return this.offset != Globals.DEFAULT_VALUE_INT && this.pageLimit != Globals.DEFAULT_VALUE_INT;
    }
}
