/*
 * Copyright (C), 2011, Гильдия Разработчиков
 * 
 * Демо программа (с базой данных)
 *
 * kki   16/05/2011   creating
 */

package com.grsoft.napoleon;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.content.Context;

import com.grsoft.database.Hitching;
import com.grsoft.database.HitchingCtor;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.AgentPrefix;
import com.grsoft.dataobjects.AgentPrefixEx;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Drivers;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.Return;
import com.grsoft.dataobjects.ReturnItem;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.ReturnImplEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.napoleon.modules.CostManagerImpl;
import com.grsoft.network.ServerCommand;
import com.grsoft.util.MenuHandler;
import com.grsoft.util.MenuPrepareHitching;

public class NapoleonApp extends NapoleonAppBase {
	@SuppressWarnings("unused")
	private static final String TAG = "NapoleonApp";
	
	class OrderEditor implements OrderImpl.PropertiesEditor {
		@Override
		public void edit(Context ctx, OrderImpl order, boolean isOldOrder) {
			CreateOrder.open(ctx, order, isOldOrder);
		}
	}
	
	@Override protected Class<? extends OrderImplBase<? extends Order>> orderImplType() { return OrderImplEx.class; }
	
	@Override
	protected void defineNewType() {
		DbObject.regNewDataType(Price.class, PriceEx.class);
		DbObject.regNewDataType(Order.class, OrderEx.class);
		DbObject.regNewDataType(AgentPrefix.class, AgentPrefixEx.class);
	}
	
	@Override
	protected void initChildDocTypes() {
		DataObjectInfo.getInstance().replaceListType(Return.class, "items", ReturnItem.class);
		DocType.addType(ReturnDoc.instance(ReturnImplEx.class));
	}
	
	@Override
	protected void initChildActivity() {
		PriceCount.activity = PriceCountEx.class;
		OrderDetail.activity = OrderDetailEx.class;
		CreateReturn.activity = CreateReturnEx.class;
		ReturnDetail.activity = ReturnDetailEx.class;
		
		CostStrategy.defaultInstance = new CostStrategyEx();
		
		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override public Hitching create() { return new RcvNewHitching(AgentPrefixEx.class, "AgentsRcv"); }
		}, UpdateDB.GEN_DATA_HITCHING);

		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override public Hitching create() { return new AgentPlanRcv(); }
		}, UpdateDB.GEN_DATA_HITCHING);

		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override public Hitching create() { return new RcvNewHitching(Drivers.class, "Drivers"); }
		}, UpdateDB.GEN_DATA_HITCHING);
		
		Main.mainMenuPrepared.add(new MenuPrepareHitching() {
			
			@Override
			public void menuPrepared(List<MenuHandler> menu, final Activity activity) {
				menu.add(3, new MenuHandler("Водители", new Runnable() {
					@Override public void run() { com.grsoft.napoleon.Drivers.open(activity); }
				}));
			}
		});
		Main.docMenuPrepared.add(new MenuPrepareHitching() {
			
			@Override
			public void menuPrepared(List<MenuHandler> menu, final Activity activity) {
				menu.add(new MenuHandler("Планы", new Runnable() {
					@Override public void run() { AgentPlanView.open(activity); }
				} ));
			}
		});
	}
	
	@Override
	protected void initChildFeature() {
		Features.COST_MANAGER = new CostManagerImpl();
		Features.CAN_CHANGE_COST = true;
		Features.WEIGHT_SCALE = 1000;
		
		CostStrategy.defaultInstance = new CostStrategyEx();
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


class AgentPlanRcv extends RcvNewHitching {
	public AgentPlanRcv () {
		super(com.grsoft.dataobjects.AgentPlan.class, "AgentPlan");
		selectCMD = "SELECT";
	}
	
	@Override
	public String getParams() throws RuntimeException {
		SimpleDateFormat simpleDateFormat =  new SimpleDateFormat("dd.MM.yyyy");
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MONTH, -6);
		c.set(Calendar.DAY_OF_MONTH, 1);
		String filter = String.format(" \"userid\" = '$CURRENT_USERID' and \"begin\" >= ToDate('%s')",
				simpleDateFormat.format(c.getTime()));
		return objectName + ":" + filter;
	}
}


