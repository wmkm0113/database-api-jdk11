package org.nervousync.database.annotations.table;

import org.nervousync.database.beans.parser.AbstractDataParser;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Documented
public @interface DataParser {
    Class<? extends AbstractDataParser> value();
}
