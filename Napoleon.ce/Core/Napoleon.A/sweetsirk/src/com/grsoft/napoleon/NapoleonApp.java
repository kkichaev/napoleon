/*
 * Copyright (C), 2011, Гильдия Разработчиков
 * 
 * Демо программа (с базой данных)
 *
 * kki   16/05/2011   creating
 */

package com.grsoft.napoleon;

import java.util.ArrayList;

import com.grsoft.database.HitchingCtor;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.dataobjects.Dover;
import com.grsoft.dataobjects.IncassDebDistr;
import com.grsoft.dataobjects.IncassEx;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.napoleon.documents.DebtDocEx;
import com.grsoft.napoleon.documents.OrderDocEx;
import com.grsoft.napoleon.util.CfgNplEx;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.ServerCommand;
import com.grsoft.util.MenuHandler;
import com.grsoft.util.MenuPreparedEvent;
import com.grsoft.util.ViewInitializer;

import android.app.Activity;
import android.content.Context;
import android.view.View;

public class NapoleonApp extends NapoleonAppBase {
	
	class OrderEditor implements OrderImpl.PropertiesEditor {
		@Override
		public void edit(Context ctx, OrderImpl order, boolean isOldOrder) {
			CreateOrder.open(ctx, order, isOldOrder);
		}
	}
	
	@SuppressWarnings("serial")
	@Override
	protected void defineNewType() {
		DebtDocEx.initialize();
		OrderDocEx.initialize();
		
		DbObject.regNewDataType(IncassDebDistr.class, IncassEx.class);
		DbObject.regNewDataType(Order.class, OrderEx.class);
		DbObject.regNewDataType(Org.class, OrgEx.class);
		DbObject.regNewDataType(Price.class, PriceEx.class);
		DbObject.regNewDataType(Delivery.class, DeliveryEx.class);
		DataObjectInfo.getInstance().replaceListType(OrderEx.class, "items", OrderItemEx.class);
		
		UpdateDB.addHitchingCtor( new HitchingCtor() {
			public com.grsoft.database.Hitching create() {
				return new RcvNewHitching(Dover.class, "Dover");
			}
		}, UpdateDB.GEN_DATA_HITCHING);
		
		CostStrategy.defaultInstance = new CostStrategyEx();
		
		UpdateDBW.initUI = new ViewInitializer() {
			@Override
			public void init(Activity activity) {
				super.init(activity);
				activity.findViewById(R.id.cbRemains).setVisibility(View.GONE);
			}
		};
		
		Main.docMenuPrepared = new MenuPreparedEvent() {
			@Override
			public void menuPrepared(ArrayList<MenuHandler> menu, Activity activity) {
				// TODO Auto-generated method stub
				super.menuPrepared(menu, activity);
			}
		};
		
		Main.mainMenuPrepared = new MenuPreparedEvent() {
			@Override
			public void menuPrepared(ArrayList<MenuHandler> menu, final Activity activity) {
				super.menuPrepared(menu, activity);
				
				menu.add(3, new MenuHandler("Презентация", new Runnable() {
					
					@Override
					public void run() {
						PresentationFolderW.open(activity, -1,"", -1, PresentationFolder.class);
					}
				}));
			}
		};
	}
	
	@Override
	protected void initChildActivity() {
		IncassDebDistrEdit.editActivity = IncassEditEx.class;
		PriceCount.activity = PriceCountEx.class;
		OrderDetail.activity = OrderDetailEx.class;
		Warehouse.activity = WarehouseEx.class;
		//Presentation.activity = WarehousePrezent.class;
		PricePresentation.activity = PricePresentationFolderEx.class;
		UpdateDB.activity = UpdateDBEx.class;
		DocList.activity = DocListEx.class;
		Presentation.activity = PresentationFolderEx.class;
		Setting.NetworkSettingActivity = ConfigurationEx.class;
	}
	
	@Override
	protected void initChildFeature() {
		Features.COUNT_DOCS_IN_DOCSLIST = true;
		Features.INPUT_QTY_IN_PACK = true;
		Features.INCASS_DEBET_DISTRIB = true;
		Features.LOAD_FULL_PRICE = true;
		Features.SHOW_WEIGHT_IN_DOC_LIST = true;
	}
	
	protected Class<? extends OrderImplBase<? extends Order>> orderImplType() { return OrderImplEx.class; }
	
	@Override
	public void onCreate() {
		ConfigManager.initConfig(new CfgNplEx());
		super.onCreate();

		OrderImpl.OrderEditor = new OrderEditor();
		setProgrammVersion();
		
		//NapoleonChat.init(this);
	}

	private void setProgrammVersion() {
		try{
			ServerCommand.ProgramVersion = getResources().getString(R.string.version);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
