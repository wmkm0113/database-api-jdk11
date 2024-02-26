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

package org.nervousync.database.commons;

import jakarta.annotation.Nonnull;
import org.nervousync.annotations.provider.Provider;
import org.nervousync.commons.Globals;
import org.nervousync.commons.io.NervousyncFile;
import org.nervousync.database.api.DatabaseClient;
import org.nervousync.database.beans.configs.table.TableConfig;
import org.nervousync.database.beans.configs.transactional.TransactionalConfig;
import org.nervousync.database.beans.task.AbstractTask;
import org.nervousync.database.beans.task.impl.ExportTask;
import org.nervousync.database.beans.task.impl.ImportTask;
import org.nervousync.database.entity.EntityManager;
import org.nervousync.database.entity.core.BaseObject;
import org.nervousync.database.enumerations.transactional.Isolation;
import org.nervousync.database.exceptions.data.DataParseException;
import org.nervousync.database.exceptions.entity.TableConfigException;
import org.nervousync.database.exceptions.operate.DropException;
import org.nervousync.database.exceptions.operate.InsertException;
import org.nervousync.database.exceptions.operate.UpdateException;
import org.nervousync.database.providers.data.TaskProvider;
import org.nervousync.database.query.QueryInfo;
import org.nervousync.exceptions.utils.DataInvalidException;
import org.nervousync.office.excel.ExcelWriter;
import org.nervousync.utils.*;

import java.io.*;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <h2 class="en-US">Data import/export utilities</h2>
 * <h2 class="zh-CN">数据导入导出工具</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Oct 26, 2023 15:04:37 $
 */
public final class DataUtils {

    private volatile static DataUtils INSTANCE = null;
    /**
     * <span class="en-US">Logger instance</span>
     * <span class="zh-CN">日志实例</span>
     */
    private static final LoggerUtils.Logger LOGGER = LoggerUtils.getLogger(DataUtils.class);
    private static final int TYPE_LENGTH = 64;
    /**
     * <span class="en-US">Registered task information adapter identify code and implementation class mapping table</span>
     * <span class="zh-CN">注册的任务信息适配器识别代码和实现类映射表</span>
     */
    private static final Hashtable<String, Class<?>> REGISTERED_TASK_PROVIDERS = new Hashtable<>();
    private final TaskProvider taskProvider;
    /**
     * <span class="en-US">The base path for system execution</span>
     * <span class="zh-CN">系统执行的基础路径</span>
     */
    private final String basePath;
    /**
     * <span class="en-US">Current node identify code, generate by system.</span>
     * <span class="zh-CN">当前节点的唯一识别代码，系统自动生成</span>
     */
    private final String identifyCode;
    /**
     * <span class="en-US">The maximum number of threads allowed to perform processing tasks</span>
     * <span class="zh-CN">允许执行处理任务的最大线程数</span>
     */
    private int threadLimit;

    /**
     * <span class="en-US">Start the scheduler execution status of the import and export task</span>
     * <span class="zh-CN">启动导入导出任务的调度程序执行状态</span>
     */
    private boolean scheduleRunning = Boolean.FALSE;
    /**
     * <span class="en-US">Remove scheduler execution status of completed tasks</span>
     * <span class="zh-CN">删除已完成任务的调度程序执行状态</span>
     */
    private boolean removeRunning = Boolean.FALSE;
    /**
     * <span class="en-US">
     * Automatically delete the currently delayed task information.
     * When the value is <code>-1</code>, the deletion operation will not be performed.
     * </span>
     * <span class="zh-CN">自动删除当前延时的任务信息，值为<code>-1</code>时不执行删除操作</span>
     */
    private long expireTime;
    /**
     * <span class="en-US">Remove scheduler execution status of completed tasks</span>
     * <span class="zh-CN">定时任务调度器服务</span>
     */
    private ScheduledExecutorService scheduledExecutorService;
    /**
     * <span class="en-US">List of processing threads being executed</span>
     * <span class="zh-CN">正在执行的处理线程列表</span>
     */
    private final List<ProcessThread> runningThreads;

    static {
        ServiceLoader.load(TaskProvider.class)
                .forEach(taskProvider ->
                        Optional.ofNullable(taskProvider.getClass().getAnnotation(Provider.class))
                                .ifPresent(provider -> REGISTERED_TASK_PROVIDERS.put(provider.name(),
                                        taskProvider.getClass())));
    }

