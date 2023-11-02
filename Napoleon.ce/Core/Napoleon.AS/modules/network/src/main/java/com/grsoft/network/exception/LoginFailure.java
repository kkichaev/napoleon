/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Ошибка при подключении
 *
 * kki   19/10/2010   creating
 */
package com.grsoft.network.exception;

import com.grsoft.dataobjects.ServerAnswer;

@SuppressWarnings("serial")
public class LoginFailure extends Exception
{

	private ServerAnswer serverAnswer;

	public LoginFailure(ServerAnswer serverAnswer)
	{
		this.setServerAnswer(serverAnswer);
	}

	public void setServerAnswer(ServerAnswer serverAnswer)
	{
		this.serverAnswer = serverAnswer;
	}

	public ServerAnswer getServerAnswer()
	{
		return serverAnswer;
	}
	
	@Override
	public String getMessage()
	{
		return serverAnswer.message;
	}

}
