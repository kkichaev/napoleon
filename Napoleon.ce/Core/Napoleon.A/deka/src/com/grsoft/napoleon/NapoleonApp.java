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
import com.grsoft.dataobjects.Distributor;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.Return;
import com.grsoft.dataobjects.ReturnEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.ServerCommand;
import com.grsoft.util.ViewInitializer;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.CheckBox;

public class NapoleonApp extends NapoleonAppBase {
	
	class OrderEditor implements OrderImpl.PropertiesEditor {
		@Override
		public void edit(Context ctx, OrderImpl order, boolean isOldOrder) {
			CreateOrder.open(ctx, order, isOldOrder);
		}
	}
	
	@Override
	protected void defineNewType() {
		
		UpdateDB.addHitchingCtor(new HitchingCtor(){
			@Override public Hitching create() { return new RcvNewHitching(Distributor.class); }
		}, UpdateDB.GEN_DATA_HITCHING);
		
		DbObject.regNewDataType(Order.class, OrderEx.class);
		DbObject.regNewDataType(Return.class, ReturnEx.class);
		DbObject.regNewDataType(Price.class, PriceEx.class);
	}
	
	@Override
	protected void initChildDocTypes() {
		DocType.addType(ReturnDoc.instance());
	}
	
	@Override
	protected void initChildActivity() {
		CreateReturn.activity = CreateReturnEx.class;	
		PriceCount.activity = PriceCountEx.class;
		
		UpdateDB.initUI = new ViewInitializer(){
			@Override
			public void init(Activity activity) {
				super.init(activity);
				
				CheckBox cb = (CheckBox) activity.findViewById(R.id.cbRemains);
				cb.setChecked(true);
				cb.setVisibility(View.GONE);
			}
		};
	}
	
	@Override
	protected void initChildFeature() {
		Features.LOAD_FULL_PRICE = true;
//		Features.INPUT_QTY_IN_PACK = true;
		Features.USE_COST_IN_RETURNS = true;
		Features.CAN_CHANGE_COST = true;
		Features.START_VISIT_OPEN_CAMERA = true;
		Features.INPUT_QTY_IN_PACK = true;
		Features.FOCUSED_ITEMS = true;
		Features.CANT_CHANGE_SEND_FLAG = true;
	}
	
	@Override
	public void onCreate() {
		CostStrategy.defaultInstance = new CostStrategyEx();
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
	
	@Override protected Class<? extends OrderImplBase<? extends Order>> orderImplType() { return OrderImplEx.class; }
}
