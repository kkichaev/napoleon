/*
 * Copyright (C), 2011, Гильдия Разработчиков
 * 
 * Демо программа (с базой данных)
 *
 * kki   16/05/2011   creating
 */

package com.grsoft.napoleon;

import android.app.Activity;
import android.content.Context;
import android.widget.CheckBox;

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
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.Sales;
import com.grsoft.dataobjects.SalesEx;
import com.grsoft.dataobjects.SalesItemEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.SalesImpl;
import com.grsoft.napoleon.documents.ArchSalesDoc;
import com.grsoft.napoleon.documents.DebtDocEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.IncassDoc;
import com.grsoft.napoleon.documents.SalesDoc;
import com.grsoft.napoleon.documents.SalesDocEx;
import com.grsoft.napoleon.modules.print.NPrinter;
import com.grsoft.napoleon.modules.print.Print;
import com.grsoft.napoleon.printsources.SalesPrintEx;
import com.grsoft.network.ServerCommand;
import com.grsoft.util.MenuHandler;
import com.grsoft.util.MenuPrepareHitching;
import com.grsoft.util.ViewInitializer;

import java.util.List;

public class NapoleonApp extends NapoleonAppBase {
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

	@Override
	protected void defineNewType() {
		Features.INCASS_DEBET_DISTRIB = false;
		SalesDocEx.init();
		DebtDocEx.initialize();
		Print.init();

		super.defineNewType();

		DbObject.regNewDataType(Price.class, PriceEx.class);
		DbObject.regNewDataType(Order.class, OrderEx.class);
		DbObject.regNewDataType(Org.class, OrgEx.class);
		DbObject.regNewDataType(Sales.class, SalesEx.class);
		DbObject.regNewDataType(Firm.class, FirmEx.class);
		DbObject.regNewDataType(Incass.class, IncassEx.class);
		DbObject.regNewDataType(AgentPrefix.class, AgentPrefixEx.class);

		DataObjectInfo doi = DataObjectInfo.getInstance();
		doi.replaceListType(SalesEx.class, "items", SalesItemEx.class);

		NPrinter.forms.put("Расходные накладные", "rn");
		NPrinter.forms.put("incasslist", "incasslist");
		NPrinter.forms.put("doclist", "doclist");
		NPrinter.forms.put("loadlist", "loadlist");
		NPrinter.forms.put("money_list", "money_list");
		NPrinter.forms.put(DocListEx.RECEIPT_TITLE, DocListEx.RECEIPT_NAME);
		NPrinter.forms.put(SalesDetailEx.TR_NAKL_TITLE, SalesDetailEx.TR_NAKL_NAME);

		SalesDetail.SalesPrintType = SalesPrintEx.class;

		UpdateDB.initUI = new ViewInitializer() {
			@Override
			public void init(Activity activity) {
				((CheckBox) activity.findViewById(R.id.cbVisit)).setChecked(true);
			}
		};

		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override public Hitching create() { return new DocumentRestore(IncassDoc.instance()); }
		}, UpdateDB.RESTORE_DATA_HITCHING);

		Main.docMenuPrepared.add(new MenuPrepareHitching() {
			@Override
			public void menuPrepared(List<MenuHandler> menu, final Activity activity) {
				menu.add(new MenuHandler(getString(R.string.recalc_script), new Runnable() {
					@Override public void run() {	((MainEx)activity).calcScriptSums(); }
				}));
			}
		});
	}

	@Override
	protected void initChildDocTypes() {
		super.initChildDocTypes();
		DocType.addType(ArchSalesDoc.instance());
	}

	@Override
	protected void initChildActivity() {
		super.initChildActivity();

		Documents.activity = DocumentsPrintEx.class;
		SalesDetail.activity = SalesDetailEx.class;
		IncassEdit.activity = IncassEditEx.class;
		DocList.activity = DocListEx.class;
		Setting.NetworkSettingActivity = ConfigurationEx.class;
	}

	@Override
	protected void initChildFeature() {
		super.initChildFeature();
		Features.INCASS_DEBET_DISTRIB = false;
		Features.SHOW_NUMBER_IN_ORDER = true;
		Features.INPUT_QTY_IN_PACK = true;
		Features.SCRIPT_SUM_ONLY_FOR_SALES = true;
		Features.CANT_SEND_SCRIPT_PART = true;
		Features.CAN_SET_SEND_FLAG = true;
		Features.BLOCK_IN_STOP_LIST = true;
		Features.DISABLE_EDIT_AFTER_PRINT = true;
		Features.LOAD_FULL_PRICE = true;
		Features.UPD = true;
}

	@Override
	public void setDefDocType() {
		DocType.setCurDoc(SalesDoc.instance());
	}

	@Override
	public void onCreate() {
		super.onCreate();

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
