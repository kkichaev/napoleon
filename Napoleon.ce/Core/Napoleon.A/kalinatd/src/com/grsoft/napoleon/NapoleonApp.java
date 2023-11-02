/*
 * Copyright (C), 2011, Гильдия Разработчиков
 * 
 * Демо программа (с базой данных)
 *
 * kki   16/05/2011   creating
 */

package com.grsoft.napoleon;

import com.grsoft.database.Hitching;
import com.grsoft.database.HitchingCtor;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.MonitoringItem;
import com.grsoft.dataobjects.MonitoringItemEx;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.OrderRemark;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.MonitoringDoc;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.CfgNplEx;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.ServerCommand;
import com.grsoft.script.ScriptEdit;
import com.grsoft.util.Consts;

import android.content.Context;

public class NapoleonApp extends NapoleonAppBase {
	
	class OrderEditor implements OrderImpl.PropertiesEditor {
		@Override
		public void edit(Context ctx, OrderImpl order, boolean isOldOrder) {
			CreateOrder.open(ctx, order, isOldOrder);
		}
	}
	
	@Override
	public void onCreate() {
		ConfigManager.initConfig(new CfgNplEx());
		CostStrategy.defaultInstance = new CostStrategyEx();
		
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
	
	@Override
	protected void initChildFeature() {
		super.initChildFeature();
		
		Features.DEL_VISIT_WITHOUT_PHOTO = true;
		Features.CHECK_UNCOMPLETE_SCRIPTS = true;
		Features.START_VISIT_OPEN_CAMERA = true;
		Features.WEIGHT_SCALE = Consts.WEIGHT_SCALE;
		Features.LOAD_FULL_PRICE = true;
	}
	
	@Override
	protected void defineNewType() {
		super.defineNewType();
		
		DataObjectInfo.getInstance().replaceListType(Order.class, "items", OrderItemEx.class);

		DbObject.regNewDataType(Org.class, OrgEx.class);
		DbObject.regNewDataType(Order.class, OrderEx.class);
		DbObject.regNewDataType(Org.class, OrgEx.class);
		DbObject.regNewDataType(Price.class, PriceEx.class);
		DbObject.regNewDataType(MonitoringItem.class, MonitoringItemEx.class);
	}
	
	@Override
	protected void initChildActivity() {
		super.initChildActivity();
		
		OrderDetail.activity = OrderDetailEx.class;
		ScriptEdit.activity = ScriptEditEx.class;
		PriceCount.activity = PriceCountEx.class;
		Warehouse.activity = WarehouseEx.class;
		MonitoringEdit.activity = MonitoringEditEx.class;
		Setting.WarehouseSettingActivity = WarehouseSettingEx.class;
		
		UpdateDB.addHitchingCtor(new HitchingCtor(){@Override public Hitching create() { return new RcvNewHitching(OrderRemark.class); }}, UpdateDB.GEN_DATA_HITCHING);
		
	}
	
	@Override
	protected void initChildDocTypes() {
		super.initChildDocTypes();
		DocType.addType(MonitoringDoc.instance());
	}
}
