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
import com.grsoft.dataobjects.CommonMatrix;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.OrgMtx;
import com.grsoft.dataobjects.Return;
import com.grsoft.dataobjects.ReturnItem;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.ReturnImplEx;
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
	public void onCreate() {
		ConfigManager.initConfig(new CfgNpl());
		super.onCreate();
		OrderImpl.OrderEditor = new OrderEditor();
		setProgrammVersion();
		
		UpdateDB.addHitchingCtor(new HitchingCtor() { 
			@Override public Hitching create() { return new RcvNewHitching(OrgMtx.class); }}, UpdateDB.GEN_DATA_HITCHING);
		UpdateDB.addHitchingCtor(new HitchingCtor() { 
			@Override public Hitching create() { return new RcvNewHitching(CommonMatrix.class); }}, UpdateDB.GEN_DATA_HITCHING);
	}
	
	@Override
	protected void defineNewType() {
		DataObjectInfo.getInstance().replaceListType(Return.class, "items", ReturnItem.class);
	}
	
	@Override
	protected void initDocTypes() {
		super.initDocTypes();
		DocType.addType(ReturnDoc.instance(ReturnImplEx.class));
	}
	
	@Override
	protected void initAcivity() {
		super.initAcivity();
		
		Warehouse.activity = WarehouseEx.class;
		CreateReturn.activity = CreateReturnEx.class;
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
		
		Features.LOAD_FULL_PRICE = true;
		Features.FOCUSED_ITEMS = true;
	}
	
}
