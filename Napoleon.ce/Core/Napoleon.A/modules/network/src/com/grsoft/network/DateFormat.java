/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Формат данных типа даты
 *
 * kki   11/10/2010   creating
 */

package com.grsoft.network;

public class DateFormat extends DateStampFormat
{
	public DateFormat(String name){
		super(name, "yyyy-MM-dd", ":d");
	}
}
