/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Исключение если невозможно сделать преобразование типа 
 * к типу SQLite
 *
 * kki   12/10/2010   creating
 */
package com.grsoft.network.exception;

@SuppressWarnings("serial")
public class UnknownTypeForSqlConversion extends Exception
{

	private Class<?> type;

	public UnknownTypeForSqlConversion(Class<?> type)
	{
		this.type = type;
	}

	public Class<?> getType()
	{
		return type;
	}
	
	@Override
	public String getMessage()
	{
		return "Unknown type for SQL conversions: " + getType().toString();
	}
}
