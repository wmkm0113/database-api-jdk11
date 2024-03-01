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

package org.nervousync.database.query.core;

import jakarta.annotation.Nonnull;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlTransient;
import org.nervousync.beans.core.BeanObject;
import org.nervousync.commons.Globals;
import org.nervousync.database.enumerations.query.OrderType;

import java.util.Comparator;

/**
 * <h2 class="en-US">Abstract query item define</h2>
 * <h2 class="zh-CN">抽象查询项信息定义</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Oct 9, 2020 11:30:54 $
 */
@XmlTransient
public abstract class SortedItem extends BeanObject {
    /**
     * <span class="en-US">Serial version UID</span>
     * <span class="zh-CN">序列化UID</span>
     */
    private static final long serialVersionUID = -8808799678096056072L;

    /**
     * <span class="en-US">Sort code</span>
     * <span class="zh-CN">排序代码</span>
     */
    @XmlElement(name = "sort_code")
    private int sortCode;

    /**
     * <h3 class="en-US">Getter method for sort code</h3>
     * <h3 class="zh-CN">排序代码的Getter方法</h3>
     *
     * @return <span class="en-US">Sort code</span>
     * <span class="zh-CN">排序代码</span>
     */
    public int getSortCode() {
        return sortCode;
    }

    /**
     * <h3 class="en-US">Setter method for sort code</h3>
     * <h3 class="zh-CN">排序代码的Setter方法</h3>
     *
     * @param sortCode <span class="en-US">Sort code</span>
     *                 <span class="zh-CN">排序代码</span>
     */
    public void setSortCode(int sortCode) {
        this.sortCode = sortCode;
    }

    public static SortedItemComparator asc() {
        return new SortedItemComparator(OrderType.ASC);
    }

    public static SortedItemComparator desc() {
        return new SortedItemComparator(OrderType.DESC);
    }

    public static final class SortedItemComparator implements Comparator<SortedItem> {

        private final OrderType orderType;

        private SortedItemComparator(@Nonnull final OrderType orderType) {
            this.orderType = orderType;
        }

        @Override
        public int compare(final SortedItem o1, final SortedItem o2) {
            int compare = Integer.compare(o1.getSortCode(), o2.getSortCode());
            if (OrderType.DESC.equals(this.orderType)) {
                compare *= Globals.DEFAULT_VALUE_INT;
            }
            return compare;
        }
    }
}
