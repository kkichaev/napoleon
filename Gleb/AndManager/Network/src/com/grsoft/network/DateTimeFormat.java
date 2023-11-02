/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Формат данных типа даты
 *
 * kki   25/10/2010   creating
 */

package com.grsoft.network;

import com.grsoft.network.exception.RuntimeException;

public abstract class DateTimeFormat extends MemberFormat
	implements StringFormatValue
{
	
	public DateTimeFormat(String name, Class<?> memberType, 
			String formatString ){
		super(name, memberType, formatString);
	}

	@Override
	public boolean readMember(Member m, ByteStream stream)
			throws RuntimeException
	{
		try
		{
			StringBuilder str = new StringBuilder();
			 
	        do
	        {
	        	char sym = stream.current();
	            if (sym == ',' || sym == ']')
	               break;
	            str.append(sym);
	        } while (stream.moveNext());
	        
	        m.setValue(parse(str.toString()));
	        
			return true;
		}
		catch(Exception exception)
		{
			throw new RuntimeException(exception);
		}
	}
	
	public abstract Object parse(String str);
}
