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
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.grsoft.dataobjects.Message;
import com.grsoft.napoleon.R;
import com.grsoft.network.UpdateProcessInfo.UpdateStatus;
import com.grsoft.util.MessageStock;
import com.grsoft.util.Util;
import com.grsoft.view.SimpleMessageBox;

public abstract class NetworkAsyncTask 
	extends AsyncTask<Void, ProgressValue, Boolean>
	implements UpdateProcessListener
{
	public static int MESSAGE_VIEW_LAYOUT = R.layout.messages;
	public static int MESSAGE_ROW_LAYOUT = R.layout.msg_list_row;

	protected ProgressHelper progressHelper;
	
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
	
	public void showErrorMsg(String message, Context context)
	{
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
	
	protected boolean showRecievedMessage(final Runnable doAfterDialog)
	{
		Message[] receivedMessages = MessageStock.getNewMessage();
		
		if(receivedMessages.length == 0)
			return false;
		
		try{
						
			Context context = progressHelper.getContext();
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setTitle(R.string.message_input);
			View dialogView = View.inflate(context, MESSAGE_VIEW_LAYOUT, null);
			ListView lvMessages = (ListView) dialogView.findViewById(R.id.lvMessages);
			
			class NewMessageAdapter extends BaseAdapter
			{
				private Message[] message;
				private Context context;
				
				public NewMessageAdapter(Context context, Message[] message) {
					this.message = message;
					this.context = context;
				}
	
				@Override
				public int getCount() { return message.length; }
	
				@Override
				public Object getItem(int arg0) { return message[arg0]; }
	
				@Override
				public long getItemId(int arg0) { return 0; }
	
				@Override
				public View getView(int arg0, View arg1, ViewGroup arg2) {
					Message message = (Message) getItem(arg0);
					
					if (arg1 == null)
						arg1 = View.inflate(context, MESSAGE_ROW_LAYOUT, null);
					
					TextView tvDate = (TextView) arg1.findViewById(R.id.tvDate);
					tvDate.setText(Util.simpleDateFormat.format(message.date));
					
					TextView tvMessage = (TextView) arg1.findViewById(R.id.tvMessage);
					tvMessage.setText(message.message);
					
					return arg1;
				}
			}
			
			lvMessages.setAdapter(new NewMessageAdapter(context, receivedMessages));
			builder.setView(dialogView);
			
			
			builder.setCancelable(true);
			
			if (doAfterDialog != null) 
				builder.setOnCancelListener(new OnCancelListener() {
					
					@Override
					public void onCancel(DialogInterface dialog) {
						doAfterDialog.run();
					}
				});
			
			AlertDialog newMessagesDlg = builder.create();
			newMessagesDlg.show();
			return true;
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
