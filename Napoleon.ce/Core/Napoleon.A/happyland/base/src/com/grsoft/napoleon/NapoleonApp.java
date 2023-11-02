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
import com.grsoft.database.TaskInfoHitching;
import com.grsoft.database.TaskInfoRestore;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.IncassDoc;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.napoleon.documents.SmartTaskEndDoc;
import com.grsoft.napoleon.documents.SmartTaskStartDoc;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.network.ServerCommand;
import com.grsoft.script.ScriptEdit;
import com.grsoft.util.DocFilterOnClickListener;
import com.grsoft.util.FirstRunInit;

import android.app.Application;
import android.content.Context;

public class NapoleonApp extends Application {
	static final String GLOBAL_PREFERENCES = "global_preferences";
	static final String ID_ORG_IN_WORK = "id_org_in_work";

	@SuppressWarnings("unused")
	private static final String TAG = "NapoleonApp";
	
	class OrderEditor implements OrderImpl.PropertiesEditor {
		@Override
		public void edit(Context ctx, OrderImpl order, boolean isOldOrder) {
			CreateOrder.open(ctx, order, isOldOrder);
		}
	}
	
	private void initDocTypes() {
		DocType.addType(OrderDoc.instance());
		DocType.addType(DebtDoc.instance());
		DocType.addType(VisitDoc.instance());
		DocType.addType(RemnantsDoc.instance());
		DocType.addType(IncassDoc.instance());
		DocType.addType(SmartTaskStartDoc.instance());
		DocType.addType(SmartTaskEndDoc.instance());
		
		DocType.setCurDoc(OrderDoc.instance());		

		Features.SCRIPT_DOC = true;
		Features.START_STOP = true;
		
		Warehouse.activity = WarehouseNew.class;
		Presentation.activity = PresentationFolder.class;
		PricePresentation.activity = PricePresentationFolder.class;
		ScriptEdit.activity = ScriptEditEx.class;
		
		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override
			public Hitching create() { return new TaskInfoHitching(); }
		}, UpdateDB.EXPORT_DATA_HITCHING);
		
		UpdateDB.addHitchingCtor(new HitchingCtor() {
			
			@Override
			public Hitching create() { return new TaskInfoRestore(); }
		}, UpdateDB.RESTORE_DATA_HITCHING);
		
		DocFilterOnClickListener.HiddenTypes.add(SmartTaskStartDoc.instance());
		DocFilterOnClickListener.HiddenTypes.add(SmartTaskEndDoc.instance());
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
//	
//	@Override
//	public String getInWork(){
//		SharedPreferences pref = getSharedPreferences(NapoleonApp.GLOBAL_PREFERENCES, Context.MODE_PRIVATE);
//		return pref.getString(NapoleonApp.ID_ORG_IN_WORK, "");
//	}
//	
//	@Override
//	public void putInWork(String inWork) {
//		SharedPreferences pref = getSharedPreferences(NapoleonApp.GLOBAL_PREFERENCES, Context.MODE_PRIVATE);
//		SharedPreferences.Editor ed = pref.edit();
//		ed.putString(NapoleonApp.ID_ORG_IN_WORK, inWork);
//		ed.commit();
//	}
//
}
