package org.nervousync.database.api;

public interface DatabaseManager {

	boolean initialize();

	int registerTable(final Class<?>... entityClasses);

	void removeTable(final Class<?>... entityClasses);

	int dropTable(final Class<?>... entityClass);

	DatabaseClient readOnlyClient();

	DatabaseClient generateClient();

	DatabaseClient generateClient(final Class<?> clazz, final String methodName);

	void destroy();
}
