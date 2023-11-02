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
import android.widget.CheckBox;
import java.util.List;
import com.grsoft.database.DocumentRestore;
import com.grsoft.database.Hitching;
import com.grsoft.database.HitchingCtor;
import com.grsoft.dataobjects.AgentPrefix;
import com.grsoft.dataobjects.AgentPrefixEx;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Firm;
import com.grsoft.dataobjects.FirmEx;
import com.grsoft.dataobjects.Incass;
import com.grsoft.dataobjects.IncassEx;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderPrintEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgPrintEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PricePrintEx;
import com.grsoft.dataobjects.Sales;
import com.grsoft.dataobjects.SalesEx;
import com.grsoft.dataobjects.SalesItem;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.SalesImpl;
import com.grsoft.napoleon.documents.ArchSalesDoc;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DebtDocEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.IncassDoc;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.napoleon.documents.SalesDoc;
import com.grsoft.napoleon.documents.SalesDocEx;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.napoleon.modules.print.NPrinter;
import com.grsoft.napoleon.modules.print.Print;
import com.grsoft.napoleon.printsources.SalesPrintEx;
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
	
	class SalesEditor extends SalesPropertiesEditor {
		@Override
		public void edit(Context ctx, SalesImpl doc, boolean isOldOrder) {
			SalesProperties.open(ctx, doc.getRowid(), isOldOrder);
		}
	}
	
	private void initDocTypes() {
		DebtDocEx.initialize();
		Print.init();
		DbObject.regNewDataType(Price.class, PricePrintEx.class);
		SalesDocEx.init();
		
		Documents.activity = DocumentsPrintEx.class;
		SalesDetail.activity = SalesDetailEx.class;
		Warehouse.activity = WarehouseNew.class;
		IncassEdit.activity = IncassEditEx.class;
		
		DocType.addType(OrderDoc.instance());
		DocType.addType(DebtDoc.instance());
		DocType.addType(VisitDoc.instance());
		DocType.addType(RemnantsDoc.instance());
		DocType.addType(SalesDocEx.instance());
		DocType.addType(IncassDoc.instance());
		DocType.setCurDoc(SalesDoc.instance());
		DocType.addType(ArchSalesDoc.instance());
		
		
		DbObject.regNewDataType(Order.class, OrderPrintEx.class);
		DbObject.regNewDataType(Org.class, OrgPrintEx.class);
		DbObject.regNewDataType(Sales.class, SalesEx.class);
		DbObject.regNewDataType(Firm.class, FirmEx.class);
		DbObject.regNewDataType(Incass.class, IncassEx.class);
		DbObject.regNewDataType(AgentPrefix.class, AgentPrefixEx.class);
		
		DataObjectInfo doi = DataObjectInfo.getInstance(); 
		doi.replaceListType(SalesEx.class, "items", SalesItem.class);
		
		NPrinter.forms.put("Расходные накладные", "rn");
		NPrinter.forms.put("incasslist", "incasslist");
		NPrinter.forms.put("doclist", "doclist");
		NPrinter.forms.put("loadlist", "loadlist");
		NPrinter.forms.put("money_list", "money_list");
		NPrinter.forms.put(DocList2Ex.RECEIPT_TITLE, DocList2Ex.RECEIPT_NAME);
		NPrinter.forms.put(SalesDetailEx.TR_NAKL_TITLE, SalesDetailEx.TR_NAKL_NAME);
		
		Features.RECEIVE_REMNANTS_WHEN_SENDING = true;
		Features.SHOW_NUMBER_IN_ORDER = true;
		Features.INPUT_QTY_IN_PACK = true;
		Features.PRINT_MODULE = true;
		Features.SCRIPT_DOC = true;
		Features.DEL_VISIT_WITHOUT_PHOTO = true;
		Features.SCRIPT_SUM_ONLY_FOR_SALES = true;
		Features.CANT_SEND_SCRIPT_PART = true;
		Features.CAN_SET_SEND_FLAG = true;
		Features.BLOCK_IN_STOP_LIST = true;
		Features.DISABLE_EDIT_AFTER_PRINT = true;
		Features.CHECK_UNCOMPLETE_SCRIPTS = true;
		
		SalesDetail.SalesPrintType = SalesPrintEx.class;
		DocList.activity = DocList2Ex.class;
		Setting.NetworkSettingActivity = ConfigurationEx.class;
		
		UpdateDB.initUI = new ViewInitializer() {
			@Override
			public void init(Activity activity) {
				((CheckBox) activity.findViewById(R.id.cbVisit)).setChecked(true);
			}
		};
		
		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override public Hitching create() { return new DocumentRestore(IncassDoc.instance()); }
		}, UpdateDB.RESTORE_DATA_HITCHING);
		
		Napoleon.docMenuPrepared.add(new MenuPrepareHitching() {
			@Override
			public void menuPrepared(List<MenuHandler> menu, final Activity activity) {
				menu.add(new MenuHandler(getString(R.string.recalc_script), new Runnable() {
					@Override public void run() {	((Napoleon2Ex)activity).calcScriptSums(); }
				}));
			}
		});
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
