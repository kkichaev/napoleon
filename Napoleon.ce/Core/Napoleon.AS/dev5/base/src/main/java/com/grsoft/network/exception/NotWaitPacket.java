/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Ошибка при чтении WaitPacket
 *
 * kki   12/10/2010   creating
 */
package com.grsoft.network.exception;
import com.grsoft.aceteam.R;

@SuppressWarnings("serial")
public class NotWaitPacket extends Exception
{
	@Override
	public String getMessage()
	{
		return "WaitPacket error!";
	}
}
