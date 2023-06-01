/*
 * Copyright © 2003 Nervousync® Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of
 * Nervousync Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with Nervousync Studio.
 */

package org.nervousync.database.entity;

import org.nervousync.commons.core.Globals;
import org.nervousync.database.beans.configs.table.TableConfig;
import org.nervousync.utils.ConvertUtils;
import org.nervousync.utils.SecurityUtils;
import org.nervousync.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * The type Entity manager.
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision : 1.0 $ $Date: 2021/1/9 14:57 $
 */
public final class EntityManager {

	/**
	 * The constant INSTANCE.
	 */
	private static final EntityManager INSTANCE = new EntityManager();

	/**
	 * The constant LOGGER.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(EntityManager.class);

	/**
	 * The Table configs.
	 */
	/* Register database table mapping, key: table name/class name and value: table configure object */
	private final Hashtable<String, TableConfig> registeredTableConfig = new Hashtable<>();

	/**
	 * Instantiates a new Entity manager.
	 */
	private EntityManager() {
	}

	/**
	 * Gets instance.
	 *
	 * @return the instance
	 */
	public static EntityManager getInstance() {
		return EntityManager.INSTANCE;
	}

	/**
	 * Register entity mapping table
	 *
	 * @param entityClasses the entity classes
	 *
	 * @return Registered TableConfig object list
	 */
	public List<TableConfig> registerTable(final Class<?>... entityClasses) {
		List<TableConfig> registeredTables = new ArrayList<>();
		Arrays.asList(entityClasses).forEach(entityClass ->
				TableConfig.newInstance(entityClass)
						.ifPresent(tableConfig -> {
							if (this.tableExists(entityClass)) {
								LOGGER.warn("Override table config, entity class: {} table name: {}",
										entityClass.getName(), tableConfig.getTableName());
							}
							this.registeredTableConfig.put(tableConfig.getTableName(), tableConfig);
							this.registeredTableConfig.put(entityClass.getName(), tableConfig);
							this.registeredTableConfig.put(identifyKey(entityClass.getName()), tableConfig);
							registeredTables.add(tableConfig);
							if (LOGGER.isDebugEnabled()) {
								LOGGER.debug("Register class {} mapping to table {}",
										entityClass.getName(), tableConfig.getTableName());
							}
						}));
		return registeredTables;
	}

	/**
	 * Remove registered table
	 *
	 * @param entityClass the entity class
	 */
	public void removeTable(final Class<?> entityClass) {
		if (this.registeredTableConfig.containsKey(entityClass.getName())) {
			TableConfig tableConfig = this.registeredTableConfig.get(entityClass.getName());
			this.registeredTableConfig.remove(tableConfig.getTableName());
			this.registeredTableConfig.remove(entityClass.getName());
			this.registeredTableConfig.remove(identifyKey(entityClass.getName()));
		}
	}

	/**
	 * Check given entity define class was registered
	 *
	 * @param defineClass Entity define class
	 * @return <code>true</code> if registered or <code>false</code> for not register
	 */
	public boolean tableExists(final Class<?> defineClass) {
		return this.tableExists(defineClass.getName());
	}

	/**
	 * Check given entity define class was registered
	 *
	 * @param identifyKey Entity identify key
	 * @return <code>true</code> if registered or <code>false</code> for not register
	 */
	public boolean tableExists(final String identifyKey) {
		if (StringUtils.isEmpty(identifyKey)) {
			return Boolean.FALSE;
		}
		return this.registeredTableConfig.containsKey(identifyKey(identifyKey));
	}

	/**
	 * Retrieve Entity table configure by given entity define class
	 *
	 * @param defineClass Entity define class
	 * @return TableConfig optional
	 * @see TableConfig
	 */
	public TableConfig retrieveTableConfig(final Class<?> defineClass) {
		return this.retrieveTableConfig(defineClass.getName());
	}

	/**
	 * Retrieve Entity table configure by given identify key
	 *
	 * @param identifyKey Entity identify key
	 * @return TableConfig optional
	 * @see TableConfig
	 */
	public TableConfig retrieveTableConfig(final String identifyKey) {
		if (StringUtils.isEmpty(identifyKey)) {
			return null;
		}
		String mapKey;
		if (identifyKey.contains("$")) {
			mapKey = identifyKey.substring(0, identifyKey.indexOf("$"));
		} else {
			mapKey = identifyKey;
		}
		return this.registeredTableConfig.get(mapKey);
	}

	public boolean matchAlias(final Object object, final String aliasName) {
		return Optional.ofNullable(this.retrieveTableConfig(object.getClass()))
				.map(tableConfig -> tableConfig.getAliasName().equals(aliasName))
				.orElse(Boolean.FALSE);
	}

	/**
	 * Convert define class to identify key
	 *
	 * @param className the class name
	 * @return Identify key
	 */
	public static String identifyKey(final String className) {
		if (StringUtils.isEmpty(className)) {
			return Globals.DEFAULT_VALUE_STRING;
		}

		String identifyName = className.contains("$") ? className.substring(0, className.indexOf("$")) : className;
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Class identify name: {}", identifyName);
		}
		return ConvertUtils.byteToHex(SecurityUtils.SHA256(identifyName));
	}

	/**
	 * Destroy.
	 */
	public static void destroy() {
		INSTANCE.registeredTableConfig.clear();
	}
}
