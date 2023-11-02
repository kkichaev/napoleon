/*
 * Copyright (C), 2011, Гильдия Разработчиков
 * 
 * Демо программа (с базой данных)
 *
 * kki   16/05/2011   creating
 */

package com.grsoft.napoleon;

import android.app.Application;

import com.grsoft.database.DocumentRestore;
import com.grsoft.database.Hitching;
import com.grsoft.database.HitchingCtor;
import com.grsoft.napoleon.documents.CellsAuditDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.IncassDoc;
import com.grsoft.napoleon.documents.RestockDoc;
import com.grsoft.napoleon.documents.VandReloadDoc;
import com.grsoft.napoleon.documents.VandSellDoc;
import com.grsoft.network.ServerCommand;
import com.grsoft.util.DocFilterOnClickListener;
import com.grsoft.util.FirstRunInit;

public class NapoleonApp extends Application {
	@SuppressWarnings("unused")
	private static final String TAG = "NapoleonApp";
	
	private void initDocTypes() {
		DocType.addType(IncassDoc.instance());
		DocType.addType(VandSellDoc.instance());
		DocType.addType(CellsAuditDoc.instance());
		DocType.addType(VandReloadDoc.instance());
		DocType.addType(RestockDoc.instance());
//		DocType.addType(RemnantsDoc.instance());
		
		DocType.setCurDoc(VandSellDoc.instance());		

		Warehouse.activity = WarehouseNew.class;
		Presentation.activity = PresentationFolder.class;
		PricePresentation.activity = PricePresentationFolder.class;
		
		DocFilterOnClickListener.HiddenTypes.add(RestockDoc.instance());
		
		Features.POTENZIAL_ORG = false;

		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override public Hitching create() { return new DocumentRestore(IncassDoc.instance()); }
		}, UpdateDB.RESTORE_DATA_HITCHING);
		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override public Hitching create() { return new DocumentRestore(VandSellDoc.instance()); }
		}, UpdateDB.RESTORE_DATA_HITCHING);
		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override public Hitching create() { return new DocumentRestore(CellsAuditDoc.instance()); }
		}, UpdateDB.RESTORE_DATA_HITCHING);
		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override public Hitching create() { return new DocumentRestore(VandReloadDoc.instance()); }
		}, UpdateDB.RESTORE_DATA_HITCHING);
		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override public Hitching create() { return new DocumentRestore(RestockDoc.instance()); }
		}, UpdateDB.RESTORE_DATA_HITCHING);
	}
	
	@Override
	public void onCreate() {
		ServerCommand.Category = "vend";
		
		super.onCreate();
		FirstRunInit.init(this);

		initDocTypes();
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
