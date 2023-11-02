package com.grsoft.network.exception;
import com.grsoft.aceteam.R;

@SuppressWarnings("serial")
public class ItemNotFound extends Exception
{
	private Object item;
	
	public ItemNotFound(Object item)
	{
		this.item = item;
	}
	
	public Object getItem()
	{
		return item;
	}
}
