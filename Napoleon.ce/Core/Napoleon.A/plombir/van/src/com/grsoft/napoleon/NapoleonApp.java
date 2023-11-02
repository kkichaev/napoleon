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
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.Sales;
import com.grsoft.dataobjects.SalesEx;
import com.grsoft.dataobjects.SalesItem;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.SalesImpl;
import com.grsoft.dataobjects.impl.SalesImplEx;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.IncassDoc;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.QuestionDoc;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.napoleon.documents.SalesDoc;
import com.grsoft.napoleon.documents.TaskDoneDoc;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.napoleon.modules.print.Print;
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
	
	class SalesEditor extends SalesPropertiesEditor {
		@Override
		public void edit(Context ctx, SalesImpl doc, boolean isOldOrder) {
			SalesProperties.open(ctx, doc.getRowid(), isOldOrder);
		}
	}
	
	private void initDocTypes() {
		Base.init();
		Print.init();

		Documents.activity = DocumentsEx.class;
				
		DbObject.regNewDataType(Order.class, OrderEx.class);
		DbObject.regNewDataType(Org.class, OrgEx.class);
		DbObject.regNewDataType(Price.class, PriceEx.class);
		DbObject.regNewDataType(Delivery.class, DeliveryEx.class);
		DbObject.regNewDataType(Sales.class, SalesEx.class);
		
		DataObjectInfo.getInstance().replaceListType(SalesEx.class, "items", SalesItem.class);

		DocType.addType(OrderDoc.instance());
		DocType.addType(DebtDoc.instance());
		DocType.addType(VisitDoc.instance());
		DocType.addType(RemnantsDoc.instance());
		DocType.addType(SalesDoc.instance(SalesImplEx.class));
		DocType.addType(IncassDoc.instance());
		DocType.addType(TaskDoneDoc.instance());
		DocType.addType(QuestionDoc.instance());

		DocType.setCurDoc(SalesDoc.instance());

		UpdateDB.activity = UpdateDBEx.class;
		Warehouse.activity = WarehouseNew.class;
		
		Features.SCRIPT_DOC = true;
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
