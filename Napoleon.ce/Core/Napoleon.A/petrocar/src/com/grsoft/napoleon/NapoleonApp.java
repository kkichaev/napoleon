/*
 * Copyright (C), 2011, Гильдия Разработчиков
 * 
 * Демо программа (с базой данных)
 *
 * kki   16/05/2011   creating
 */

package com.grsoft.napoleon;

import android.content.Context;

import java.util.Arrays;
import java.util.List;

import com.grsoft.database.Hitching;
import com.grsoft.database.HitchingCtor;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.AgentMonthlyPlans;
import com.grsoft.dataobjects.AgentPlan;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Return;
import com.grsoft.dataobjects.ReturnItemEx;
import com.grsoft.dataobjects.impl.BalanceDeliveryImplex;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.ReturnImplEx;
import com.grsoft.napoleon.documents.DebtDocEx;
import com.grsoft.napoleon.documents.DebtDocList;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.ServerCommand;

public class NapoleonApp extends NapoleonAppBase {
	
	class OrderEditor implements OrderImpl.PropertiesEditor {
		@Override
		public void edit(Context ctx, OrderImpl order, boolean isOldOrder) {
			CreateOrder.open(ctx, order, isOldOrder);
		}
	}
	
	@Override
	protected void defineNewType() {
		DebtDocEx.initialize();
		
		DebtDocList.DeliveryType = BalanceDeliveryImplex.class;
		
		DbObject.regNewDataType(Org.class, OrgEx.class);
		
		DataObjectInfo.getInstance().replaceListType(Return.class, "items", ReturnItemEx.class);
		DataObjectInfo.getInstance().replaceListType(Order.class, "items", OrderItemEx.class);
		
		CostStrategy.defaultInstance = new CostStrategyEx();
		
		UpdateDB.addHitchingCtor(new HitchingCtor(){
			@Override
			public List<Hitching> createList() {
				CostStrategyEx.clear();
				Hitching[] h = new Hitching[] {
						new RcvNewHitching(AgentMonthlyPlans.class),
						new RcvNewHitching(AgentPlan.class),
				};
				return Arrays.asList(h);
			}
		}, UpdateDB.GEN_DATA_HITCHING);
	}
	
	@Override
	protected void initChildDocTypes() {
		DocType.addType(ReturnDoc.instance(ReturnImplEx.class));
	}
	
	@Override
	protected void initChildActivity() {
		PriceCount.activity = PriceCountEx.class;
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
