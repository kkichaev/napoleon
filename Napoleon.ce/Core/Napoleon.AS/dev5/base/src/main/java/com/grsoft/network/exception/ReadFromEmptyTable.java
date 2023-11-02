/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Попытка чтения из пустой таблицы
 *
 * kki   1/11/2010   creating
 */
package com.grsoft.network.exception;
import com.grsoft.aceteam.R;

@SuppressWarnings("serial")
public class ReadFromEmptyTable extends Exception
{

	private String name;

	public ReadFromEmptyTable(String name)
	{
		this.name = name;
	}

	@Override
	public String getMessage()
	{
		return String.format("The table %s is empty", name);
	}
}
