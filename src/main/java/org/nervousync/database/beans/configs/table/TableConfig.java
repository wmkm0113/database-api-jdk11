/*
 * Copyright © 2003 Nervousync® Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of
 * Nervousync Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with Nervousync Studio.
 */
package org.nervousync.database.beans.configs.table;

import jakarta.persistence.*;
import org.nervousync.commons.core.Globals;
import org.nervousync.database.annotations.table.Options;
import org.nervousync.database.beans.configs.column.ColumnConfig;
import org.nervousync.database.beans.configs.generator.GeneratorConfig;
import org.nervousync.database.beans.configs.reference.ReferenceConfig;
import org.nervousync.database.commons.DatabaseCommons;
import org.nervousync.database.dialects.Converter;
import org.nervousync.database.entity.core.BaseObject;
import org.nervousync.database.enumerations.drop.DropOption;
import org.nervousync.database.enumerations.generation.GenerationOption;
import org.nervousync.database.enumerations.lock.LockOption;
import org.nervousync.database.exceptions.entity.TableConfigException;
import org.nervousync.database.exceptions.security.DataModifiedException;
import org.nervousync.database.provider.VerifyProvider;
import org.nervousync.database.provider.factory.ProviderFactory;
import org.nervousync.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Table config
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision : 1.0 $ $Date: Mar 23, 2010 10:13:02 AM $
 */
public final class TableConfig implements Serializable {

    /**
     * The constant serialVersionUID.
     */
    private static final long serialVersionUID = -6261205588355266688L;

    /**
     * The constant LOGGER.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TableConfig.class);

    /**
     * Database schema name
     */
    private final String schemaName;
    /**
     * Table Name
     */
    private final String tableName;
    /**
     * Data cache: Y/N
     */
    private final boolean cacheable;
    /**
     * Mapping class name
     */
    private final Class<?> defineClass;
    /**
     * Mapping column List
     */
    private final List<ColumnConfig> columnConfigList;
    /**
     * Define search index
     */
    private final Index[] indexes;
    /**
     * Table lock option
     */
    private final LockOption lockOption;
    /**
     * Table drop option
     */
    private final DropOption dropOption;
    /**
     * Lazy load method mapping
     */
    private final Hashtable<String, ReferenceConfig> referenceConfigs;

