package com.grsoft.network.util;
import com.grsoft.aceteam.R;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.SystemClock;

import com.grsoft.napoleon.Features;
import com.grsoft.aceteam.R;
import com.grsoft.network.NetworkAsyncTask;
import com.grsoft.network.ProgressHelper;
import com.grsoft.network.ProgressValue;
import com.grsoft.network.UpdateProcessInfo.UpdateStatus;
import com.grsoft.view.SimpleMessageBox;

public class ProgressManager extends ProgressHelper
	implements OnCancelListener
{
	protected static final int REFRESH_INTERVAL = 2000; // in ms
	protected static final int TITLE_UPDATE = R.string.base_updating;
	protected static final int REQUEST_STR = R.string.wait_for_server_answer;
	protected static final int WRITE_STR = R.string.save_to_base;
	protected static final int TITLE_SEND = R.string.data_sending;
	protected static final int ENDREQUESTSEND_MSG = R.string.data_sending;
	protected static final int STEP_SEND_MSG = R.string.wait_form_server_answer;
	protected static final int GPS_SEND_MSG = R.string.wait_for_send_gps;
	
	@SuppressWarnings("unused")
	private static final String TAG = "ProgressManagerEx";
	private NetworkAsyncTask updateProcess;
	private long lastUpdate;
	
	public ProgressManager(Context context){
		super(context);
	}

	public void setUpdateProcess(NetworkAsyncTask updateProcess){
		this.updateProcess = updateProcess;
	}
	
	protected void updateStatus(UpdateStatus status, int progress, SimpleMessageBox simpleMessageBox) {
		switch(status) {
			case BEGIN_UPDATE:
				createProgressDialog(TITLE_UPDATE, REQUEST_STR);
				break;
			
			case BEGIN_SEND:
				createProgressDialog(TITLE_SEND, REQUEST_STR);
				break;
				
			case BEGIN_SEND_VISITS:
				createProgressDialog(TITLE_SEND, R.string.sending_data);
				progressDialog.setMax(progress);
				break;

			case ENDREQUEST_UPDATE:
				progressDialog.setMax(progress);
				progressDialog.setMessage(context.getString(WRITE_STR));
				break;
			
			case ENDREQUEST_SEND:
				progressDialog.setMax(3);
				progressDialog.setMessage(context.getString(ENDREQUESTSEND_MSG));
				progressDialog.setProgress(2);
				
			case STEP:
				progressDialog.setProgress(progress);
				break;
				
			case STEP_SEND:
				progressDialog.setMessage(context.getString(STEP_SEND_MSG));
				progressDialog.setProgress(3);
			case END:
				break;
				
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
				break;
			
			case SHOW_MESSAGE:
				if (simpleMessageBox != null)
					simpleMessageBox.show();
				break;
			case GPS_UPDATE:
				progressDialog.setMessage(context.getString(GPS_SEND_MSG));
				break;
		default:
			break;	
		}		
	}
	
	@Override
	public void onUpdate(ProgressValue value)
	{
		UpdateStatus status = value.status;
		int progress = value.progress;
		SimpleMessageBox simpleMessageBox = value.simpleMessageBox;
		
		long now = SystemClock.uptimeMillis();
		if( status == UpdateStatus.STEP && now - lastUpdate < REFRESH_INTERVAL )
			return;
		lastUpdate = now;
		
		updateStatus(status, progress, simpleMessageBox);
	}
	
	@Override
	protected void createProgressDialog(int title, int message) {
		super.createProgressDialog(title, message);
		progressDialog.setOnCancelListener(this);
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		updateProcess.cancel(false);
	}
}