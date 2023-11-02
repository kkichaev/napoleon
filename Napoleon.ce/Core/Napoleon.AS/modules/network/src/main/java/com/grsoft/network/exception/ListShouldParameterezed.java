/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Исключение, если лист представлен без generic параметра
 *
 * kki   12/10/2010   creating
 */
package com.grsoft.network.exception;


@SuppressWarnings("serial")
public class ListShouldParameterezed extends Exception
{

	private Class<?> type;

	public ListShouldParameterezed(Class<?> type)
	{
		this.type = type;
	}
	
	@Override
	public String getMessage()
	{
		return "The type of list should be parameterezed" + type.toString();
	}

}
