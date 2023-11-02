/*
 * Copyright (C), 2011, Гильдия Разработчиков
 * 
 * Демо программа (с базой данных)
 *
 * kki   16/05/2011   creating
 */

package com.grsoft.napoleon;

import android.content.Context;

import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.napoleon.util.CfgNplEx;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.ServerCommand;

public class NapoleonApp extends NapoleonAppBase {
	
	class OrderEditor implements OrderImpl.PropertiesEditor {
		@Override
		public void edit(Context ctx, OrderImpl order, boolean isOldOrder) {
			CreateOrder.open(ctx, order, isOldOrder);
		}
	}
	
	@Override
	protected void defineNewType() {
		super.defineNewType();
		DbObject.regNewDataType(Price.class, PriceEx.class);
		DbObject.regNewDataType(Org.class, OrgEx.class);
	}
	
	@Override
	protected void initChildActivity() {
		super.initChildActivity();
		PriceCount.activity = PriceCountEx.class;
		Setting.BehaviorSettingActivity = BehaviorSettingEx.class;
		Documents.activity = DocumentsEx.class;
	}
	
	@Override
	protected void initChildFeature() {
		super.initChildFeature();
		Features.CAN_CHANGE_COST = true;
	}
	
	@Override
	public void onCreate() {
		ConfigManager.initConfig(new CfgNplEx());
		super.onCreate();

		OrderImpl.OrderEditor = new OrderEditor();
		setProgrammVersion();
		
		//NapoleonChat.init(this);
	}

	private void setProgrammVersion() {
		try{
			ServerCommand.ProgramVersion = getResources().getString(R.string.version);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
