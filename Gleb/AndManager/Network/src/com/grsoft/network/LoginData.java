/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Данные необходимые для соединения с сервером
 *
 * kki   12/10/2010   creating
 */
package com.grsoft.network;

import android.content.Context;
import android.content.SharedPreferences;

import com.grsoft.dataobjects.ServerAnswer;


public class LoginData extends UserInfo
{
	private static final String LOGIN_PREF = "LoginData";
	private static final String DURATION_KEY = "Duration";
	private int duration;
	
	public LoginData(String user, String password, Context ctx)
	{
		super(user, password);
		
		this.setDuration(ctx);
	}
	
	public static void putDuration(ServerAnswer answ, Context ctx) {
		try {
			SharedPreferences prf = ctx.getSharedPreferences(LOGIN_PREF, Context.MODE_PRIVATE);
			SharedPreferences.Editor e = prf.edit();
			int duration = Integer.parseInt(answ.message, 16);
			e.putInt(DURATION_KEY, duration);
			e.commit();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}

	private void setDuration(Context ctx)
	{
		SharedPreferences prf = ctx.getSharedPreferences(LOGIN_PREF, Context.MODE_PRIVATE);
		this.duration = prf.getInt(DURATION_KEY, 0);
	}

	public int getDuration()
	{
		return duration;
	}
}
