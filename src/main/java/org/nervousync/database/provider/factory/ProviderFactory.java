/*
 * Copyright © 2003 Nervousync® Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of
 * Nervousync Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with Nervousync Studio.
 */

package org.nervousync.database.provider.factory;

import org.nervousync.commons.core.Globals;
import org.nervousync.database.annotations.provider.Provider;
import org.nervousync.database.provider.DataProvider;
import org.nervousync.database.provider.VerifyProvider;
import org.nervousync.utils.ObjectUtils;
import org.nervousync.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * The type Provider factory.
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision : 1.0 $ $Date: 2021/1/8 11:43 $
 */
public final class ProviderFactory {

	/**
	 * The constant INSTANCE.
	 */
	private static final ProviderFactory INSTANCE = new ProviderFactory();

	/**
	 * The Logger.
	 */
	private final Logger logger = LoggerFactory.getLogger(ProviderFactory.class);

	/**
	 * Registered data provider
	 */
	private final Hashtable<String, Class<?>> registeredDataProviders = new Hashtable<>();

	/**
	 * Registered verify provider
	 */
	private final Hashtable<String, Class<?>> registeredVerifyProviders = new Hashtable<>();

	private String verifyProvider = Globals.DEFAULT_VALUE_STRING;

	/**
	 * Instantiates a new Provider factory.
	 */
	private ProviderFactory() {
	}

	/**
	 * Register.
	 */
	public static void register() {
		ServiceLoader.load(DataProvider.class).stream()
				.filter(provider -> provider.getClass().isAnnotationPresent(Provider.class))
				.forEach(dataProvider -> {
					Class<?> providerClass = dataProvider.getClass();
					Provider provider = providerClass.getAnnotation(Provider.class);
					INSTANCE.registerDataProvider(provider.value(), providerClass);
				});
		ServiceLoader.load(VerifyProvider.class).stream()
				.filter(provider -> provider.getClass().isAnnotationPresent(Provider.class))
				.forEach(verifyProvider -> {
					Class<?> providerClass = verifyProvider.getClass();
					Provider provider = providerClass.getAnnotation(Provider.class);
					INSTANCE.registerVerifyProvider(provider.value(), providerClass);
				});
	}

	/**
	 * Gets instance.
	 *
	 * @return the instance
	 */
	public static ProviderFactory getInstance() {
		return ProviderFactory.INSTANCE;
	}

	/**
	 * Sets verify provider.
	 *
	 * @param verifyProvider the verify provider
	 */
	public void setVerifyProvider(String verifyProvider) {
		this.verifyProvider = verifyProvider;
	}

	/**
	 * Destroy.
	 */
	public static void destroy() {
		INSTANCE.registeredDataProviders.clear();
	}

	/**
	 * Check Register Status by Given Provider Type and Provider Name
	 *
	 * @param providerName Provider Name
	 * @return Register Status
	 */
	public static boolean registeredProvider(String providerName) {
		return INSTANCE.registeredDataProviders.containsKey(providerName);
	}

	/**
	 * Check Register Status by Given Provider Name
	 *
	 * @param providerName Provider Name
	 * @return Register Status
	 */
	public static boolean registeredVerifier(String providerName) {
		return INSTANCE.registeredVerifyProviders.containsKey(providerName);
	}

	/**
	 * Registered Provider Name List by Given Provider Type
	 *
	 * @return Registered Data Provider Name List
	 */
	public List<String> dataProviders() {
		List<String> providerList = new ArrayList<>();
		this.registeredDataProviders.forEach((key, value) -> providerList.add(key));
		return providerList;
	}

	/**
	 * Registered Provider Name List by Given Provider Type
	 *
	 * @return Registered Verify Provider Name List
	 */
	public List<String> verifyProviders() {
		List<String> providerList = new ArrayList<>();
		this.registeredVerifyProviders.forEach((key, value) -> providerList.add(key));
		return providerList;
	}

	/**
	 * Initialize data provider optional.
	 *
	 * @param providerName the provider name
	 * @return the optional
	 */
	public Optional<DataProvider> dataProvider(String providerName) {
		Class<?> protectClass = this.registeredDataProviders.get(providerName);
		return Optional.ofNullable((DataProvider) ObjectUtils.newInstance(protectClass));
	}

	/**
	 * Initialize verify provider optional.
	 *
	 * @return the optional
	 */
	public VerifyProvider verifyProvider() {
		if (StringUtils.notBlank(this.verifyProvider)
				&& this.registeredVerifyProviders.containsKey(this.verifyProvider)) {
			return (VerifyProvider) ObjectUtils.newInstance(this.registeredVerifyProviders.get(this.verifyProvider));
		}
		return null;
	}

	/**
	 * Register Data Provider Class
	 *
	 * @param providerName  the provider name
	 * @param providerClass the provider class
	 */
	private void registerDataProvider(String providerName, Class<?> providerClass) {
		if (this.registeredDataProviders.containsKey(providerName)) {
			this.logger.warn("Override provider, name: {}, original class: {}, new class: {}",
					providerName, this.registeredDataProviders.get(providerName).getName(), providerClass.getName());
		}
		this.registeredDataProviders.put(providerName, providerClass);
	}

	/**
	 * Register Verify Provider Class
	 *
	 * @param providerName  the provider name
	 * @param providerClass the provider class
	 */
	private void registerVerifyProvider(String providerName, Class<?> providerClass) {
		if (this.registeredVerifyProviders.containsKey(providerName)) {
			this.logger.warn("Override provider, name: {}, original class: {}, new class: {}",
					providerName, this.registeredVerifyProviders.get(providerName).getName(), providerClass.getName());
		}
		this.registeredVerifyProviders.put(providerName, providerClass);
	}
}
