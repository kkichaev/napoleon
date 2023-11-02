/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Текущий контекст для обхода(визита) при чтении формата из потока
 *
 * kki   10/10/2010   creating
 */

package com.grsoft.network;

public class ReadMemberContext
{
	private ByteStream stream;
	private String memberName;
	private Format format;
	
	public ReadMemberContext(Format format, ByteStream stream)
	{
		this.memberName = new String();
		this.stream = stream;
		this.format = format;
	}
	
	public char getCurSymbol()
	{
		return stream.current();
	}
	
	public char getNextChar()
	{
		return stream.next();
	}
	
	public boolean moveNext()
	{
		return stream.moveNext();
	}
	
	public void setMemberName(String value)
	{
		memberName = value;
	}
	
	public String getMemberName()
	{
		return memberName;
	}
	
	public String getFormatName()
	{
		return format.getName();
	}
	
	public ByteStream getStream()
	{
		return stream;
	}
}
