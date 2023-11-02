/*
 * Copyright (C), 2011, Гильдия Разработчиков
 * 
 * Демо программа (с базой данных)
 *
 * kki   16/05/2011   creating
 */

package com.grsoft.napoleon;

import android.content.Context;

import com.grsoft.database.Hitching;
import com.grsoft.database.HitchingCtor;
import com.grsoft.database.LoadedOrdersHitching;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Present;
import com.grsoft.dataobjects.PresentEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.Return;
import com.grsoft.dataobjects.ReturnItemEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.ReturnImplEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.network.ServerCommand;

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
		DbObject.regNewDataType(Price.class, PriceEx.class);
		DbObject.regNewDataType(Order.class, OrderEx.class);
		DbObject.regNewDataType(Present.class, PresentEx.class);
		
		DataObjectInfo.getInstance().replaceListType(Return.class, "items", ReturnItemEx.class);
		DataObjectInfo.getInstance().replaceListType(OrderEx.class, "items", OrderItemEx.class);
		CostStrategy.defaultInstance = new CostStrategyEx();

		UpdateDB.addHitchingCtor(new HitchingCtor(){
			@Override
			public Hitching create() {
				return new LoadedOrdersHitching();
			}
		}, UpdateDB.DEBET_DATA_HITCHING);
	}
	
	@Override
	protected void initChildDocTypes() {
		DocType.addType(ReturnDoc.instance(ReturnImplEx.class));
	}

	@Override
	protected void initChildFeature() {
		Features.ID_COLUMN_IN_PRICE_LIST = true;
		//Features.USE_COST_IN_RETURNS = true;
		
		Features.MAX_FOTO_WIDTH = 5000;
		Features.MAX_FOTO_HEIGHT = 5000;
	}
	
	@Override
	protected void initChildActivity() {
		UpdateDB.activity = UpdateDBEx.class;
		PriceCount.activity = PriceCountEx.class;
		Warehouse.activity = WarehouseEx.class;
		Presentation.activity = PresentationFoldreEx.class;
		Documents.activity = DocumentsEx.class;
		DocList.activity = DocListEx.class;
		OrderDetail.activity = OrderDetailEx.class;
	}
	
	@Override
	protected Class<? extends OrderImplBase<? extends Order>> orderImplType() { return OrderImplEx.class; }
	
	@Override
	public void onCreate() {
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
}
