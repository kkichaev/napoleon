/*
 * Copyright (C), 2011, Гильдия Разработчиков
 *
 * Обработчик событий прогресса
 *
 * kki   22/03/2011   creating
 */
package com.grsoft.network;

import android.app.ProgressDialog;
import android.content.Context;

public abstract class ProgressHelper
{
	protected ProgressDialog progressDialog = null;
	protected Context context;
	
	public ProgressHelper(Context context)
	{
		this.context = context;
	}
	
	public abstract void onUpdate(ProgressValue value);
	
	protected void createProgressDialog(int title, int message)
	{
		if (progressDialog == null )
		{
			progressDialog = new ProgressDialog(context);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		}
		
		progressDialog.setTitle(title);
		progressDialog.setMessage(context.getString(message));
		
		if (!progressDialog.isShowing())
			try{
				progressDialog.show();
			}catch(Exception e){
				e.printStackTrace();
			}
	}
	
	public Context getContext() { return context; }
}