    /**
     * <h3 class="en-US">Private constructor</h3>
     * <h3 class="zh-CN">私有的构造方法</h3>
     *
     * @param basePath     <span class="en-US">The base path for system execution</span>
     *                     <span class="zh-CN">系统执行的基础路径</span>
     * @param providerName <span class="en-US">Task store provider name to use</span>
     *                     <span class="zh-CN">使用的任务存储适配器名称</span>
     * @param threadLimit  <span class="en-US">Number of tasks allowed to be executed simultaneously</span>
     *                     <span class="zh-CN">允许同时执行的任务数</span>
     * @param expireTime   <span class="en-US">Expiration time for automatic deletion of completed tasks</span>
     *                     <span class="zh-CN">已完成任务自动删除的过期时间</span>
     */
    private DataUtils(final String basePath, final String providerName, final int threadLimit, final long expireTime) {
        this.basePath = basePath;
        if (registeredProvider(providerName)) {
            this.taskProvider =
                    Optional.ofNullable(REGISTERED_TASK_PROVIDERS.get(providerName))
                            .map(providerClass -> (TaskProvider) ObjectUtils.newInstance(providerClass))
                            .orElse(new MemoryTaskProviderImpl());
        } else {
            this.taskProvider = new MemoryTaskProviderImpl();
        }
        this.taskProvider.initialize();
        this.threadLimit = (threadLimit <= Globals.INITIALIZE_INT_VALUE)
                ? DatabaseCommons.DEFAULT_PROCESS_THREAD_LIMIT
                : threadLimit;
        this.expireTime = (expireTime < Globals.DEFAULT_VALUE_LONG)
                ? DatabaseCommons.DEFAULT_STORAGE_EXPIRE_TIME
                : expireTime;
        this.identifyCode = DataUtils.identifyCode(this.basePath);
        this.runningThreads = new ArrayList<>();
        this.scheduledExecutorService = Executors.newScheduledThreadPool(2);
        this.scheduledExecutorService.scheduleAtFixedRate(this::scheduleTask,
                0L, 1000L, TimeUnit.MILLISECONDS);
        this.scheduledExecutorService.scheduleAtFixedRate(this::removeTask,
                0L, 1000L, TimeUnit.MILLISECONDS);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Data_Utils_Config", this.threadLimit, this.expireTime);
        }
    }

    /**
     * <h3 class="en-US">
     * Static method is used to initialize data import and export tools,
     * Use the default maximum number of threads and expiration time
     * and the memory-only task adapter implementation class
     * </h3>
     * <h3 class="zh-CN">静态方法用于初始化数据导入导出工具，使用默认的最大线程数和过期时间和仅使用内存的任务适配器实现类</h3>
     *
     * @param basePath <span class="en-US">The base path for system execution</span>
     *                 <span class="zh-CN">系统执行的基础路径</span>
     */
    public static void initialize(final String basePath) {
        initialize(basePath, Globals.DEFAULT_VALUE_STRING);
    }

    /**
     * <h3 class="en-US">Static method is used to initialize data import and export tools, Use default maximum number of threads and expiration time</h3>
     * <h3 class="zh-CN">静态方法用于初始化数据导入导出工具，使用默认的最大线程数和过期时间</h3>
     *
     * @param basePath     <span class="en-US">The base path for system execution</span>
     *                     <span class="zh-CN">系统执行的基础路径</span>
     * @param providerName <span class="en-US">Task store provider name to use</span>
     *                     <span class="zh-CN">使用的任务存储适配器名称</span>
     */
    public static void initialize(final String basePath, final String providerName) {
        initialize(basePath, providerName, Globals.DEFAULT_VALUE_INT, Globals.DEFAULT_VALUE_LONG);
    }

    /**
     * <h3 class="en-US">Static method is used to initialize data import and export tools</h3>
     * <h3 class="zh-CN">静态方法用于初始化数据导入导出工具</h3>
     *
     * @param basePath     <span class="en-US">The base path for system execution</span>
     *                     <span class="zh-CN">系统执行的基础路径</span>
     * @param providerName <span class="en-US">Task store provider name to use</span>
     *                     <span class="zh-CN">使用的任务存储适配器名称</span>
     * @param threadLimit  <span class="en-US">Number of tasks allowed to be executed simultaneously</span>
     *                     <span class="zh-CN">允许同时执行的任务数</span>
     * @param expireTime   <span class="en-US">Expiration time for automatic deletion of completed tasks</span>
     *                     <span class="zh-CN">已完成任务自动删除的过期时间</span>
     */
    public static void initialize(final String basePath, final String providerName,
                                  final int threadLimit, final long expireTime) {
        if (INSTANCE == null) {
            INSTANCE = new DataUtils(basePath, providerName, threadLimit, expireTime);
        }
    }

    /**
     * <h3 class="en-US">Checks whether the given task store provider identification code is registered</h3>
     * <h3 class="zh-CN">检查给定的任务存储适配器识别代码是否注册</h3>
     *
     * @param providerName <span class="en-US">Task store provider name</span>
     *                     <span class="zh-CN">任务存储适配器名称</span>
     * @return <span class="en-US">Check result</span>
     * <span class="zh-CN">检查结果</span>
     */
    public static boolean registeredProvider(final String providerName) {
        if (StringUtils.isEmpty(providerName)) {
            return Boolean.FALSE;
        }
        return REGISTERED_TASK_PROVIDERS.containsKey(providerName);
    }

    public static DataUtils getInstance() {
        return INSTANCE;
    }

    /**
     * <h3 class="en-US">Add task information</h3>
     * <h3 class="zh-CN">添加任务信息</h3>
     *
     * @param inputStream <span class="en-US">data input stream</span>
     *                    <span class="zh-CN">数据输入流</span>
     * @param userCode    <span class="en-US">User identification code</span>
     *                    <span class="zh-CN">用户识别代码</span>
     * @return <span class="en-US">Task unique identification code</span>
     * <span class="zh-CN">任务唯一识别代码</span>
     */
    public long addTask(final InputStream inputStream, final Long userCode) {
        return this.addTask(inputStream, userCode, Boolean.FALSE, Globals.DEFAULT_VALUE_INT);
    }

    /**
     * <h3 class="en-US">Add task information</h3>
     * <h3 class="zh-CN">添加任务信息</h3>
     *
     * @param inputStream   <span class="en-US">data input stream</span>
     *                      <span class="zh-CN">数据输入流</span>
     * @param userCode      <span class="en-US">User identification code</span>
     *                      <span class="zh-CN">用户识别代码</span>
     * @param transactional <span class="en-US">Import tasks using transactions</span>
     *                      <span class="zh-CN">导入任务使用事务</span>
     * @param timeout       <span class="en-US">Transaction timeout</span>
     *                      <span class="zh-CN">事务超时时间</span>
     * @return <span class="en-US">Task unique identification code</span>
     * <span class="zh-CN">任务唯一识别代码</span>
     */
    public long addTask(final InputStream inputStream, final Long userCode,
                        final boolean transactional, final int timeout) {
        if (inputStream == null) {
            return Globals.DEFAULT_VALUE_LONG;
        }

        Long generateCode = IDUtils.snowflake();
        return Optional.ofNullable(generateCode)
                .filter(taskCode -> !ObjectUtils.nullSafeEquals(taskCode, Globals.DEFAULT_VALUE_LONG))
                .map(taskCode -> this.saveData(taskCode, inputStream))
                .map(dataPath -> {
                    ImportTask taskInfo = new ImportTask();
                    taskInfo.setTaskCode(generateCode);
                    taskInfo.setCreateTime(DateTimeUtils.currentUTCTimeMillis());
                    taskInfo.setDataPath(dataPath);
                    taskInfo.setUserCode(userCode);
                    taskInfo.setTransactional(transactional);
                    taskInfo.setTimeout(timeout);
                    return this.taskProvider.addTask(taskInfo) ? generateCode : Globals.DEFAULT_VALUE_LONG;
                })
                .orElse(Globals.DEFAULT_VALUE_LONG);
    }

    /**
     * <h3 class="en-US">Add task information</h3>
     * <h3 class="zh-CN">添加任务信息</h3>
     *
     * @param userCode   <span class="en-US">User identification code</span>
     *                   <span class="zh-CN">用户识别代码</span>
     * @param queryInfos <span class="en-US">Data query information array</span>
     *                   <span class="zh-CN">数据查询信息数组</span>
     * @return <span class="en-US">Task unique identification code</span>
     * <span class="zh-CN">任务唯一识别代码</span>
     */
    public long addTask(final Long userCode, final QueryInfo... queryInfos) {
        Long generateCode = IDUtils.snowflake();
        return Optional.ofNullable(generateCode)
                .filter(taskCode -> !ObjectUtils.nullSafeEquals(taskCode, Globals.DEFAULT_VALUE_LONG))
                .map(dataPath -> {
                    ExportTask taskInfo = new ExportTask();
                    taskInfo.setTaskCode(generateCode);
                    taskInfo.setCreateTime(DateTimeUtils.currentUTCTimeMillis());
                    taskInfo.setQueryInfoList(Arrays.asList(queryInfos));
                    taskInfo.setUserCode(userCode);
                    return this.taskProvider.addTask(taskInfo) ? generateCode : Globals.DEFAULT_VALUE_LONG;
                })
                .orElse(Globals.DEFAULT_VALUE_LONG);
    }

    /**
     * <h3 class="en-US">Update configure information</h3>
     * <h3 class="zh-CN">更新配置信息</h3>
     *
     * @param threadLimit <span class="en-US">Number of tasks allowed to be executed simultaneously</span>
     *                    <span class="zh-CN">允许同时执行的任务数</span>
     * @param expireTime  <span class="en-US">Expiration time for automatic deletion of completed tasks</span>
     *                    <span class="zh-CN">已完成任务自动删除的过期时间</span>
     */
    public void config(final int threadLimit, final long expireTime) {
        if (threadLimit > 0) {
            this.threadLimit = threadLimit;
        }
        this.expireTime = (expireTime < Globals.DEFAULT_VALUE_LONG)
                ? DatabaseCommons.DEFAULT_STORAGE_EXPIRE_TIME
                : expireTime;
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Data_Utils_Config", this.threadLimit, this.expireTime);
        }
    }

    /**
     * <h3 class="en-US">Drop task information based on the given task identification code and user identification code</h3>
     * <h3 class="zh-CN">根据给定的用户代码和任务识别代码删除任务信息</h3>
     *
     * @param userCode <span class="en-US">User identification code</span>
     *                 <span class="zh-CN">用户识别代码</span>
     * @param taskCode <span class="en-US">task identification code</span>
     *                 <span class="zh-CN">任务识别代码</span>
     * @return <span class="en-US">Process result</span>
     * <span class="zh-CN">执行结果</span>
     */
    public boolean dropTask(final Long userCode, final Long taskCode) {
        return this.taskProvider.dropTask(userCode, taskCode);
    }

    /**
     * <h3 class="en-US">Get the task information list based on the given user code and paging information</h3>
     * <h3 class="zh-CN">根据给定的用户代码和分页信息，获取任务信息列表</h3>
     *
     * @param userCode  <span class="en-US">User identification code</span>
     *                  <span class="zh-CN">用户识别代码</span>
     * @param pageNo    <span class="en-US">Current page number</span>
     *                  <span class="zh-CN">当前页数</span>
     * @param limitSize <span class="en-US">Maximum number of records per page</span>
     *                  <span class="zh-CN">每页的最大记录条数</span>
     * @return <span class="en-US">Task details list</span>
     * <span class="zh-CN">任务详细信息列表</span>
     */
    public List<AbstractTask> taskList(final Long userCode, final Integer pageNo, final Integer limitSize) {
        return this.taskProvider.taskList(userCode, pageNo, limitSize);
    }

    /**
     * <h3 class="en-US">Read task information based on the given task identification code and user identification code</h3>
     * <h3 class="zh-CN">根据给定的用户代码和任务识别代码读取任务信息</h3>
     *
     * @param userCode <span class="en-US">User identification code</span>
     *                 <span class="zh-CN">用户识别代码</span>
     * @param taskCode <span class="en-US">task identification code</span>
     *                 <span class="zh-CN">任务识别代码</span>
     * @return <span class="en-US">Task details</span>
     * <span class="zh-CN">任务详细信息</span>
     */
    public AbstractTask taskInfo(Long userCode, Long taskCode) {
        return this.taskProvider.taskInfo(userCode, taskCode);
    }

    /**
     * <h3 class="en-US">Initialize the data generator instance object</h3>
     * <h3 class="zh-CN">初始化数据生成器实例对象</h3>
     *
     * @param dataPath <span class="en-US">Data storage path</span>
     *                 <span class="zh-CN">数据保存地址</span>
     * @return <span class="en-US">Data generator instance object</span>
     * <span class="zh-CN">数据生成器实例对象</span>
     * @throws FileNotFoundException <span class="en-US">Error creating data file</span>
     *                               <span class="zh-CN">创建数据文件出错</span>
     */
    public static DataGenerator newGenerator(final String dataPath) throws FileNotFoundException {
        return new DataGenerator(dataPath);
    }

    /**
     * <h3 class="en-US">Initialize the data exporter instance object</h3>
     * <h3 class="zh-CN">初始化数据导出器实例对象</h3>
     *
     * @param dataPath <span class="en-US">Data storage path</span>
     *                 <span class="zh-CN">数据保存地址</span>
     * @return <span class="en-US">Data exporter instance object</span>
     * <span class="zh-CN">数据导出器实例对象</span>
     * @throws DataInvalidException <span class="en-US">File format error</span>
     *                              <span class="zh-CN">文件格式错误</span>
     */
    public static DataExporter newExporter(final String dataPath) throws DataInvalidException {
        return new DataExporter(dataPath);
    }

    /**
     * <h3 class="en-US">Destroy current instance</h3>
     * <h3 class="zh-CN">销毁当前实例</h3>
     */
    public static void destroy() {
        if (INSTANCE == null) {
            return;
        }
        if (INSTANCE.scheduledExecutorService != null) {
            INSTANCE.scheduledExecutorService.shutdown();
            INSTANCE.scheduledExecutorService = null;
        }
        INSTANCE.taskProvider.destroy();
        INSTANCE = null;
    }

    /**
     * <h3 class="en-US">Generate a unique identification code for the current node</h3>
     * <h3 class="zh-CN">生成当前节点的唯一识别代码</h3>
     *
     * @param basePath <span class="en-US">The base path for system execution</span>
     *                 <span class="zh-CN">系统执行的基础路径</span>
     * @return <span class="en-US">Generated unique identification code</span>
     * <span class="zh-CN">生成的唯一识别代码</span>
     */
    private static String identifyCode(final String basePath) {
        TreeMap<String, String> identifyMap = new TreeMap<>();
        identifyMap.put("IdentifyKey", SystemUtils.identifiedKey());
        identifyMap.put("BasePath", basePath);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Identify_Information_Data",
                    StringUtils.objectToString(identifyMap, StringUtils.StringType.JSON, Boolean.TRUE));
        }
        return ConvertUtils.toHex(SecurityUtils.SHA256(identifyMap));
    }

    /**
     * <h3 class="en-US">Static methods are used to update the processing node and start execution time of the task</h3>
     * <h3 class="zh-CN">静态方法用于更新任务的处理节点和开始执行时间</h3>
     *
     * @param taskCode     <span class="en-US">task identification code</span>
     *                     <span class="zh-CN">任务识别代码</span>
     * @param identifyCode <span class="en-US">Execution node unique identification code</span>
     *                     <span class="zh-CN">执行节点唯一识别代码</span>
     */
    private static void processTask(@Nonnull final Long taskCode, final String identifyCode) {
        if (INSTANCE != null) {
            INSTANCE.taskProvider.processTask(taskCode, identifyCode);
        }
    }

    /**
     * <h3 class="en-US">Complete the thread task and save the task processing results to the task list</h3>
     * <h3 class="zh-CN">完成线程任务，并将任务处理结果保存到任务列表中</h3>
     *
     * @param processThread <span class="en-US">Task processing thread instance object</span>
     *                      <span class="zh-CN">任务处理线程实例对象</span>
     */
    private static void finishTask(final ProcessThread processThread) {
        if (INSTANCE != null) {
            synchronized (INSTANCE.runningThreads) {
                INSTANCE.runningThreads.remove(processThread);
            }
            INSTANCE.taskProvider.finishTask(processThread.getTaskCode(), processThread.isHasError(),
                    processThread.errorMessage());
        }
    }

    /**
     * <h3 class="en-US">Save the data in the input stream to a temporary directory to wait for processing</h3>
     * <h3 class="zh-CN">将输入流中的数据保存到临时目录中等待处理</h3>
     *
     * @param taskCode    <span class="en-US">task identification code</span>
     *                    <span class="zh-CN">任务识别代码</span>
     * @param inputStream <span class="en-US">data input stream</span>
     *                    <span class="zh-CN">数据输入流</span>
     * @return <span class="en-US">data file location</span>
     * <span class="zh-CN">数据文件位置</span>
     */
    private String saveData(@Nonnull final Long taskCode, final InputStream inputStream) {
        if (ObjectUtils.nullSafeEquals(taskCode, Globals.DEFAULT_VALUE_LONG)) {
            return Globals.DEFAULT_VALUE_STRING;
        }
        String dataPath = this.dataPath(taskCode);
        if (FileUtils.saveFile(inputStream, dataPath)) {
            return dataPath;
        }
        return Globals.DEFAULT_VALUE_STRING;
    }

    /**
     * <h3 class="en-US">Get the data file location for the given task code</h3>
     * <h3 class="zh-CN">获取给定任务代码的数据文件位置</h3>
     *
     * @param taskCode <span class="en-US">task identification code</span>
     *                 <span class="zh-CN">任务识别代码</span>
     * @return <span class="en-US">data file location</span>
     * <span class="zh-CN">数据文件位置</span>
     */
    private String dataPath(@Nonnull final Long taskCode) {
        return this.basePath + Globals.DEFAULT_PAGE_SEPARATOR + Long.toHexString(taskCode)
                + DatabaseCommons.DATA_FILE_EXTENSION_NAME;
    }

    /**
     * <h3 class="en-US">Get the data file location for the given task code</h3>
     * <h3 class="zh-CN">获取给定任务代码的数据文件位置</h3>
     *
     * @param taskCode <span class="en-US">task identification code</span>
     *                 <span class="zh-CN">任务识别代码</span>
     * @return <span class="en-US">data file location</span>
     * <span class="zh-CN">数据文件位置</span>
     */
    private String exportPath(@Nonnull final Long taskCode, final boolean compatibilityMode) {
        return this.basePath + Globals.DEFAULT_PAGE_SEPARATOR + Long.toHexString(taskCode)
                + (compatibilityMode ? OfficeUtils.EXCEL_FILE_EXT_NAME_2003 : OfficeUtils.EXCEL_FILE_EXT_NAME_2007);
    }

    /**
     * <h3 class="en-US">Scheduling tasks, used to regularly start pending tasks in the task queue</h3>
     * <h3 class="zh-CN">调度任务，用于定时启动任务队列中的待处理任务</h3>
     */
    private void scheduleTask() {
        if (this.scheduleRunning) {
            return;
        }
        this.scheduleRunning = Boolean.TRUE;

        try {
            while (this.runningThreads.size() < this.threadLimit) {
                AbstractTask taskInfo = this.taskProvider.nextTask(this.identifyCode);
                if (taskInfo == null
                        || this.runningThreads.stream().anyMatch(processThread ->
                        ObjectUtils.nullSafeEquals(processThread.taskCode, taskInfo.getTaskCode()))) {
                    break;
                }
                ProcessThread processThread;
                if (taskInfo instanceof ImportTask) {
                    processThread = new ImportThread((ImportTask) taskInfo);
                } else if (taskInfo instanceof ExportTask) {
                    processThread = new ExportThread((ExportTask) taskInfo);
                } else {
                    return;
                }
                synchronized (this.runningThreads) {
                    this.runningThreads.add(processThread);
                }
                processThread.start();
            }
        } catch (Exception e) {
            LOGGER.error("Data_Task_Schedule_Error");
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Stack_Message_Error", e);
            }
        }

        this.scheduleRunning = Boolean.FALSE;
    }

    /**
     * <h3 class="en-US">Remove expired completed task information</h3>
     * <h3 class="zh-CN">移除过期已完成的任务信息</h3>
     */
    private void removeTask() {
        if (this.removeRunning || this.expireTime == Globals.DEFAULT_VALUE_LONG) {
            return;
        }
        this.removeRunning = Boolean.TRUE;
        this.taskProvider.dropTask(this.expireTime);
        this.removeRunning = Boolean.FALSE;
    }

    /**
     * <h2 class="en-US">Memory-only task adapter implementation class</h2>
     * <h2 class="zh-CN">仅使用内存的任务适配器实现类</h2>
     *
     * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
     * @version $Revision: 1.0.0 $ $Date: Oct 26, 2023 16:42:18 $
     */
    private static final class MemoryTaskProviderImpl implements TaskProvider {

        /**
         * <span class="en-US">Multilingual agent object instance</span>
         * <span class="zh-CN">国际化代理实例对象</span>
         */
        private final MultilingualUtils.Agent multiAgent = MultilingualUtils.newAgent(this.getClass());
        /**
         * <span class="en-US">List of currently stored task information</span>
         * <span class="zh-CN">当前存储的任务信息列表</span>
         */
        private final List<AbstractTask> taskInfoList;

        /**
         * <h3 class="en-US">Constructor of a memory-only task adapter implementation class</h3>
         * <h3 class="zh-CN">仅使用内存的任务适配器实现类的构造方法</h3>
         */
        public MemoryTaskProviderImpl() {
            this.taskInfoList = new ArrayList<>();
        }

        /*
         * (non-Javadoc)
         * @see org.nervousync.database.providers.data.TaskProvider#initialize()
         */
        @Override
        public void initialize() {
        }

        /*
         * (non-Javadoc)
         * @see org.nervousync.database.providers.data.TaskProvider#name(java.lang.String)
         */
        @Override
        public String name(String languageCode) {
            return this.multiAgent.findMessage("memory.name.task.provider");
        }

        /*
         * (non-Javadoc)
         * @see org.nervousync.database.providers.data.TaskProvider#destroy()
         */
        @Override
        public void destroy() {
            this.taskInfoList.clear();
        }

        /*
         * (non-Javadoc)
         * @see org.nervousync.database.providers.data.TaskProvider#addTask(org.nervousync.database.bean.data.TaskInfo)
         */
        @Override
        public boolean addTask(@Nonnull final AbstractTask taskInfo) {
            synchronized (this.taskInfoList) {
                if (this.taskInfoList.stream().anyMatch(existTask -> ObjectUtils.nullSafeEquals(existTask, taskInfo))) {
                    return Boolean.TRUE;
                }
                return this.taskInfoList.add(taskInfo);
            }
        }

        /*
         * (non-Javadoc)
         * @see org.nervousync.database.providers.data.TaskProvider#updateTask(java.lang.Long, java.lang.Integer)
         */
        @Override
        public void processTask(@Nonnull final Long taskCode, final String identifyCode) {
            synchronized (this.taskInfoList) {
                this.taskInfoList.replaceAll(taskInfo -> {
                    if (ObjectUtils.nullSafeEquals(taskInfo.getTaskCode(), taskCode)
                            && ObjectUtils.nullSafeEquals(taskInfo.getIdentifyCode(), identifyCode)) {
                        taskInfo.setStartTime(DateTimeUtils.currentUTCTimeMillis());
                        taskInfo.setTaskStatus(DatabaseCommons.DATA_TASK_STATUS_PROCESS);
                    }
                    return taskInfo;
                });
            }
        }

        /*
         * (non-Javadoc)
         * @see org.nervousync.database.providers.data.TaskProvider#dropTask(java.lang.Long)
         */
        @Override
        public void dropTask(@Nonnull final Long expireTime) {
            if (ObjectUtils.nullSafeEquals(expireTime, Globals.DEFAULT_VALUE_LONG)) {
                return;
            }
            long expireEndTime = DateTimeUtils.currentUTCTimeMillis() + expireTime;
            synchronized (this.taskInfoList) {
                this.taskInfoList.removeIf(taskInfo ->
                        ObjectUtils.nullSafeEquals(taskInfo.getTaskStatus(), DatabaseCommons.DATA_TASK_STATUS_FINISH)
                                && taskInfo.getEndTime() < expireEndTime);
            }
        }

        /*
         * (non-Javadoc)
         * @see org.nervousync.database.providers.data.TaskProvider#dropTask(java.lang.Long, java.lang.Long)
         */
        @Override
        public boolean dropTask(@Nonnull final Long userCode, @Nonnull final Long taskCode) {
            synchronized (this.taskInfoList) {
                Iterator<AbstractTask> iterator = this.taskInfoList.iterator();
                while (iterator.hasNext()) {
                    AbstractTask abstractTask = iterator.next();
                    if (ObjectUtils.nullSafeEquals(abstractTask.getTaskCode(), taskCode)
                            && ObjectUtils.nullSafeEquals(abstractTask.getUserCode(), userCode)) {
                        if (FileUtils.removeFile(INSTANCE.dataPath(abstractTask.getTaskCode()))) {
                            iterator.remove();
                        } else {
                            return Boolean.FALSE;
                        }
                    }
                }
                return Boolean.TRUE;
            }
        }

        /*
         * (non-Javadoc)
         * @see org.nervousync.database.providers.data.TaskProvider#nextTask()
         */
        @Override
        public AbstractTask nextTask(@Nonnull final String identifyCode) {
            synchronized (this.taskInfoList) {
                AbstractTask abstractTask =
                        this.taskInfoList.stream().filter(currentTask -> this.processingTask(currentTask, identifyCode))
                                .findFirst()
                                .orElseGet(() ->
                                        this.taskInfoList.stream().filter(this::waitingTask)
                                                .findFirst()
                                                .map(currentTask -> {
                                                    this.lockTask(currentTask.getTaskCode(), identifyCode);
                                                    return currentTask;
                                                })
                                                .orElse(null));
                if (abstractTask != null) {
                    this.lockTask(abstractTask.getTaskCode(), identifyCode);
                }
                return abstractTask;
            }
        }

        private boolean processingTask(final AbstractTask abstractTask, final String identifyCode) {
            return ObjectUtils.nullSafeEquals(abstractTask.getTaskStatus(), DatabaseCommons.DATA_TASK_STATUS_PROCESS)
                    && ObjectUtils.nullSafeEquals(abstractTask.getIdentifyCode(), identifyCode);
        }

        private boolean waitingTask(final AbstractTask abstractTask) {
            return ObjectUtils.nullSafeEquals(abstractTask.getTaskStatus(), DatabaseCommons.DATA_TASK_STATUS_CREATE);
        }

        private void lockTask(final long taskCode, final String identifyCode) {
            this.taskInfoList.replaceAll(abstractTask -> {
                if (ObjectUtils.nullSafeEquals(abstractTask.getTaskCode(), taskCode)) {
                    abstractTask.setIdentifyCode(identifyCode);
                }
                return abstractTask;
            });
        }

        /*
         * (non-Javadoc)
         * @see org.nervousync.database.providers.data.TaskProvider#finishTask(java.lang.Long, java.lang.Boolean, java.lang.String)
         */
        @Override
        public void finishTask(@Nonnull final Long taskCode, @Nonnull final Boolean hasError,
                               @Nonnull final String errorMessage) {
            synchronized (this.taskInfoList) {
                this.taskInfoList.replaceAll(taskInfo -> {
                    if (ObjectUtils.nullSafeEquals(taskInfo.getTaskCode(), taskCode)
                            && ObjectUtils.nullSafeEquals(taskInfo.getTaskStatus(), DatabaseCommons.DATA_TASK_STATUS_PROCESS)) {
                        taskInfo.setTaskStatus(DatabaseCommons.DATA_TASK_STATUS_FINISH);
                        taskInfo.setEndTime(DateTimeUtils.currentUTCTimeMillis());
                        taskInfo.setHasError(hasError);
                        taskInfo.setErrorMessage(errorMessage);
                    }
                    return taskInfo;
                });
            }
        }

        /*
         * (non-Javadoc)
         * @see org.nervousync.database.providers.data.TaskProvider#taskList(java.lang.Long, java.lang.Integer, java.lang.Integer)
         */
        @Override
        public List<AbstractTask> taskList(@Nonnull final Long userCode, Integer pageNo, Integer limitSize) {
            List<AbstractTask> taskList = new ArrayList<>();
            Integer currentPage = pageNo;
            if (currentPage == null || currentPage <= Globals.INITIALIZE_INT_VALUE) {
                currentPage = DatabaseCommons.DEFAULT_PAGE_NO;
            }
            Integer currentLimit = limitSize;
            if (currentLimit == null || currentLimit <= Globals.INITIALIZE_INT_VALUE) {
                currentLimit = DatabaseCommons.DEFAULT_PAGE_LIMIT;
            }

            int beginIndex = ((currentPage - 1) * currentLimit);
            int endIndex = Math.min(this.taskInfoList.size(), beginIndex + currentLimit);
            final AtomicInteger currentIndex = new AtomicInteger(Globals.INITIALIZE_INT_VALUE);
            this.taskInfoList.stream()
                    .filter(taskInfo -> ObjectUtils.nullSafeEquals(taskInfo.getUserCode(), userCode))
                    .forEach(taskInfo -> {
                        int index = currentIndex.get();
                        if (index >= beginIndex && index < endIndex) {
                            taskList.add(taskInfo);
                        }
                        currentIndex.incrementAndGet();
                    });
            return taskList;
        }

        /*
         * (non-Javadoc)
         * @see org.nervousync.database.providers.data.TaskProvider#taskInfo(java.lang.Long, java.lang.Long)
         */
        @Override
        public AbstractTask taskInfo(@Nonnull final Long userCode, @Nonnull final Long taskCode) {
            synchronized (this.taskInfoList) {
                return this.taskInfoList.stream()
                        .filter(taskInfo ->
                                ObjectUtils.nullSafeEquals(taskInfo.getTaskCode(), taskCode)
                                        && ObjectUtils.nullSafeEquals(taskInfo.getUserCode(), userCode))
                        .findFirst()
                        .orElse(null);
            }
        }
    }

    private static final class DataOperate {
        private final boolean removeOperate;
        private final Class<?> entityClass;
        private final Map<String, String> primaryKey;
        private final Map<String, String> dataMap;

        private DataOperate(final boolean removeOperate, final Class<?> entityClass,
                            final Map<String, String> primaryKey, final Map<String, String> dataMap) {
            this.removeOperate = removeOperate;
            this.entityClass = entityClass;
            this.primaryKey = primaryKey;
            this.dataMap = dataMap;
        }

        public static DataOperate fromBytes(final List<String> identifyKeys, final byte[] dataBytes)
                throws DataInvalidException {
            if (dataBytes.length < 2) {
                return null;
            }
            int index = RawUtils.readInt(dataBytes, 1, ByteOrder.LITTLE_ENDIAN);
            if (identifyKeys.size() < index) {
                return null;
            }
            boolean remove = (dataBytes[0] == ((byte) 1));
            String dataContent = RawUtils.readString(dataBytes, 5, dataBytes.length - 5);
            return Optional.ofNullable(EntityManager.tableConfig(identifyKeys.get(index)))
                    .map(tableConfig -> {
                        Map<String, Object> recordMap = StringUtils.dataToMap(dataContent, StringUtils.StringType.JSON);
                        if (recordMap.isEmpty()) {
                            return null;
                        }
                        Map<String, String> primaryKey = new HashMap<>();
                        Map<String, String> dataMap = new HashMap<>();
                        recordMap.forEach((key, value) ->
                                Optional.ofNullable(tableConfig.columnConfig(key))
                                        .ifPresent(columnConfig -> {
                                            if (columnConfig.isPrimaryKey()) {
                                                primaryKey.put(columnConfig.columnName(), (String) value);
                                            } else {
                                                dataMap.put(columnConfig.columnName(), (String) value);
                                            }
                                        }));
                        return new DataOperate(remove, tableConfig.getDefineClass(), primaryKey, dataMap);
                    })
                    .orElse(null);
        }

        public boolean isRemoveOperate() {
            return removeOperate;
        }

        public Class<?> getEntityClass() {
            return entityClass;
        }

        public Map<String, String> getPrimaryKey() {
            return primaryKey;
        }

        public Map<String, String> getDataMap() {
            return dataMap;
        }
    }

    public static final class DataExporter implements Closeable {
        private final ExcelWriter excelWriter;

        private DataExporter(final String dataPath) throws DataInvalidException {
            this.excelWriter = OfficeUtils.newWriter(dataPath);
        }

        public void appendData(@Nonnull final Object object) {
            EntityManager.appendToExcel(this.excelWriter, object);
        }

        @Override
        public void close() throws IOException {
            this.excelWriter.write();
            this.excelWriter.close();
        }
    }

    public static final class DataGenerator implements Closeable {
        private final String dataPath;
        private final NervousyncFile tmpFile;
        private final List<String> recordTypes;
        private long totalCount = 0L;

        private DataGenerator(final String dataPath) throws FileNotFoundException {
            this.dataPath = dataPath;
            String tmpPath = this.dataPath + DatabaseCommons.DATA_TMP_FILE_EXTENSION_NAME;
            if (FileUtils.isExists(tmpPath)) {
                FileUtils.removeFile(tmpPath);
            }
            this.tmpFile = new NervousyncFile(tmpPath, Boolean.TRUE);
            this.recordTypes = new ArrayList<>();
        }

        public void appendData(final boolean removeRecord, @Nonnull final BaseObject recordObject) {
            this.writeBytes(removeRecord, ClassUtils.originalClassName(recordObject.getClass()),
                    EntityManager.objectToMap(removeRecord, recordObject));
        }

        public void appendData(final String excelFilePath) {
            for (Map.Entry<String, List<List<String>>> entry : OfficeUtils.readExcel(excelFilePath).entrySet()) {
                TableConfig tableConfig = EntityManager.tableConfig(entry.getKey());
                if (tableConfig != null) {
                    entry.getValue()
                            .stream()
                            .filter(dataValues -> !CollectionUtils.isEmpty(dataValues))
                            .forEach(dataValues ->
                                    this.writeBytes(Boolean.FALSE, tableConfig.getTableName(),
                                            EntityManager.parseList(tableConfig.getDefineClass(), dataValues)));
                }
            }
        }

        private void writeBytes(final boolean removeRecord, final String string,
                                @Nonnull final Map<String, String> dataMap) {
            if (dataMap.isEmpty()) {
                return;
            }
            String recordType = DatabaseUtils.tableKey(string);
            if (!CollectionUtils.contains(this.recordTypes, recordType)) {
                this.recordTypes.add(recordType);
            }
            int index = this.recordTypes.indexOf(recordType);
            String dataContent = StringUtils.objectToString(dataMap, StringUtils.StringType.JSON, Boolean.FALSE);
            if (StringUtils.isEmpty(dataContent)) {
                return;
            }
            int totalLength = dataContent.getBytes(StandardCharsets.UTF_8).length + 5;
            byte[] dataBytes = new byte[totalLength + 4];
            try {
                RawUtils.writeInt(dataBytes, ByteOrder.LITTLE_ENDIAN, totalLength);
                dataBytes[4] = removeRecord ? (byte) 1 : (byte) 0;
                RawUtils.writeInt(dataBytes, 5, ByteOrder.LITTLE_ENDIAN, index);
                RawUtils.writeString(dataBytes, 9, dataContent);
                this.tmpFile.write(dataBytes);
                this.totalCount++;
            } catch (DataInvalidException | IOException e) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Stack_Message_Error", e);
                }
            }
        }

        @Override
        public void close() throws IOException {
            this.tmpFile.close();
            String tmpFilePath = this.dataPath + DatabaseCommons.DATA_TMP_FILE_EXTENSION_NAME;
            try (NervousyncFile dataFile = new NervousyncFile(this.dataPath, Boolean.TRUE);
                 FileInputStream fileInputStream = new FileInputStream(tmpFilePath)) {
                byte[] buffer = new byte[8];
                RawUtils.writeLong(buffer, ByteOrder.LITTLE_ENDIAN, this.totalCount);
                dataFile.write(buffer);
                buffer = new byte[4];
                RawUtils.writeInt(buffer, ByteOrder.LITTLE_ENDIAN, this.recordTypes.size());
                dataFile.write(buffer);
                for (String recordType : this.recordTypes) {
                    buffer = new byte[TYPE_LENGTH];
                    RawUtils.writeString(buffer, recordType);
                    dataFile.write(buffer);
                }
                byte[] readBuffer = new byte[Globals.DEFAULT_BUFFER_SIZE];
                int readLength;
                while ((readLength = fileInputStream.read(readBuffer)) != Globals.DEFAULT_VALUE_INT) {
                    dataFile.write(readBuffer, Globals.INITIALIZE_INT_VALUE, readLength);
                }
            } catch (DataInvalidException e) {
                throw new IOException(e);
            }
            FileUtils.removeFile(this.dataPath + DatabaseCommons.DATA_TMP_FILE_EXTENSION_NAME);
        }
    }

    private static final class DataParser implements Closeable {
        private final boolean transactional;
        private final int timeout;
        private final NervousyncFile dataFile;
        private final List<String> recordTypes;
        private final long totalCount;
        private final long endPosition;
        private long successCount = 0L;
        private long failedCount = 0L;
        private long position = 0L;
        /**
         * <span class="en-US">Error message builder</span>
         * <span class="zh-CN">错误信息收集器</span>
         */
        private final StringBuilder errorLog;

        public DataParser(final boolean transactional, final int timeout, final String dataPath)
                throws DataParseException {
            this.transactional = transactional;
            this.timeout = timeout;
            this.errorLog = new StringBuilder();
            if (StringUtils.isEmpty(dataPath)) {
                throw new DataParseException(0x00DB00000007L);
            }

            try {
                this.dataFile = new NervousyncFile(dataPath);
                this.endPosition = FileUtils.fileSize(dataPath);
            } catch (FileNotFoundException e) {
                this.errorLog.append(e.getMessage()).append(FileUtils.CRLF);
                throw new DataParseException(0x00DB00000007L, e);
            }

            try {
                byte[] longBuffer = new byte[8];
                if (this.dataFile.read(longBuffer) == 8) {
                    this.position += 8;
                    this.totalCount = RawUtils.readLong(longBuffer, ByteOrder.LITTLE_ENDIAN);
                } else {
                    throw new DataParseException(0x00DB00000006L);
                }

                byte[] intBuffer = new byte[4];
                int headerCount;
                if (this.dataFile.read(intBuffer) == 4) {
                    this.position += 4;
                    headerCount = RawUtils.readInt(intBuffer, ByteOrder.LITTLE_ENDIAN);
                } else {
                    throw new DataParseException(0x00DB00000006L);
                }

                this.recordTypes = new ArrayList<>();
                byte[] readBuffer;
                do {
                    readBuffer = new byte[TYPE_LENGTH];
                    if (this.dataFile.read(readBuffer) == TYPE_LENGTH) {
                        this.recordTypes.add(RawUtils.readString(readBuffer));
                    } else {
                        throw new DataParseException(0x00DB00000006L);
                    }
                    this.position += TYPE_LENGTH;
                    headerCount--;
                } while (headerCount > 0);
            } catch (IOException | DataInvalidException e) {
                this.errorLog.append(e.getMessage()).append(FileUtils.CRLF);
                throw new DataParseException(0x00DB00000008L, e);
            }
        }

        public void process() throws DataParseException, DataInvalidException, IOException {
            TransactionalConfig txConfig = this.transactional
                    ? TransactionalConfig.newInstance(this.timeout, Isolation.ISOLATION_READ_COMMITTED,
                    new Class[]{InsertException.class, UpdateException.class, DropException.class})
                    : null;
            DatabaseClient databaseClient = DatabaseUtils.restoreClient(txConfig);
            if (databaseClient == null) {
                return;
            }
            byte[] intBuffer = new byte[4];
            byte[] readBuffer;
            boolean rollback = Boolean.FALSE;
            while (this.position < this.endPosition) {
                boolean success = Boolean.FALSE;
                if (this.dataFile.read(intBuffer) == 4) {
                    this.position += 4;
                    int dataLength = RawUtils.readInt(intBuffer, ByteOrder.LITTLE_ENDIAN);
                    if (dataLength > 0) {
                        readBuffer = new byte[dataLength];
                        if (this.dataFile.read(readBuffer) == dataLength) {
                            DataOperate dataOperate = DataOperate.fromBytes(this.recordTypes, readBuffer);
                            if (dataOperate != null) {
                                try {
                                    this.process(databaseClient, dataOperate);
                                    success = Boolean.TRUE;
                                } catch (Exception e) {
                                    if (txConfig != null && databaseClient.rollbackException(e)) {
                                        databaseClient.rollbackTransactional();
                                        rollback = Boolean.TRUE;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    this.position += dataLength;
                } else {
                    throw new DataParseException(0x00DB00000009L, this.position);
                }
                if (success) {
                    this.successCount++;
                } else {
                    this.failedCount++;
                }
            }
            if (txConfig != null && !rollback) {
                databaseClient.endTransactional();
            }
        }

        public boolean hasError() {
            return (this.failedCount > 0) || ((this.successCount + this.failedCount) != this.totalCount);
        }

        public String errorMessage() {
            return this.errorLog.toString();
        }

        @Override
        public void close() throws IOException {
            this.dataFile.close();
        }

        private void process(final DatabaseClient databaseClient, final DataOperate dataOperate) throws Exception {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Data_Parse_Result", dataOperate.isRemoveOperate(),
                        dataOperate.getEntityClass().toString(),
                        StringUtils.objectToString(dataOperate.getPrimaryKey(), StringUtils.StringType.JSON, Boolean.TRUE),
                        StringUtils.objectToString(dataOperate.getDataMap(), StringUtils.StringType.JSON, Boolean.TRUE));
            }
            TableConfig tableConfig =
                    EntityManager.tableConfig(ClassUtils.originalClassName(dataOperate.getEntityClass()));
            if (tableConfig == null) {
                throw new TableConfigException(0x00DB00000001L);
            }
            Map<String, Object> primaryKeyMap =
                    EntityManager.unmarshalMap(dataOperate.getEntityClass(), dataOperate.getPrimaryKey());
            BaseObject recordObject =
                    (BaseObject) databaseClient.retrieve(primaryKeyMap, dataOperate.getEntityClass(), Boolean.TRUE);
            if (dataOperate.isRemoveOperate()) {
                databaseClient.dropRecords(recordObject);
            } else {
                boolean newObj = (recordObject == null);
                if (recordObject == null) {
                    recordObject = (BaseObject) ObjectUtils.newInstance(tableConfig.getDefineClass());
                    for (Map.Entry<String, Object> entry : primaryKeyMap.entrySet()) {
                        ReflectionUtils.setField(entry.getKey(), recordObject, entry.getValue());
                    }
                }
                for (Map.Entry<String, Object> entry :
                        EntityManager.unmarshalMap(dataOperate.getEntityClass(), dataOperate.getDataMap()).entrySet()) {
                    ReflectionUtils.setField(entry.getKey(), recordObject, entry.getValue());
                }
                if (newObj) {
                    databaseClient.saveRecords(recordObject);
                } else {
                    databaseClient.updateRecords(recordObject);
                }
            }
        }
    }

    private static abstract class ProcessThread extends Thread {
        /**
         * <span class="en-US">Task unique identification code</span>
         * <span class="zh-CN">任务唯一识别代码</span>
         */
        private final Long taskCode;
        /**
         * <span class="en-US">Error message builder</span>
         * <span class="zh-CN">错误信息收集器</span>
         */
        protected final StringBuilder errorLog;
        /**
         * <span class="en-US">An exception occurred during task execution</span>
         * <span class="zh-CN">任务执行过程中出现异常</span>
         */
        protected boolean hasError = Boolean.FALSE;

        protected ProcessThread(final long taskCode) {
            this.taskCode = taskCode;
            this.errorLog = new StringBuilder();
        }

        /*
         * (non-Javadoc)
         * @see java.lang.Thread#run()
         */
        @Override
        public void run() {
            DataUtils.processTask(this.taskCode, INSTANCE.identifyCode);
            this.process();
            DataUtils.finishTask(this);
        }

        public abstract void process();

        public Long getTaskCode() {
            return taskCode;
        }

        public boolean isHasError() {
            return hasError;
        }

        public String errorMessage() {
            return this.errorLog.toString();
        }
    }

    private static final class ExportThread extends ProcessThread {
        /**
         * <span class="en-US">Export Excel using compatibility mode</span>
         * <span class="zh-CN">使用兼容模式输出Excel</span>
         */
        private final boolean compatibilityMode;
        /**
         * <span class="en-US">Query information list for data export tasks</span>
         * <span class="zh-CN">数据导出任务的查询信息列表</span>
         */
        private final List<QueryInfo> queryInfoList;

        public ExportThread(final ExportTask exportTask) {
            super(exportTask.getTaskCode());
            this.compatibilityMode = exportTask.isCompatibilityMode();
            this.queryInfoList = exportTask.getQueryInfoList();
        }

        @Override
        public void process() {
            try (DataExporter dataExporter =
                         new DataExporter(INSTANCE.exportPath(this.getTaskCode(), this.compatibilityMode))) {
                DatabaseClient databaseClient = DatabaseUtils.readOnlyClient();
                if (databaseClient != null) {
                    for (QueryInfo queryInfo : this.queryInfoList) {
                        databaseClient.queryList(queryInfo).getResultList()
                                .forEach(dataExporter::appendData);
                    }
                    this.hasError = Boolean.FALSE;
                } else {
                    this.hasError = Boolean.TRUE;
                }
            } catch (Exception e) {
                this.errorLog.append(e.getMessage()).append(FileUtils.CRLF);
                this.hasError = Boolean.TRUE;
            }
        }
    }

    private static final class ImportThread extends ProcessThread {

        /**
         * <span class="en-US">Task data storage path</span>
         * <span class="zh-CN">任务数据存储路径</span>
         */
        private final String dataPath;
        /**
         * <span class="en-US">Import tasks using transactions</span>
         * <span class="zh-CN">导入任务使用事务</span>
         */
        private final boolean transactional;
        /**
         * <span class="en-US">Transaction timeout</span>
         * <span class="zh-CN">事务超时时间</span>
         */
        private final int timeout;

        public ImportThread(final ImportTask taskInfo) {
            super(taskInfo.getTaskCode());
            this.dataPath = taskInfo.getDataPath();
            this.transactional = taskInfo.isTransactional();
            this.timeout = taskInfo.getTimeout();
        }

        @Override
        public void process() {
            try (final DataParser dataParser = new DataParser(this.transactional, this.timeout, this.dataPath)) {
                dataParser.process();
                this.hasError = dataParser.hasError();
                this.errorLog.append(dataParser.errorMessage());
            } catch (Exception e) {
                this.errorLog.append(e.getMessage()).append(FileUtils.CRLF);
                this.hasError = Boolean.TRUE;
            }
        }
    }
}
