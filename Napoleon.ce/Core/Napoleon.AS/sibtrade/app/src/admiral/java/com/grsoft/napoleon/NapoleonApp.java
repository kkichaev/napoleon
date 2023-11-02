/*
 * Copyright (C), 2011, Гильдия Разработчиков
 * 
 * Демо программа (с базой данных)
 *
 * kki   16/05/2011   creating
 */

package com.grsoft.napoleon;

import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.Org2Ex;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.Return;
import com.grsoft.dataobjects.ReturnEx;
import com.grsoft.dataobjects.ReturnItemEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.ReturnImplEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.ReturnDoc;

import android.content.Context;

public class NapoleonApp extends NapoleonAppBaseSBTR {
	@SuppressWarnings("unused")
	private static final String TAG = "NapoleonApp";

	class OrderEditorEx implements OrderImpl.PropertiesEditor {
		@Override
		public void edit(Context ctx, OrderImpl order, boolean isOldOrder) {
			CreateOrderEx.open(ctx, order, isOldOrder);
		}
	}
	
	@Override
	protected void initChildActivity() {
		super.initChildActivity();
		Warehouse.activity = WarehouseEx.class;
		Documents.activity = Documents2Ex.class;
	}
	
	@Override
	protected void initChildFeature() {
		super.initChildFeature();
		
		Features.LOAD_FULL_PRICE = true;
		Features.WH_QTY = true;
	}
	
	@Override
	protected void defineNewType() {
		DataObjectInfo.getInstance().replaceListType(Order.class, "items", OrderItemEx.class);
		DocType.addType(ReturnDoc.instance(ReturnImplEx.class));
		DataObjectInfo.getInstance().replaceListType(Return.class, "items", ReturnItemEx.class);

		super.defineNewType();

		DbObject.regNewDataType(Org.class, Org2Ex.class);
		DbObject.regNewDataType(Return.class, ReturnEx.class);
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		OrderImpl.OrderEditor = new OrderEditorEx();
	}

}
