package org.nervousync.database.beans.parser.impl;

import org.nervousync.enumerations.xml.DataType;
import org.nervousync.database.beans.parser.AbstractDataParser;
import org.nervousync.utils.ClassUtils;
import org.nervousync.utils.ObjectUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;

public final class NumberDataParser extends AbstractDataParser {
    
    @Override
    @SuppressWarnings("unchecked")
    public <T> T parse(String value, Class<T> clazz) {
		if (DataType.NUMBER.equals(ObjectUtils.retrieveSimpleDataType(clazz))) {
			if (clazz.equals(BigInteger.class)) {
				return clazz.cast(new BigInteger(value));
			}
			if (clazz.equals(BigDecimal.class)) {
				return clazz.cast(new BigDecimal(value));
			}
			String stringValue = value;
			if (clazz.equals(Integer.class) || clazz.equals(int.class)
					|| clazz.equals(Short.class) || clazz.equals(short.class)
					|| clazz.equals(Long.class) || clazz.equals(long.class)) {
				if (stringValue.contains(".")) {
					stringValue = stringValue.substring(0, stringValue.indexOf("."));
				}
			}

			Method method = ClassUtils.findMethod(ClassUtils.primitiveWrapper(clazz),
					"valueOf", new Class[]{String.class});
			if (method != null) {
				try {
					Object targetObject = method.invoke(null, stringValue);
					if (clazz.isPrimitive()) {
						String className = clazz.getName();
						String methodName = className + "Value";
						Method convertMethod = ClassUtils.findMethod(ClassUtils.primitiveWrapper(clazz), methodName);
						if (convertMethod != null) {
							return (T) convertMethod.invoke(targetObject);
						}
					} else {
						return clazz.cast(targetObject);
					}
				} catch (IllegalAccessException | InvocationTargetException ignored) {
				}
			}
		}
		return null;
    }
}
