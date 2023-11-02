/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Формат данных типа даты
 *
 * kki   25/10/2010   creating
 */

package com.grsoft.network;

public class StampFormat extends DateStampFormat{

	public StampFormat(String name){
		super(name, "yyyy-MM-dd HH:mm:ss", ":dt");
	}
}
