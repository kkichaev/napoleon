package com.grsoft.network.exception;
import com.grsoft.aceteam.R;

import java.lang.reflect.Type;

@SuppressWarnings("serial")
public class ObjectCantConvert extends Exception
{
	private Type src;
	private Type dest;
	
	public ObjectCantConvert (Type src, Type dest)
	{
		this.src = src;
		this.dest = dest;
	} 
	
	public Type getSrc() {return src;}
	public Type getDest() {return dest;}
}
