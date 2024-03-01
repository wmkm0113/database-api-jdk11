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

import jakarta.annotation.Nonnull;
import net.bytebuddy.asm.Advice;
import org.nervousync.commons.Globals;
import org.nervousync.database.api.DatabaseClient;
import org.nervousync.database.beans.configs.reference.JoinConfig;
import org.nervousync.database.beans.configs.reference.ReferenceConfig;
import org.nervousync.database.commons.DatabaseUtils;
import org.nervousync.database.entity.EntityManager;
import org.nervousync.database.entity.core.BaseObject;
import org.nervousync.database.exceptions.operate.RetrieveException;
import org.nervousync.database.exceptions.query.QueryException;
import org.nervousync.database.query.QueryResult;
import org.nervousync.database.query.builder.QueryBuilder;
import org.nervousync.database.query.condition.Condition;
import org.nervousync.enumerations.core.ConnectionCode;
import org.nervousync.exceptions.builder.BuilderException;
import org.nervousync.utils.LoggerUtils;
import org.nervousync.utils.ObjectUtils;
import org.nervousync.utils.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.*;

/**
 * <h2 class="en-US">Data field lazy load interceptor</h2>
 * <h2 class="zh-CN">属性懒加载拦截器</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Mar 30, 2016 17:46:19 $
 */
public final class LazyLoadInterceptor {

	private static final LoggerUtils.Logger LOGGER = LoggerUtils.getLogger(LazyLoadInterceptor.class);

	/**
	 * <h3 class="en-US">Interceptor method</h3>
	 * <h3 class="zh-CN">拦截方法</h3>
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
		String fieldName = ReflectionUtils.fieldName(method.getName());
		if ((target instanceof BaseObject) && !((BaseObject) target).isNewObject()
				&& !((BaseObject) target).loadedField(fieldName)) {
			Optional.ofNullable(EntityManager.tableConfig(target.getClass()))
					.ifPresent(tableConfig -> {
						Object fieldValue = null;
						if (tableConfig.isColumn(fieldName)) {
							if (tableConfig.lazyLoad(fieldName)) {
								fieldValue = Optional.ofNullable(DatabaseUtils.readOnlyClient())
										.map(databaseClient ->
												databaseClient.lazyColumn(DatabaseUtils.primaryKeyMap(target),
														target.getClass(), fieldName))
										.orElse(null);
							}
						} else {
							fieldValue =
									Optional.ofNullable(tableConfig.referenceConfig(fieldName))
											.map(referenceConfig ->
													loadReference(referenceConfig, (BaseObject) target,
															method.getReturnType().isArray()))
											.orElse(null);
						}
						ReflectionUtils.setField(fieldName, target, fieldValue);
					});
		}
	}

	private static <T> Object loadReference(@Nonnull final ReferenceConfig<T> referenceConfig,
	                                        @Nonnull final BaseObject record, final boolean returnArray) {
		boolean forUpdate = record.getForUpdate();
		long transactionalCode = record.getTransactionalCode();
		DatabaseClient databaseClient;
		if (forUpdate) {
			if (transactionalCode != Globals.DEFAULT_VALUE_LONG) {
				databaseClient = DatabaseUtils.retrieveClient(transactionalCode);
				if (databaseClient == null) {
					LOGGER.error("Transactional_Not_Found", transactionalCode);
				}
			} else {
				databaseClient = DatabaseUtils.retrieveClient();
			}
		} else {
			databaseClient = DatabaseUtils.readOnlyClient();
		}

		if (databaseClient == null) {
			return null;
		}

		try {
			Class<T> entityClass = referenceConfig.getReferenceClass();
			if (referenceConfig.isReturnArray()) {
				List<Condition> conditionList = new ArrayList<>();
				for (JoinConfig joinConfig : referenceConfig.getJoinColumnList()) {
					conditionList.add(Condition.equalTo(Globals.DEFAULT_VALUE_INT, ConnectionCode.AND,
							entityClass, joinConfig.getReferenceField(),
							ReflectionUtils.getFieldValue(joinConfig.getCurrentField(), record)));
				}
				QueryResult queryResult =
						databaseClient.queryList(QueryBuilder.newQuery(entityClass, forUpdate, conditionList));
				if (returnArray) {
					return queryResult.asList(entityClass).toArray(ObjectUtils.newArray(entityClass));
				} else {
					return queryResult.asList(entityClass);
				}
			} else {
				Map<String, Object> queryMap = new HashMap<>();
				referenceConfig.getJoinColumnList()
						.forEach(joinConfig ->
								queryMap.put(joinConfig.getReferenceField(),
										ReflectionUtils.getFieldValue(joinConfig.getCurrentField(), record)));
				return databaseClient.retrieve(queryMap, entityClass, forUpdate);
			}
		} catch (RetrieveException | BuilderException | QueryException e) {
			LOGGER.error("Lazy_Load_Data_Error");
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Stack_Message_Error", e);
			}
			return null;
		}
	}
}
