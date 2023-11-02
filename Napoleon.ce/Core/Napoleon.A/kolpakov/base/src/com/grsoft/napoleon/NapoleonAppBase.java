/*
 * Copyright (C), 2011, Гильдия Разработчиков
 * 
 * Демо программа (с базой данных)
 *
 * kki   16/05/2011   creating
 */

package com.grsoft.napoleon;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.view.View;

import com.grsoft.database.Hitching;
import com.grsoft.database.HitchingCtor;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Dogovor;
import com.grsoft.dataobjects.Firm;
import com.grsoft.dataobjects.FirmEx;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceCost;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.PriceType;
import com.grsoft.dataobjects.Sales;
import com.grsoft.dataobjects.SalesEx;
import com.grsoft.dataobjects.SalesItemEx;
import com.grsoft.dataobjects.Sklads;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.SalesImplEx;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.napoleon.documents.SalesDoc;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.napoleon.modules.print.NPrinter;
import com.grsoft.napoleon.modules.print.Print;
import com.grsoft.napoleon.modules.print.util.BaseDocNumberStrategy;
import com.grsoft.network.ServerCommand;
import com.grsoft.util.FirstRunInit;
import com.grsoft.util.ViewInitializer;

public class NapoleonAppBase extends Application {
	@SuppressWarnings("unused")
	private static final String TAG = "NapoleonApp";
	
	class OrderEditor implements OrderImpl.PropertiesEditor {
		@Override
		public void edit(Context ctx, OrderImpl order, boolean isOldOrder) {
			CreateOrder.open(ctx, order, isOldOrder);
		}
	}
	
	protected void init() {
		
	}
	
	protected void initDocTypes(boolean changeDocMenu) {
		SalesDoc.instance(SalesImplEx.class);
		Print.init(changeDocMenu);
		
		BaseDocNumberStrategy.FormatDocStr = "%s%d";
		
		DbObject.regNewDataType(Price.class, PriceEx.class);
		DbObject.regNewDataType(Order.class, OrderEx.class);
		DbObject.regNewDataType(Sales.class, SalesEx.class);
		DbObject.regNewDataType(Org.class, OrgEx.class);
		DbObject.regNewDataType(Firm.class, FirmEx.class);

		DataObjectInfo.getInstance().replaceListType(OrderEx.class, "items", OrderItemEx.class);
		DataObjectInfo.getInstance().replaceListType(SalesEx.class, "items", SalesItemEx.class);
		
		DocType.addType(OrderDoc.instance(OrderImplEx.class));
		DocType.addType(DebtDoc.instance());
		DocType.addType(VisitDoc.instance());
		DocType.addType(RemnantsDoc.instance());
		
		DocType.setCurDoc(OrderDoc.instance());		

		Warehouse.activity = WarehouseEx.class;
		CreateSales.activity = CreateSalesEx.class;
		SalesDetail.activity = SalesDetailEx.class;
		VanRestReport.activity = VanRestReportEx.class;

		NPrinter.setPrintStrategy(NPrinter.TEXT);
		Setting.addTabs.remove(TextPrinterSetting.class);
		Setting.addTabs.add(WiFiPrinterSettings.class);
		
		CostStrategy.defaultInstance = new CostStrategyEx();
		
		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override public Hitching create() { return new RcvNewHitching(Sklads.class, "Sklads"); }
		}, UpdateDB.GEN_DATA_HITCHING);
		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override public Hitching create() { return new RcvNewHitching(PriceType.class, "PriceType"); }
		}, UpdateDB.GEN_DATA_HITCHING);		
		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override public Hitching create() { return new RcvNewHitching(Dogovor.class, "Dogovor"); }
		}, UpdateDB.GEN_DATA_HITCHING);		
		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override public Hitching create() {
				CostStrategyEx.restCache();
				return new RcvNewHitching(PriceCost.class, "PriceCost"); 
			}
		}, UpdateDB.GEN_DATA_HITCHING);
		
		Features.LOAD_FULL_PRICE = true;
		UpdateDB.initUI = new ViewInitializer() {
			@Override public void init(Activity activity) { activity.findViewById(R.id.cbRemains).setVisibility(View.GONE); }
		};
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		FirstRunInit.init(this);

		init();
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
