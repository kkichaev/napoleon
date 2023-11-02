package com.grsoft.network;

public class PacketOptions
{
	private String name;
	private String value;

	public PacketOptions(String str)
	{
		int pos = str.indexOf('(');
		
		if (pos < 0)
		{
			name = str;
			value = "";
		}
		else
		{
			int ep = str.indexOf(")", pos + 1);
			if (ep > 0) 
				ep = str.length() - 1;
			
			name = str.substring(0, pos);
			value = str.substring(pos + 1, ep);
		}
	}

	public String getValue()
	{
		return value;
	}

	public String getName()
	{
		return name;
	}

}
