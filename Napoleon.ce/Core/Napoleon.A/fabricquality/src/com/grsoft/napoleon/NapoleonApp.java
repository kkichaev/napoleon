/*
 * Copyright (C), 2011, Гильдия Разработчиков
 * 
 * Демо программа (с базой данных)
 *
 * kki   16/05/2011   creating
 */

package com.grsoft.napoleon;

import android.content.Context;

import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.ServerCommand;

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
	protected void defineNewType() {
		DbObject.regNewDataType(Price.class, PriceEx.class);
		DbObject.regNewDataType(Order.class, OrderEx.class);
		DbObject.regNewDataType(Org.class, OrgEx.class);
		DataObjectInfo.getInstance().replaceListType(Order.class, "items", OrderItemEx.class);
	}
	
	@Override
	protected void initChildActivity() {
		Warehouse.activity = WarehouseEx.class;
		OrderDetail.activity = OrderDetailEx.class;
		PriceCount.activity = PriceCountEx.class;
	}

	@Override protected Class<? extends OrderImplBase<? extends Order>> orderImplType() { return OrderImplEx.class; }
	
	@Override
	protected void initChildFeature() {
		Features.INPUT_QTY_IN_PACK = true;
		Features.LOAD_FULL_PRICE = true;
	}

		@Override
	public void onCreate() {
			ConfigManager.initConfig(new CfgNpl());
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