    /**
     * Instantiates a new Table config.
     *
     * @param clazz the clazz
     * @throws TableConfigException the table config exception
     */
    private TableConfig(final Class<?> clazz) throws TableConfigException {
        this.columnConfigList = new ArrayList<>();
        this.referenceConfigs = new Hashtable<>();

        Table table = clazz.getAnnotation(Table.class);

        this.schemaName = StringUtils.isEmpty(table.schema()) ? DatabaseCommons.DEFAULT_DATABASE_ALIAS : table.schema();
        this.cacheable = clazz.isAnnotationPresent(Cacheable.class)
                ? clazz.getAnnotation(Cacheable.class).value()
                : Boolean.FALSE;
        if (clazz.isAnnotationPresent(Options.class)) {
            Options options = clazz.getAnnotation(Options.class);
            this.lockOption = options.lockOption();
            this.dropOption = options.dropOption();
        } else {
            this.lockOption = LockOption.NONE;
            this.dropOption = DropOption.NONE;
        }
        this.indexes = table.indexes();

        this.tableName = StringUtils.notBlank(table.name()) ? table.name() : clazz.getSimpleName();
        this.defineClass = clazz;

        List<Field> fieldList = this.retrieveDeclaredFields(this.defineClass);

        Object object = ObjectUtils.newInstance(this.defineClass);

        for (Field field : fieldList) {
            if (field.isAnnotationPresent(Column.class)) {
                this.parseColumnField(field, object, field.isAnnotationPresent(Id.class))
                        .ifPresent(this.columnConfigList::add);
            } else if (lazyLoadField(field)) {
                String fieldName = field.getName();
                Class<?> referenceClass = null;
                CascadeType[] cascadeType;
                boolean lazyLoad = false;

                if (field.isAnnotationPresent(OneToMany.class)) {
                    OneToMany oneToMany = field.getAnnotation(OneToMany.class);
                    referenceClass = oneToMany.targetEntity();
                    lazyLoad = FetchType.LAZY.equals(oneToMany.fetch());
                    cascadeType = oneToMany.cascade();
                } else if (field.isAnnotationPresent(ManyToOne.class)) {
                    ManyToOne manyToOne = field.getAnnotation(ManyToOne.class);
                    referenceClass = manyToOne.targetEntity();
                    lazyLoad = FetchType.LAZY.equals(manyToOne.fetch());
                    cascadeType = manyToOne.cascade();
                } else if (field.isAnnotationPresent(OneToOne.class)) {
                    OneToOne oneToOne = field.getAnnotation(OneToOne.class);
                    referenceClass = oneToOne.targetEntity();
                    lazyLoad = FetchType.LAZY.equals(oneToOne.fetch());
                    cascadeType = oneToOne.cascade();
                } else {
                    cascadeType = new CascadeType[0];
                }

                boolean returnArray = (List.class.isAssignableFrom(field.getType()) || field.getType().isArray());

                if (void.class.equals(referenceClass)) {
                    if (returnArray) {
                        Type type = field.getGenericType();
                        if (type instanceof ParameterizedType) {
                            Type[] fieldTypes = ((ParameterizedType) type).getActualTypeArguments();
                            if (fieldTypes.length == 1) {
                                referenceClass = (Class<?>) fieldTypes[0];
                            } else {
                                throw new TableConfigException("Reference configure error! Cannot found target entity class...");
                            }
                        }
                    } else {
                        referenceClass = field.getType();
                    }
                }

                if (referenceClass != null) {
                    JoinColumn[] joinColumns = null;

                    if (field.isAnnotationPresent(JoinColumns.class)) {
                        JoinColumns annotationColumns = field.getAnnotation(JoinColumns.class);
                        joinColumns = annotationColumns.value();
                    } else if (field.isAnnotationPresent(JoinColumn.class)) {
                        joinColumns = new JoinColumn[]{field.getAnnotation(JoinColumn.class)};
                    }

                    ReferenceConfig referenceConfig = ReferenceConfig.initialize(referenceClass, fieldName, lazyLoad,
                            returnArray, cascadeType, joinColumns);

                    if (referenceConfig != null) {
                        this.referenceConfigs.put(fieldName, referenceConfig);
                    }
                }
            }
        }

        Method[] methods = ReflectionUtils.getAllDeclaredMethods(clazz);

        for (Method method : methods) {
            String methodName = method.getName();
            if ((method.isAnnotationPresent(OneToMany.class) || method.isAnnotationPresent(ManyToOne.class))
                    && (method.isAnnotationPresent(JoinColumns.class) || method.isAnnotationPresent(JoinColumn.class))
                    && (methodName.startsWith("get") || methodName.startsWith("is"))) {
                Class<?> referenceClass = null;
                CascadeType[] cascadeType;
                boolean lazyLoad = false;

                if (method.isAnnotationPresent(OneToMany.class)) {
                    OneToMany oneToMany = method.getAnnotation(OneToMany.class);
                    referenceClass = oneToMany.targetEntity();
                    lazyLoad = FetchType.LAZY.equals(oneToMany.fetch());
                    cascadeType = oneToMany.cascade();
                } else if (method.isAnnotationPresent(ManyToOne.class)) {
                    ManyToOne manyToOne = method.getAnnotation(ManyToOne.class);
                    referenceClass = manyToOne.targetEntity();
                    lazyLoad = FetchType.LAZY.equals(manyToOne.fetch());
                    cascadeType = manyToOne.cascade();
                } else {
                    cascadeType = new CascadeType[0];
                }

                if (referenceClass != null) {
                    boolean returnArray = List.class.isAssignableFrom(method.getReturnType())
                            || method.getReturnType().isArray();

                    JoinColumn[] joinColumns = null;

                    if (method.isAnnotationPresent(JoinColumns.class)) {
                        JoinColumns annotationColumns = method.getAnnotation(JoinColumns.class);
                        joinColumns = annotationColumns.value();
                    } else if (method.isAnnotationPresent(JoinColumn.class)) {
                        joinColumns = new JoinColumn[]{method.getAnnotation(JoinColumn.class)};
                    }

                    ReferenceConfig referenceConfig = ReferenceConfig.initialize(referenceClass,
                            ReflectionUtils.fieldName(methodName), lazyLoad, returnArray, cascadeType, joinColumns);

                    if (referenceConfig != null) {
                        this.referenceConfigs.put(methodName, referenceConfig);
                    }
                }
            }
        }
    }

