/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Прочитанный символ из потока не ожидаем обработчиком
 *
 * kki   14/09/2010   creating
 */


package com.grsoft.network.exception;

import com.grsoft.network.ByteStream;

public class UnexpectedCharInStream extends Exception
{
	private static final long serialVersionUID = 1L;
	private ByteStream stream;
	
	public UnexpectedCharInStream(ByteStream stream)
	{
		this.stream = stream;
	}

	public ByteStream getStream()
	{
		return stream;
	}
}
