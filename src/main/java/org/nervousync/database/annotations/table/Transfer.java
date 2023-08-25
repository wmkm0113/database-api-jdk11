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
package org.nervousync.database.annotations.table;

import org.nervousync.beans.converter.Adapter;

import java.lang.annotation.*;

/**
 * <h2 class="en-US">The annotation of data transfer configure</h2>
 * <h2 class="zh-CN">数据传输配置的注解</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Ayg 7, 2023 15:32:18 $
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Transfer {
    /**
     * @return <span class="en-US">Column order number in Excel</span>
     * <span class="zh-CN">Excel表中的列排列序号</span>
     */
    int order();

    /**
     * @return <span class="en-US">Data converter class</span>
     * <span class="zh-CN">数据转换类</span>
     */
    Class<?> converter() default Adapter.class;

}
