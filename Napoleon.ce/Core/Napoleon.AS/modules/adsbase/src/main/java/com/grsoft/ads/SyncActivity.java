package com.grsoft.ads;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import androidx.fragment.app.FragmentActivity;
import com.grsoft.ads.AdsService.AdsServiceBinder;

public class SyncActivity extends FragmentActivity {
	protected AdsService service;
	private boolean bound = false;
	private SyncProgress progress = new SyncProgress();
	
	public void sync(){
		try{
			showProgress();
			sendBroadcast(new Intent(AdsService.SYNC_ACTION));
		}catch(Exception e){}
	}

	protected void showProgress() {
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				if (progress != null && !progress.isAdded())
					progress.show(getSupportFragmentManager(), progress.getClass().toString());
			}
		});
	}
	
	protected void hideProgress() {
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				if (progress != null)
					progress.dismiss();
			}
		});
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		Intent intent = new Intent(this, AdsService.MAIN_SERVICE);
		bindService(intent, connector,  Context.BIND_AUTO_CREATE);
		registerReceiver(syncterminate, new IntentFilter(SyncProgress.SYNC_TERMINATE));
		registerReceiver(syncfinished, new IntentFilter(AdsService.SYNC_FINISHED));
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		
		if(bound){
			unbindService(connector);
			bound = false;
		}
		
		unregisterReceiver(syncterminate);
		unregisterReceiver(syncfinished);
	};
	
    private ServiceConnection connector = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			bound = false;
		}
		
		@Override
		public void onServiceConnected(ComponentName arg0, IBinder binder) {
			service = ((AdsServiceBinder)binder).getService(); 
			bound = true;
		}
	};
	
	private BroadcastReceiver syncterminate = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			if(bound && service != null)
				service.syncterminate();			
		}
		
	};
	
	private BroadcastReceiver syncfinished = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			boolean res = intent.getBooleanExtra(AdsService.SYNC_RESULT, false);
			boolean bkg = intent.getBooleanExtra(AdsService.SYNC_PROCESS_MODE, false);
			
			if(res && !bkg)
				hideProgress();
			
			onSyncFinished(res);
		}
	};
	
	protected void onSyncFinished(boolean result){}
}
