/*
 * Copyright (C), 2011, Гильдия Разработчиков
 *
 * Фоновая работа с сетевыми соединениями
 *
 * kki   22/03/2011   creating
 */
package com.grsoft.network;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;

import com.grsoft.napoleon.R;
import com.grsoft.network.UpdateProcessInfo.UpdateStatus;
import com.grsoft.view.SimpleMessageBox;

public abstract class NetworkAsyncTask 
	extends AsyncTask<Void, ProgressValue, Boolean>
	implements UpdateProcessListener
{
	protected ProgressHelper progressHelper;
	public static int MESSAGE_VIEW_LAYOUT = R.layout.messages;
	public static int MESSAGE_ROW_LAYOUT = R.layout.msg_list_row;

	public static IRecievedMessageDlg rcvMsgDlg = new RecievedMessageDlg();
	
	public NetworkAsyncTask(ProgressHelper progressHelper)
	{
		this.progressHelper = progressHelper;
	}
	
	@Override
	protected void onProgressUpdate(ProgressValue... values)
	{
		if (progressHelper != null)
			progressHelper.onUpdate(values[0]);
	}
	
	public void onUpdate(UpdateStatus status, int progress)
	{
		ProgressValue pv = new ProgressValue(status, progress);
		publishProgress(pv);
	}
	
	public void onUpdateMessage(SimpleMessageBox simpleMessageBox)
	{
		ProgressValue pv = new ProgressValue(UpdateStatus.SHOW_MESSAGE, 0, simpleMessageBox);
		publishProgress(pv);
	}

	public void showErrorMsg(String message, Context context) {
		try{
			progressHelper.progressDialog.dismiss();
		}catch(Exception e){}
		
		SimpleMessageBox meb = new SimpleMessageBox(message, context);
		meb.setButton(AlertDialog.BUTTON_NEUTRAL, context.getString(R.string.close), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		
		onUpdateMessage(meb);
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		showRecievedMessage(null);
		super.onPostExecute(result);
	}
	
	protected boolean showRecievedMessage(final Runnable doAfterDialog){
		return  rcvMsgDlg.showDialogue(progressHelper.context, doAfterDialog);
	}
}
