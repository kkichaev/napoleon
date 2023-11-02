/*
 * Copyright (C), 2011, Гильдия Разработчиков
 * 
 * Демо программа (с базой данных)
 *
 * kki   16/05/2011   creating
 */

package com.grsoft.napoleon;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Payment;
import com.grsoft.dataobjects.PaymentEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.Visit;
import com.grsoft.dataobjects.VisitEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.napoleon.documents.DebtDocEx;
import com.grsoft.napoleon.documents.DeliveryDoc;
import com.grsoft.napoleon.documents.DisplayDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.util.ConfigAgama;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.ServerCommand;
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
	
	@Override protected Class<? extends OrderImplBase<? extends Order>> orderImplType() {	return OrderImplEx.class;}
	
	@Override
	protected void initChildDocTypes() {
		super.initChildDocTypes();
		DocType.addType(DeliveryDoc.instance());
		DocType.addType(DisplayDoc.instance());
	}
	
	@Override
	protected void initChildFeature() {
		super.initChildFeature();
		Features.ID_COLUMN_IN_PRICE_LIST = true;
		Features.INPUT_QTY_IN_PACK = true;
		Features.ORG_TASK = true;
	}
	
	@Override
	protected void initChildActivity() {
		super.initChildActivity();
		PriceCount.activity = PriceCountEx.class;
		Warehouse.activity = WarehouseEx.class;
		UpdateDB.activity = UpdateDBEx.class;
		VisitEdit.activity = VisitEditEx.class;
		Setting.WarehouseSettingActivity = WarehouseSettingEx.class;
		OrderDetail.activity = OrderDetailEx.class;
	}
	
	protected void initDocTypes() {
		DebtDocEx.init();
		DbObject.regNewDataType(Order.class, OrderEx.class);
		DbObject.regNewDataType(Payment.class, PaymentEx.class);
		DbObject.regNewDataType(Price.class, PriceEx.class);
		DbObject.regNewDataType(Org.class, OrgEx.class);
		DbObject.regNewDataType(Visit.class, VisitEx.class);
		
		super.initDocTypes();
	}
	
	@Override
	public void onCreate() {
		ConfigManager.initConfig(new ConfigAgama());
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
