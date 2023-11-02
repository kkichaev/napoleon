package com.grsoft.types;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Пишем имя feature из Features (в точности) по значению ее (true или false) поле включается или выключается в БД
 * @author 1111
 *
 */
@Retention(value=RetentionPolicy.RUNTIME)
public @interface Feature {
	String feature(); 
}
