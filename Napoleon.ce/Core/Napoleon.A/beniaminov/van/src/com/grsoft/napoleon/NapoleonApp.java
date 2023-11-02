/*
 * Copyright (C), 2011, Гильдия Разработчиков
 * 
 * Демо программа (с базой данных)
 *
 * kki   16/05/2011   creating
 */

package com.grsoft.napoleon;

import java.util.List;
import com.grsoft.database.DocumentRestore;
import com.grsoft.database.Hitching;
import com.grsoft.database.HitchingCtor;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.Firm;
import com.grsoft.dataobjects.FirmEx;
import com.grsoft.dataobjects.Income;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PricePrintEx;
import com.grsoft.dataobjects.Sales;
import com.grsoft.dataobjects.SalesEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.SalesImplEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.SalesDoc;
import com.grsoft.napoleon.documents.SalesDocEx;
import com.grsoft.napoleon.documents.TotalSumConvertor;
import com.grsoft.napoleon.documents.WSOrderDoc;
import com.grsoft.napoleon.modules.print.NPrinter;
import com.grsoft.napoleon.modules.print.Print;
import com.grsoft.napoleon.modules.print.util.DocHelper;
import com.grsoft.napoleon.modules.print.util.DocNumberStrategy;
import com.grsoft.napoleon.printsources.SalesPrintEx;
import com.grsoft.napoleon.util.CfgNplEx;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.util.MenuHandler;
import com.grsoft.util.MenuPrepareHitching;
import android.app.Activity;


public class NapoleonApp extends NapoleonAppBase{
	public static final String T12_SCF = "Торг-12/Сч-ф"; 
	
	@Override
	protected void initDocTypes() {
		SalesDocEx.initilize(SalesImplEx.class);
		
		IS_PRESELLING_PROGRAM = false;
		
		DbObject.regNewDataType(Sales.class, SalesEx.class);

		Print.init();
		super.initDocTypes();
		
		DbObject.regNewDataType(Price.class, PricePrintEx.class);
		DbObject.regNewDataType(Firm.class, FirmEx.class);
		
		CreateSales.activity = CreateSalesEx.class;
		SalesDetail.SalesPrintType = SalesPrintEx.class;
		SalesDetail.activity = SalesDetailEx.class;
		VanRestReport.activity = VanRestReportEx.class;
		Warehouse.activity = WarehouseEx.class;
//		PriceCount.activity = PriceCountEx.class;
		
		Features.USE_PACK_QTY_IN_FORMS = true;
//		Features.INPUT_QTY_IN_PACK = false;
//		Features.QTY_IN_PACK_IN_DOCS = false;
		DocumentsEx.NOT_CHECK_ORG_BLOCKED = true;
		
		DocType.addType(SalesDoc.instance());
		
		DocType.setCurDoc(SalesDoc.instance());
		
		NPrinter.forms.put("Накладная", "nakl");
		NPrinter.forms.put(T12_SCF, NPrinter.TORG_12_NAME + "," + NPrinter.TORG_12_NAME + "," + NPrinter.SCHET_FACT_NAME);
		NPrinter.forms.put("Счет", "bill");
		NPrinter.forms.put("Доверенность", "dover");
		NPrinter.forms.put("Удостоверение качества", "otk");
		NPrinter.forms.put("ws_order", "ws_order");
		NPrinter.forms.put("Остатки", "vanrest");
		NPrinter.forms.put("ТТН на остаток", "ttn_rest");
		NPrinter.forms.put("salelistpage", "salelistpage");
		
		DocHelper.makeDocNumberStrategy = new DocNumberStrategy();
		
		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override public Hitching create() { return new DocumentRestore(WSOrderDoc.instance()); }
		}, UpdateDB.RESTORE_DATA_HITCHING);
		
		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override public Hitching create() { return new RcvNewHitching(Income.class, "Incomes"); }
		}, UpdateDB.DEBET_DATA_HITCHING);

		Napoleon.docMenuPrepared.add( new MenuPrepareHitching() {
			@Override
			public void menuPrepared(List<MenuHandler> menu, final Activity activity) {
				menu.add(new MenuHandler(getString(R.string.wsorder_title), new Runnable() {
					@Override public void run() { WSOrderList.open(activity); }
				}));
				menu.add(new MenuHandler("Приходы", new Runnable() {
					@Override public void run() { IncomeList.open(activity); }
				}));
			}
		});
		
		Setting.BehaviorSettingActivity = BehaviorSettingEx.class;
		UpdateDB.activity = UpdateDBEx.class;
		
		DocType.SumConverter = new TotalSumConvertor(){
			@Override
			public String toString(long sum) {
				String result = super.toString(sum);
				
				if(((CfgNplEx)ConfigManager.getConfig()).simpleMode)
					result = getString(R.string.sum_str, result);
				
				return result;
			}
		};
	}
}
