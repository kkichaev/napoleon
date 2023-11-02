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
import com.grsoft.database.Hitching;
import com.grsoft.database.HitchingCtor;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.AgentSalesPlan;
import com.grsoft.dataobjects.Incass;
import com.grsoft.dataobjects.IncassEx;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.impl.AgentSalesPlanImpl;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.IncassDoc;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.napoleon.util.PresentSdcard;
import com.grsoft.network.ServerCommand;
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
		DbObject.regNewDataType(Order.class, OrderEx.class);
		DbObject.regNewDataType(Incass.class, IncassEx.class);
		
		DocType.addType(OrderDoc.instance(OrderImplEx.class));
		DocType.addType(DebtDoc.instance());
		DocType.addType(IncassDoc.instance());
		DocType.addType(VisitDoc.instance());
		DocType.addType(RemnantsDoc.instance());
		
		DocType.setCurDoc(OrderDoc.instance());		

		Warehouse.activity = WarehouseEx.class;
		IncassEdit.activity = IncassEditEx.class;
		PriceCount.activity = PriceCountEx.class;
		UpdateDB.activity = UpdateDBEx.class;
		DeliveryDetail.activity = DeliveryDetailEx.class;
		
		Features.REQUSET_FOCUS_IN_SEARCH = true;
		Features.PRESENTATION_ON_SDCARD = true;
		Features.INPUT_QTY_IN_PACK = true;
		Features.LOAD_FULL_PRICE = true;
		
		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override
			public Hitching create() {
				AgentSalesPlanImpl.clearCache();
				return new RcvNewHitching(AgentSalesPlan.class, "AgentSalesPlan"); 
			}
		}, UpdateDB.GEN_DATA_HITCHING);
		
		Napoleon.docMenuPrepared.add( new MenuPrepareHitching() {			
			@Override
			public void menuPrepared(List<MenuHandler> menu, final Activity activity) {
				menu.add(new MenuHandler("План", new Runnable() {
					@Override public void run() { PlanView.open(activity); }
				}));
			}
		});

		
		PresentSdcard.initStrategy = new InitPresentation();
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
