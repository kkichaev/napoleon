package com.grsoft.napoleon.dostavka;

import com.grsoft.database.DataBaseManager;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.DeliveryItemEx;
import com.grsoft.dataobjects.Waybill;
import com.grsoft.napoleon.DispositionActivity;
import com.grsoft.napoleon.Features;
import com.grsoft.napoleon.documents.DIncassDoc;
import com.grsoft.napoleon.documents.DReturnDoc;
import com.grsoft.napoleon.documents.DShipmentDoc;
import com.grsoft.napoleon.documents.DTaskDoc;
import com.grsoft.napoleon.documents.DVisitDoc;
import com.grsoft.napoleon.documents.DispatchDoc;
import com.grsoft.napoleon.documents.DocTypeBase;
import com.grsoft.napoleon.util.CfgNplW;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.napoleon.util.debug.Path;
import com.grsoft.network.ServerCommand;
import com.grsoft.util.MainExceptionHandler;
import com.grsoft.util.SrcDataCounter;

import android.app.Application;
import android.content.Intent;


public class NapoleonAppBase extends Application {
	protected boolean srvbnd;
	protected MainService exchsrv;
	
	
	protected void defineNewType() {}
	protected void initChildFeatures() {}
	protected void initChildDocTypes() {}
	protected void initChildActivities() {}
	
	protected void initDocTypes() {
		DocTypeBase.addType(DVisitDoc.instance());
		DocTypeBase.addType(DIncassDoc.instance());
		DocTypeBase.addType(DispatchDoc.instance());
		DocTypeBase.addType(DTaskDoc.instance());
		DocTypeBase.addType(DShipmentDoc.instance());
		DocTypeBase.addType(DReturnDoc.instance());
		
		initChildDocTypes();
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		ConfigManager.initConfig(new CfgNplW());
		Path.init(this);
		
		DataBaseManager.init();
		SrcDataCounter.init(this);
		ConfigManager.load(this);
		
		CfgNplW cfg = (CfgNplW) ConfigManager.getConfig();
		
		cfg.dataDirShare = true;
		cfg.androidPhoto = true;
		
		ServerCommand.Category = "expeditorpda";
		ServerCommand.ProgramVersion = getResources().getString(R.string.version);
		
		ConfigManager.save();
		
		DataObjectInfo doi = DataObjectInfo.getInstance();
		doi.replaceListType(Waybill.class, "items", DeliveryItemEx.class);
		
		defineNewType();
		initDocTypes();
						
		Intent intent = new Intent(this, MainService.class);
		startService(intent);
		
		Features.CANT_SEND_SCRIPT_PART = false;
		
		DispositionActivity.activity = DispositionDelivery.class; 
		
		initChildFeatures();
		initChildActivities();
		//MichelChat.init(this);
		
		Thread.setDefaultUncaughtExceptionHandler(new MainExceptionHandler(this, Path.SHARED_FOLDER));
	}
}
