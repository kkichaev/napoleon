/*
 * Copyright (C), 2011, Гильдия Разработчиков
 * 
 * Демо программа (с базой данных)
 *
 * kki   16/05/2011   creating
 */

package com.grsoft.napoleon;

import com.grsoft.database.DocumentRestore;
import com.grsoft.database.Hitching;
import com.grsoft.database.HitchingCtor;
import com.grsoft.database.PriceHitchingEx;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.Action;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Payment;
import com.grsoft.dataobjects.PaymentEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.WHouses;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.napoleon.documents.BonusDoc;
import com.grsoft.napoleon.documents.ClaimDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.OrderDocEx;
import com.grsoft.network.ServerCommand;
import com.grsoft.script.dataobjects.impl.ScriptDefImpl;

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
		OrderDocEx.init();

		DbObject.regNewDataType(Org.class, OrgEx.class);
		DbObject.regNewDataType(Order.class, OrderEx.class);
		DbObject.regNewDataType(Price.class, PriceEx.class);
		DbObject.regNewDataType(Delivery.class, DeliveryEx.class);
		DbObject.regNewDataType(Payment.class, PaymentEx.class);
		
		DataObjectInfo doi = DataObjectInfo.getInstance();
		doi.replaceListType(OrderEx.class, "items", OrderItemEx.class);
		doi.replacePrimaryKey(PaymentEx.class, "id,payType,number");
		doi.replacePrimaryKey(DeliveryEx.class, "id,payType,number");

		UpdateDB.priceHitchingClass = PriceHitchingEx.class;
		UpdateDB.addHitchingCtor(new HitchingCtor() { 
			@Override public Hitching create() { return new Hitching(WHouses.class); }
		}, UpdateDB.GEN_DATA_HITCHING);
		
		UpdateDB.addHitchingCtor(new HitchingCtor() { 
			@Override public Hitching create() { return new RcvNewHitching(Action.class); }
		}, UpdateDB.GEN_DATA_HITCHING);
		
		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override
			public Hitching create() {
				return new DocumentRestore(BonusDoc.instance());
			}
		}, UpdateDB.RESTORE_DATA_HITCHING);
	}
	
	@Override
	protected void initChildDocTypes() {
		super.initChildDocTypes();
		
		DocType.addType(BonusDoc.instance());
		DocType.addType(ClaimDoc.instance());
		ScriptDefImpl.docInScript.add(ClaimDoc.instance());
	}
	
	@Override protected Class<? extends OrderImplBase<? extends Order>> orderImplType() { return OrderImplEx.class; }
	
	@Override
	protected void initChildFeature() {
		Features.INPUT_QTY_IN_PACK = true;
		Features.RECEIVE_REMNANTS_WHEN_SENDING = true;
		Features.LOAD_FULL_PRICE = true;
		Features.SYNC_INFO = true;
		Features.DDLV = true;
	}
	
	@Override
	protected void initChildActivity() {
		DocList.activity = DocListEx.class;
		PriceCount.activity = PriceCountEx.class;
		OrderDetail.activity = OrderDetailEx.class;
		Documents.activity = DocumentsEx.class;
		UpdateDB.activity = UpdateDBEx.class;
		Warehouse.activity = WarehosueEx.class;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		OrderImpl.OrderEditor = new OrderEditor();
		try{
			ServerCommand.ProgramVersion = getResources().getString(R.string.version);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
