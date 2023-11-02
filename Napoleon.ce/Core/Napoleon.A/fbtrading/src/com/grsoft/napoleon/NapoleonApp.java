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

import com.grsoft.database.Hitching;
import com.grsoft.database.HitchingCtor;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DeliveryItemEx;
import com.grsoft.dataobjects.MaxDiscounts;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.OrgDiscount;
import com.grsoft.dataobjects.OwnSklad;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.ServerCommand;
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
	protected void initChildFeature() {
		Features.DDLV = true;
		Features.LOAD_FULL_PRICE = true;
	}
	
	@Override
	protected void initChildActivity() {
		PriceCount.activity = PriceCountEx.class;
		Warehouse.activity = WarehouseEx.class;
	}
	
	@Override
	protected void defineNewType() {
		DbObject.regNewDataType(Price.class, PriceEx.class);
		DataObjectInfo.getInstance().replaceListType(Order.class, "items", OrderItemEx.class);
		DataObjectInfo.getInstance().replaceListType(Delivery.class, "items", DeliveryItemEx.class);
		
		UpdateDB.addHitchingCtor(new HitchingCtor(){
			@Override
			public List<Hitching> createList() {
				Hitching[] h = new Hitching[] {
					new RcvNewHitching(MaxDiscounts.class, "MaxDiscounts"),
					new RcvNewHitching(OrgDiscount.class, "OrgDiscount"),
					new RcvNewHitching(OwnSklad.class, "OwnSklad"),
				};
				return Arrays.asList(h);
			}
		}, UpdateDB.GEN_DATA_HITCHING);
	}
	
	@Override protected Class<? extends OrderImplBase<? extends Order>> orderImplType() { return OrderImplEx.class; }
	
	@Override
	public void onCreate() {
		ConfigManager.initConfig(new CfgNpl());
		super.onCreate();

		OrderImpl.OrderEditor = new OrderEditor();
		setProgrammVersion();
		
		Main.docMenuPrepared.add(new MenuPrepareHitching() {
			@Override
			public void menuPrepared(List<MenuHandler> menu, final Activity activity) {
				menu.add(new MenuHandler(getString(R.string.dlvitems), new Runnable() {
					@Override public void run() { DeliveryList.open(activity); }
				}));
			}
		});

		
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
