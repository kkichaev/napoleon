/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Исключение, если операция для проверяемого типа не реализована
 *
 * kki   02/11/2010   creating
 */
package com.grsoft.network.exception;
import com.grsoft.aceteam.R;

@SuppressWarnings("serial")
public class TypeNotImplemented extends Exception
{

	private Class<?> type;

	public TypeNotImplemented(Class<?> type)
	{
		this.type = type;
	}
	
	@Override
	public String getMessage()
	{
		return String.format("Operation for type = %s hasn't implemented", type.toString());
	}
}
