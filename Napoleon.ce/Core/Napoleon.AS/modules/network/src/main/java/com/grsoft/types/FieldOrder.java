package com.grsoft.types;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Порядок следования полей в структуре - важно для вложенных коллекций.
 * Нумерация идет с 0
 * @author 1111
 *
 */
@Retention(value=RetentionPolicy.RUNTIME)
public @interface FieldOrder {
	int order();
}
