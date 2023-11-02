package com.grsoft.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import com.grsoft.dataobjects.Log;
import com.grsoft.dataobjects.impl.LogImpl;

public class SystemActionReciever extends BroadcastReceiver {
	private static long tick = 0;
	private SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
	
	@Override
	public void onReceive(Context context, Intent arg1) {
		String action = arg1.getAction(); 
		
		android.util.Log.d(getClass().getCanonicalName(), String.format("action: %s, ", action));
		
		if (action.equals("android.location.PROVIDERS_CHANGED")){
			LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
			LogImpl.log(lm.isProviderEnabled(LocationManager.GPS_PROVIDER) ? 
					Log.GPS_ON : Log.GPS_OFF);
		}else if (action.equals("android.intent.action.TIME_SET") || 
				action.equals("android.intent.action.DATE_SET"))
		{
			
			Date now = new Date();
			final long TIME_LIMIT = 1000 * 60 * 2; 
			
			if(Math.abs((now.getTime() - tick)) > TIME_LIMIT){
				//Регистрируем два событие одно на время, которое изменилось
				//второе на время которое было до изменения
				LogImpl logImpl = new LogImpl();
				Log log = logImpl.getData();
				log.date = new Date(tick);
				log.unixtime = log.date.getTime();
				log.action = Log.TIME_CHANGED;
				
				String old = log.date.getTime() == 0 ? "неизвестно" : sdf.format(log.date); 
				log.comments = String.format("%s - %s", old, sdf.format(now));
				log.category = Log.MANAGER;
				
				if(tick > 0)
					logImpl.write();
				
				android.util.Log.d(getClass().getCanonicalName(), String.format("unixtime: %d, rowid: %d", log.unixtime, logImpl.getRowid()));
				
				log.date = now;
				log.unixtime = log.date.getTime();
				logImpl.write();
				
				android.util.Log.d(getClass().getCanonicalName(), String.format("unixtime: %d, rowid: %d", log.unixtime, logImpl.getRowid()));
			}
			
			tick = System.currentTimeMillis();
		}
		
		else if(action.equals("android.intent.action.BOOT_COMPLETED"))
			LogImpl.log(Log.SYSTEM_LOADED);
		else if(action.equals("android.intent.action.ACTION_SHUTDOWN"))
			LogImpl.log(Log.SYSTEM_SHUTDOWN);
		else if (action.equals( "android.intent.action.TIME_TICK" ))
			tick = System.currentTimeMillis();
		else if(action.equals("android.intent.action.TIMEZONE_CHANGED"))
			LogImpl.log(Log.TIME_CHANGED, Log.MANAGER, "Изменен часовой пояс");
	}
}
