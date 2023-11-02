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
import com.grsoft.dataobjects.MonItem;
import com.grsoft.dataobjects.MonOrg;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.PriceOrgMonDoc;
import com.grsoft.network.ServerCommand;
import com.grsoft.util.FirstRunInit;

public class NapoleonApp extends NapoleonAppBase {
	@SuppressWarnings("unused")
	private static final String TAG = "NapoleonApp";
	
	class OrderEditor implements OrderImpl.PropertiesEditor {
		@Override
		public void edit(Context ctx, OrderImpl order, boolean isOldOrder) {
			CreateOrder.open(ctx, order, isOldOrder);
		}
	}
	
	@Override
	protected void defineNewType() {
		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override public Hitching create() { return new RcvNewHitching(MonOrg.class, "PriceMonOrgs"); }
		}, UpdateDB.GEN_DATA_HITCHING);
		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override public Hitching create() { return new RcvNewHitching(MonItem.class, "PriceMonItems"); }
		}, UpdateDB.GEN_DATA_HITCHING);
	}
	
	@Override
	protected void initChildDocTypes() {
		DocType.addType(PriceOrgMonDoc.instance());
	}
	
	@Override
	protected void initChildActivity() {
		Documents.activity = DocumentsEx.class;
	}
	
//	private void initDocTypes() {
//		DocType.addType(OrderDoc.instance());
//		DocType.addType(DebtDoc.instance());
//		DocType.addType(VisitDoc.instance());
//		DocType.addType(PriceOrgMonDoc.instance());
//		DocType.addType(RemnantsDoc.instance());
//		
//		DocType.setCurDoc(OrderDoc.instance());
//		
//		Warehouse.activity = WarehouseNew.class;
//		Documents.activity = DocumentsEx.class;
//		
//		UpdateDB.addHitchingCtor(new HitchingCtor() {
//			@Override public Hitching create() { return new RcvNewHitching(MonOrg.class, "PriceMonOrgs"); }
//		}, UpdateDB.GEN_DATA_HITCHING);
//		UpdateDB.addHitchingCtor(new HitchingCtor() {
//			@Override public Hitching create() { return new RcvNewHitching(MonItem.class, "PriceMonItems"); }
//		}, UpdateDB.GEN_DATA_HITCHING);
//	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		FirstRunInit.init(this);

		initDocTypes();
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
