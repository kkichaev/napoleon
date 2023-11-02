package com.grsoft.napoleon.dostavka;

import com.grsoft.database.DataBaseManager;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.DeliveryItemEx;
import com.grsoft.dataobjects.Waybill;
import com.grsoft.napoleon.documents.DIncassDoc;
import com.grsoft.napoleon.documents.DReturnDoc;
import com.grsoft.napoleon.documents.DShipmentDoc;
import com.grsoft.napoleon.documents.DTaskDoc;
import com.grsoft.napoleon.documents.DVisitDoc;
import com.grsoft.napoleon.documents.DispatchDoc;
import com.grsoft.napoleon.documents.DocTypeBase;
import com.grsoft.napoleon.util.Config;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.napoleon.util.debug.Path;
import com.grsoft.util.SrcDataCounter;
import android.app.Application;
import android.content.Intent;


public class NapoleonApp extends Application {
	protected boolean srvbnd;
	protected MainService exchsrv;
	
	
	@Override
	public void onCreate() {
		super.onCreate();
		Path.init(this);
		DataBaseManager.init();
		SrcDataCounter.init(this);
		ConfigManager.load(this);
		
		Config cfg = ConfigManager.getConfig();
		cfg.dataDirShare = true;
		ConfigManager.save();
		
		DocTypeBase.addType(DVisitDoc.instance());
		DocTypeBase.addType(DIncassDoc.instance());
		DocTypeBase.addType(DispatchDoc.instance());
		DocTypeBase.addType(DTaskDoc.instance());
		DocTypeBase.addType(DShipmentDoc.instance());
		DocTypeBase.addType(DReturnDoc.instance());
		
		DataObjectInfo doi = DataObjectInfo.getInstance();
		doi.replaceListType(Waybill.class, "items", DeliveryItemEx.class);
		
		Intent intent = new Intent(this, MainService.class);
		startService(intent);
		
		//MichelChat.init(this);
	}
}
