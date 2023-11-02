/*
 * Copyright (C), 2011, Гильдия Разработчиков
 *
 * Обработчик событий прогресса
 *
 * kki   22/03/2011   creating
 */
package com.grsoft.network;
import com.grsoft.aceteam.R;

import com.grsoft.napoleon.Features;
import com.grsoft.aceteam.R;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

public abstract class ProgressHelper
{
	protected ProgressDialog progressDialog = null;
	protected Context context;
	private ButtonAction buttonAction;
	
	public interface ButtonAction{
		void progressClosed();
	}
	
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
			
			if (Features.KEEP_DIALOG_AFTER_SYNC) 
				progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, context.getString(R.string.close), new DialogInterface.OnClickListener() {
				    @Override
				    public void onClick(DialogInterface dialog, int which) {
				        dialog.dismiss();
				        
				        if (buttonAction != null)
				        	buttonAction.progressClosed();
				    }
				});
		}
		
		progressDialog.setTitle(title);
		progressDialog.setMessage(context.getString(message));
		progressDialog.setCancelable(false);
		
		if (!progressDialog.isShowing())
			try{
				progressDialog.show();
			}catch(Exception e){
				e.printStackTrace();
			}
		
		if (Features.KEEP_DIALOG_AFTER_SYNC)
			progressDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setEnabled(false);
	}
	
	public Context getContext() { return context; }
	
	public ButtonAction getButtonAction() {
		return buttonAction;
	}
	
	public void setButtonAction(ButtonAction value) {
		buttonAction = value;
	}
}
