package com.ksoft.ardalarm;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.sax.StartElementListener;

public class TimerSrv extends Service {
	public static final String HOUR = "hour";
	public static final String MIN = "min";
	public static final String SEC = "sec";
	
	CountDownThread timer;

	public class LocalBinder extends Binder {
		TimerSrv getService() {
			return TimerSrv.this;
		}
	}

	private final IBinder binder = new LocalBinder();
	
	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Bundle bundle = intent.getExtras();
		timer = new CountDownThread(this, bundle.getInt(HOUR), bundle.getInt(MIN),bundle.getInt(SEC));
		return START_STICKY;
		
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		if(timer != null){
			timer.cancel();
		}
	}
	

}

class CountDownThread extends Timer{
	int hour = 0;
	int min = 0; 
	int sec = 0;
	long lastupd = 0;
	
	public CountDownThread(final Context context, int h, int m, int s){
		hour = h;
		min = m; 
		sec = s;
		
		scheduleAtFixedRate(new TimerTask() {
			
			@Override
			public void run() {
				if(sec > 0)
					sec--;
				else if (min > 0){
					sec = 59;
					min--;
				}else if (hour > 0){
					sec = 59;
					min = 59;
					hour--;
				}
				
				
				if(sec <= 0 && min <= 0 && hour <= 0){
					cancel();
					Intent intent = new Intent(context, SoundPlay.class);
					context.startService(intent); 
				}
				
				Intent intent = new Intent(TimerView.TICK_BROADCAST_ACTION);
				intent.putExtra(TimerSrv.HOUR, hour);
				intent.putExtra(TimerSrv.MIN, min);
				intent.putExtra(TimerSrv.SEC, sec);
				context.sendBroadcast(intent);
				
			}
		}, 1000, 1000);
	}
}
