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
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.TypeDistrib;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.Visit;
import com.grsoft.dataobjects.VisitItemEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.IncassDoc;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.ServerCommand;

import android.content.Context;

public class NapoleonApp extends NapoleonAppBase {
	
	class OrderEditor implements OrderImpl.PropertiesEditor {
		@Override
		public void edit(Context ctx, OrderImpl order, boolean isOldOrder) {
			CreateOrder.open(ctx, order, isOldOrder);
		}
	}
	
	@Override
	protected void defineNewType() {
		DbObject.regNewDataType(Org.class, OrgEx.class);
		DbObject.regNewDataType(Price.class, PriceEx.class);
		DataObjectInfo.getInstance().replaceListType(Order.class, "items", OrderItemEx.class);
		DataObjectInfo.getInstance().replaceListType(Visit.class, "items", VisitItemEx.class);
		
		UpdateDB.addHitchingCtor(new HitchingCtor(){
			public Hitching create() { return new Hitching(TypeDistrib.class);}}, UpdateDB.GEN_DATA_HITCHING);
	}
	
	@Override
	protected void initChildFeature() {
		Features.MAX_FOTO_HEIGHT = 5000;
		Features.MAX_FOTO_WIDTH = 5000;
		Features.SHOW_WEIGHT_IN_DOC_LIST = true;
		Features.SHOW_WEIGHT_IN_MAIN_FORM = true;
//		Features.INPUT_QTY_IN_PACK = true;
	}
	
	@Override
	protected void initChildDocTypes() {
		DocType.removeType(IncassDoc.instance()	);
	}
	
	@Override
	public void onCreate() {
		ConfigManager.initConfig(new CfgNpl());
		super.onCreate();
		OrderImpl.OrderEditor = new OrderEditor();
		setProgrammVersion();
		
		//NapoleonChat.init(this);
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
		VisitEdit.activity = VisitEditEx.class;
		PriceCount.activity = PriceCountEx.class;
	}
}
