package com.grsoft.napoleon.dostavka;

import com.grsoft.database.DataBaseManager;
import com.grsoft.napoleon.util.Config;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.napoleon.util.debug.Path;
import com.grsoft.util.SrcDataCounter;
import android.app.Application;
import android.content.Intent;


public class NapoleonApp extends Application {
	public static String AUTORIZATION_KEY = "autorization_key"; 
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
		
		Intent intent = new Intent(this, MainService.class);
		startService(intent);
	}
}
