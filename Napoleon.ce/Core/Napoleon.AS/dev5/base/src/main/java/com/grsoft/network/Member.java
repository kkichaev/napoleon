/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Базовый класс для полей данных
 *
 * kki   14/09/2010   creating
 */

package com.grsoft.network;

public abstract class Member
{
	private Object value;
	
	public Object getValue() { return value;}
	public void setValue(Object value) { this.value = value;}
}
