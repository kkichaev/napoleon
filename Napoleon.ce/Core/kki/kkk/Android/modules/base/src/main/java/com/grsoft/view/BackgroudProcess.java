/*
 * Copyright (C), 2011, Гильдия Разработчиков
 *
 * Запускает диалог ожидания, пока выполняется
 * фоновая работа
 *
 * kki   24/03/2011   creating
 */
package com.grsoft.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import com.grsoft.napoleon.R;

public class BackgroudProcess extends AsyncTask<Void, Void, Void>
{
	private ProgressDialog progressDialog;
	private final int TITLE = R.string.waiting;
	private final int MESSAGE_STR = R.string.please_wait; 
	private RunnableProcess process;
	
	public BackgroudProcess(Context context, RunnableProcess process)
	{
		progressDialog = new ProgressDialog(context);
		progressDialog.setTitle(TITLE);
		progressDialog.setMessage(context.getString(MESSAGE_STR));
		this.process = process;
	}
	
	@Override
	protected Void doInBackground(Void... arg0)
	{
		process.run();
		return null;
	}

	@Override
	protected void onPreExecute()
	{
		process.onPreExecute();
		progressDialog.show();
	}
	
	@Override
	protected void onPostExecute(Void result)
	{
		progressDialog.hide();
		process.onPostExecute();
	}
}
