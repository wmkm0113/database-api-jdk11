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
package org.nervousync.database.exceptions.record;

import org.nervousync.exceptions.AbstractException;

/**
 * <h2 class="en-US">Abstract Record Exception</h2>
 * <h2 class="zh-CN">抽象数据记录异常</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Aug 25, 2020 15:46:54 $
 */
public abstract class RecordException extends AbstractException {
    /**
     * <span class="en-US">Serial version UID</span>
     * <span class="zh-CN">序列化UID</span>
     */
    private static final long serialVersionUID = 2866407847994405260L;

    /**
     * <h3 class="en-US">Protect constructor method for RecordException</h3>
     * <span class="en-US">Create a new RecordException with the specified message.</span>
     * <h3 class="zh-CN">RecordException构造方法</h3>
     * <span class="zh-CN">使用特定的信息创建 RecordException 实例对象。</span>
     *
     * @param errorCode   <span class="en-US">Error identified code</span>
     *                    <span class="zh-CN">错误识别代码</span>
     * @param messageKey  <span class="en-US">Message identify key</span>
     *                    <span class="zh-CN">信息识别键值</span>
     * @param collections <span class="en-US">given parameters of information formatter</span>
     *                    <span class="zh-CN">用于资源信息格式化的参数</span>
     */
    protected RecordException(long errorCode, String messageKey, Object... collections) {
        super(errorCode, messageKey, collections);
    }

    /**
     * <h3 class="en-US">Protect constructor method for RecordException</h3>
     * <span class="en-US">Create a new RecordException with the specified message and root cause.</span>
     * <h3 class="zh-CN">RecordException构造方法</h3>
     * <span class="zh-CN">使用特定的信息以及异常信息对象实例创建 RecordException 实例对象。</span>
     *
     * @param errorCode   <span class="en-US">Error identified code</span>
     *                    <span class="zh-CN">错误识别代码</span>
     * @param messageKey  <span class="en-US">Message identify key</span>
     *                    <span class="zh-CN">信息识别键值</span>
     * @param cause       <span class="en-US">The root cause</span>
     *                    <span class="zh-CN">异常信息对象实例</span>
     * @param collections <span class="en-US">given parameters of information formatter</span>
     *                    <span class="zh-CN">用于资源信息格式化的参数</span>
     */
    protected RecordException(final long errorCode, final String messageKey,
                              final Throwable cause, final Object... collections) {
        super(errorCode, messageKey, cause, collections);
    }
}
