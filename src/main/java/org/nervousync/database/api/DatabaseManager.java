package org.nervousync.database.api;

public interface DatabaseManager {

	boolean initialize(final String basePath);

	void registerTable(final Class<?> entityClass, final String moduleBundle);

	DatabaseClient readOnlyClient();

	DatabaseClient generateClient();

	DatabaseClient generateClient(final Class<?> clazz, final String methodName);

	void disableModule(final String moduleBundle);

	void destroy();
}
