/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Поле данных типа масссива байт
 *
 * kki   10/10/2010   creating
 */

package com.grsoft.network;
import com.grsoft.aceteam.R;

public class BytesMember extends Member
{
	public BytesMember()
	{
		setValue(null);
	}
	
	public byte[] toBytes()
	{
		return (byte[])getValue();
	}
	
	@Override
	public String toString()
	{
		return getValue() != null 
			? new String((byte[])getValue())
			: new String();
	}
}
