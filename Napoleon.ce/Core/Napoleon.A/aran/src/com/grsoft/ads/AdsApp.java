package com.grsoft.ads;

import android.app.Application;

import com.grsoft.ads.database.OrderItemEx;
import com.grsoft.ads.database.Return;
import com.grsoft.ads.dataobjects.Order;
import com.grsoft.ads.dataobjects.OrderEx;
import com.grsoft.ads.dataobjects.impl.OrderImplEx;
import com.grsoft.ads.documents.OrderDocEx;
import com.grsoft.ads.documents.ReturnDoc;
import com.grsoft.ads.utils.ConfigReader;
import com.grsoft.ads.utils.gps.WorkDayTracking;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.napoleon.CameraPreview;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.util.Config;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.napoleon.util.debug.Path;
import com.grsoft.network.ServerCommand;
import com.grsoft.util.CameraHelper;
import com.grsoft.util.Size;
import com.grsoft.util.SrcDataCounter;
import com.grsoft.util.gps.GpsTrackingManager;

public class AdsApp extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		setProgrammVersion();

		Path.BASE_NAME = "ads.db";
		Path.filesDirPath = getFilesDir().toString();
		Path.init(this);
		
		DbObject.regNewDataType(Order.class, OrderEx.class);
		DataObjectInfo.getInstance().replaceTableName(Return.class, "returns");
		DataObjectInfo.getInstance().replaceListType(OrderEx.class, "items", OrderItemEx.class);
		DataObjectInfo.getInstance().replaceListType(Return.class, "items", OrderItemEx.class);
		DocType.addType(OrderDocEx.instance());
		DocType.addType(ReturnDoc.instance());
		ConfigManager.initConfig(new ConfigReader(this));
		OrderTabActivity.orderTabActivity = OrderTabActivityEx.class;
		ConfigImpl configImpl = new ConfigImpl();
		configImpl.getData().key = GpsTrackingManager.KEY_VAL;
		CameraPreview.photoDocType = OrderImplEx.class;
		Path.init(this);
		SrcDataCounter.init(this);
		if (!configImpl.read()){
			configImpl.getData().value = GpsTrackingManager.GPS_ROUTE_ID;
			configImpl.write();
		}
		
		configImpl.close();
		
		GpsTrackingManager.tracking = WorkDayTracking.instance();
		
		Config cfg = ConfigManager.getConfig(); 
		Size size = cfg.getCameraSize();
		
		if (size.width == 0 || size.hight == 0)
		{
			size = CameraHelper.getMinCamSize();
			cfg.setCameraSize(size);
		}
		
		Ads.serviceType = AdsServiceEx.class;
	}

	private void setProgrammVersion() {
		try{
			ServerCommand.ProgramVersion = getResources().getString(R.string.version);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
