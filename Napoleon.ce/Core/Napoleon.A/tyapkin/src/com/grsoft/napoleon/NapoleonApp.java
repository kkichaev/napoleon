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
import com.grsoft.dataobjects.AgentPrefix;
import com.grsoft.dataobjects.AgentPrefixEx;
import com.grsoft.dataobjects.Firm;
import com.grsoft.dataobjects.FirmEx;
import com.grsoft.dataobjects.Incass;
import com.grsoft.dataobjects.IncassEx;
import com.grsoft.dataobjects.Matrix;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.napoleon.documents.DebtDocEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.IncassDoc;
import com.grsoft.napoleon.documents.IncassDocEx;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.napoleon.documents.SalesDoc;
import com.grsoft.napoleon.documents.SalesDocEx;
import com.grsoft.napoleon.documents.WSOrderDoc;
import com.grsoft.napoleon.modules.print.NPrinter;
import com.grsoft.napoleon.modules.print.Print;
import com.grsoft.napoleon.modules.print.TextPrinter;
import com.grsoft.napoleon.printsources.SalesPrintEx;
import com.grsoft.network.ServerCommand;
import com.grsoft.util.AssortmentMatrixAdapter;
import com.grsoft.util.MenuHandler;
import com.grsoft.util.MenuPrepareHitching;

import android.app.Activity;
import android.content.Context;

public class NapoleonApp extends NapoleonAppBase {
	@SuppressWarnings("unused")
	private static final String TAG = "NapoleonApp";
	
	class OrderEditor implements OrderImpl.PropertiesEditor {
		@Override
		public void edit(Context ctx, OrderImpl order, boolean isOldOrder) {
			CreateOrder.open(ctx, order, isOldOrder);
		}
	}
	
	@Override
	protected void initFeatures() {
		super.initFeatures();
		
		Print.init();
		DebtDocEx.init();
		IncassDocEx.init();
		SalesDocEx.init();
	}
	
	@Override
	protected void initChildDocTypes() {
		super.initChildDocTypes();
		
		DbObject.regNewDataType(AgentPrefix.class, AgentPrefixEx.class);
		DbObject.regNewDataType(Price.class, PriceEx.class);
		DbObject.regNewDataType(Org.class, OrgEx.class);
		DbObject.regNewDataType(Firm.class, FirmEx.class);
		DbObject.regNewDataType(Incass.class, IncassEx.class);
		
		NPrinter.setPrintStrategy(NPrinter.TEXT);
		NPrinter.forms.put("Накладная", "nakl");
		TextPrinter.PAGE_ROW_COUNT = 85;
		TextPrinter.STR_DIVIDER = " ,-+%*\\";
		
		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override public Hitching create() { return new RcvNewHitching(Matrix.class, "CommonMatrix"); }
		}, UpdateDB.GEN_DATA_HITCHING);
		
		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override public Hitching create() { return new DocumentRestore(WSOrderDoc.instance()); }
		}, UpdateDB.RESTORE_DATA_HITCHING);
		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override public Hitching create() { return new DocumentRestore(IncassDoc.instance()); }
		}, UpdateDB.RESTORE_DATA_HITCHING);
		
		NPrinter.forms.put(getString(R.string.print_price_onboard), "price");
		NPrinter.forms.put(getString(R.string.print_price), "price2");
		NPrinter.forms.put("rest", "rest");
		
		DocType.addType(SalesDoc.instance());
		DocType.addType(ReturnDoc.instance());
		
		Napoleon.docMenuPrepared.add(new MenuPrepareHitching() {
			
			@Override
			public void menuPrepared(List<MenuHandler> menu, final Activity activity) {
				menu.add(new MenuHandler(activity.getString(R.string.group_report), new Runnable() {
					@Override public void run() { GroupReport.open(activity); }
				}));
			}
		});
	}
	
	@Override
	public void setDefDocType() {
		super.setDefDocType();
		
		DocType.setCurDoc(SalesDoc.instance());
	}
	@Override
	protected void initChildFeature() {
		super.initChildFeature();
		
		Features.BLOCK_IN_STOP_LIST = true;
		Features.CAN_CHANGE_COST = true;
		Features.CAN_CHANGE_COST_IN_SALES = true;
		Features.ASSORTMENT_MATRIX = true;
		Features.FOCUSED_GROUP = true;
		Features.SCRIPT_DOC = true;
		Features.SHOW_NUMBER_IN_ORDER = true;
		Features.POTENZIAL_ORG = false;
		Features.OK_BTN_INCASS = true;
		Features.SHOW_PRESENT_IMG = true;
		Features.UPD = true;
		Features.SHOW_DAILY_SALES_IN_WAREHOUSE = true;
		Features.FOCUSED_GROUP = true;
		Features.FOCUSED_ITEMS = true;
		Features.UNLIMIT_VISIT_ITEMS = true;
		Features.MAX_FOTO_HEIGHT = 5000;
		Features.MAX_FOTO_WIDTH = 5000;
		
		AssortmentMatrixAdapter.PERIOD_IN_MONTH = 2;
	}
	
	@Override
	protected void initChildActivity() {
		super.initChildActivity();
		Warehouse.activity = WarehouseEx.class;
		Documents.activity = DocumentsEx.class;
		PriceCount.activity = PriceCountEx.class;
		SalesDetail.activity = SalesDetailEx.class;
		CreateReturn.activity = CreateReturnEx.class;
		IncassEdit.activity = IncassEditEx.class;
		OrderDetail.activity = OrderDetailEx.class;
		DocList.activity = DocListEx.class;
		VisitEdit.activity = VisitEditNew.class;
		
		SalesDetail.SalesPrintType = SalesPrintEx.class;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
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
