/*
 * Copyright (C), 2011, Гильдия Разработчиков
 * 
 * Демо программа (с базой данных)
 *
 * kki   16/05/2011   creating
 */

package com.grsoft.napoleon;

import android.content.Context;
import com.grsoft.database.Hitching;
import com.grsoft.database.HitchingCtor;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgInv;
import com.grsoft.dataobjects.RemnantItemEx;
import com.grsoft.dataobjects.Remnants;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.RemnantsImpl;
import com.grsoft.dataobjects.impl.RemnantsImplEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.InvAuditDoc;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.napoleon.modules.CostManagerImpl;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.ReadService;
import com.grsoft.network.ServerCommand;
import com.grsoft.network.WriteService;

public class NapoleonApp extends NapoleonAppBase {
	
	class OrderEditor implements OrderImpl.PropertiesEditor {
		@Override
		public void edit(Context ctx, OrderImpl order, boolean isOldOrder) {
			CreateOrder.open(ctx, order, isOldOrder);
		}
	}
	
	@Override
	protected void initDocTypes() {
		super.initDocTypes();
		DocType.addType(ReturnDoc.instance());

		CostStrategy.defaultInstance = new CostStrategyEx();
		Features.COST_MANAGER = new CostManagerImpl();
	}

	@Override
	public void onCreate() {
		ConfigManager.initConfig(new CfgNpl());
		DataObjectInfo.getInstance().replaceListType(Remnants.class, "items", RemnantItemEx.class);
		super.onCreate();
		OrderImpl.OrderEditor = new OrderEditor();
		setProgrammVersion();
		
		Hitching osh = new OrgStopHitching();
		WriteService.recievers.add(osh);
		ReadService.recievers.add(osh);
		
		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override public Hitching create() { return new RcvNewHitching(OrgInv.class);} }, UpdateDB.GEN_DATA_HITCHING);
				
	}

	private void setProgrammVersion() {
		try{
			ServerCommand.ProgramVersion = getResources().getString(R.string.version);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Override
	protected Class<? extends RemnantsImpl> remantsImplType() { return RemnantsImplEx.class; }
	
	@Override
	protected void initChildFeature() {
		super.initChildFeature();
		
		Features.LOAD_FULL_PRICE = true;
		Features.START_STOP = true;
	}
	
	@Override
	protected void initChildActivity() {
		super.initChildActivity();
		
		PriceCount.activity = PriceCountEx.class;
		Documents.activity = DocumentsEx.class;
		RemnantsDetail.activity = RemnantsDetailEx.class;
		Warehouse.activity = WarehouseEx.class;
	}
	
	@Override
	protected void defineNewType() {
		super.defineNewType();
		
		DbObject.regNewDataType(Org.class, OrgEx.class);
	}
	
	@Override
	protected void initChildDocTypes() {
		super.initChildDocTypes();
		DocType.addType(InvAuditDoc.instance());
	}
}
