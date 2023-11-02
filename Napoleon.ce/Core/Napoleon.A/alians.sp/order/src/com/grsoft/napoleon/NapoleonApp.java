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

import com.grsoft.dataobjects.ContactEx;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.Visit;
import com.grsoft.dataobjects.VisitItemEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.VisitImplEx;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.IncassDoc;
import com.grsoft.napoleon.documents.MonitoringDoc;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.QuestionDoc;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.napoleon.modules.MonitoringInit;
import com.grsoft.napoleon.util.BuildSetThreadEx;
import com.grsoft.network.ServerCommand;
import com.grsoft.util.BuildSetThread;
import com.grsoft.util.FirstRunInit;
import com.grsoft.util.MenuHandler;
import com.grsoft.util.MenuPrepareHitching;

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
		DbObject.regNewDataType(Org.class, OrgEx.class);
		DbObject.regNewDataType(Price.class, PriceEx.class);
		
		MonitoringInit.init();
		DocType.addType(OrderDoc.instance());
		DocType.addType(DebtDoc.instance());
		DocType.addType(VisitDoc.instance(VisitImplEx.class));
		//DocType.addType(RemnantsDoc.instance());
		DocType.addType(IncassDoc.instance());
		DocType.addType(MonitoringDoc.instance());
		DocType.addType(QuestionDoc.instance());
		
		DocType.setCurDoc(OrderDoc.instance());
		
		DataObjectInfo.getInstance().replaceListType(Org.class, "contacts", ContactEx.class);
		DataObjectInfo.getInstance().replaceListType(Visit.class, "items", VisitItemEx.class);
		
		Features.PACK_INPUT = true;
		
		PriceCount.activity = PriceCountEx.class;
		Documents.activity = DocumentsEx.class;
		Warehouse.activity = WarehouseEx.class;
		UpdateDB.activity = UpdateDBEx.class;
		
		BuildSetThread.type = BuildSetThreadEx.class;
		Napoleon.docMenuPrepared.add(new MenuPrepareHitching() {
			
			@Override
			public void menuPrepared(List<MenuHandler> menu, final Activity activity) {
				menu.add(new MenuHandler(getString(R.string.incomes),
						new Runnable() { 
							@Override public void run() { IncomeForm.open(activity); }
						}));
			}
		});
		
		CostStrategy.defaultInstance = new CostStrategyEx();
	}
	
	@Override
	public void onCreate() {
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
}
