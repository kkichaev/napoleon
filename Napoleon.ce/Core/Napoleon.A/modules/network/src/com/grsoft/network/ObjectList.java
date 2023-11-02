/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 *
 *
 * kki   14/09/2010   creating
 */

package com.grsoft.network;

import java.util.ArrayList;

import com.grsoft.network.exception.RuntimeException;

@SuppressWarnings("serial")
public class ObjectList extends ArrayList<RawObject>
{
	private Format format;
	
	protected ObjectList(){}
	
	public ObjectList(Format format)
	{
		this.format = format;
	}

	public RawObject addObject() throws RuntimeException
	{
		RawObject result = new RawObject(format);
		add(result);
		
		return result;
	}
	
	public String getName()
	{
		return format.getName();
	}
	
	protected Format getFormat()
	{
		return format;
	}
	
	protected void setFormat(Format format)
	{
		this.format = format;
	}
}
