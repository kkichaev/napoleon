package com.grsoft.ads;

import com.grsoft.ads.dataobjects.OrgEx;
import com.grsoft.ads.dataobjects.VisitEx;
import com.grsoft.ads.utils.CfgAds;
import com.grsoft.database.DataBaseManager;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.Visit;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.napoleon.Features;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.napoleon.util.debug.Path;
import com.grsoft.network.ServerCommand;

import android.app.Application;
import android.content.Intent;
import android.os.Build;

public class AdsApp extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		setProgrammVersion();
		
		DbObject.regNewDataType(Visit.class, VisitEx.class);
		DbObject.regNewDataType(Org.class, OrgEx.class);
		
		Path.SHARED_FOLDER = "ADS";
		Path.init(this);
		
		DataBaseManager.init();
		ConfigManager.initConfig(new CfgAds());
		ConfigManager.load(this);
		
		Features.ZIP_PACKET = false;

		Intent si = new Intent(this, AdsService.MAIN_SERVICE);

		if (Build.VERSION.SDK_INT >= 26)
			startForegroundService(si);
		else
			startService(si);
	}

	public void exit() {
		stopService(new Intent(this, AdsService.MAIN_SERVICE));
	}
	
	private void setProgrammVersion() {
		try{
			ServerCommand.ProgramVersion = getResources().getString(R.string.version);
			ServerCommand.Category = "ads";
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
