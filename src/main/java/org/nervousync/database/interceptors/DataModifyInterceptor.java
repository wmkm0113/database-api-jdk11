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

package org.nervousync.database.interceptors;

import net.bytebuddy.asm.Advice;
import org.nervousync.database.entity.core.BaseObject;
import org.nervousync.utils.ReflectionUtils;

import java.lang.reflect.Method;

/**
 * <h2 class="en-US">Data field modified interceptor</h2>
 * <h2 class="zh-CN">属性修改拦截器</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Mar 30, 2016 17:22:51 $
 */
public final class DataModifyInterceptor {

	/**
	 * <h4 class="en-US">Interceptor method</h4>
	 * <h4 class="zh-CN">拦截方法</h4>
	 *
	 * @param method    <span class="en-US">Invoke method</span>
	 *                  <span class="zh-CN">调用方法</span>
	 * @param arguments <span class="en-US">Parameter array for method</span>
	 *                  <span class="zh-CN">方法的参数</span>
	 * @param target    <span class="en-US">Invoke object instance</span>
	 *                  <span class="zh-CN">调用对象实例</span>
	 */
    @Advice.OnMethodEnter
    public static void onMethodEnter(@Advice.Origin Method method, @Advice.AllArguments Object[] arguments,
									 @Advice.This Object target) {
        if (target instanceof BaseObject) {
            String fieldName = ReflectionUtils.fieldName(method.getName());
            if (((BaseObject) target).loadedField(fieldName)) {
                ((BaseObject) target).modifyField(fieldName);
            } else {
                ((BaseObject) target).loadField(fieldName);
            }
        }
    }
}
