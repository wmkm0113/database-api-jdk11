/*
 * Copyright © 2003 Nervousync® Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of
 * Nervousync Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with Nervousync Studio.
 */
package org.nervousync.database.query;

import org.nervousync.commons.core.Globals;
import org.nervousync.database.commons.CerebrateCommons;
import org.nervousync.database.exceptions.entity.EntityStatusException;
import org.nervousync.database.exceptions.security.DataModifiedException;
import org.nervousync.database.query.result.ResultMap;
import org.nervousync.utils.StringUtils;

import java.io.Serializable;
import java.util.*;

/**
 * Partial collection
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision : 1.0 $ $Date: Jan 13, 2010 4:07:14 PM $
 */
public final class PartialCollection implements Serializable {

	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = 2086690645677391624L;

	/**
	 * The constant TOTAL_COUNT_KEY.
	 */
	private static final String TOTAL_COUNT_KEY = "totalCount";
	/**
	 * The constant RESULT_LIST_KEY.
	 */
	private static final String RESULT_LIST_KEY = "objectList";

	/**
	 * Collection of entities (part of some another collection)
	 */
	private final List<ResultMap> resultList;

	/**
	 * Total number of elements in collection this collection is part of
	 */
	private final long totalCount;

	/**
	 * Creates new instance of PartialCollection with specified collection and total
	 *
	 * @param resultList Result list
	 * @param totalCount Total size of collection, which part is contained in this instance
	 */
	public PartialCollection(List<ResultMap> resultList, long totalCount) {
		this.resultList = resultList;
		this.totalCount = totalCount;
	}

	/**
	 * As list.
	 *
	 * @return the list
	 */
	public List<ResultMap> asList() {
		return this.resultList;
	}

	/**
	 * As list.
	 *
	 * @param <T>   the type parameter
	 * @param clazz the clazz
	 * @return the list
	 */
	public <T> List<T> asList(final Class<T> clazz) {
		return this.asList(clazz, Boolean.FALSE, Globals.DEFAULT_VALUE_LONG);
	}

	/**
	 * As list.
	 *
	 * @param <T>               the type parameter
	 * @param clazz             the clazz
	 * @param forUpdate         the for update
	 * @param transactionalCode the transactional code
	 * @return the list
	 * @throws DataModifiedException the data modified exception
	 * @throws EntityStatusException the entity status exception
	 */
	public <T> List<T> asList(final Class<T> clazz, final boolean forUpdate, final long transactionalCode)
			throws DataModifiedException, EntityStatusException {
		List<T> returnList = new ArrayList<>();
		if (ResultMap.class.equals(clazz)) {
			this.resultList.forEach(resultMap -> returnList.add(clazz.cast(resultMap)));
		} else {
			this.resultList.forEach(resultMap ->
					returnList.add(resultMap.unwrap(clazz, forUpdate, Globals.DEFAULT_VALUE_LONG, transactionalCode)));
		}
		return returnList;
	}

	/**
	 * Gets the size of part of initial collection that is contained here
	 *
	 * @return number of elements in partial collection
	 */
	public int size() {
		return this.resultList.size();
	}

	/**
	 * Figures out is partial collection empty
	 *
	 * @return <code>true</code> if this collection is empty
	 */
	public boolean isEmpty() {
		return this.resultList.isEmpty();
	}

	/**
	 * Add boolean.
	 *
	 * @param resultMap the result map
	 * @return the boolean
	 */
	public boolean add(ResultMap resultMap) {
		return this.resultList.add(resultMap);
	}

	/**
	 * Iterator.
	 *
	 * @return the iterator
	 */
	public Iterator<ResultMap> iterator() {
		return this.resultList.iterator();
	}

	/**
	 * Iterator.
	 *
	 * @param <T>               the type parameter
	 * @param clazz             the clazz
	 * @param forUpdate         the for update
	 * @param transactionalCode the transactional code
	 * @return the iterator
	 */
	public <T> Iterator<T> iterator(final Class<T> clazz, final boolean forUpdate, final long transactionalCode) {
		return this.asList(clazz, forUpdate, transactionalCode).iterator();
	}

	/**
	 * Gets total number of elements in initial collection
	 *
	 * @return total number of elements
	 */
	public long getTotalCount() {
		return this.totalCount;
	}

	/**
	 * Parse partial collection.
	 *
	 * @param cacheData the cache data
	 * @return the partial collection
	 */
	public static PartialCollection parse(String cacheData) {
		if (StringUtils.isEmpty(cacheData)) {
			return null;
		}

		Map<String, Object> convertMap = StringUtils.dataToMap(cacheData, StringUtils.StringType.JSON);
		if (convertMap.isEmpty()) {
			return null;
		}
		long totalCount = Long.parseLong((String)convertMap.get(TOTAL_COUNT_KEY), 16);
		List<String> dataList = StringUtils.stringToList((String)convertMap.get(RESULT_LIST_KEY),
				Globals.DEFAULT_ENCODING, String.class);

		List<ResultMap> objectList = new ArrayList<>();
		dataList.forEach(dataInfo -> objectList.add(new ResultMap(CerebrateCommons.cacheDataToMap(dataInfo))));

		return new PartialCollection(objectList, totalCount);
	}

	/**
	 * Cache data string.
	 *
	 * @return the string
	 */
	public String cacheData() {
		List<String> objectList = new ArrayList<>(this.resultList.size());
		for (ResultMap object : this.resultList) {
			objectList.add(object.cacheData());
		}
		Map<String, Object> convertMap = new HashMap<>();
		convertMap.put(TOTAL_COUNT_KEY, Long.toHexString(this.totalCount));
		convertMap.put(RESULT_LIST_KEY,
				StringUtils.objectToString(objectList, StringUtils.StringType.JSON, Boolean.FALSE));

		return StringUtils.objectToString(convertMap, StringUtils.StringType.JSON, true);
	}
}
