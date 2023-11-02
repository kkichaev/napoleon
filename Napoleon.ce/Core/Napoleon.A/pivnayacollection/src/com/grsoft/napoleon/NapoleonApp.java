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
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.dataobjects.Inventory;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrgInv;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.napoleon.documents.DebtDocEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.InvAuditDoc;
import com.grsoft.napoleon.documents.TareDoc;
import com.grsoft.napoleon.modules.CostManagerImpl;
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
		
		DbObject.regNewDataType(Order.class, OrderEx.class);
		DbObject.regNewDataType(Delivery.class, DeliveryEx.class);
		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override
			public List<Hitching> createList() {
				Hitching[] h = new Hitching[] {
						new RcvNewHitching(OrgInv.class),
						new RcvNewHitching(Inventory.class),
				};
				return Arrays.asList(h);
			}}, 
		UpdateDB.GEN_DATA_HITCHING);	}
	
	@Override
	protected void initChildFeature() {
		Features.COST_MANAGER = new CostManagerImpl();
		Features.FOCUSED_ITEMS = true;
	}
	
	@Override
	protected void initChildDocTypes() {
		DocType.addType(InvAuditDoc.instance());
		DocType.addType(TareDoc.instance());
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
