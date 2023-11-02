/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Исключение, если поле поле по имени в объкте не было найдено
 *
 * kki   07/10/2010   creating
 */

package com.grsoft.network.exception;
import com.grsoft.aceteam.R;

/**
 * Исключение может быть сгенерировано, 
 * если искомое поле типа Member по имени не найдено в объекте
 * @author kki
 *
 */
public class MemberNotFound extends Exception
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String memberName;
	
	public MemberNotFound(String memberName)
	{
		this.memberName = memberName;
	}
	
	public String GetMemberName()
	{
		return memberName;
	}
}
