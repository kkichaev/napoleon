/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Диалог с сообщением
 *
 * kki   04/11/2010   creating
 */
package com.grsoft.util;

import com.grsoft.napoleon.R;

import android.app.AlertDialog;
import android.content.Context;
import android.text.Html;

public class MessageBox
{
	public static void show(Context context, String title, String message)
	{
		AlertDialog ad = new AlertDialog.Builder(context).create();
		ad.setTitle(title);
		ad.setMessage(Html.fromHtml(message));
		ad.show();
	}
	
	public static void showError(Context context, String message, Exception exception)
	{
		final String TITLE = context.getString(R.string.error);
		final String MESSAGE = message == null ? exception.getMessage() : String.format("%s\n%s", message, exception.getMessage());
		show(context, TITLE, MESSAGE);
	}
	
	public static void show(Context context, int title, int message){
		show(context, context.getString(title), context.getString(message));
	}
}
