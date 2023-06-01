package org.nervousync.database.api;

import org.nervousync.database.exceptions.record.*;
import org.nervousync.database.query.PartialCollection;
import org.nervousync.database.query.QueryInfo;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface DatabaseClient {

	void rollbackTransactional(final Exception e);

	void endTransactional();

	void saveRecords(final Object... recordObjects) throws InsertException;

	void updateRecords(final Object... recordObjects) throws UpdateException;

	void dropRecords(final Object... recordObjects) throws DeleteException;

	<T> T retrieve(final Serializable primaryKey, final Class<T> entityClass, final boolean forUpdate) throws RetrieveException;

	<T> T retrieve(final Map<String, Object> queryParameters, final Class<T> entityClass, final boolean forUpdate)
			throws RetrieveException;

	long queryTotal(final QueryInfo queryInfo) throws QueryException;

	PartialCollection query(final QueryInfo queryInfo) throws QueryException;

	<T> List<T> query(final QueryInfo queryInfo, final Class<T> entityClass) throws QueryException;

}
