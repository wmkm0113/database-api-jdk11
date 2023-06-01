package org.nervousync.database.beans.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractDataParser {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    public abstract <T> T parse(final String value, final Class<T> clazz);

}
