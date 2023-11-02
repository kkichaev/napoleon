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
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.RemnantsImplEx;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DefectDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.MoneyProxyDoc;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.napoleon.documents.VisitDoc;
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
				
		DbObject.regNewDataType(Org.class, OrgEx.class);
		DbObject.regNewDataType(Payment.class, PaymentEx.class);
		DbObject.regNewDataType(Order.class, OrderEx.class);
		
		DocType.addType(OrderDoc.instance(OrderImplEx.class));
		DocType.addType(DebtDoc.instance());
		DocType.addType(VisitDoc.instance());
		DocType.addType(RemnantsDoc.instance(RemnantsImplEx.class));
		DocType.addType(MoneyProxyDoc.instance());
		DocType.addType(DefectDoc.instance());
		
		DocType.setCurDoc(OrderDoc.instance());
		
		Warehouse.activity = WarehouseEx.class;
		
		Features.DISABLE_DOC_COPY = true;
		Features.SCRIPT_DOC = true;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(Consts.D_TAG, "NapoleonApp.onCreate");
		
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
