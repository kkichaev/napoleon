package com.grsoft.network;
import com.grsoft.aceteam.R;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

import com.grsoft.napoleon.Features;
import com.grsoft.aceteam.R;
import com.grsoft.view.SimpleMessageBox;

public class SendProgressManager extends ProgressHelper {
	private final int TITLE_UPDATE = R.string.wait_for_sending;
	private final int START_MESSAGE = R.string.wait_for_connecting;
	private final int REGISTER_MESSAGE = R.string.register_on_server;
	private final int SENDING_DATA = R.string.sending_data;
	private final int RECIVING_DATA = R.string.reciving_data;
	final int TITLE_SEND = R.string.data_sending;
	
	private View control;
	
	public SendProgressManager(Context context, View control) {
		super(context);
		this.control = control;
	}

	@Override
	public void onUpdate(ProgressValue value)
	{
		SimpleMessageBox simpleMessageBox = value.simpleMessageBox;
		int progress = value.progress;
		
		switch (value.status)
		{
			case START_OF_PROCESS:
				if (control != null)
					control.setEnabled(false);
				
				createProgressDialog(TITLE_UPDATE, START_MESSAGE);
				progressDialog.setMax(3);
				break;
				
			case BEGIN_SEND:
				createProgressDialog(TITLE_UPDATE, REGISTER_MESSAGE);
				progressDialog.setProgress(1);
				break;

			case BEGIN_SEND_VISITS:
				createProgressDialog(TITLE_SEND, SENDING_DATA);
				progressDialog.setMax(progress);
				break;
				
			case ENDREQUEST_SEND:
				createProgressDialog(TITLE_UPDATE, SENDING_DATA);
				progressDialog.setProgress(2);
				break;
				
			case STEP:
				progressDialog.setProgress(progress);
				break;

			case STEP_SEND:
				createProgressDialog(TITLE_UPDATE, RECIVING_DATA);
				progressDialog.setProgress(3);
				
			case END_OF_PROCESS:
				if (Features.KEEP_DIALOG_AFTER_SYNC) {
					createProgressDialog(TITLE_UPDATE, R.string.sync_process_success);
					progressDialog.setProgress(progressDialog.getMax());
					progressDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setEnabled(true);
				}
				
				if (progressDialog != null && 
						progressDialog.isShowing() && 
						!Features.KEEP_DIALOG_AFTER_SYNC)
					try{
						progressDialog.dismiss();
					}catch(Exception e){
						
					}
				
				if (control != null)
					control.setEnabled(true);
				
				break;
				
			case SHOW_MESSAGE:
				if (simpleMessageBox != null)
					simpleMessageBox.show();
				break;
		default:
			break;
		}
	}
}
