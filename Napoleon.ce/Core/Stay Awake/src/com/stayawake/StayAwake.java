package com.stayawake;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class StayAwake extends Activity {
	private final static String TAG = "StayAwake"; 
	private StayAwakeService awakeService;
	private boolean serviceBound;
	private Button btnSwitchStayAwake;
	private Controller controller;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.main);
        controller = new Controller(this);
        Intent intent = new Intent(this, StayAwakeService.class);
		startService(intent);
	    
        btnSwitchStayAwake = (Button) findViewById(R.id.btnSwitchStayAwake);
        btnSwitchStayAwake.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.d(TAG, "WakeLock isHeld = " + Boolean.toString(awakeService.isSleepStatus()));
				
				if (awakeService != null){
					awakeService.switchSleepStatus();
					controller.updateStatusText();
				}
			}
		});
    }
    
    @Override
    protected void onStart() {
    	super.onStart();
    	Intent intent = new Intent(this, StayAwakeService.class);
    	boolean bindResult = getApplicationContext().
    		bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    	
    	Log.d(TAG, "bind result = " + Boolean.toString(bindResult));
    	Log.d(TAG, "service bound = " + Boolean.toString(serviceBound));
    	
    	controller.setLockStatus(serviceBound && awakeService != null);
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	
    	if (serviceBound && awakeService != null && !awakeService.isSleepStatus()){
    		Intent intent = new Intent(this, StayAwakeService.class);
    		stopService(intent);
    	}
    }
    private ServiceConnection serviceConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			serviceBound = false;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			awakeService = ((StayAwakeService.LocalBinder) service)
					.getService();
			serviceBound = true;
			controller.setLockStatus(true);
		}
	};
	
	public boolean isSleepStatus(){
		return awakeService != null ? awakeService.isSleepStatus() : false;
	}
}

class Controller{
	private StayAwake stayAwake;
	private Button btnSwitchStayAwake;
	
	public Controller(StayAwake stayAwake){
		this.stayAwake = stayAwake;
		
		btnSwitchStayAwake = 
			(Button) stayAwake.findViewById(R.id.btnSwitchStayAwake);
	}
	
	public void setLockStatus(boolean enabled){
		
		
		btnSwitchStayAwake.setEnabled(enabled);
		
		if (enabled)
			updateStatusText();
		else
			btnSwitchStayAwake.setText("Waiting service...");
	}
	
	public void updateStatusText(){
		btnSwitchStayAwake.setText(
				stayAwake.isSleepStatus() ? "Allow sleep" : "Stay awake");
	}
}