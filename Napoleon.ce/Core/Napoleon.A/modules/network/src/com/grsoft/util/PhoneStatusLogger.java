package com.grsoft.util;

import java.util.Calendar;
import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import com.grsoft.dataobjects.impl.LogImpl;
import com.grsoft.napoleon.R;
import com.grsoft.util.gps.GPSUtilNew;

public class PhoneStatusLogger extends BroadcastReceiver {
	private final static String TAG = "PhoneStatusLogger";
	
	int i = 0;
	private Date lastDate = null;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "onReceive " + i++);
		
		try {
			int level = 0;
			
			if (intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED))
				level = intent.getIntExtra("level", 0);
			
			Date now = Calendar.getInstance().getTime();
			
			int hours = 0;
			
			if (lastDate != null){
				long sec = (now.getTime() - lastDate.getTime()) / 1000;
				hours = (int)(sec / 1800);
			}
			
			boolean isGpsSystemOn = GPSUtilNew.isGpsSystemOn(context); 
			
			String gpsStatus = isGpsSystemOn
						? context.getString(R.string.on) : context.getString(R.string.off);
						
			if (lastDate == null || hours >= 1){
				lastDate = now;
				
				StringBuilder logStr = new StringBuilder(String.format(context.getString(R.string.charge) +
						": %d%%, gps: %s", level, gpsStatus));
				
				if(isGpsSystemOn){
					LocationManager locationManager = (LocationManager) context
							.getSystemService(Context.LOCATION_SERVICE);
					Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
					
					int acc = -1;
					int num = -1;
					
					if(location != null && ((now.getTime() - location.getTime()) < GPSUtilNew.VALID_LOC_TIME)){
						acc = (int)location.getAccuracy();
						num = GPSUtilNew.satellitesCount;
					}
					
					if(num > 0){
						logStr.append(", ")
							.append(context.getString(R.string.satellites))
							.append(" ").append(num);
					
						if(acc > -1)
							logStr.append(", ").append(context.getString(R.string.accuracy))
							.append(" ").append(acc);
					}else
						logStr.append(", ").append(context.getString(R.string.not_signal));
				}
				
				LogImpl.log(com.grsoft.dataobjects.Log.PDA_STATUS, 
						com.grsoft.dataobjects.Log.MANAGER, 
						logStr.toString());
				
				Log.d(TAG, logStr.toString());
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
