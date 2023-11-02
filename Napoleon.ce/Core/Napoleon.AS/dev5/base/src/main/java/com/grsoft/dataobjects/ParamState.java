/*
 * Copyright (C), 2011, Гильдия Разработчиков
 *
 * Значение параметра состояний DataObject
 *
 * kki   06/03/2011   creating
 */
package com.grsoft.dataobjects;
import com.grsoft.aceteam.R;

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
	
	/**
	 * флаг в обработке
	 */	
	public static int ofProceeded = 0x20000;
	
	/**
	 * документ распечатан
	 */
	public static int ofPrinted = 0x10;

//	public static int ofSended = 0x20;
}
