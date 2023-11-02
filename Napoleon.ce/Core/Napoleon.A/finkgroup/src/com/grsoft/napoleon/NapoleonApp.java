/*
 * Copyright (C), 2011, Гильдия Разработчиков
 * 
 * Демо программа (с базой данных)
 *
 * kki   16/05/2011   creating
 */

package com.grsoft.napoleon;

import android.app.Application;
import android.content.Context;

import com.grsoft.database.Hitching;
import com.grsoft.database.HitchingCtor;
import com.grsoft.database.RcvGather;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.RemnantItemEx;
import com.grsoft.dataobjects.Remnants;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.RemnantsImplEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.InvDoc;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.napoleon.documents.RemnantsDocEx;
import com.grsoft.network.ServerCommand;
import com.grsoft.util.FirstRunInit;

public class NapoleonApp extends Application {
	@SuppressWarnings("unused")
	private static final String TAG = "NapoleonApp";
	
	class OrderEditor implements OrderImpl.PropertiesEditor {
		@Override
		public void edit(Context ctx, OrderImpl order, boolean isOldOrder) {
		}
	}
	
	private void initDocTypes() {
		RemnantsDocEx.initialize(RemnantsImplEx.class);
		
		DocType.addType(RemnantsDoc.instance());
		DocType.addType(InvDoc.instance());
		
		DocType.setCurDoc(RemnantsDoc.instance());
		
		DataObjectInfo doi = DataObjectInfo.getInstance();
		doi.replaceListType(Remnants.class, "items", RemnantItemEx.class);
		
		ServerCommand.Category = "skaldwspda";
		RemnantsDetail.activity = RemnantsDetailEx.class;
		Setting.activity = SettingEx.class;
		Warehouse.activity = WarehouseNew.class;
		DocList.activity = DocListEx.class;
		
		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override public Hitching create() { return new RcvGather(); }
		}, UpdateDB.GEN_DATA_HITCHING);
	}
	
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
