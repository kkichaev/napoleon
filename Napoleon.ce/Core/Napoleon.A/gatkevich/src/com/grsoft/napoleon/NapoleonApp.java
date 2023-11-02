/*
 * Copyright (C), 2011, Гильдия Разработчиков
 * 
 * Демо программа (с базой данных)
 *
 * kki   16/05/2011   creating
 */

package com.grsoft.napoleon;

import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.dataobjects.IncassDebDistr;
import com.grsoft.dataobjects.IncassEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.ServerCommand;

import android.content.Context;

public class NapoleonApp extends NapoleonAppBase {
	
	class OrderEditor implements OrderImpl.PropertiesEditor {
		@Override
		public void edit(Context ctx, OrderImpl order, boolean isOldOrder) {
			CreateOrder.open(ctx, order, isOldOrder);
		}
	}
	
	@Override
	protected void defineNewType() {
		DbObject.regNewDataType(IncassDebDistr.class, IncassEx.class);
		DbObject.regNewDataType(Delivery.class, DeliveryEx.class);
	}
	
	@Override
	public void onCreate() {
		ConfigManager.initConfig(new CfgNpl());
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
	
	@Override
	protected void initChildDocTypes() {
		super.initChildDocTypes();
		DocType.addType(ReturnDoc.instance());
	}
	
	@Override
	protected void initChildFeature() {
		super.initChildFeature();
		
		Features.USE_COST_IN_RETURNS = true;
		Features.INCASS_DEBET_DISTRIB = true;
	}
	
	@Override
	protected void initChildActivity() {
		super.initChildActivity();
		IncassDebDistrEdit.editActivity = IncassEditEx.class;
		
		DocType.addType(ReturnDoc.instance());
	}
}
