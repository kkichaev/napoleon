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

import com.grsoft.database.CityExport;
import com.grsoft.database.Hitching;
import com.grsoft.database.HitchingCtor;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.City;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgDistrib;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgType;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.OrgDistribItem;
import com.grsoft.dataobjects.TypeTP;
import com.grsoft.dataobjects.VisitType;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.napoleon.documents.DistribDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.QuestionDoc;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.ServerCommand;
import com.grsoft.script.documents.ScriptDoc;

import android.content.Context;

public class NapoleonApp extends NapoleonAppBase {
	
	class OrderEditor implements OrderImpl.PropertiesEditor {
		@Override
		public void edit(Context ctx, OrderImpl order, boolean isOldOrder) {
			CreateOrder.open(ctx, order, isOldOrder);
		}
	}
	
	protected void initDocTypes(){
		initFeatures();

		DocType.addType(VisitDoc.instance());
		DocType.addType(QuestionDoc.instance());
		DocType.addType(ScriptDoc.instance(scriptImplType()));
		
		initChildDocTypes();
	
		setDefDocType();

		initAcivity();
		ServerCommand.Category = "btl";
	}
	
	@Override
	public void onCreate() {
		ConfigManager.initConfig(new CfgNpl());
		super.onCreate();
		OrderImpl.OrderEditor = new OrderEditor();
		setProgrammVersion();
		
		UpdateDB.addHitchingCtor(new HitchingCtor() {
			
			@Override
			public List<Hitching> createList() {
				List<Hitching> result = new ArrayList<Hitching>();
				result.add(new RcvNewHitching(City.class));
				result.add(new RcvNewHitching(OrgType.class));
				result.add(new RcvNewHitching(VisitType.class));
				result.add(new RcvNewHitching(TypeTP.class));
				return result;
			}
		}, UpdateDB.GEN_DATA_HITCHING);
		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override
			public Hitching create() {
				return new CityExport();
			}
		}, UpdateDB.EXPORT_DATA_HITCHING);
	}
	
	@Override
	protected void defineNewType() {
		super.defineNewType();
		
		DocType.addType(DistribDoc.instance());
		DbObject.regNewDataType(Org.class, OrgEx.class);
		DbObject.regNewDataType(Price.class, PriceEx.class);
		
		DataObjectInfo.getInstance().replaceListType(OrgDistrib.class, "items", OrgDistribItem.class);
	}
	
	public void setDefDocType() {
		DocType.setCurDoc(DistribDoc.instance());		
	}
	
	private void setProgrammVersion() {
		try{
			ServerCommand.ProgramVersion = getResources().getString(R.string.version);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Override
	protected void initChildActivity() {
		super.initChildActivity();
		
		Warehouse.activity = WarehouseEx.class;
	}
	@Override
	protected void initChildFeature() {
		super.initChildFeature();
	}
}
