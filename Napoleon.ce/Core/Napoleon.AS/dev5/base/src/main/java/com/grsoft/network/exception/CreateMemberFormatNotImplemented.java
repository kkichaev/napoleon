/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Фабричный метод для данного контекста не реализован
 *
 * kki   10/10/2010   creating
 */
package com.grsoft.network.exception;
import com.grsoft.aceteam.R;

import com.grsoft.network.ReadMemberContext;

public class CreateMemberFormatNotImplemented extends Exception
{
	private ReadMemberContext context;
	private static final long serialVersionUID = 1L;
	
	public CreateMemberFormatNotImplemented(ReadMemberContext context)
	{
		this.context = context;
	}

	public ReadMemberContext getContext()
	{
		return context;
	}

}
