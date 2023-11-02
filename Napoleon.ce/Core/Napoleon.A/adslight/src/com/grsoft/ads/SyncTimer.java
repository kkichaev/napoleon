package com.grsoft.ads;

import java.util.Timer;
import java.util.TimerTask;
import android.content.Intent;


public class SyncTimer extends Timer {
	private AdsService service;
	private SyncTimerTask timerTask = new SyncTimerTask();
	
	public SyncTimer(AdsService service, long delay){
		this.service = service;
		schedule(timerTask, delay, delay);
	}
	
	class SyncTimerTask extends TimerTask{
		@Override
		public void run() { service.sendBroadcast(new Intent(AdsService.SYNC_ACTION));	}
	}
}
