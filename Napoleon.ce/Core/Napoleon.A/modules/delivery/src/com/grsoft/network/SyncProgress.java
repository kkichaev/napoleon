package com.grsoft.network;

import java.util.Timer;
import java.util.TimerTask;

import com.grsoft.napoleon.dostavka.R;
import com.grsoft.network.UpdateProcessInfo.UpdateStatus;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;


public class SyncProgress extends DialogFragment {
	private ProgressBar progress;
	private TextView tvMessage;
	private View btnCancel;
	private Timer closeTimer = null;
	 
	public static final String SYNC_TERMINATE = "com.grsoft.ads.SyncProgress.SYNC_PARAMS";
	
	public android.view.View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Dialog dialog = getDialog();
		
		dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		
		setCancelable(false);
		
		View view = View.inflate(getActivity(), R.layout.progress, null); 
		inflateView(view);
		init();
		return view;
	};
	
	private void init() {
		btnCancel.setOnClickListener(cancelClick());
	}

	private OnClickListener cancelClick() {
		return new OnClickListener() { @Override public void onClick(View v) {	
			dismiss();
			getActivity().sendBroadcast(new Intent(SYNC_TERMINATE));
		}};
	}

	private void inflateView(View view) {
		progress = (ProgressBar) view.findViewById(R.id.progress);
		tvMessage = (TextView) view.findViewById(R.id.tvMessage);
		btnCancel = view.findViewById(R.id.btnCancel);
	}

	public void onStart() {
		super.onStart();
		Activity parent = getActivity();
		parent.registerReceiver(syncresult, new IntentFilter(UpdateProcess.UPDATE_PROCESS_RESULT));
		parent.registerReceiver(syncerror, new IntentFilter(UpdateProcess.UPDATE_PROCESS_ERROR));
		parent.registerReceiver(syncstep, new IntentFilter(UpdateProcess.UPDATE_PROCESS_STEP));
	};
	
	public void onStop() {
		super.onStop();
		Activity parent = getActivity();
		parent.unregisterReceiver(syncresult);
		parent.unregisterReceiver(syncerror);
		parent.unregisterReceiver(syncstep);
		
		if (closeTimer != null)
				closeTimer.cancel();
	};
	
	private int byteToKB(int b){
		final int KB_SZ = 1024;
		return b / KB_SZ;
	}
	
	BroadcastReceiver syncstep = new BroadcastReceiver() {
		int size = 0;
		String msg = "";
		
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent != null){
				String name = intent.getStringExtra(UpdateProcess.STATUS);
				int progress = intent.getIntExtra(UpdateProcess.PROGRESS, 0);
				try{
					UpdateStatus status = UpdateStatus.valueOf(name);
					final String BREAK_LINE = "<br>";
					final String SPACE = " "; 
					
					switch(status){
					case BEGIN_UPDATE:
						size = 0;
						msg = gluestr(BREAK_LINE, getString(R.string.base_updating), getString(R.string.wait_for_server_answer));
						break;
					case BEGIN_SEND:
						msg = gluestr(BREAK_LINE, getString(R.string.data_sending), getString(R.string.wait_for_server_answer));
						break;
					case ENDREQUEST_UPDATE:
						size = progress;
						msg = gluestr(BREAK_LINE, getString(R.string.save_to_base), getString(R.string.kilobyte, byteToKB(progress)));
						break;
					case ENDREQUEST_SEND:
						msg = getString(R.string.data_sending);
					case STEP:
						msg = gluestr(SPACE, getString(R.string.kilobyte, byteToKB(progress), getString(R.string.kilobyte, byteToKB(size))));
					case STEP_SEND:
						msg = getString(R.string.wait_form_server_answer);
					default:
					}
					
					getActivity().runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							tvMessage.setText(Html.fromHtml(msg));
						}
					});
					
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
	};
	
	private String gluestr(String del, String ...items){
		StringBuilder sb = new StringBuilder();
		
		for(int i = 0; i < items.length; i++){
			if(sb.length() > 0 && del != null)
				sb.append(del);
			
			sb.append(items[i]);
		}
		
		return sb.toString();
	}
	
	private void endSync(){
		setCancelable(true);
		getActivity().runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				progress.setVisibility(View.INVISIBLE);
			}
		});
		
	}

	BroadcastReceiver syncerror = new BroadcastReceiver() {
		String msg = "";
		
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent != null){
				msg = intent.getStringExtra(UpdateProcess.MESSAGE);
				Log.d(getClass().getCanonicalName(), msg);
				if (msg != null) {
					getActivity().runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							tvMessage.setText(Html.fromHtml(msg));
						}
					});
				}
				
				endSync();
			}
		}
	};
	
	BroadcastReceiver syncresult = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent != null){
				final int tr = intent.getIntExtra(UpdateProcess.TRAFFIC, 0);
				getActivity().runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						tvMessage.setText(Html.fromHtml(getString(R.string.updateresult, tr)));
					}
				});
				
				endSync();
				final int CLOSE_AFTER_TIME = 1500;
				
				closeTimer = new Timer();
				closeTimer.schedule(new TimerTask() { @Override	public void run() { dismiss();}}, CLOSE_AFTER_TIME);
			}
		}
	};
}
