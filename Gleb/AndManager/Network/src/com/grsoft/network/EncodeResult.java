package com.grsoft.network;

public class EncodeResult
{
	private byte[] buf;
	private String param;
	
	public EncodeResult(String param, byte[] buf)
	{
		this.param = param;
		this.buf = buf;
	}
	
	public String getParam(){return param;}
	public byte[] getBuf() {return buf;}
}
