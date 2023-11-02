/*
 * Copyright (C), 2011, Гильдия Разработчиков
 * 
 * Демо программа (с базой данных)
 *
 * kki   16/05/2011   creating
 */

package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.Hitching;
import com.grsoft.database.HitchingCtor;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.DMPType;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgLocation;
import com.grsoft.dataobjects.OrgMatrix;
import com.grsoft.dataobjects.RejectCause;
import com.grsoft.dataobjects.Visit;
import com.grsoft.dataobjects.VisitItemEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.ScriptImplEx;
import com.grsoft.napoleon.documents.DMPDoc;
import com.grsoft.napoleon.documents.DistribDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.ServerCommand;
import com.grsoft.script.dataobjects.impl.ScriptImpl;

import android.content.Context;

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
		DataObjectInfo.getInstance().replaceListType(Visit.class, "items", VisitItemEx.class);
	}

	@Override
	public void onCreate() {
		ConfigManager.initConfig(new CfgNpl());
		super.onCreate();
		OrderImpl.OrderEditor = new OrderEditor();
		setProgrammVersion();
		
		//NapoleonChat.init(this);
		
		UpdateDB.addHitchingCtor(new HitchingCtor() {
		@Override
		public List<Hitching> createList() {
			List<Hitching> result = new ArrayList<Hitching>();
			result.add(new RcvNewHitching(OrgMatrix.class));
			result.add(new RcvNewHitching(RejectCause.class));
			result.add(new RcvNewHitching(OrgLocation.class));
			result.add(new RcvNewHitching(DMPType.class));
			return result;
		}}, UpdateDB.GEN_DATA_HITCHING);
	}

	@Override
	protected Class<? extends ScriptImpl> scriptImplType() {
		return ScriptImplEx.class;
	}
	
	private void setProgrammVersion() {
		try{
			ServerCommand.ProgramVersion = getResources().getString(R.string.version);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	
	@Override
	protected void initChildDocTypes() {
		DocType.addType(DistribDoc.instance());
		DocType.addType(DMPDoc.instance());
		DbObject.regNewDataType(Org.class, OrgEx.class);
	}
	
	@Override
	protected void initChildActivity() {
		UpdateDB.activity = UpdateDBEx.class;
	}
	
	@Override
	protected void initChildFeature() {
		Features.MAX_FOTO_HEIGHT = 5000;
		Features.MAX_FOTO_WIDTH = 5000;
		Features.UPDATE_DB_CHECK_VISITS = true;
		Features.LOAD_FULL_PRICE = true;
	}
}
