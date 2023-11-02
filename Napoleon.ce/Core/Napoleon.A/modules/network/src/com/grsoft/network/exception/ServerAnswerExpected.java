package com.grsoft.network.exception;

import com.grsoft.network.ByteStream;

@SuppressWarnings("serial")
public class ServerAnswerExpected extends Exception
{

	private ByteStream stream;

	public ServerAnswerExpected(ByteStream stream)
	{
		this.stream = stream;
	}

	public ByteStream getStream()
	{
		return stream;
	}
}
