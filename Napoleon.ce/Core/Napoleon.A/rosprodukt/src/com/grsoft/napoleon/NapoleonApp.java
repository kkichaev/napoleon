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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.grsoft.database.DocumentRestore;
import com.grsoft.database.Hitching;
import com.grsoft.database.HitchingCtor;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.dataobjects.Incass;
import com.grsoft.dataobjects.IncassEx;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.Ret1c;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DebtDocEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.IncassDoc;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.network.RawObject;
import com.grsoft.network.ServerCommand;
import com.grsoft.network.exception.RuntimeException;
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
		DbObject.regNewDataType(Org.class, OrgEx.class);
		DbObject.regNewDataType(Order.class, OrderEx.class);
		DbObject.regNewDataType(Incass.class, IncassEx.class);
		DbObject.regNewDataType(Delivery.class, DeliveryEx.class);
		DbObject.regNewDataType(Price.class, PriceEx.class);
		
		DebtDocEx.init();
		
		DocType.addType(OrderDoc.instance(OrderImplEx.class));
		DocType.addType(IncassDoc.instance());
		DocType.addType(DebtDoc.instance());
		DocType.addType(VisitDoc.instance());
		DocType.addType(RemnantsDoc.instance());
		
		DocType.setCurDoc(OrderDoc.instance());		

		IncassEdit.activity = IncassEditEx.class;
		UpdateDB.activity = UpdateDBEx.class;
		OrderDetail.activity = OrderDetailEx.class;
		DocList.activity = DocListEx.class;
		Warehouse.activity = WarehouseEx.class;
		
		Features.LOAD_FULL_PRICE = true;
		
		UpdateDB.addHitchingCtor(new HitchingCtor(){
			@Override public Hitching create() { return new RcvNewHitching(Ret1c.class); }
		}, UpdateDB.DEBET_DATA_HITCHING);
		
//		UpdateDB.addHitchingCtor(new HitchingCtor() {
//			@Override public Hitching create() { return new IncassRestore(); }
//		}, UpdateDB.RESTORE_DATA_HITCHING);
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
//
//class IncassRestore extends DocumentRestore {
//	OrgImpl oi = new OrgImpl();
//	
//	public IncassRestore() {
//		super(IncassDoc.instance());
//	}
//	
//	@Override
//	public void onEnd() {
//		oi.close();
//		super.onEnd();
//	}
//	
//	@Override
//	protected void makeDocReceiveCondition(String timeField, int months, int days) {
//		Calendar calendar = Calendar.getInstance();
//		calendar.add(Calendar.MONTH, -months);
//		calendar.add(Calendar.DATE, -days);
//		Date begin = calendar.getTime();
//		
//		SimpleDateFormat simpleDateFormat =  new SimpleDateFormat("dd.MM.yyyy");
//		setCondition(String.format("\"%s\" >= ToDate('%s 00:00:00')",
//				timeField, simpleDateFormat.format(begin)));
//	}
//	
//	@Override
//	public void onRead(RawObject rawObject) throws RuntimeException {
//		IncassEx doc = (IncassEx) rawObject.createDataObject(dataObject);
//		Org o = oi.getData();
//		o.id = doc.id;
//		if(oi.read()) {
//			beforeWrite(doc);
//			dbProxy.insertRecord(doc);			
//		}
//	}
//}
