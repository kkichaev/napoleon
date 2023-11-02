/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Поле данных типа строка
 *
 * kki   10/10/2010   creating
 */

package com.grsoft.network;

public class StringMember extends Member
{
	public StringMember()
	{
		setValue(new String());
	}
	
	@Override
	public String toString()
	{
		return getValue().toString();
	}
}
