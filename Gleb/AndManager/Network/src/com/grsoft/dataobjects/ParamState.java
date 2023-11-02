/*
 * Copyright (C), 2011, Гильдия Разработчиков
 *
 * Значение параметра состояний DataObject
 *
 * kki   06/03/2011   creating
 */
package com.grsoft.dataobjects;

public class ParamState
{
	/**
	 * Объект отправлен на сервер
	 */
	public static int ofExported = 1;
	
	/**
	 * Наличные
	 */
	public static int ofCash = 2; // надо чтобы совпадал с WM
	
	public static int ofProceeded = 0x20000;
}
