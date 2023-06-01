package org.nervousync.database.provider;

import org.nervousync.database.exceptions.security.DataModifiedException;
import org.nervousync.database.operator.process.impl.RecordOperator;

public interface VerifyProvider {

    void patch(RecordOperator recordOperator) throws DataModifiedException;

    void patch(Object recordObject, long identifyCode);

    byte[] signature(Object recordObject);

    boolean verify(Object recordObject);

    boolean verify(String key, Object object);

}
