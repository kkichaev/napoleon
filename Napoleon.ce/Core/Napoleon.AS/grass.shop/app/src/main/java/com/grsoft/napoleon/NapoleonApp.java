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

import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.napmobile.BuildConfig;
import com.grsoft.napmobile.R;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.Config;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.ServerCommand;

public class NapoleonApp extends NapoleonAppBase {
	
	class OrderEditor implements OrderImpl.PropertiesEditor {
		@Override
		public void edit(Context ctx, OrderImpl order, boolean isOldOrder) {

		}
	}

	@Override
	protected void defineNewType() {
		super.defineNewType();
		DbObject.regNewDataType(Price.class, PriceEx.class);
		DbObject.regNewDataType(Order.class, OrderEx.class);
		DataObjectInfo.getInstance().replaceListType(Order.class, "items", OrderItemEx.class);
	}

	@Override
	public void onCreate() {

		if(BuildConfig.FLAVOR.equals("progtest")) {
			Config.HOST_URL = "https://dev.aceteam.app";
			Config.SERVER_CODE = "a9f5006fdf74fbea";
		} else {
			Config.HOST_URL = "https://napmobile.ru";
			Config.SERVER_CODE = "8ef5106fdf74f1e2";
		}

		ConfigManager.initConfig(new CfgNpl());
		super.onCreate();

		OrderImpl.OrderEditor = new OrderEditor();
		setProgramVersion();
	}

	private void setProgramVersion() {
		try{
			Resources res = getResources();
			ServerCommand.ProgramVersion = res.getString(R.string.version);
			ServerCommand.Project = res.getString(R.string.project);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
