/*
 * Copyright (C), 2011, Гильдия Разработчиков
 * 
 * Демо программа (с базой данных)
 *
 * kki   16/05/2011   creating
 */

package com.grsoft.napoleon;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Payment;
import com.grsoft.dataobjects.PaymentEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.napoleon.documents.AgentTaskDoc;
import com.grsoft.napoleon.documents.CashPayDoc;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DebtDocEx;
import com.grsoft.napoleon.documents.DeliveryDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.MoneyProxyDoc;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.PPayDoc;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.napoleon.documents.RfrDoc;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.napoleon.util.CfgNplEx;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.ServerCommand;
import com.grsoft.util.Consts;
import com.grsoft.util.FirstRunInit;

public class NapoleonApp extends Application {
	
	class OrderEditor implements OrderImpl.PropertiesEditor {
		@Override
		public void edit(Context ctx, OrderImpl order, boolean isOldOrder) {
			CreateOrder.open(ctx, order, isOldOrder);
		}
	}
	
	public void initDocTypes() {
		CostStrategy.defaultInstance = new CostStrategyEx();
		Documents.activity = DocumentsEx.class;
		PriceCount.activity = PriceCountEx.class;
		UpdateDB.activity = UpdateDBEx.class;
		Setting.NetworkSettingActivity = ConfigurationEx.class;
		Warehouse.activity = WarehouseNew.class;
		OrderDetail.activity = OrderDetailEx.class;
		
		DbObject.regNewDataType(Org.class, OrgEx.class);
		DbObject.regNewDataType(Order.class, OrderEx.class);
		DbObject.regNewDataType(Price.class, PriceEx.class);
		DbObject.regNewDataType(Payment.class, PaymentEx.class);
		
		DebtDoc.LoadDelivery = false;
		
		DebtDocEx.initialize();
		
		DocType.addType(OrderDoc.instance(OrderImplEx.class));
		DocType.addType(DeliveryDoc.instance());
		DocType.addType(DebtDoc.instance());
		DocType.addType(VisitDoc.instance());
		DocType.addType(MoneyProxyDoc.instance());
		DocType.addType(RemnantsDoc.instance());
		DocType.addType(PPayDoc.instance());
		DocType.addType(CashPayDoc.instance());
		DocType.addType(AgentTaskDoc.instance());
		DocType.addType(RfrDoc.instance());
		
		DocType.setCurDoc(OrderDoc.instance());
		
		Features.CAN_CHANGE_COST = true;
		Features.HAVE_VISIT_CAUSE = true;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(Consts.D_TAG, "NapoleonApp.onCreate");
		
		ConfigManager.initConfig(new CfgNplEx());
		ConfigManager.load(this);

		initDocTypes();
		
		FirstRunInit.init(this);
		
		OrderImpl.OrderEditor = new OrderEditor();

		try{
			ServerCommand.ProgramVersion = getResources().getString(R.string.version);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
