/*
 * Copyright (C), 2011, Гильдия Разработчиков
 * 
 * Демо программа (с базой данных)
 *
 * kki   16/05/2011   creating
 */

package com.grsoft.napoleon;

import android.content.Context;

import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.dataobjects.MonitoringItem;
import com.grsoft.dataobjects.MonitoringItemEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.napoleon.documents.DebtDocEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.MonitoringDoc;
import com.grsoft.napoleon.util.CfgNpl;
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
	public void onCreate() {
		ConfigManager.initConfig(new CfgNpl());
		DebtDocEx.initialize();
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

		DbObject.regNewDataType(Delivery.class, DeliveryEx.class);
		DbObject.regNewDataType(Org.class, OrgEx.class);
		DbObject.regNewDataType(MonitoringItem.class, MonitoringItemEx.class);

		DocType.addType(MonitoringDoc.instance());

		CostStrategy.defaultInstance = new CostStrategyEx();
	}

	@Override
	protected void initChildActivity() {
		super.initChildActivity();

		PriceCount.activity = PriceCountEx.class;
		Warehouse.activity = WarehouseEx.class;
		UpdateDB.activity = UpdateDBEx.class;
		MonitoringEdit.activity = MonitoringEditEx.class;
		DocList.activity = DocListEx.class;
	}


	@Override
	protected void initChildFeature() {
		super.initChildFeature();

		Features.SCRIPT_DOC = true;
		Features.CANT_SEND_SCRIPT_PART = true;
		Features.DEL_VISIT_WITHOUT_PHOTO = true;
		Features.SCRIPT_OFF_IN_DOC_LIST = true;
		Features.ALLOW_CREATE_DOC_WHITHOUT_GPS_POS = true;
		Features.SHOW_ORG_ADDRESS = true;
		Features.LOAD_FULL_PRICE = true;
	}
}
