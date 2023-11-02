/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Формат данных типа время
 *
 * kki   11/10/2010   creating
 */

package com.grsoft.network;


import java.sql.Time;

import com.grsoft.network.exception.RuntimeException;


public class TimeFormat extends DateTimeFormat
{
	public TimeFormat(String name){
		super(name, Time.class, ":t");
	}

	@Override
	public Time parse(String str){
		Time result = Time.valueOf(str);
		return result;
	}

	@Override
	public boolean readMember(Member m, ByteStream stream)
			throws RuntimeException {
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

	@Override
	public String valueToFormatString(Object value) {
		if (value != null){
			Time result = (Time) value;
			return result.toString();
		}
		return "00:00:00";
	}
}
