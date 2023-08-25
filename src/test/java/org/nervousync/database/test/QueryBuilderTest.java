package org.nervousync.database.test;

import org.apache.logging.log4j.Level;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.nervousync.database.entity.EntityManager;
import org.nervousync.database.entity.distribute.DistributeReference;
import org.nervousync.database.entity.distribute.TestDistribute;
import org.nervousync.database.entity.relational.RelationalReference;
import org.nervousync.database.entity.relational.TestRelational;
import org.nervousync.database.enumerations.join.JoinType;
import org.nervousync.database.enumerations.lock.LockOption;
import org.nervousync.database.query.QueryInfo;
import org.nervousync.database.query.builder.QueryBuilder;
import org.nervousync.database.query.condition.MatchCondition;
import org.nervousync.database.query.item.QueryItem;
import org.nervousync.utils.DateTimeUtils;
import org.nervousync.utils.LoggerUtils;
import org.nervousync.utils.StringUtils;

import java.text.ParseException;
import java.util.Optional;

public final class QueryBuilderTest {

    private final LoggerUtils.Logger logger = LoggerUtils.getLogger(QueryBuilderTest.class);

    @BeforeAll
    public static void registerEntity() {
        LoggerUtils.initLoggerConfigure(Level.DEBUG);
        EntityManager.registerTable(DistributeReference.class, TestDistribute.class,
                RelationalReference.class, TestRelational.class);
    }

    @Test
    public void test000Builder() throws ParseException {
        QueryInfo queryInfo = QueryBuilder.newBuilder(TestRelational.class)
                .joinTable(JoinType.LEFT, TestRelational.class, RelationalReference.class)
                .joinTable(JoinType.LEFT, TestRelational.class, TestDistribute.class)
                .joinTable(JoinType.LEFT, TestDistribute.class, DistributeReference.class)
                .queryColumn(TestRelational.class, "identifyCode")
                .queryColumn(TestRelational.class, "msgTitle")
                .queryFunction("COUNT", "COUNT", QueryItem.queryConstant(1))
                .orderByItem(TestRelational.class, "testTime")
                .groupByItem(TestRelational.class, "testShort")
                .configPager(2, 20)
                .forUpdate(Boolean.TRUE)
                .useCache(Boolean.FALSE)
                .lockOption(LockOption.PESSIMISTIC_UPGRADE)
                .equal(RelationalReference.class, "refStatue", 1)
                .equal(RelationalReference.class, "refStatue", MatchCondition.condition(1))
                .greater(TestRelational.class, "testShort", 1)
                .greater(TestRelational.class, "testShort", MatchCondition.condition(1))
                .greaterEqual(TestRelational.class, "testDouble", 1.0d)
                .greaterEqual(TestRelational.class, "testDouble", MatchCondition.condition(1.0d))
                .less(TestRelational.class, "testInt", 5)
                .less(TestRelational.class, "testInt", MatchCondition.condition(5))
                .lessEqual(TestRelational.class, "testFloat", 2.1f)
                .lessEqual(TestRelational.class, "testFloat", MatchCondition.condition(2.1f))
                .isNull(TestDistribute.class, "msgBytes")
                .notNull(TestDistribute.class, "msgTitle")
                .notEqual(DistributeReference.class, "refStatue", 2)
                .notEqual(DistributeReference.class, "refStatue", MatchCondition.condition(2))
                .like(TestRelational.class, "msgTitle", "%Keywords")
                .like(TestRelational.class, "msgTitle", MatchCondition.condition("%Keywords"))
                .notLike(TestDistribute.class, "msgTitle", "%Keywords")
                .notLike(TestDistribute.class, "msgTitle", MatchCondition.condition("%Keywords"))
                .betweenAnd(TestRelational.class, "testTimestamp",
                        DateTimeUtils.parseDate("20230101", "yyyyMMdd"),
                        DateTimeUtils.parseDate("20231231", "yyyyMMdd"))
                .betweenAnd(TestRelational.class, "testTimestamp",
                        MatchCondition.condition(DateTimeUtils.parseDate("20230101", "yyyyMMdd"),
                                DateTimeUtils.parseDate("20231231", "yyyyMMdd")))
                .notBetweenAnd(TestDistribute.class, "testTimestamp",
                        DateTimeUtils.parseDate("20230101", "yyyyMMdd"),
                        DateTimeUtils.parseDate("20231231", "yyyyMMdd"))
                .notBetweenAnd(TestDistribute.class, "testTimestamp",
                        MatchCondition.condition(DateTimeUtils.parseDate("20230101", "yyyyMMdd"),
                                DateTimeUtils.parseDate("20231231", "yyyyMMdd")))
                .in(TestDistribute.class, "testInt", 1, 2, 3, 4)
                .build();
        this.logger.info("Pager_Query", queryInfo.pagerQuery());
        String xmlData = queryInfo.toXML(Boolean.TRUE);
        if (StringUtils.notBlank(xmlData)) {
            this.logger.info("Generated_Query_Info", xmlData);
            Optional.ofNullable(StringUtils.stringToObject(xmlData, QueryInfo.class, "https://nervousync.org/schemas/query"))
                    .ifPresent(info -> this.logger.info("Parsed_Query_Info", info.toFormattedJson()));
        }
    }
}
