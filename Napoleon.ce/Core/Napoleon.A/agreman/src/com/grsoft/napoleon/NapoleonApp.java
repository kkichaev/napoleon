/*
 * Copyright (C), 2011, Гильдия Разработчиков
 * 
 * Демо программа (с базой данных)
 *
 * kki   16/05/2011   creating
 */

package com.grsoft.napoleon;

import java.util.List;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.view.View;

import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgSum;
import com.grsoft.dataobjects.OrgSumEx;
import com.grsoft.dataobjects.Payment;
import com.grsoft.dataobjects.PaymentEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DebtDocEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.OrderDocEx;
import com.grsoft.napoleon.documents.PurchaseDoc;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.network.ServerCommand;
import com.grsoft.util.FirstRunInit;
import com.grsoft.util.MenuHandler;
import com.grsoft.util.MenuPrepareHitching;
import com.grsoft.util.ViewInitializer;

public class NapoleonApp extends Application {
	@SuppressWarnings("unused")
	private static final String TAG = "NapoleonApp";
	
	class OrderEditor implements OrderImpl.PropertiesEditor {
		@Override
		public void edit(Context ctx, OrderImpl order, boolean isOldOrder) {
			CreateOrder.open(ctx, order, isOldOrder);
		}
	}
	
	private void initDocTypes() {
		DbObject.regNewDataType(Org.class, OrgEx.class);
		DbObject.regNewDataType(Delivery.class, DeliveryEx.class);
		DbObject.regNewDataType(Payment.class, PaymentEx.class);
		DbObject.regNewDataType(Order.class, OrderEx.class);
		DbObject.regNewDataType(Price.class, PriceEx.class);
		DbObject.regNewDataType(OrgSum.class, OrgSumEx.class);
		
		OrderDocEx.init();
		DebtDocEx.initialize();
		
		DocType.addType(OrderDoc.instance());
		DocType.addType(DebtDoc.instance());
		DocType.addType(VisitDoc.instance());
		DocType.addType(RemnantsDoc.instance());
		DocType.addType(PurchaseDoc.instance());
		
		DocType.setCurDoc(OrderDoc.instance());
		
		UpdateDB.activity = UpdateDBEx.class;
		PriceCount.activity = PriceCountEx.class;
		OrderDetail.activity = OrderDetailEx.class;
		Warehouse.activity = WarehouseNewEx.class;
		Documents.activity = DocumentsEx.class;
		
		//Features.FOCUSED_GROUP = true;
		Features.DELIVERY_ADDRESS = true;
		Features.CANT_RESEND_SENDED_DOCUMENT = true;
		Features.CAN_CHANGE_COST = true;
		Features.LOAD_FULL_PRICE = true;
		
		CostStrategy.defaultInstance = new CostStrategyEx();
		
		DataObjectInfo.getInstance().replaceListType(OrderEx.class, "items", OrderItemEx.class);
		
		Napoleon.docMenuPrepared.add(new DocMenuHitching());
		
		UpdateDB.initUI = new ViewInitializer() {
			@Override
			public void init(Activity activity) {
				activity.findViewById(R.id.cbRemains).setVisibility(View.INVISIBLE);
			}
		};
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		FirstRunInit.init(this);
		initDocTypes();
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

	class DocMenuHitching implements MenuPrepareHitching {

	@Override public void menuPrepared(List<MenuHandler> menu, final Activity activity) {
		menu.add(new MenuHandler("План",
				new Runnable() { 
					@Override public void run() { Planes.open(activity); }
				}));
	}
}
	



