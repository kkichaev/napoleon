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

import java.util.Arrays;
import java.util.List;

import com.grsoft.database.Hitching;
import com.grsoft.database.HitchingCtor;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.FolderDiscount;
import com.grsoft.dataobjects.Income;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.PayType;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.Return;
import com.grsoft.dataobjects.ReturnEx;
import com.grsoft.dataobjects.ReturnItemEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.ReturnImplEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.ServerCommand;
import com.grsoft.util.MenuHandler;
import com.grsoft.util.MenuPrepareHitching;

public class NapoleonApp extends NapoleonAppBase {
	
	class OrderEditor implements OrderImpl.PropertiesEditor {
		@Override
		public void edit(Context ctx, OrderImpl order, boolean isOldOrder) {
			CreateOrder.open(ctx, order, isOldOrder);
		}
	}
	
	@Override protected Class<? extends OrderImplBase<? extends Order>> orderImplType() {	return OrderImplEx.class; }

	@Override
	protected void defineNewType() {
		DbObject.regNewDataType(Order.class, OrderEx.class);
		DbObject.regNewDataType(Org.class, OrgEx.class);
		DbObject.regNewDataType(Price.class, PriceEx.class);

		DbObject.regNewDataType(Return.class, ReturnEx.class);
		DbObject.regNewDataType(Price.class, PriceEx.class);
		DataObjectInfo.getInstance().replaceListType(ReturnEx.class, "items", ReturnItemEx.class);
		DataObjectInfo.getInstance().replaceListType(OrderEx.class, "items", OrderItemEx.class);
		
		UpdateDB.addHitchingCtor(new HitchingCtor(){
			@Override
			public List<Hitching> createList() {
				WarehouseEx.resetCache();
				
				Hitching[] h = new Hitching[] {
					new RcvNewHitching(Income.class),	
					new RcvNewHitching(PayType.class),
					new RcvNewHitching(FolderDiscount.class),
				};
				return Arrays.asList(h);
			}
		}, UpdateDB.GEN_DATA_HITCHING);
		
		Main.docMenuPrepared.add(new MenuPrepareHitching() {
			
			@Override
			public void menuPrepared(List<MenuHandler> menu, final Activity activity) {
				menu.add(new MenuHandler("Приходы", new Runnable() {
					@Override public void run() { IncomeForm.open(activity); }
				}));
			}
		});
		
		CostStrategy.defaultInstance = new CostStrategyEx();
	}
	
	@Override
	protected void initChildFeature() {
		Features.HAVE_PRICE_MOVER = true;
		Features.DDLV = true;
		Features.FOCUSED_ITEMS = true;
		Features.USE_COST_IN_RETURNS = true;
		Features.LOAD_FULL_PRICE = true;
	}
	
	@Override
	protected void initChildDocTypes() {
		DocType.addType(ReturnDoc.instance(ReturnImplEx.class));
	}
	
	
	@Override
	protected void initChildActivity() {
		Warehouse.activity = WarehouseEx.class;
		PriceCount.activity = PriceCountEx.class;
		UpdateDB.activity = UpdateDBEx.class;
		OrderDetail.activity = OrderDetailEx.class;
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
