/*
 * Copyright (C), 2011, Гильдия Разработчиков
 * 
 * Демо программа (с базой данных)
 *
 * kki   16/05/2011   creating
 */

package com.grsoft.napoleon;

import java.util.List;

import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.Return;
import com.grsoft.dataobjects.ReturnEx;
import com.grsoft.dataobjects.ReturnItemEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.ReturnImplEx;
import com.grsoft.napoleon.documents.DebtDocEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.ReqNewOrgDoc;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.ServerCommand;
import com.grsoft.util.DocFilterOnClickListener;
import com.grsoft.util.MenuHandler;
import com.grsoft.util.MenuPrepareHitching;

import android.app.Activity;
import android.content.Context;

public class NapoleonApp extends NapoleonAppBase {
	
	class OrderEditor implements OrderImpl.PropertiesEditor {
		@Override
		public void edit(Context ctx, OrderImpl order, boolean isOldOrder) {
			CreateOrder.open(ctx, order, isOldOrder);
		}
	}
	
	@Override
	protected void defineNewType() {
		DebtDocEx.init();
		
		DbObject.regNewDataType(Price.class, PriceEx.class);
		DbObject.regNewDataType(Org.class, OrgEx.class);
		DbObject.regNewDataType(Delivery.class, DeliveryEx.class);
		DbObject.regNewDataType(Return.class, ReturnEx.class);
		DataObjectInfo.getInstance().replaceListType(Return.class, "items", ReturnItemEx.class);
		
		CostStrategy.defaultInstance = new CostStrategyEx();
		Main.mainMenuPrepared.add(new MenuPrepareHitching() {
			
			@Override
			public void menuPrepared(List<MenuHandler> menu, final Activity activity) {
				menu.add(new MenuHandler("Заявка на создание организации в 1С", new Runnable() {
					@Override public void run() { ReqNewOrgList.open(activity); }
				}));
			}
		});
	}
	
	@Override
	protected void initChildDocTypes() {
		DocType.addType(ReqNewOrgDoc.instance());
		DocType.addType(ReturnDoc.instance(ReturnImplEx.class));
		
		DocFilterOnClickListener.HiddenTypes.add(ReqNewOrgDoc.instance());
	}
	
	@Override
	protected void initChildFeature() {
		Features.HAVE_PRICE_MOVER = true;
		Features.INCASS_DEBET_DISTRIB = true;
		Features.UNLIMIT_VISIT_ITEMS = true;
	}
	
	@Override
	protected void initChildActivity() {
		PriceCount.activity = PriceCountEx.class;
		Documents.activity = DocumentsEx.class;
		Warehouse.activity = WarehouseEx.class;
		CreateReturn.activity = CreateReturnEx.class;
		Presentation.activity = PresentationFolderEx.class;
	}
	
	@Override
	public void onCreate() {
		ConfigManager.initConfig(new CfgNpl());
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
