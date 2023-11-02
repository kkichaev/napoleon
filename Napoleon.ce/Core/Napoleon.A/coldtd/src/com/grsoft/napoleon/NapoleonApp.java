/*
 * Copyright (C), 2011, Гильдия Разработчиков
 * 
 * Демо программа (с базой данных)
 *
 * kki   16/05/2011   creating
 */

package com.grsoft.napoleon;

import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.grsoft.database.DocumentRestore;
import com.grsoft.database.Hitching;
import com.grsoft.database.HitchingCtor;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.database.RfrgRcv;
import com.grsoft.dataobjects.AgentPrefix;
import com.grsoft.dataobjects.AgentPrefixEx;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Firm;
import com.grsoft.dataobjects.FirmEx;
import com.grsoft.dataobjects.Income;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgDogovor;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.PriceSeries;
import com.grsoft.dataobjects.PrintFormLoader;
import com.grsoft.dataobjects.Sales;
import com.grsoft.dataobjects.SalesEx;
import com.grsoft.dataobjects.SalesItemEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.ReturnImplEx;
import com.grsoft.dataobjects.impl.SalesImplEx;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.ItemsAuditDoc;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.PkoDoc;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.napoleon.documents.RfrgAuditDoc;
import com.grsoft.napoleon.documents.SalesDoc;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.napoleon.documents.WSOrderDoc;
import com.grsoft.napoleon.modules.print.NPrinter;
import com.grsoft.napoleon.modules.print.Print;
import com.grsoft.napoleon.modules.print.util.DocHelper;
import com.grsoft.napoleon.printsources.SalesPrintEx;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.napoleon.utl.CfgNplEx;
import com.grsoft.napoleon.utl.DocNumberStrategy;
import com.grsoft.network.ServerCommand;
import com.grsoft.util.FirstRunInit;
import com.grsoft.util.MenuHandler;
import com.grsoft.util.MenuPrepareHitching;

public class NapoleonApp extends Application {
	
	static Context context;
	
	@SuppressWarnings("unused")
	private static final String TAG = "NapoleonApp";
	
	class OrderEditor implements OrderImpl.PropertiesEditor {
		public void edit(Context ctx, OrderImpl order, boolean isOldOrder) {
			CreateOrder.open(ctx, order, isOldOrder);
		}
	}
	
	private void initDocTypes() {
		SalesDoc.instance(SalesImplEx.class);
		Print.init();
		
		DbObject.regNewDataType(Org.class, OrgEx.class);
		DbObject.regNewDataType(Order.class, OrderEx.class);
		DbObject.regNewDataType(Price.class, PriceEx.class);
		DbObject.regNewDataType(AgentPrefix.class, AgentPrefixEx.class);
		DbObject.regNewDataType(Firm.class, FirmEx.class);
		DbObject.regNewDataType(Sales.class, SalesEx.class);
		
		DataObjectInfo.getInstance().replaceListType(SalesEx.class, "items", SalesItemEx.class);

		DocType.addType(OrderDoc.instance(OrderImplEx.class));
		DocType.addType(DebtDoc.instance());
		DocType.addType(VisitDoc.instance());
		DocType.addType(SalesDoc.instance());
		DocType.addType(PkoDoc.instance());
		DocType.addType(ReturnDoc.instance(ReturnImplEx.class));
		DocType.addType(ItemsAuditDoc.instance());
		DocType.addType(RfrgAuditDoc.instance());
		DocType.addType(WSOrderDoc.instance());
		
		DocType.setCurDoc(SalesDoc.instance());
		
		DocHelper.makeDocNumberStrategy = new DocNumberStrategy();
		
		Warehouse.activity = WarehouseEx.class;
		Documents.activity = DocumentsEx.class;
		SalesDetail.activity = SalesDetailEx.class;
		OrderDetail.activity = OrderDetailEx.class;
		PriceCount.activity = PriceCountEx.class;
		CameraPreview.activity = CameraPreviewEx.class;
		VanRestReport.activity = VanRestReportEx.class;
		CreateSales.activity = CreateSalesEx.class;
		ReturnDetail.activity = ReturnDetailEx.class;
		DeliveryDetail.activity = DeliveryDetailEx.class;
		PkoInfo.activity = PkoInfoEx.class;
		UpdateDB.activity = UpdateDBEx.class;
		

		NPrinter.setPrintStrategy(NPrinter.TEXT);
		NPrinter.forms.put("daily_report", "daily_report");
		NPrinter.forms.put("price", "price");
		NPrinter.forms.put("rest", "rest");
		
		Setting.addTabs.remove(TextPrinterSetting.class);
		Setting.addTabs.add(WiFiPrinterSettings.class);

		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override
			public List<Hitching> createList() {
				Hitching[] h = new Hitching[] {
						new DocumentRestore(WSOrderDoc.instance()),
						new DocumentRestore(ItemsAuditDoc.instance()),
						new DocumentRestore(RfrgAuditDoc.instance()),
				};
				return Arrays.asList(h);
			}
		}, UpdateDB.RESTORE_DATA_HITCHING);
		
		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override
			public List<Hitching> createList() {
				Hitching[] h = new Hitching[] {
						new RcvNewHitching(PrintFormLoader.class, "PrintForm"),
						new RcvNewHitching(OrgDogovor.class, "OrgDogovors"),
						new RcvNewHitching(Income.class, "Incomes"),
						new RcvNewHitching(PriceSeries.class),
						new RfrgRcv(),
				};
				return Arrays.asList(h);
			}
		}, UpdateDB.GEN_DATA_HITCHING);
		

		SalesDetail.SalesPrintType = SalesPrintEx.class;
		
		Features.INPUT_QTY_IN_PACK = true;
		Features.DOC_SUM_BY_PERIOD = true;
		Features.QTY_IN_PACK_IN_DOCS = true;
		Features.RECIEVE_REMNANTS_IN_MAIN_MENU = false;
		Features.USE_COST_IN_RETURNS = true;
		Features.POTENZIAL_ORG = false;
		Features.UPD = true;
		Features.CANT_RESEND_SENDED_DOCUMENT = true;
		Features.SYNC_INFO = true;
		
		Napoleon.docMenuPrepared.add( new MenuPrepareHitching() {
			@Override
			public void menuPrepared(List<MenuHandler> menu, final Activity activity) {
				menu.add(new MenuHandler("Приходы", new Runnable() {
					@Override public void run() { IncomeListForm.open(activity); }
				}));
			}
		});
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		context = getApplicationContext();
		
		ConfigManager.initConfig(new CfgNplEx());
		
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
	
	public static Context getAppContext() { return context; }
}
