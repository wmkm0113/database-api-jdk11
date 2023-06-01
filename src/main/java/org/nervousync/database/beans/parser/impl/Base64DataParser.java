package org.nervousync.database.beans.parser.impl;

import org.nervousync.database.beans.parser.AbstractDataParser;
import org.nervousync.utils.StringUtils;

public final class Base64DataParser extends AbstractDataParser {

    @Override
    public <T> T parse(String value, Class<T> clazz) {
        return clazz.cast(StringUtils.base64Decode(value));
    }
}
