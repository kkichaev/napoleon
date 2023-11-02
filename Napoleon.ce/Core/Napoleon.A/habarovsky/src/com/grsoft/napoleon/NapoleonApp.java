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
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.dataobjects.Dover;
import com.grsoft.dataobjects.Incass;
import com.grsoft.dataobjects.IncassEx;
import com.grsoft.dataobjects.Return;
import com.grsoft.dataobjects.ReturnEx;
import com.grsoft.dataobjects.ReturnItemEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.IncassImplEx;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.ReturnImplEx;
import com.grsoft.napoleon.documents.DebtDocEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.IncassDoc;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.network.ServerCommand;
import com.grsoft.util.FirstRunInit;

public class NapoleonApp extends Application {
	@SuppressWarnings("unused")
	private static final String TAG = "NapoleonApp";
	
	class OrderEditor implements OrderImpl.PropertiesEditor {
		@Override
		public void edit(Context ctx, OrderImpl order, boolean isOldOrder) {
			CreateOrder.open(ctx, order, isOldOrder);
		}
	}
	
	private void initDocTypes() {
		DebtDocEx.initialize();
		
		DataObjectInfo.getInstance().replaceListType(Return.class, "items", ReturnItemEx.class);
		DbObject.regNewDataType(Return.class, ReturnEx.class);
		DbObject.regNewDataType(Incass.class, IncassEx.class);
		DbObject.regNewDataType(Delivery.class, DeliveryEx.class);
		
		VisitEdit.activity = VisitEditorEx.class;
		Documents.activity = DocumentsEx.class;
		OrderDetail.activity = OrderDetailEx.class;
		IncassEdit.activity = IncassEditEx.class;
		
		DocType.addType(OrderDoc.instance());
		DocType.addType(DebtDocEx.instance());
		DocType.addType(VisitDoc.instance());
		DocType.addType(RemnantsDoc.instance());
		DocType.addType(ReturnDoc.instance(ReturnImplEx.class));
		DocType.addType(IncassDoc.instance(IncassImplEx.class));
		
		DocType.setCurDoc(OrderDoc.instance());
		
		Warehouse.activity = WarehouseNew.class;
		
		Features.FOCUSED_ITEMS = true;
		Features.LAST_SALED_ITEMS_PERIOD = 1;
		
		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override
			public Hitching create() {	return new RcvNewHitching(Dover.class, "Dover"); }
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
