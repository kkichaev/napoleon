package com.grsoft.napoleon.manager;

import android.app.Application;

import com.grsoft.database.DataBaseManager;
import com.grsoft.napoleon.documents.DocTypeBase;
import com.grsoft.napoleon.util.CfgMgr;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.napoleon.util.debug.Path;
import com.grsoft.network.ServerCommand;
import com.grsoft.napoleon.manager.R;

public class ManagerApp extends Application {
	@SuppressWarnings("unused")
	private static final String TAG = "ManagerApp";
	
	void init() {
		ConfigManager.tryInitConfig(new CfgMgr());
		Path.init(this);
			
		DataBaseManager.init();
		DocTypeBase.checkTables();

		ConfigManager.load(this);
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		init();		
		
		setProgrammVersion();
	}

	private void setProgrammVersion() {
		try{
			ServerCommand.ProgramVersion = getResources().getString(R.string.version);
			ServerCommand.Category = "manager";
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
