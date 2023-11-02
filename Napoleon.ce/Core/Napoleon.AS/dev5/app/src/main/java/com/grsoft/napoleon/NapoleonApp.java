/*
 * Copyright (C), 2011, Гильдия Разработчиков
 * 
 * Демо программа (с базой данных)
 *
 * kki   16/05/2011   creating
 */

package com.grsoft.napoleon;

import android.content.Context;
import android.content.res.Resources;

import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.napmobile.BuildConfig;
import com.grsoft.napmobile.R;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.Config;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.ConnectionHelper;
import com.grsoft.network.ServerCommand;

public class NapoleonApp extends NapoleonAppBase {
	
	class OrderEditor implements OrderImpl.PropertiesEditor {
		@Override
		public void edit(Context ctx, OrderImpl order, boolean isOldOrder) {
			CreateOrder.open(ctx, order, isOldOrder);
		}
	}

	@Override
	public void onCreate() {

		Config.HOST_URL = "https://napmobile.ru";
//		if(BuildConfig.FLAVOR.equals("progtest")) {
//			Config.HOST_URL = "https://dev.aceteam.app";
//		}
		CfgNpl cfg = new CfgNpl();
		ConfigManager.initConfig(cfg);

		if(BuildConfig.DEBUG) {
////			ConfigManager.load(getApplicationContext());
////			cfg = (CfgNpl) ConfigManager.getConfig();
//////			cfg.userid = "bd2de314-3136-11ee-0a80-091d00111113";
//////			cfg.uuid = "4be15fc5a7b84c12b1a1d1fbfaa41e7d";
////			cfg.serverCode = "39f6f06fdf7efaea";
////
////			cfg.uuid = "c0ef850e02034cac91e729e5a2cd5008";
////			cfg.userid = "92432e5d-2bc4-11ee-0a80-0e8100114ee0";
////			ConfigManager.save();
////
//			Config.HOST_URL = "http://172.25.211.121";
//			ConnectionHelper.TESTING = true;
//			ConnectionHelper.ADDR = "172.25.211.121";
//			ConnectionHelper.UUID = "43866be79b1c44899d982311f8416d50";
//			ConnectionHelper.PORT = 3000;
		}

		super.onCreate();

//		initDemo();
		OrderImpl.OrderEditor = new OrderEditor();
		setProgrammVersion();
	}

	private void setProgrammVersion() {
		try{
			Resources res = getResources();
			ServerCommand.ProgramVersion = res.getString(R.string.version);
			ServerCommand.Project = res.getString(R.string.project);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	protected void initChildFeature() {
		super.initChildFeature();
	}
}
