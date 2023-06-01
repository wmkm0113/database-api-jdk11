package org.nervousync.database.annotations.table;

import org.nervousync.database.enumerations.drop.DropOption;
import org.nervousync.database.enumerations.lock.LockOption;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
public @interface Options {

	LockOption lockOption() default LockOption.NONE;

	DropOption dropOption() default DropOption.NONE;

}
