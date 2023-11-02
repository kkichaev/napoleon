package com.grsoft.napoleon.util.debug.exception;

public class ENotDbFoundInList extends Exception
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String PREF_MSG = "Data Base does not present: %s";
	
	public ENotDbFoundInList(String msg)
	{
		super(String.format(PREF_MSG, msg));
	}

}
