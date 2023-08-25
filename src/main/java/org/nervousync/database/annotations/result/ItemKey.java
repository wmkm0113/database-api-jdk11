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
package org.nervousync.database.annotations.result;

import org.nervousync.commons.Globals;

import java.lang.annotation.*;

/**
 * <h2 class="en-US">The annotation of result item</h2>
 * <h2 class="zh-CN">查询结果项的注解</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Ayg 10, 2023 11:01:39 $
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface ItemKey {

    /**
     * @return <span class="en-US">Item key value</span>
     * <span class="zh-CN">结果项识别字符串</span>
     */
    String value() default Globals.DEFAULT_VALUE_STRING;
}
