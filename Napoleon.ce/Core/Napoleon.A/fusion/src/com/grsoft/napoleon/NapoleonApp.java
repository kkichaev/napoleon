/*
 * Copyright (C), 2011, Гильдия Разработчиков
 * 
 * Демо программа (с базой данных)
 *
 * kki   16/05/2011   creating
 */

package com.grsoft.napoleon;

import com.grsoft.dataobjects.Firm;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Sales;
import com.grsoft.dataobjects.SalesEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.SalesImpl;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.PaDoc;
import com.grsoft.napoleon.documents.PkoDoc;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.napoleon.documents.SalesDoc;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.napoleon.modules.print.NPrinter;
import com.grsoft.napoleon.modules.print.Print;
import com.grsoft.napoleon.modules.print.util.DocHelper;
import com.grsoft.napoleon.modules.print.util.SalesDocNumberStrategy;
import com.grsoft.napoleon.printsources.SalesPrintEx;
import com.grsoft.network.ServerCommand;
import com.grsoft.util.FirstRunInit;
import android.app.Application;
import android.content.Context;

public class NapoleonApp extends Application {
	@SuppressWarnings("unused")
	private static final String TAG = "NapoleonApp";
	
	class OrderEditor implements OrderImpl.PropertiesEditor {
		@Override
		public void edit(Context ctx, OrderImpl order, boolean isOldOrder) {
			CreateOrder.open(ctx, order, isOldOrder);
		}
	}
	
	class SalesEditor extends SalesPropertiesEditor {
		@Override
		public void edit(Context ctx, SalesImpl doc, boolean isOldOrder) {
			SalesProperties.open(ctx, doc.getRowid(), isOldOrder);
		}
	}
	
	private void initDocTypes() {
		Print.init();
		
		DbObject.regNewDataType(Org.class, OrgEx.class);
		DbObject.regNewDataType(Firm.class, Firm.class);
		DbObject.regNewDataType(Sales.class, SalesEx.class);
		
		SalesDetail.SalesPrintType = SalesPrintEx.class;
		
		DocType.addType(OrderDoc.instance());
		DocType.addType(DebtDoc.instance());
		DocType.addType(VisitDoc.instance());
		DocType.addType(RemnantsDoc.instance());
		DocType.addType(SalesDoc.instance());
		DocType.addType(PkoDoc.instance());
		DocType.addType(PaDoc.instance());
		DocType.setCurDoc(SalesDoc.instance());
		
		Features.CAN_CHANGE_COST = true;
		Features.UPD = true;
		SalesDetail.activity = SalesDetailEx.class;
		NPrinter.forms.put("Накладная", "nakl");
		
		DocHelper.makeDocNumberStrategy = new SalesDocNumberStrategy();
		Warehouse.activity = WarehouseNew.class;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		FirstRunInit.init(this);
		initDocTypes();
		
		OrderImpl.OrderEditor = new OrderEditor();
		SalesImpl.Editor = new SalesEditor();
		
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
