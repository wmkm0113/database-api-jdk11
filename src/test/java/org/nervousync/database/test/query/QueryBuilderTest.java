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

package org.nervousync.database.test.query;

import org.junit.jupiter.api.Test;
import org.nervousync.commons.Globals;
import org.nervousync.database.entity.distribute.DistributeReference;
import org.nervousync.database.entity.distribute.TestDistribute;
import org.nervousync.database.entity.relational.RelationalReference;
import org.nervousync.database.entity.relational.TestRelational;
import org.nervousync.database.enumerations.join.JoinType;
import org.nervousync.database.enumerations.lock.LockOption;
import org.nervousync.database.enumerations.query.ConditionCode;
import org.nervousync.database.query.QueryInfo;
import org.nervousync.database.query.builder.QueryBuilder;
import org.nervousync.database.query.condition.Condition;
import org.nervousync.database.query.param.AbstractParameter;
import org.nervousync.database.test.AbstractTest;
import org.nervousync.enumerations.core.ConnectionCode;
import org.nervousync.exceptions.builder.BuilderException;
import org.nervousync.security.factory.SecureFactory;
import org.nervousync.utils.DateTimeUtils;
import org.nervousync.utils.StringUtils;

import java.util.Optional;

public final class QueryBuilderTest extends AbstractTest {

	@Test
	public void test000Builder() throws BuilderException {
		SecureFactory.registerConfig("sensitiveData", SecureFactory.SecureAlgorithm.AES256);
		QueryInfo queryInfo = QueryBuilder.newBuilder(TestRelational.class)
				.joinTable(TestRelational.class, RelationalReference.class)
				.joinTable(TestRelational.class, JoinType.LEFT, TestDistribute.class)
				.joinTable(TestDistribute.class, JoinType.LEFT, DistributeReference.class)
				.addColumn(TestRelational.class, "identifyCode")
				.addColumn(TestRelational.class, "msgTitle")
				.addFunction("COUNT", "COUNT", AbstractParameter.constant(1))
				.orderBy(TestRelational.class, "testTime")
				.groupBy(TestRelational.class, "testShort")
				.configPager(2, 20)
				.forUpdate(Boolean.TRUE)
				.useCache(Boolean.FALSE)
				.lockOption(LockOption.PESSIMISTIC_UPGRADE)
				.equalTo(TestRelational.class, "identifyCode", RelationalReference.class, "identifyCode")
				.equalTo(TestRelational.class, "chnId", "110105198405289439")
				.equalTo(RelationalReference.class, "refStatue", 1)
				.greater(TestRelational.class, "testShort", 1)
				.greaterEqual(TestRelational.class, "testDouble", 1.0d)
				.less(TestRelational.class, "testInt", 5)
				.lessEqual(TestRelational.class, "testFloat", 2.1f)
				.matchNull(TestDistribute.class, "msgBytes")
				.notNull(TestDistribute.class, "msgTitle")
				.notEqual(DistributeReference.class, "refStatue", 2)
				.like(TestRelational.class, "msgTitle", "%Keywords")
				.notLike(TestDistribute.class, "msgTitle", "%Keywords")
				.group(Condition.column(Globals.DEFAULT_VALUE_INT, ConnectionCode.AND,
								ConditionCode.BETWEEN_AND, TestRelational.class, "testTimestamp",
								AbstractParameter.ranges(DateTimeUtils.parseDate("20220101", "yyyyMMdd"),
										DateTimeUtils.parseDate("20220630", "yyyyMMdd"))),
						Condition.column(Globals.DEFAULT_VALUE_INT, ConnectionCode.OR,
								ConditionCode.BETWEEN_AND, TestRelational.class, "testTimestamp",
								AbstractParameter.ranges(DateTimeUtils.parseDate("20230101", "yyyyMMdd"),
										DateTimeUtils.parseDate("20231231", "yyyyMMdd"))))
				.betweenAnd(TestRelational.class, "testTimestamp",
						DateTimeUtils.parseDate("20230101", "yyyyMMdd"),
						DateTimeUtils.parseDate("20231231", "yyyyMMdd"))
				.notBetweenAnd(TestDistribute.class, "testTimestamp",
						DateTimeUtils.parseDate("20230101", "yyyyMMdd"),
						DateTimeUtils.parseDate("20231231", "yyyyMMdd"))
				.notBetweenAnd(TestDistribute.class, "testTimestamp",
						DateTimeUtils.parseDate("20230101", "yyyyMMdd"),
						DateTimeUtils.parseDate("20231231", "yyyyMMdd"))
				.in(TestDistribute.class, "testInt", 1, 2, 3, 4)
				.notIn(TestDistribute.class, "testInt", 5, 6, 7, 8)
				.orderBy(TestRelational.class, "testTime")
				.groupBy(TestRelational.class, "identifyCode")
				.confirm();
//		this.logger.info("Pager_Query", queryInfo.pagerQuery());
		String xmlData = queryInfo.toXML(Boolean.TRUE);
		if (StringUtils.notBlank(xmlData)) {
			this.logger.info("Generated_Query_Info", xmlData);
			Optional.ofNullable(StringUtils.stringToObject(xmlData, QueryInfo.class, "https://nervousync.org/schemas/query"))
					.ifPresent(info -> this.logger.info("Parsed_Query_Info", info.toFormattedJson()));
		}
	}
}
