package com.grsoft.network.exception;
import com.grsoft.aceteam.R;

public class FormatNotPresendInFormats extends Exception
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String formatName;
	
	public FormatNotPresendInFormats(String formatName)
	{
		this.formatName = formatName;
	}
	
	public String getFormatName()
	{
		return formatName;
	}
}
