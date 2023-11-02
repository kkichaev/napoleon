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

import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Firm;
import com.grsoft.dataobjects.FirmEx;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Pko;
import com.grsoft.dataobjects.PkoEx;
import com.grsoft.dataobjects.Sales;
import com.grsoft.dataobjects.SalesItemEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DebtDocEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.OrderWDoc;
import com.grsoft.napoleon.documents.PaDoc;
import com.grsoft.napoleon.documents.PkoDoc;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.napoleon.documents.SalesDoc;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.napoleon.modules.print.Print;
import com.grsoft.napoleon.modules.print.util.DocHelper;
import com.grsoft.napoleon.modules.print.util.DocNumberStrategy;
import com.grsoft.napoleon.printsources.SalesPrintEx;
import com.grsoft.network.ServerCommand;
import com.grsoft.util.FirstRunInit;

public class NapoleonApp extends Application {
	@SuppressWarnings("unused")
	private static final String TAG = "NapoleonApp";
	
	class OrderEditor implements OrderImpl.PropertiesEditor {
		public void edit(Context ctx, OrderImpl order, boolean isOldOrder) {
			CreateOrder.open(ctx, order, isOldOrder);
		}
	}
	
	private void initDocTypes() {
		com.grsoft.napoleon.modules.print.DebtDoc.DebtDocType = DebtDocEx.class;
		Print.init();
		UpdateDB.updateDBActivity = UpdateDBEx.class;
		DocType.addType(OrderDoc.instance());
		DocType.addType(DebtDoc.instance());
		DocType.addType(VisitDoc.instance());
		DocType.addType(RemnantsDoc.instance());
		DocType.addType(SalesDoc.instance());
		DocType.addType(PkoDoc.instance());
		DocType.addType(PaDoc.instance());
		DocType.addType(OrderWDoc.instance());
		DocType.setCurDoc(SalesDoc.instance());
		
		DataObjectInfo.getInstance().replaceListType(Order.class, "items", OrderItemEx.class);
		DataObjectInfo.getInstance().replaceListType(Sales.class, "items", SalesItemEx.class);
		DbObject.regNewDataType(Firm.class, FirmEx.class);
		DbObject.regNewDataType(Pko.class, PkoEx.class);
		
		PriceCount.PriceCountActivity = PriceCountEx.class;
		DocHelper.makeDocNumberStrategy = new DocNumberStrategy();
		PkoInfo.PkoInfoActivity = PkoInfoEx.class;
		SalesDetail.SalesActivity = SalesDetailEx.class;
		
		SalesDetail.SalesPrintType = SalesPrintEx.class;
		Documents.DocumentsActivity = DocumentsPrintEx.class;
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
