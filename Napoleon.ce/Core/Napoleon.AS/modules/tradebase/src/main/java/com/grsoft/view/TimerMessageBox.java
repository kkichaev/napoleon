/*
 * Copyright (C), 2011, Гильдия Разработчиков
 *
 * Окно сообщения, которое отображается
 * определенное, время после чего закрывается
 * 
 *
 * kki   23/03/2011   creating
 */
package com.grsoft.view;

import android.content.Context;
import android.os.AsyncTask;

public class TimerMessageBox extends SimpleMessageBox
{
	private final static long DEFAULT_TIME_VALUE = 3000;
	private long time;
	
	public TimerMessageBox(String title, String message, Context context)
	{
		this(title, message, context, DEFAULT_TIME_VALUE);
	}
	
	public TimerMessageBox(String title, String message, Context context, long time)
	{
		super(title, message, context);
		this.time = time;
	}
	
	@Override
	public void show()
	{
		super.show();
		
		new AsyncTask<Void, Void, Void>()
		{

			@Override
			protected Void doInBackground(Void... params)
			{
				try
				{
					Thread.sleep(time);
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				}
				return null;
			}
			
			@Override
			protected void onPostExecute(Void result)
			{
				hide();
			}
			
		}.execute((Void[])null);
	}
	
	

}
