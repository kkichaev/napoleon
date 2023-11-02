/*
 * Copyright (C), 2011, Гильдия Разработчиков
 * 
 * Демо программа (с базой данных)
 *
 * kki   16/05/2011   creating
 */

package com.grsoft.napoleon;

import com.grsoft.database.Hitching;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.Return;
import com.grsoft.dataobjects.ReturnCommit;
import com.grsoft.dataobjects.ReturnItemEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.ReturnImplEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.napoleon.util.ConfigImplEx;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.ReadService;
import com.grsoft.network.ServerCommand;
import com.grsoft.network.WriteService;

import android.content.Context;

public class NapoleonApp extends NapoleonAppBase {
	@SuppressWarnings("unused")
	private static final String TAG = "NapoleonApp";
	
	class OrderEditor implements OrderImpl.PropertiesEditor {
		@Override
		public void edit(Context ctx, OrderImpl order, boolean isOldOrder) {
			CreateOrder.open(ctx, order, isOldOrder);
		}
	}
	
	@Override
	protected Class<? extends OrderImplBase<? extends Order>> orderImplType() {
		return OrderImplEx.class;
	}
	
	@Override
	protected void defineNewType() {
		DbObject.regNewDataType(Order.class, OrderEx.class);
		DbObject.regNewDataType(Price.class, PriceEx.class);
		DbObject.regNewDataType(Org.class, OrgEx.class);
		
		DataObjectInfo.getInstance().replaceListType(Return.class, "items", ReturnItemEx.class);
		DataObjectInfo.getInstance().replaceListType(Order.class, "items", OrderItemEx.class);

		CostStrategy.defaultInstance = new CostStrategyEx();
		
		ReadService.requestObjects.add(new Hitching(ReturnCommit.class, "ReturnCommit"));
		WriteService.requestObjects.addAll(ReadService.requestObjects);
	}

	@Override
	protected void initChildActivity() {
		PriceCount.activity = PriceCountEx.class;
		ReturnDetail.activity = ReturnDetailEx.class;
	}
	
	@Override
	protected void initChildDocTypes() {
		DocType.addType(ReturnDoc.instance(ReturnImplEx.class));
	}

	@Override
	protected void initChildFeature() {
		Features.DELIVERY_ADDRESS = true;
//		Features.BLOCK_IN_STOP_LIST = true;
		Features.ID_COLUMN_IN_PRICE_LIST = true;
		Features.CAN_CHANGE_COST = true;
		Features.USE_COST_IN_RETURNS = true;
	}
	
	@Override
	public void onCreate() {
		ConfigManager.initConfig(new ConfigImplEx());
		super.onCreate();

		OrderImpl.OrderEditor = new OrderEditor();
		setProgrammVersion();
	}

	private void setProgrammVersion() {
		try{
			ServerCommand.ProgramVersion = getResources().getString(R.string.version);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
