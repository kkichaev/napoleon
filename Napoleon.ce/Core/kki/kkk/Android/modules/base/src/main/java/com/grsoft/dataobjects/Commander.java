package com.grsoft.dataobjects;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class Commander
{
	private DataObject dataObject;
	private String CMD_GET = "GET";
	
	public Commander(DataObject dataObject)
	{
		this.dataObject = dataObject;
	}
	
	public String getCommand()
	{
		return CMD_GET;
	}
	
	public String getParams() 
		throws SecurityException, NoSuchMethodException, 
			   IllegalArgumentException, IllegalAccessException, InvocationTargetException
	{
		String GET_METHOD_NAME = "getDataObjectName";
		
		Method name_getter = dataObject.getClass().getMethod(GET_METHOD_NAME);
		
		return (String)name_getter.invoke(dataObject);
	}
}
