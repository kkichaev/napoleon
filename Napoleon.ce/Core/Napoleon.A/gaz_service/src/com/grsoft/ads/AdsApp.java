package com.grsoft.ads;

import android.app.Application;
import android.content.Context;

import com.grsoft.ads.dataobjects.OrderEx;
import com.grsoft.ads.dataobjects.UserOrderEx;
import com.grsoft.ads.documents.OrderDoc;
import com.grsoft.ads.documents.OrderDocEx;
import com.grsoft.ads.documents.UserOrderDoc;
import com.grsoft.ads.documents.UserOrderDocEx;
import com.grsoft.ads.utils.ConfigReader;
import com.grsoft.ads.utils.gps.WorkDayTracking;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.napoleon.util.debug.Path;
import com.grsoft.network.ServerCommand;
import com.grsoft.util.gps.GPSUtilNew;

public class AdsApp extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		setProgrammVersion();
		
		UserOrderDoc.instance = new UserOrderDocEx();
		OrderDocEx.instance = new OrderDocEx();
		Path.BASE_NAME = "ads.db";
		Path.filesDirPath = getFilesDir().toString();
		DocType.addType(OrderDoc.instance());
		DocType.addType(UserOrderDoc.instance());
		ConfigManager.initConfig(new ConfigReader(this));
		
		DbObject.regNewDataType(com.grsoft.ads.dataobjects.UserOrder.class, UserOrderEx.class);
		DbObject.regNewDataType(com.grsoft.ads.dataobjects.Order.class, OrderEx.class);
		
//		ConfigImpl configImpl = new ConfigImpl();
//		configImpl.getData().key = GpsTrackingManager.KEY_VAL;
//		
//		if (!configImpl.read()){
//			configImpl.getData().value = GpsTrackingManager.GPS_ROUTE_ID;
//			configImpl.write();
//		}
		
//		configImpl.close();
		UpdateProcess.processType = UpdateProcessEx.class;
		GPSUtilNew.locationListener = new WorkDayTracking.WorkDayLocationListener(){
			@Override
			protected boolean isCalcDistance() {
				return !getSharedPreferences(Setting.SHARED_PREFERENCES_NAME, 
						Context.MODE_PRIVATE).getBoolean(Setting.PAUSE, false);
			}
		};
		
		OrderTabActivity.orderTabActivity = OrderTabActivityEx.class;
		Ads.serviceType = AdsService.class;
	}

	private void setProgrammVersion() {
		try{
			ServerCommand.ProgramVersion = getResources().getString(R.string.version);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
