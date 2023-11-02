/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Исключение если поле типа не реализована в инициализаторе
 *
 * kki   07/10/2010   creating
 */

package com.grsoft.network.exception;
import com.grsoft.aceteam.R;

import com.grsoft.network.MemberFormat;

/**
 * Исключение генерируется, если инициализатор для 
 * поля типа Member не реализовано
 * @author kki
 *
 */
public class InitMemberTypeNotImplemented extends Exception
{

	private MemberFormat member;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public InitMemberTypeNotImplemented(MemberFormat member)
	{
		this.member = member;
	}

	public MemberFormat getMemberFormat()
	{
		return member;
	}
}
