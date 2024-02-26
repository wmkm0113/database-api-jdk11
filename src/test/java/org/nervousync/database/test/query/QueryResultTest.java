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

import org.apache.logging.log4j.Level;
import org.junit.jupiter.api.Test;
import org.nervousync.beans.core.BeanObject;
import org.nervousync.database.entity.relational.TestRelational;
import org.nervousync.database.query.QueryResult;
import org.nervousync.utils.IDUtils;
import org.nervousync.utils.LoggerUtils;
import org.nervousync.utils.StringUtils;

import java.util.Optional;

public final class QueryResultTest {

	private final LoggerUtils.Logger logger = LoggerUtils.getLogger(QueryBuilderTest.class);

	static {
		LoggerUtils.initLoggerConfigure(Level.DEBUG);
	}

	@Test
	public void test000Result() {
		QueryResult queryResult = new QueryResult();
		generateList(queryResult);
		queryResult.setTotalCount(28L);

		String jsonData = queryResult.toFormattedJson();
		if (StringUtils.notBlank(jsonData)) {
			this.logger.info("Partial_Collection", jsonData);
			Optional.ofNullable(StringUtils.stringToObject(jsonData, QueryResult.class))
					.map(info -> info.toXML(Boolean.TRUE))
					.ifPresent(info -> this.logger.info("Parsed_Partial_Collection", info));
		}
		String cacheData = queryResult.cacheData();
		if (StringUtils.notBlank(cacheData)) {
			this.logger.info("Cache_Data", cacheData);
			Optional.ofNullable(QueryResult.parse(cacheData))
					.map(info -> info.toXML(Boolean.TRUE))
					.ifPresent(info -> this.logger.info("Parsed_Partial_Collection", info));
		}

		String xmlData = queryResult.toXML(Boolean.TRUE);
		if (StringUtils.notBlank(xmlData)) {
			this.logger.info("Partial_Collection", xmlData);
			Optional.ofNullable(StringUtils.stringToObject(xmlData, QueryResult.class, "https://nervousync.org/schemas/query"))
					.map(BeanObject::toFormattedJson)
					.ifPresent(info -> this.logger.info("Parsed_Partial_Collection", info));
		}
	}

	private void generateList(final QueryResult queryResult) {
		int i = 0;
		while (i < 10) {
			TestRelational testRelational = new TestRelational();
			testRelational.setIdentifyCode(IDUtils.nano());
			queryResult.addResult(testRelational);
			i++;
		}
	}
}
