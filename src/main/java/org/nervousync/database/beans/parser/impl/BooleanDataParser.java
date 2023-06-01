package org.nervousync.database.beans.parser.impl;

import org.nervousync.database.beans.parser.AbstractDataParser;
import org.nervousync.utils.ClassUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class BooleanDataParser extends AbstractDataParser {
    @Override
    @SuppressWarnings("unchecked")
    public <T> T parse(String value, Class<T> clazz) {
        Boolean boolValue = Boolean.valueOf(value);
        try {
            if (clazz.isPrimitive()) {
                String className = clazz.getName();
                String methodName = className + "Value";
                Method convertMethod = ClassUtils.findMethod(ClassUtils.primitiveWrapper(clazz), methodName);
                if (convertMethod != null) {
                    return (T) convertMethod.invoke(boolValue);
                }
            }
            return clazz.cast(boolValue);
        } catch (IllegalAccessException | InvocationTargetException ignored) {
        }
        return null;
    }
}
