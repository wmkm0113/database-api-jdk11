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

package org.nervousync.database.test.data;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.nervousync.commons.Globals;
import org.nervousync.database.beans.task.impl.ImportTask;
import org.nervousync.database.commons.DataUtils;
import org.nervousync.database.commons.DatabaseCommons;
import org.nervousync.database.entity.relational.TestRelational;
import org.nervousync.database.enumerations.lock.LockOption;
import org.nervousync.database.query.QueryInfo;
import org.nervousync.database.query.builder.QueryBuilder;
import org.nervousync.database.query.param.AbstractParameter;
import org.nervousync.database.test.AbstractTest;
import org.nervousync.exceptions.builder.BuilderException;
import org.nervousync.exceptions.utils.DataInvalidException;
import org.nervousync.security.factory.SecureFactory;
import org.nervousync.utils.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public final class DataUtilsTest extends AbstractTest {

    private static final String DATA_FILE_NAME = "generate.dat";
    private static final String EXCEL_IMPORT_FILE = "data.xlsx";
    private static final String EXCEL_EXPORT_FILE = "export.xlsx";
    private long taskCode = Globals.DEFAULT_VALUE_LONG;

    @BeforeAll
    public static void init() {
        DataUtils.initialize(BASE_PATH + Globals.DEFAULT_PAGE_SEPARATOR + "DataUtils");
        FileUtils.copy("src/test/resources/data.xlsx",
                BASE_PATH + Globals.DEFAULT_PAGE_SEPARATOR + EXCEL_IMPORT_FILE);
    }

    @AfterAll
    public static void clear() {
        FileUtils.removeFile(BASE_PATH + Globals.DEFAULT_PAGE_SEPARATOR + DATA_FILE_NAME);
        FileUtils.removeFile(BASE_PATH + Globals.DEFAULT_PAGE_SEPARATOR + EXCEL_IMPORT_FILE);
        FileUtils.removeFile(BASE_PATH + Globals.DEFAULT_PAGE_SEPARATOR + EXCEL_EXPORT_FILE);
        FileUtils.removeFile(BASE_PATH + Globals.DEFAULT_PAGE_SEPARATOR + "DataUtils");
    }

    @Test
    @Order(0)
    public void testConfig() {
        DataUtils.getInstance().config(10, 24 * 60 * 60 * 1000L);
    }

    @Test
    @Order(10)
    public void testGenerator() {
        try (DataUtils.DataGenerator dataGenerator = DataUtils.newGenerator(BASE_PATH + Globals.DEFAULT_PAGE_SEPARATOR + DATA_FILE_NAME)) {
            dataGenerator.appendData(BASE_PATH + Globals.DEFAULT_PAGE_SEPARATOR + EXCEL_IMPORT_FILE);
            TestRelational testRelational = new TestRelational();
            testRelational.setIdentifyCode(IDUtils.nano());
            dataGenerator.appendData(Boolean.TRUE, testRelational);
        } catch (IOException e) {
            this.logger.error("Stack_Message_Error", e);
        }
    }

    @Test
    @Order(20)
    public void testParser() throws IOException {
        DataUtils dataUtils = DataUtils.getInstance();
        this.taskCode = dataUtils.addTask(FileUtils.loadFile(BASE_PATH + Globals.DEFAULT_PAGE_SEPARATOR + DATA_FILE_NAME), Globals.DEFAULT_VALUE_LONG);
        while (true) {
            ImportTask importTask = (ImportTask) dataUtils.taskInfo(Globals.DEFAULT_VALUE_LONG, this.taskCode);
            if (importTask.getTaskStatus() == DatabaseCommons.DATA_TASK_STATUS_FINISH) {
                break;
            }
        }
    }

    @Test
    @Order(30)
    public void testTaskList() {
        DataUtils.getInstance().taskList(Globals.DEFAULT_VALUE_LONG, DatabaseCommons.DEFAULT_PAGE_NO, DatabaseCommons.DEFAULT_PAGE_LIMIT)
                .forEach(abstractTask -> this.logger.info("Task_Info", abstractTask.toString(StringUtils.StringType.XML, Boolean.TRUE)));
    }

    @Test
    @Order(40)
    public void testExportTask() throws BuilderException {
        SecureFactory.registerConfig("sensitiveData", SecureFactory.SecureAlgorithm.AES256);
        QueryInfo queryInfo = QueryBuilder.newBuilder(TestRelational.class)
                .addColumn(TestRelational.class, "identifyCode")
                .addColumn(TestRelational.class, "msgTitle")
                .addFunction("COUNT", "COUNT", AbstractParameter.constant(1))
                .orderBy(TestRelational.class, "testTime")
                .groupBy(TestRelational.class, "testShort")
                .configPager(2, 20)
                .forUpdate(Boolean.TRUE)
                .useCache(Boolean.FALSE)
                .lockOption(LockOption.PESSIMISTIC_UPGRADE)
                .equalTo(TestRelational.class, "chnId", "110105198405289439")
                .like(TestRelational.class, "msgTitle", "%Keywords")
                .betweenAnd(TestRelational.class, "testTimestamp",
                        DateTimeUtils.parseDate("20230101", "yyyyMMdd"),
                        DateTimeUtils.parseDate("20231231", "yyyyMMdd"))
                .confirm();
        DataUtils dataUtils = DataUtils.getInstance();
        long taskCode = dataUtils.addTask(Globals.DEFAULT_VALUE_LONG, queryInfo);
        Optional.ofNullable(dataUtils.taskInfo(Globals.DEFAULT_VALUE_LONG, taskCode))
                .ifPresent(abstractTask -> this.logger.info("Task_Info", abstractTask.toString(StringUtils.StringType.XML, Boolean.TRUE)));
        dataUtils.dropTask(Globals.DEFAULT_VALUE_LONG, taskCode);
    }

    @Test
    @Order(50)
    public void testDropTask() {
        if (DataUtils.getInstance().dropTask(Globals.DEFAULT_VALUE_LONG, this.taskCode)) {
            this.logger.info("Task_Count",
                    DataUtils.getInstance().taskList(Globals.DEFAULT_VALUE_LONG,
                            DatabaseCommons.DEFAULT_PAGE_NO, DatabaseCommons.DEFAULT_PAGE_LIMIT).size());
        }
    }

    @Test
    @Order(60)
    public void testExporter() {
        try (DataUtils.DataExporter dataExporter = DataUtils.newExporter(BASE_PATH + Globals.DEFAULT_PAGE_SEPARATOR + EXCEL_EXPORT_FILE)) {
            for (int i = 0; i < 64; i++) {
                dataExporter.appendData(this.newObject());
            }
        } catch (DataInvalidException | IOException e) {
            this.logger.error("Stack_Message_Error", e);
        }

        this.logger.info("Export_Count", OfficeUtils.readExcel(BASE_PATH + Globals.DEFAULT_PAGE_SEPARATOR + EXCEL_EXPORT_FILE, "Test_Relational").size());
    }

    private TestRelational newObject() {
        TestRelational testRelational = new TestRelational();
        testRelational.setIdentifyCode(IDUtils.nano());
        testRelational.setChnId("110101190001011001");
        testRelational.setTestInt(27);
        testRelational.setTestShort((short) 21);
        testRelational.setTestDouble(10.21);
        testRelational.setTestFloat(2.27f);
        testRelational.setMsgTitle("Test title");
        testRelational.setMsgContent("Test content");
        testRelational.setMsgBytes("TestString".getBytes(StandardCharsets.UTF_8));

        return testRelational;
    }
}
