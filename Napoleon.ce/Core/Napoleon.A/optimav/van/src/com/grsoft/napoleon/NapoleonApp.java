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
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DeliveryPrintEx;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgPrintEx;
import com.grsoft.dataobjects.Sales;
import com.grsoft.dataobjects.SalesEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.IncassImplEx;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.SalesImplEx;
import com.grsoft.napoleon.documents.DebtDocEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.IncassDoc;
import com.grsoft.napoleon.documents.MovementDoc;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.napoleon.documents.SalesDoc;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.napoleon.modules.print.DebtDoc;
import com.grsoft.napoleon.modules.print.NPrinter;
import com.grsoft.napoleon.modules.print.Print;
import com.grsoft.napoleon.printsources.SalesPrintEx;
import com.grsoft.network.ServerCommand;
import com.grsoft.util.FirstRunInit;

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
		Napoleon.serviceType = NapoleonServiceEx.class;
		DebtDoc.DebtDocType = DebtDocEx.class;
		Print.init();
		 
		DbObject.regNewDataType(Sales.class, SalesEx.class);
		DbObject.regNewDataType(Org.class, OrgPrintEx.class);
		DbObject.regNewDataType(Delivery.class, DeliveryPrintEx.class);
		DbObject.regNewDataType(Order.class, OrderEx.class);
		
		DocType.addType(OrderDoc.instance());
		DocType.addType(DebtDoc.instance());
		DocType.addType(VisitDoc.instance());
		DocType.addType(IncassDoc.instance(IncassImplEx.class));
		DocType.addType(RemnantsDoc.instance());
		DocType.addType(SalesDoc.instance(SalesImplEx.class));
//		DocType.addType(PkoDoc.instance());
//		DocType.addType(PaDoc.instance());
		DocType.addType(MovementDoc.instance());

		DocType.setCurDoc(SalesDoc.instance());
		
		Warehouse.activity = WarehouseEx.class;
		UpdateDB.activity = UpdateDBEx.class;
		SalesDetail.activity = SalesDetailEx.class;
		Documents.activity = Documents2Ex.class;
		DocList.activity = DocListEx.class;
		PriceCount.activity = PriceCount2Ex.class;
		IncassEdit.activity = IncassEditEx.class;
		
		NPrinter.forms.put("Сборочной лист", "nakl");
		CostStrategyEx.defaultInstance = new CostStrategyEx();
		Features.RECIEVE_REMNANTS_IN_MAIN_MENU = true;
		Features.UPD = true;
		
		SalesDetail.SalesPrintType = SalesPrintEx.class;
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
