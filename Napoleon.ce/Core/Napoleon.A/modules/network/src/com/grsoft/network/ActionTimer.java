package com.grsoft.network;

import java.util.Timer;
import java.util.TimerTask;
import android.content.Context;
import android.content.Intent;


public class ActionTimer extends Timer {
	private Context service;
	private String action;
	private Task task = new Task();
	
	public ActionTimer(Context service, long delay, String action){
		this.service = service;
		this.action = action;
		schedule(task, delay, delay);
	}
	
	class Task extends TimerTask{
		@Override
		public void run() { 
			service.sendBroadcast(new Intent(action));
		}
	}
}
