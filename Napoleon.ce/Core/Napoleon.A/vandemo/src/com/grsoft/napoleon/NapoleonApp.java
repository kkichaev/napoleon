/*
 * Copyright (C), 2011, Гильдия Разработчиков
 * 
 * Демо программа (с базой данных)
 *
 * kki   16/05/2011   creating
 */

package com.grsoft.napoleon;

import java.util.List;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.grsoft.database.DocumentRestore;
import com.grsoft.database.Hitching;
import com.grsoft.database.HitchingCtor;
import com.grsoft.dataobjects.Sales;
import com.grsoft.dataobjects.SalesEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.PaDoc;
import com.grsoft.napoleon.documents.PkoDoc;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.napoleon.documents.SalesDoc;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.napoleon.documents.WSOrderDoc;
import com.grsoft.napoleon.modules.print.NPrinter;
import com.grsoft.napoleon.modules.print.Print;
import com.grsoft.napoleon.modules.print.util.BaseDocNumberStrategy;
import com.grsoft.napoleon.util.CfgNplEx;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.RWServiceFactory;
import com.grsoft.network.RWServiceFactoryEx;
import com.grsoft.network.ServerCommand;
import com.grsoft.util.DocFilterOnClickListener;
import com.grsoft.util.FirstRunInit;
import com.grsoft.util.MenuHandler;
import com.grsoft.util.MenuPrepareHitching;

public class NapoleonApp extends Application {
	@SuppressWarnings("unused")
	private static final String TAG = "NapoleonApp";
	static final String GLOBAL_PREFERENCES = "global_preferences";
	static final String ID_ORG_IN_WORK = "id_org_in_work";
	
	class OrderEditor implements OrderImpl.PropertiesEditor {
		@Override
		public void edit(Context ctx, OrderImpl order, boolean isOldOrder) {
			CreateOrder.open(ctx, order, isOldOrder);
		}
	}
	
	private void initDocTypes() {
		Print.init();
		
		DbObject.regNewDataType(Sales.class, SalesEx.class);
		
		DocType.addType(OrderDoc.instance());
		DocType.addType(DebtDoc.instance());
		DocType.addType(VisitDoc.instance());
		DocType.addType(RemnantsDoc.instance());
		DocType.addType(SalesDoc.instance());
		DocType.addType(ReturnDoc.instance());
		DocType.addType(PkoDoc.instance());
		DocType.addType(PaDoc.instance());
		DocType.setCurDoc(SalesDoc.instance());

		Warehouse.activity = WarehouseNew.class;
		SalesDetail.activity = SalesDetailEx.class;
		CreateSales.activity = CreateSalesEx.class;
		Documents.activity = DocumentsEx.class;
		PriceCount.activity = PriceCountEx.class;
		
		CostStrategy.defaultInstance = new CostStrategyEx();
		
		Setting.WarehouseSettingActivity = WarehouseSettingEx.class;
		
		DocFilterOnClickListener.HiddenTypes.add(WSOrderDoc.instance());
		
		RWServiceFactory.instance = new RWServiceFactoryEx();
		
		Features.HAVE_VISIT_CAUSE = true;
		Features.SHOW_NUMBER_IN_ORDER = true;
		
		BaseDocNumberStrategy.FormatDocStr = "%s%06d";

		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override public Hitching create() { return new DocumentRestore(WSOrderDoc.instance()); }
		}, UpdateDB.RESTORE_DATA_HITCHING);

		Napoleon.docMenuPrepared.add( new MenuPrepareHitching() {
			
			@Override
			public void menuPrepared(List<MenuHandler> menu, final Activity activity) {
				menu.add(new MenuHandler(getString(R.string.wsorder_title), new Runnable() {
					@Override public void run() { WSOrderList.open(activity); }
				}));
			}
		});
		
		NPrinter.forms.put("Накладная", "nakl");
	}
	
	@Override
	public void onCreate() {
		
		ConfigManager.initConfig(new CfgNplEx());
		
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

	
	public String getInWork(){
		SharedPreferences pref = getSharedPreferences(NapoleonApp.GLOBAL_PREFERENCES, Context.MODE_PRIVATE);
		return pref.getString(NapoleonApp.ID_ORG_IN_WORK, "");
	}
	
	public void putInWork(String inWork) {
		SharedPreferences pref = getSharedPreferences(NapoleonApp.GLOBAL_PREFERENCES, Context.MODE_PRIVATE);
		SharedPreferences.Editor ed = pref.edit();
		ed.putString(NapoleonApp.ID_ORG_IN_WORK, inWork);
		ed.commit();
	}
}
