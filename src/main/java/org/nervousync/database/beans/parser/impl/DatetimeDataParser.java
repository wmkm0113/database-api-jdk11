package org.nervousync.database.beans.parser.impl;

import org.nervousync.database.beans.parser.AbstractDataParser;

import java.util.Date;

public final class DatetimeDataParser extends AbstractDataParser {
    @Override
    public <T> T parse(String value, Class<T> clazz) {
			return clazz.cast(new Date(Long.parseLong(value)));
    }
}
