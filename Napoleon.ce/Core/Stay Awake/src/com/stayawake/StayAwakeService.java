package com.stayawake;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

public class StayAwakeService extends Service {
	private static final String TAG = "StayAwakeService"; 
	private PowerManager.WakeLock wakeLock;
	private final IBinder binder = new LocalBinder();
	private NotificationManager  notificationManager; 
	private int NOTIFICATION = R.string.app_name;
	
	@Override
	public void onCreate() {
		Log.d(TAG, "StayAwakeService");
		notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
	    wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, TAG);
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		return binder;
	}
	
	public boolean isSleepStatus(){
		return wakeLock.isHeld();
	}
	
	public void switchSleepStatus(){
		if (isSleepStatus()){
			wakeLock.release();
			hideNotify();
		}else{
			wakeLock.acquire();
			showNotify();
		}
		
		Log.d(TAG, "Lock status: " + Boolean.toString(wakeLock.isHeld()));
	}
	
	public class LocalBinder extends Binder {
		public StayAwakeService getService() {
			return StayAwakeService.this;
	    }
	}
	
	private void showNotify(){
		notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		Notification notification = new Notification(R.drawable.lightning, "Stay Awake" , 0);
		notification.flags |= Notification.FLAG_NO_CLEAR;
		notification.flags |= Notification.FLAG_ONGOING_EVENT;
		
		Intent intent = new Intent(this, StayAwake.class); 
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, 0);

        notification.setLatestEventInfo(this, getText(R.string.app_name),
                       "Tape to change status", contentIntent);
		
		notificationManager.notify(NOTIFICATION, notification);
	}
	
	private void hideNotify(){
		notificationManager.cancel(NOTIFICATION);
	}
}
