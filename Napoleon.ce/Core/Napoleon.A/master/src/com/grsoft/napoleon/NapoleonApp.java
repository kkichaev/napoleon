/*
 * Copyright (C), 2011, Гильдия Разработчиков
 * 
 * Демо программа (с базой данных)
 *
 * kki   16/05/2011   creating
 */

package com.grsoft.napoleon;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Arrays;
import java.util.List;

import com.grsoft.database.Hitching;
import com.grsoft.database.HitchingCtor;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgAction;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.MonitoringDoc;
import com.grsoft.napoleon.modules.MonitoringInit;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.RWServiceFactory;
import com.grsoft.network.RWServiceFactoryEx;
import com.grsoft.network.ServerCommand;

public class NapoleonApp extends NapoleonAppBase {
	@SuppressWarnings("unused")
	private static final String TAG = "NapoleonApp";
	public static final String GLOBAL_PREFERENCES = "global_preferences";
	public static final String ID_ORG_IN_WORK = "id_org_in_work";
	
	class OrderEditor implements OrderImpl.PropertiesEditor {
		@Override
		public void edit(Context ctx, OrderImpl order, boolean isOldOrder) {
			CreateOrder.open(ctx, order, isOldOrder);
		}
	}
	
	@Override
	protected void defineNewType() {
		MonitoringInit.init();
		RWServiceFactory.instance = new RWServiceFactoryEx();
		DbObject.regNewDataType(Org.class, OrgEx.class);
		
		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override
			public List<Hitching> createList() {
				CostStrategyEx.clearCach();
				
				Hitching[] list = new Hitching[] {
					new RcvNewHitching(OrgAction.class, "OrgActions"),	
				};
				return Arrays.asList(list);
			}
		}, UpdateDB.GEN_DATA_HITCHING);
		
		CostStrategy.defaultInstance = new CostStrategyEx(); 		 
	}
	
	@Override
	protected void initChildDocTypes() {
		DocType.addType(MonitoringDoc.instance());
	}
	
	@Override
	protected void initChildActivity() {
		UpdateDB.activity = UpdateDBEx.class;
//		Documents.activity = DocumentsEx.class;
		Warehouse.activity = WarehouseEx.class;
	}
	
	@Override
	protected void initChildFeature() {
		Features.MAX_FOTO_WIDTH = Integer.MAX_VALUE;
		Features.MAX_FOTO_HEIGHT = Integer.MAX_VALUE;
		Features.START_STOP = true;
	}
	
	@Override
	public void onCreate() {
		ConfigManager.initConfig(new CfgNpl());
		super.onCreate();

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
	
	public String getInWork(){
		SharedPreferences pref = getSharedPreferences(NapoleonApp.GLOBAL_PREFERENCES, Context.MODE_PRIVATE);
		return pref.getString(NapoleonApp.ID_ORG_IN_WORK, "");
	}
}
