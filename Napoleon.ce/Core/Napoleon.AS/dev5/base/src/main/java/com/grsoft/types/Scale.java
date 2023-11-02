/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Аннтоация масштаб числа FPNumber
 *
 * kki   19/10/2010   creating
 */
package com.grsoft.types;
import com.grsoft.aceteam.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(value=RetentionPolicy.RUNTIME)
public @interface Scale
{
	int value() default 0;
	boolean hideRest() default true;
}
