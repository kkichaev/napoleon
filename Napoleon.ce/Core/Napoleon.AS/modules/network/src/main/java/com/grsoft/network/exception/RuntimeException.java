package com.grsoft.network.exception;

@SuppressWarnings("serial")
public class RuntimeException extends Exception
{
	private Exception baseException;
	private String userMessage = new String();

	public RuntimeException(Exception baseException)
	{
		this.baseException = baseException;
	}
	
	public Exception getInnerException()
	{
		return baseException;
	}
	
	public boolean innerInstanceOf(Class<?> innerClass)
	{
		return getInnerException().getClass() == innerClass;
	}
	
	public void setUserMessage(String value)
	{
		userMessage = value;
	}
	
	@Override
	public String getMessage()
	{
		if (userMessage.length() == 0)
			return baseException.getMessage();
		else
			return userMessage;
	}
}