    public static boolean lazyLoadField(final Field field) {
        if (field == null) {
            return Boolean.FALSE;
        }
        return (field.isAnnotationPresent(OneToMany.class) || field.isAnnotationPresent(ManyToOne.class) || field.isAnnotationPresent(OneToOne.class))
                && (field.isAnnotationPresent(JoinColumns.class) || field.isAnnotationPresent(JoinColumn.class));
    }

    /**
     * Generate TableConfig object by given class
     *
     * @param clazz Entity class define
     * @return TableConfig object or null if class is invalid
     */
    public static Optional<TableConfig> newInstance(final Class<?> clazz) {
        TableConfig tableConfig = null;
        if (clazz.isAnnotationPresent(Table.class)) {
            try {
                tableConfig = new TableConfig(clazz);
            } catch (Exception e) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Generate table config error! ", e);
                }
            }
        }
        return Optional.ofNullable(tableConfig);
    }

    /**
     * Gets serial version uid.
     *
     * @return the serialVersionUID
     */
    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    /**
     * Gets database name.
     *
     * @return the database name
     */
    public String getSchemaName() {
        return schemaName;
    }

    /**
     * Gets table name.
     *
     * @return the tableName
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * Is cacheable boolean.
     *
     * @return the boolean
     */
    public boolean isCacheable() {
        return cacheable;
    }

    /**
     * Gets define class.
     *
     * @return the defineClass
     */
    public Class<?> getDefineClass() {
        return defineClass;
    }

    /**
     * Gets column config list.
     *
     * @return the columnConfigList
     */
    public List<ColumnConfig> getColumnConfigList() {
        return columnConfigList;
    }

    /**
     * Get indexes search index [ ].
     *
     * @return the indexes
     */
    public Index[] getIndexes() {
        return indexes == null ? new Index[0] : indexes.clone();
    }

    /**
     * Gets lock option.
     *
     * @return the lockOption
     */
    public LockOption getLockOption() {
        return lockOption;
    }

    /**
     * Gets drop option.
     *
     * @return the dropOption
     */
    public DropOption getDropOption() {
        return dropOption;
    }

    /**
     * Identify key string.
     *
     * @param object the base object
     * @return the string
     */
    public String identifyKey(Object object) {
        return this.identifyKey(object, Globals.DEFAULT_VALUE_STRING);
    }

    /**
     * Identify key string.
     *
     * @param object  the base object
     * @param itemKey the item key
     * @return the string
     */
    public String identifyKey(final Object object, final String itemKey) {
        if (object != null && this.defineClass.equals(object.getClass())) {
            TreeMap<String, Object> dataMap = new TreeMap<>();
            this.getColumnConfigList().stream()
                    .filter(ColumnConfig::isPrimaryKeyColumn)
                    .forEach(columnConfig ->
                            dataMap.put(columnConfig.getColumnName().toUpperCase(),
                                    ReflectionUtils.getFieldValue(columnConfig.getFieldName(), object)));
            dataMap.put(DatabaseCommons.CONTENT_MAP_KEY_DATABASE_NAME.toUpperCase(), this.schemaName.toUpperCase());
            dataMap.put(DatabaseCommons.CONTENT_MAP_KEY_TABLE_NAME.toUpperCase(), this.tableName.toUpperCase());
            if (StringUtils.notBlank(itemKey)) {
                dataMap.put(DatabaseCommons.CONTENT_MAP_KEY_ITEM.toUpperCase(),
                        Optional.ofNullable(this.getColumnConfig(itemKey))
                                .filter(ColumnConfig::isLazyLoad)
                                .map(columnConfig -> columnConfig.getColumnName().toUpperCase())
                                .orElseThrow(() ->
                                        new TableConfigException("Can't found column define! Item key: " + itemKey)));
            }
            return ConvertUtils.byteToHex(SecurityUtils.SHA256(dataMap));
        }
        return Globals.DEFAULT_VALUE_STRING;
    }

    /**
     * Cache keys list.
     *
     * @param object the object
     * @return the list
     */
    public List<String> cacheKeys(final Object object) {
        List<String> cacheKeys = new ArrayList<>();
        SortedMap<String, Object> primaryKeyMap = this.generatePrimaryKeyMap(object);
        this.columnConfigList.stream()
                .filter(ColumnConfig::isLazyLoad)
                .forEach(columnConfig -> {
                    TreeMap<String, Object> keyMap = new TreeMap<>(primaryKeyMap);
                    keyMap.put(DatabaseCommons.CONTENT_MAP_KEY_ITEM.toUpperCase(), columnConfig.getColumnName());
                    cacheKeys.add(DatabaseCommons.cacheKey(this.defineClass.getName(), keyMap));
                });
        cacheKeys.add(DatabaseCommons.cacheKey(this.defineClass.getName(), primaryKeyMap));
        return cacheKeys;
    }

    /**
     * Cache key string.
     *
     * @param object    the object
     * @param fieldName the field name
     * @return the string
     */
    public String cacheKey(final Object object, final String fieldName) {
        return Optional.ofNullable(this.getColumnConfig(fieldName))
                .map(columnConfig -> {
                    SortedMap<String, Object> keyMap = this.generatePrimaryKeyMap(object);
                    keyMap.put(DatabaseCommons.CONTENT_MAP_KEY_ITEM.toUpperCase(), columnConfig.getColumnName());
                    return this.cacheKey(keyMap);
                })
                .orElse(Globals.DEFAULT_VALUE_STRING);
    }

    public String cacheKey(final SortedMap<String, Object> primaryKey) {
		primaryKey.put(DatabaseCommons.CONTENT_MAP_KEY_DATABASE_NAME.toUpperCase(), this.schemaName);
		primaryKey.put(DatabaseCommons.CONTENT_MAP_KEY_TABLE_NAME.toUpperCase(), this.tableName);
		String jsonKey = StringUtils.objectToString(primaryKey, StringUtils.StringType.JSON, Boolean.FALSE);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Cache key map: {}", jsonKey);
		}
		return ConvertUtils.byteToHex(SecurityUtils.SHA256(jsonKey));
    }

    /**
     * Verify.
     *
     * @param object       the object
     * @param identifyCode identify code
     * @throws DataModifiedException the data modified exception
     */
    public void verify(final Object object, final long identifyCode) throws DataModifiedException {
        VerifyProvider verifyProvider = ProviderFactory.getInstance().verifyProvider();
        if (verifyProvider == null) {
            //	Not configure verify provider or configured verify provider not exists
            return;
        }
        verifyProvider.patch(object, identifyCode);
        if (verifyProvider.verify(object)) {
            return;
        }
        LOGGER.warn("Data record signature invalid! ");
        throw new DataModifiedException("Data record signature invalid! ");
    }

    /**
     * Column name string.
     *
     * @param identifyName column identify name
     * @return the string
     */
    public String columnName(final String identifyName) {
        if (this.isColumn(identifyName)) {
            return Optional.ofNullable(this.getColumnConfig(identifyName))
                    .map(ColumnConfig::getColumnName)
                    .orElse(Globals.DEFAULT_VALUE_STRING);
        }
        return Globals.DEFAULT_VALUE_STRING;
    }

    /**
     * Check given identifyName is a column
     *
     * @param identifyName maybe is field name or column name
     * @return check result
     */
    public boolean isColumn(final String identifyName) {
        return this.columnConfigList.stream().anyMatch(columnConfig -> columnConfig.matchIdentifyKey(identifyName));
    }

    /**
     * Check given identifyName is a lazy load column
     *
     * @param identifyName maybe is field name or column name
     * @return check result
     */
    public boolean isLazyLoad(final String identifyName) {
        if (StringUtils.isEmpty(identifyName)) {
            return Boolean.FALSE;
        }
        for (ColumnConfig columnConfig : this.columnConfigList) {
            if (columnConfig.matchIdentifyKey(identifyName)) {
                return columnConfig.isLazyLoad();
            }
        }

        if (this.referenceConfigs.containsKey(identifyName)) {
            return this.referenceConfigs.get(identifyName).isLazyLoad();
        }

        return Boolean.FALSE;
    }

    /**
     * Retrieve column config by given identifyName
     *
     * @param identifyName maybe is field name or column name
     * @return Retrieve column config, or null if not found
     */
    public ColumnConfig getColumnConfig(final String identifyName) {
        if (identifyName != null && identifyName.length() > 0) {
            for (ColumnConfig currentColumn : this.columnConfigList) {
                if (currentColumn.matchIdentifyKey(identifyName)) {
                    return currentColumn;
                }
            }
        }
        return null;
    }

    /**
     * Retrieve identified version column config
     *
     * @return Retrieve column config, or null if not defined
     */
    public Optional<ColumnConfig> identifyVersionColumn() {
        ColumnConfig columnConfig = null;
        for (ColumnConfig currentColumn : this.columnConfigList) {
            if (currentColumn.isIdentifyVersion()) {
                columnConfig = currentColumn;
            }
        }
        return Optional.ofNullable(columnConfig);
    }

    /**
     * Retrieve reference config by given field name
     *
     * @param identifyName Reference column identify name, maybe field name or reference entity class name
     * @return Retrieve ReferenceConfig object, or null if not found
     */
    public ReferenceConfig findReferenceConfig(final String identifyName) {
        if (this.referenceConfigs.containsKey(identifyName)) {
            return this.referenceConfigs.get(identifyName);
        } else {
            for (ReferenceConfig referenceConfig : this.referenceConfigs.values()) {
                if (referenceConfig.getReferenceClass().getName().equalsIgnoreCase(identifyName)) {
                    return referenceConfig;
                }
            }
        }
        return null;
    }

    /**
     * Iterator reference config
     *
     * @return Reference Iterator
     * @see Iterator
     */
    public Iterator<ReferenceConfig> referenceIterator() {
        return this.referenceConfigs.values().iterator();
    }

    /**
     * Generate primary key value
     *
     * @param object the object
     */
    public void generatePrimaryKey(final Object object) {
        this.columnConfigList.stream()
                .filter(ColumnConfig::isPrimaryKeyColumn)
                .forEach(columnConfig -> {
                    GeneratorConfig generatorConfig = columnConfig.getGeneratorConfig();
                    Object generateValue = null;
                    if (GenerationOption.GENERATOR.equals(generatorConfig.getGenerationOption())) {
                        switch (generatorConfig.getGeneratorName()) {
                            case IDUtils.SNOWFLAKE:
                                generateValue = IDUtils.snowflake();
                                break;
                            case IDUtils.NANO_ID:
                                generateValue = IDUtils.nano();
                                break;
                            case IDUtils.UUIDv1:
                                generateValue = IDUtils.UUIDv1();
                                break;
                            case IDUtils.UUIDv2:
                                generateValue = IDUtils.UUIDv2();
                                break;
                            case IDUtils.UUIDv4:
                                generateValue = IDUtils.UUIDv4();
                                break;
                        }
                    }
                    if (generateValue != null) {
                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug("Generated value: {}", generateValue);
                        }
                        ReflectionUtils.setField(columnConfig.getFieldName(), object, generateValue);
                    }
                });
    }

    /**
     * Generate primary key column parameter map
     *
     * @param object the object
     * @return Primary key map
     */
    public SortedMap<String, Object> generatePrimaryKeyMap(final Object object) {
        return this.generatePrimaryKeyMap(object, Boolean.FALSE);
    }

    /**
     * Generate primary key column parameter map
     *
     * @param object    the object
     * @param forUpdate the for update
     * @return Primary key map
     */
    public SortedMap<String, Object> generatePrimaryKeyMap(final Object object, final boolean forUpdate) {
        SortedMap<String, Object> parameterMap = new TreeMap<>();
        if (this.defineClass.isAssignableFrom(object.getClass())) {
            this.columnConfigList.stream()
                    .filter(ColumnConfig::isPrimaryKeyColumn)
                    .forEach(columnConfig -> parameterMap.put(columnConfig.getColumnName(),
                            ReflectionUtils.getFieldValue(columnConfig.getFieldName(), object)));
            if (forUpdate && LockOption.OPTIMISTIC_UPGRADE.equals(this.lockOption)) {
                this.identifyVersionColumn()
                        .ifPresent(columnConfig -> parameterMap.put(columnConfig.getColumnName(),
                                ReflectionUtils.getFieldValue(columnConfig.getFieldName(), object)));
            }
        }
        return parameterMap;
    }

    /**
     * Generate primary key columns and normal column parameter map
     *
     * @param object    the object
     * @param converter the converter
     * @return Data map
     */
    public SortedMap<String, Object> convertToDataMap(final Object object, final Converter converter) {
        SortedMap<String, Object> parameterMap = new TreeMap<>();
        if (this.defineClass.isAssignableFrom(object.getClass())) {
            this.columnConfigList.forEach(columnConfig ->
                    Optional.ofNullable(this.retrieveValue(object, columnConfig, converter))
                            .ifPresent(fieldValue -> parameterMap.put(columnConfig.getColumnName(), fieldValue)));
        }
        return parameterMap;
    }

    /**
     * Generate primary key columns and normal column parameter map
     *
     * @param object    the object
     * @param converter the converter
     * @return Data map
     */
    public SortedMap<String, Object> convertToUpdateMap(final Object object, final Converter converter) {
        TreeMap<String, Object> parameterMap = new TreeMap<>();
        if (this.defineClass.isAssignableFrom(object.getClass())) {
            if (object instanceof BaseObject) {
                ((BaseObject) object).modifiedColumns()
                        .forEach(identifyKey ->
                                Optional.ofNullable(this.getColumnConfig(identifyKey))
                                        .ifPresent(columnConfig ->
                                                parameterMap.put(columnConfig.getColumnName(),
                                                        this.retrieveValue(object, columnConfig, converter))));
            } else {
                this.columnConfigList.stream()
                        .filter(columnConfig -> !columnConfig.isPrimaryKeyColumn() && columnConfig.isUpdatable())
                        .forEach(columnConfig -> parameterMap.put(columnConfig.getColumnName(),
                                this.retrieveValue(object, columnConfig, converter)));
            }
        }
        return parameterMap;
    }

    /**
     * Retrieve value optional.
     *
     * @param object      the object
     * @param identifyKey the identify key
     * @return the optional
     */
    public Object retrieveValue(final Object object, final String identifyKey) {
        return Optional.ofNullable(this.getColumnConfig(identifyKey))
                .map(columnConfig -> this.retrieveValue(object, columnConfig, null))
                .orElse(null);
    }

    /**
     * Retrieve value object.
     *
     * @param columnConfig the column config
     * @param converter    the converter
     * @return the object
     */
    private Object retrieveValue(final Object object, final ColumnConfig columnConfig, final Converter converter) {
        Object columnValue = ReflectionUtils.getFieldValue(columnConfig.getFieldName(), object);
        if (columnValue == null || converter == null) {
            return columnValue;
        }
        return converter.convertValue(columnConfig, columnValue);
    }

    private Optional<ColumnConfig> parseColumnField(final Field field, final Object object,
                                                    final boolean primaryKeyColumn) {
        if (field.isAnnotationPresent(Version.class) && !LockOption.OPTIMISTIC_UPGRADE.equals(this.lockOption)) {
            return Optional.empty();
        }
        if (!this.isColumn(field.getName())) {
            return Optional.of(ColumnConfig.newInstance(field, object, primaryKeyColumn));
        }
        return Optional.empty();
    }

    /**
     * Retrieve declared fields list.
     *
     * @param clazz the clazz
     * @return the list
     */
    private List<Field> retrieveDeclaredFields(final Class<?> clazz) {
        if (clazz != null) {
            List<Field> declaredFields = new ArrayList<>();
            declaredFields.addAll(this.retrieveSuperClassFields(clazz.getSuperclass()));
            declaredFields.addAll(annotationFields(clazz));
            return declaredFields;
        }
        return new ArrayList<>();
    }

    /**
     * Retrieve super class fields list.
     *
     * @param clazz the clazz
     * @return the list
     */
    private List<Field> retrieveSuperClassFields(final Class<?> clazz) {
        List<Field> declaredFields = new ArrayList<>();
        if (clazz != null) {
            Class<?> superClass = clazz.getSuperclass();
            if (superClass != null) {
                declaredFields.addAll(this.retrieveSuperClassFields(superClass));
            }
            if (clazz.isAnnotationPresent(MappedSuperclass.class)) {
                declaredFields.addAll(annotationFields(clazz));
            }
        }
        return declaredFields;
    }

    private List<Field> annotationFields(final Class<?> clazz) {
        if (clazz == null) {
            return new ArrayList<>();
        }
        List<Field> declaredFields = new ArrayList<>();
        Arrays.stream(clazz.getDeclaredFields())
                .filter(this::annotationField)
                .forEach(declaredFields::add);
        return declaredFields;
    }

    private boolean annotationField(final Field field) {
        return field.isAnnotationPresent(Column.class) || field.isAnnotationPresent(EmbeddedId.class)
                || (field.isAnnotationPresent(OneToMany.class) || field.isAnnotationPresent(ManyToOne.class)
                || field.isAnnotationPresent(OneToOne.class)) &&
                (field.isAnnotationPresent(JoinColumns.class) || field.isAnnotationPresent(JoinColumn.class));
    }
}
