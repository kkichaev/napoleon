/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Поле данных типа даты
 *
 * kki   10/10/2010   creating
 */

package com.grsoft.network;
import com.grsoft.aceteam.R;

import java.util.Date;

public class DateMember extends Member
{
	public DateMember()
	{
		setValue(new Date());
	}
	
	public Date toDate()
	{
		return (Date)getValue();
	}
	
	@Override
	public String toString()
	{
		return ((Date)getValue()).toString();
	}
}
