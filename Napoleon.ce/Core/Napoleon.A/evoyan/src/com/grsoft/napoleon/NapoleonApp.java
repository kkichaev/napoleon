/*
 * Copyright (C), 2011, Гильдия Разработчиков
 * 
 * Демо программа (с базой данных)
 *
 * kki   16/05/2011   creating
 */

package com.grsoft.napoleon;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;

import com.grsoft.database.DocumentRestore;
import com.grsoft.database.Hitching;
import com.grsoft.database.HitchingCtor;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.Firm;
import com.grsoft.dataobjects.FirmEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PricePrintEx;
import com.grsoft.dataobjects.Sales;
import com.grsoft.dataobjects.SalesEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.SalesImplEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.SalesDoc;
import com.grsoft.napoleon.documents.WSOrderDoc;
import com.grsoft.napoleon.modules.print.NPrinter;
import com.grsoft.napoleon.modules.print.Print;
import com.grsoft.napoleon.modules.print.util.DocHelper;
import com.grsoft.napoleon.modules.print.util.DocNumberStrategy;
import com.grsoft.napoleon.printsources.SalesPrintEx;
import com.grsoft.util.DocFilterOnClickListener;
import com.grsoft.util.MenuHandler;
import com.grsoft.util.MenuPrepareHitching;


public class NapoleonApp extends NapoleonAppBase{
	public static final String T12_SCF = "Торг-12/Сч-ф"; 
	
	@Override
	protected void initDocTypes() {
		IS_PRESELLING_PROGRAM = false;
		
		DbObject.regNewDataType(Sales.class, SalesEx.class);

		SalesDoc.instance(SalesImplEx.class);
		
		Print.init();
		super.initDocTypes();
		
		DbObject.regNewDataType(Price.class, PricePrintEx.class);
		DbObject.regNewDataType(Firm.class, FirmEx.class);
		
		CreateSales.activity = CreateSalesEx.class;
		SalesDetail.SalesPrintType = SalesPrintEx.class;
		SalesDetail.activity = SalesDetailEx.class;
		VanRestReport.activity = VanRestReportEx.class;
		Warehouse.activity = WarehouseEx.class;
		PriceCount.activity = PriceCountEx.class;
		
		Features.USE_PACK_QTY_IN_FORMS = true;
//		Features.QTY_IN_PACK_IN_DOCS = false;
		DocumentsEx.NOT_CHECK_ORG_BLOCKED = true;
		
		DocType.addType(SalesDoc.instance());
		
		DocType.setCurDoc(SalesDoc.instance());
		
		DocFilterOnClickListener.HiddenTypes.add(WSOrderDoc.instance());
		
		NPrinter.forms.put("Накладная", "nakl");
		NPrinter.forms.put(T12_SCF, NPrinter.TORG_12_NAME + "," + NPrinter.TORG_12_NAME + "," + NPrinter.SCHET_FACT_NAME);
		NPrinter.forms.put("Счет", "bill");
		NPrinter.forms.put("Доверенность", "dover");
		//NPrinter.forms.put("Удостоверение качества", "otk");
		NPrinter.forms.put("ws_order", "ws_order");
		
		
		DocHelper.makeDocNumberStrategy = new DocNumberStrategy();
		
		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override public Hitching create() { return new DocumentRestore(WSOrderDoc.instance()); }
		}, UpdateDB.RESTORE_DATA_HITCHING);

		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override public Hitching create() { return new AgentPlanRcv(); }
		}, UpdateDB.GEN_DATA_HITCHING);

		Napoleon.docMenuPrepared.add( new MenuPrepareHitching() {
			
			@Override
			public void menuPrepared(List<MenuHandler> menu, final Activity activity) {
				menu.add(new MenuHandler(getString(R.string.wsorder_title), new Runnable() {
					@Override public void run() { WSOrderList.open(activity); }
				}));
			}
		});

		Napoleon.docMenuPrepared.add(new MenuPrepareHitching() {
			
			@Override
			public void menuPrepared(List<MenuHandler> menu, final Activity activity) {
				menu.add(new MenuHandler("Планы", new Runnable() {
					@Override public void run() { AgentPlanView.open(activity); }
				} ));
			}
		} );

	}
}

class AgentPlanRcv extends RcvNewHitching {
	public AgentPlanRcv () {
		super(com.grsoft.dataobjects.AgentPlan.class, "AgentPlan");
		selectCMD = "SELECT";
	}
	
	@Override
	public String getParams() throws RuntimeException {
		SimpleDateFormat simpleDateFormat =  new SimpleDateFormat("dd.MM.yyyy");
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MONTH, -6);
		c.set(Calendar.DAY_OF_MONTH, 1);
		String filter = String.format(" \"userid\" = '$CURRENT_USERID' and \"begin\" >= ToDate('%s')",
				simpleDateFormat.format(c.getTime()));
		return objectName + ":" + filter;
	}
}

