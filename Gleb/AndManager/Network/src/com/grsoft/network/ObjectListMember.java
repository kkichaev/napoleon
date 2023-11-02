/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Поле данных типа список объектов
 *
 * kki   10/10/2010   creating
 */

package com.grsoft.network;

public class ObjectListMember extends Member
{
	public ObjectListMember()
	{
		setValue(null);
	}
	
	public ObjectList toObjectList()
	{
		return (ObjectList)getValue();
	}
	
	@Override
	public String toString()
	{
		return getValue() != null
			? ((ObjectList)getValue()).toString()
			: new String();
	}
}
