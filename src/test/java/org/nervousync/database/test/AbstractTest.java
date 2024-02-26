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

package org.nervousync.database.test;

import org.apache.logging.log4j.Level;
import org.junit.jupiter.api.*;
import org.nervousync.commons.Globals;
import org.nervousync.configs.ConfigureManager;
import org.nervousync.database.entity.EntityManager;
import org.nervousync.database.entity.distribute.DistributeReference;
import org.nervousync.database.entity.distribute.TestDistribute;
import org.nervousync.database.entity.relational.RelationalReference;
import org.nervousync.database.entity.relational.TestRelational;
import org.nervousync.security.factory.SecureFactory;
import org.nervousync.utils.FileUtils;
import org.nervousync.utils.LoggerUtils;

import java.util.Optional;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public abstract class AbstractTest {

    protected final LoggerUtils.Logger logger = LoggerUtils.getLogger(this.getClass());
    protected static final String BASE_PATH;

    static {
        String tmpDir = System.getProperty("java.io.tmpdir");
        BASE_PATH = tmpDir.endsWith(Globals.DEFAULT_PAGE_SEPARATOR)
                ? tmpDir.substring(0, tmpDir.length() - 1)
                : tmpDir;
    }

    @BeforeAll
    public static void registerEntity() {
        LoggerUtils.initLoggerConfigure(Level.DEBUG);
        EntityManager.registerTable(DistributeReference.class, TestDistribute.class,
                RelationalReference.class, TestRelational.class);
    }

    @AfterAll
    public static void clear() {
        Optional.ofNullable(ConfigureManager.getInstance())
                .ifPresent(configureManager -> configureManager.removeConfigure(SecureFactory.class));
        FileUtils.removeDir(BASE_PATH + Globals.DEFAULT_PAGE_SEPARATOR + "Configs");
    }

}
