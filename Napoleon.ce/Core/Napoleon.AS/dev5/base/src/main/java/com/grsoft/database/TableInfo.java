/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Аннтоация по которой будет строится PrimaryKey
 *
 * kki   19/10/2010   creating
 */
package com.grsoft.database;
import com.grsoft.aceteam.R;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(value=RetentionPolicy.RUNTIME)
@Inherited
public @interface TableInfo 
{
	/**
	 * Имя таблицы в базе данных.
	 * @return
	 */
	String name() default "";
	
	/**
	 * Первичный ключ
	 * @return
	 */
	String keyFields() default "";
	
	/**
	 * Индексы таблицы в формат f1,f2:f1,f2 два индекса по два поля
	 * @return
	 */
	String indexes() default "";
}
