package org.nervousync.database.beans.parser.impl;

import org.nervousync.database.beans.parser.AbstractDataParser;
import org.nervousync.utils.StringUtils;

public final class Base32DataParser extends AbstractDataParser {
    @Override
    public <T> T parse(final String value, final Class<T> clazz) {
        return clazz.cast(StringUtils.base32Decode(value));
    }
}
