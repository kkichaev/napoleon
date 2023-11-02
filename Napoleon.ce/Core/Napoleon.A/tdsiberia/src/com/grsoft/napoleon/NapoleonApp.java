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
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.dataobjects.MatrixOrder;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.DeliveryImplEx;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.napoleon.documents.DebtDocEx;
import com.grsoft.napoleon.documents.DebtDocList;
import com.grsoft.napoleon.documents.OrderDocEx;
import com.grsoft.network.ServerCommand;
import com.grsoft.util.Consts;
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
		super.defineNewType();
		DebtDocList.DeliveryType = DeliveryImplEx.class;
		DebtDocEx.initialize();
		OrderDocEx.initialize();
		
		DbObject.regNewDataType(Delivery.class, DeliveryEx.class);
		DbObject.regNewDataType(Order.class, OrderEx.class);
		
		DataObjectInfo doi = DataObjectInfo.getInstance();
		doi.replaceListType(OrderEx.class, "items", OrderItemEx.class);
	}
	
	@Override
	protected void initChildFeature() {
		super.initChildFeature();
		
		Features.WEIGHT_SCALE = Consts.WEIGHT_SCALE;
		Features.LAST_SALED_ITEMS_PERIOD = 1;
		Features.RECEIVE_REMNANTS_WHEN_SENDING = true;
		
		Features.FOCUSED_GROUP = true;
		Features.FOCUSED_ITEMS = true;
		Features.BLOCK_ORDER_WITHOUT_FOCUS = true;
		Features.DEL_VISIT_WITHOUT_PHOTO = true;
		Features.CHECK_UNCOMPLETE_SCRIPTS = true;
	}
	
	@Override
	protected void initChildActivity() {
		super.initChildActivity();
		
		Warehouse.activity = WarehouseEx.class;
		DocList.activity = DocListEx.class;
		Documents.activity = DocumentsEx.class;
		PriceCount.activity = PriceCountEx.class;
	}
	
	protected void initDocTypes() {
		super.initDocTypes();		
		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override public Hitching create() { return new RcvNewHitching(MatrixOrder.class, "MatrixOrder"); }
		}, UpdateDB.GEN_DATA_HITCHING);
	}
	
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
