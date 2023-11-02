/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Поле данных типа с плавающей запятой
 *
 * kki   10/10/2010   creating
 */

package com.grsoft.network;
import com.grsoft.aceteam.R;

public class NumberMember extends Member
{
	public NumberMember()
	{
		setValue(0);
	}
	
	public Double toDouble()
	{
		return Double.parseDouble(getValue().toString());
	}
	
	@Override
	public String toString()
	{
		return getValue().toString();
	}
}
