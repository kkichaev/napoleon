package com.grsoft.network;

import android.content.Context;
import android.view.View;

import com.grsoft.napoleon.R;
import com.grsoft.view.SimpleMessageBox;

public class SendProgressManager extends ProgressHelper {
	private final int TITLE_UPDATE = R.string.wait_for_sending;
	private final int START_MESSAGE = R.string.wait_for_connecting;
	private final int REGISTER_MESSAGE = R.string.register_on_server;
	private final int SENDING_DATA = R.string.sending_data;
	private final int RECIVING_DATA = R.string.reciving_data;
	
	private View control;
	
	public SendProgressManager(Context context, View control) {
		super(context);
		this.control = control;
	}

	@Override
	public void onUpdate(ProgressValue value)
	{
		SimpleMessageBox simpleMessageBox = value.simpleMessageBox;
		
		switch (value.status)
		{
			case START_OF_PROCESS:
				createProgressDialog(TITLE_UPDATE, START_MESSAGE);
				progressDialog.setMax(3);
				break;
				
			case BEGIN_SEND:
				createProgressDialog(TITLE_UPDATE, REGISTER_MESSAGE);
				progressDialog.setProgress(1);
				break;
				
			case ENDREQUEST_SEND:
				createProgressDialog(TITLE_UPDATE, SENDING_DATA);
				progressDialog.setProgress(2);
				break;
				
			case STEP_SEND:
				createProgressDialog(TITLE_UPDATE, RECIVING_DATA);
				progressDialog.setProgress(3);
				
			case END_OF_PROCESS:
				if (progressDialog != null && progressDialog.isShowing())
					try{
						progressDialog.dismiss();
					}catch(Exception e){}
				
				
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
