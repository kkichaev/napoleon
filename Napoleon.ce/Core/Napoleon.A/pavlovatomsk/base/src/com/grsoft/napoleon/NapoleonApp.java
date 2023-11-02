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
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.Bonus;
import com.grsoft.dataobjects.BonusDef;
import com.grsoft.dataobjects.BonusItem;
import com.grsoft.dataobjects.ConfigHelper;
import com.grsoft.dataobjects.ConfigHelper.DlvDateType;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Incass;
import com.grsoft.dataobjects.IncassEx;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.Return;
import com.grsoft.dataobjects.ReturnCause;
import com.grsoft.dataobjects.ReturnItem;
import com.grsoft.dataobjects.Sklad;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.ReturnImplEx;
import com.grsoft.dataobjects.impl.ScriptImplEx;
import com.grsoft.napoleon.documents.BonusDoc;
import com.grsoft.napoleon.documents.DebtDocEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.IncassDocEx;
import com.grsoft.napoleon.documents.OrderDocEx;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.ServerCommand;
import com.grsoft.script.ScriptEdit;
import com.grsoft.script.dataobjects.impl.ScriptImpl;

import android.content.Context;
import android.os.AsyncTask;

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
		IncassDocEx.initialize();
		OrderDocEx.initialize();
		
		DbObject.regNewDataType(Org.class, OrgEx.class);
		DbObject.regNewDataType(Price.class, PriceEx.class);
		DbObject.regNewDataType(Incass.class, IncassEx.class);
		DbObject.regNewDataType(Order.class, OrderEx.class);
		DataObjectInfo.getInstance().replaceListType(Bonus.class, "items", BonusItem.class);
		DocType.addType(ReturnDoc.instance(ReturnImplEx.class));
		
		DataObjectInfo doi = DataObjectInfo.getInstance(); 
		doi.replaceListType(Return.class, "items", ReturnItem.class);
		
		UpdateDB.addHitchingCtor(new HitchingCtor(){
			@Override public Hitching create() { return new RcvNewHitching(BonusDef.class, "ActionDef"); }
		}, UpdateDB.GEN_DATA_HITCHING);
		
		UpdateDB.addHitchingCtor(new HitchingCtor(){
			@Override public Hitching create() { return new RcvNewHitching(Sklad.class); }
		}, UpdateDB.GEN_DATA_HITCHING);
		
		UpdateDB.addHitchingCtor(new HitchingCtor(){
			@Override public Hitching create() {return new Hitching( ReturnCause.class);}
		},UpdateDB.GEN_DATA_HITCHING);
		
		UpdateDB.addHitchingCtor(new HitchingCtor(){
			@Override public Hitching create() {return new DecisionHitching();}
		},UpdateDB.GEN_DATA_HITCHING);
	}
	
	@Override
	protected void initChildDocTypes() {
		DocType.addType(BonusDoc.instance());
	}
	
	@Override
	protected void initChildActivity() {
		Warehouse.activity = WarehouseEx.class;
		OrderDetail.activity = OrderDetailEx.class;
		PriceCount.activity = PriceCountEx.class;
		Documents.activity = DocumentsEx.class;
		IncassEdit.activity = IncassEditEx.class;
		ScriptEdit.activity = ScriptEditEx.class;
		DocList.activity = DocListEx.class;
		UpdateDB.activity = UpdateDBEx.class;
	}
	
	@Override
	protected void initChildFeature() {
		Features.CHECK_UNCOMPLETE_SCRIPTS = true;
		Features.DEL_VISIT_WITHOUT_PHOTO = true;
		ConfigHelper.DEFAULT_DATE_TYPE = DlvDateType.nextday;
		//Features.START_STOP = true;
		Features.FOCUSED_GROUP = true;
		Features.FOCUSED_ITEMS = true;
		Features.START_VISIT_OPEN_CAMERA = true;
		Features.REMOVE_EMPTY_ORDERS = true;
		Features.EXCLUDE_RETURN_DOC_SUM_FROM_SCRIPT = true;
	}
	
	@Override
	public void onCreate() {
		ConfigManager.initConfig(new CfgNpl());
		super.onCreate();
		OrderImpl.OrderEditor = new OrderEditor();
		setProgrammVersion();
		
		new AsyncTask<Void, Void, Void>(){

			@Override
			protected Void doInBackground(Void... params) {
				PriceCash.load();
				return null;
			}}.execute((Void[])null);
		
		//NapoleonChat.init(this);
	}

	private void setProgrammVersion() {
		try{
			ServerCommand.ProgramVersion = getResources().getString(R.string.version);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Override protected Class<? extends ScriptImpl> scriptImplType() { return ScriptImplEx.class;	}
}
