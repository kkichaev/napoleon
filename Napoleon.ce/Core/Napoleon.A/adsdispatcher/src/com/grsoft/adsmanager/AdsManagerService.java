package com.grsoft.adsmanager;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class AdsManagerService extends Service {
	public static Class<? extends Service> MAIN_SERVICE = AdsManagerService.class;
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public class AdsManagerServiceBinder extends Binder{
		public AdsManagerService getService(){ return AdsManagerService.this;	}
	}


}
