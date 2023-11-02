/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Неизвестный MemberType 
 *
 * kki   09/11/2010   creating
 */
package com.grsoft.network.exception;

import com.grsoft.network.MemberFormat;

@SuppressWarnings("serial")
public class MemberTypeNotImplemented extends Exception
{
	private MemberFormat memberFormat;
	
	public MemberTypeNotImplemented(MemberFormat member)
	{
		this.memberFormat = member;
	}

	public MemberFormat getMemberFormat()
	{
		return memberFormat;
	}
	
	@Override
	public String getMessage()
	{
		return "Not implemented: " + getMemberFormat().toString(); 
	}
}
