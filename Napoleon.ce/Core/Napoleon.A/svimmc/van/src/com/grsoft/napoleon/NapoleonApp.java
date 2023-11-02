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
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.Sales;
import com.grsoft.dataobjects.SalesEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.ReturnImplEx;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.IncassDoc;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.QuestionDoc;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.napoleon.documents.SalesDoc;
import com.grsoft.napoleon.documents.StockDoc;
import com.grsoft.napoleon.documents.TaskDoneDoc;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.napoleon.modules.print.NPrinter;
import com.grsoft.napoleon.modules.print.Print;
import com.grsoft.napoleon.modules.print.TextPrinter;
import com.grsoft.napoleon.printsources.SalesPrint;
import com.grsoft.napoleon.printsources.SalesPrintEx;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.ServerCommand;
import com.grsoft.util.CfgNplEx;
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
		Print.init();
		
		SVIBase.init();
		
		DbObject.regNewDataType(Delivery.class, DeliveryEx.class);
		DbObject.regNewDataType(Order.class, OrderEx.class);
		DbObject.regNewDataType(Org.class, OrgEx.class);
		DbObject.regNewDataType(Price.class, PriceEx.class);
		DbObject.regNewDataType(Sales.class, SalesEx.class);
				
		DataObjectInfo doi = DataObjectInfo.getInstance();
		doi.replaceListType(OrderEx.class, "items", OrderItemEx.class);
		
		DocType.addType(OrderDoc.instance(OrderImplEx.class));
		DocType.addType(DebtDoc.instance());
		DocType.addType(SalesDoc.instance());
		
		DocType.addType(VisitDoc.instance());
		DocType.addType(IncassDoc.instance());
		DocType.addType(RemnantsDoc.instance());
		DocType.addType(ReturnDoc.instance(ReturnImplEx.class));
		DocType.addType(QuestionDoc.instance());
		DocType.addType(StockDoc.instance());
		DocType.addType(TaskDoneDoc.instance());
		
		DocType.setCurDoc(SalesDoc.instance());		

		UpdateDB.activity = UpdateDBEx.class;
		SalesDetail.activity = SalesDetailEx.class;
		CreateSales.activity = CreateSalesEx.class;

		Features.CAN_CHANGE_COST_IN_SALES = true;
	
		NPrinter.setPrintStrategy(NPrinter.TEXT);
		NPrinter.forms.put(getString(R.string.delivery), "nakl");
		TextPrinter.OUTPUT_FILE_ENCODE = ((CfgNplEx)ConfigManager.getConfig()).encoding;
		if( TextPrinter.OUTPUT_FILE_ENCODE.length() == 0 )
			TextPrinter.OUTPUT_FILE_ENCODE = "windows-1251";
	
		SalesDetail.SalesPrintType = SalesPrintEx.class;
		Setting.NetworkSettingActivity = ConfigurationEx.class;
		VisitEdit.activity = VisitEditEx.class;
		Setting.GPSSettingActivity =  GpsSettingEx.class;
		
		SalesPrint.SUM_TEXT_FORMAT = "%s,%02d";
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		SVIBase.begin();
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
