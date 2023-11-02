/*
 * Copyright (C), 2011, Гильдия Разработчиков
 * 
 * Демо программа (с базой данных)
 *
 * kki   16/05/2011   creating
 */

package com.grsoft.napoleon;

import android.content.Context;

import com.grsoft.database.PriceHitchinEx;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.dataobjects.DeliveryItemEx;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.Return;
import com.grsoft.dataobjects.ReturnItem;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.ReturnImpl;
import com.grsoft.dataobjects.impl.ReturnImplEx;
import com.grsoft.napoleon.documents.DebtDocEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.CfgNplEx;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.ServerCommand;

public class NapoleonApp extends NapoleonAppBase {
	
	class OrderEditor implements OrderImpl.PropertiesEditor {
		@Override
		public void edit(Context ctx, OrderImpl order, boolean isOldOrder) {
			CreateOrder.open(ctx, order, isOldOrder);
		}
	}

	@Override
	protected void defineNewType() {
		DebtDocEx.init();

		super.defineNewType();

		DbObject.regNewDataType(Price.class, PriceEx.class);
		DbObject.regNewDataType(Delivery.class, DeliveryEx.class);

		CostStrategy.defaultInstance = new CostStrategyEx();

		DataObjectInfo doi = DataObjectInfo.getInstance();
		doi.replaceListType(Delivery.class, "items", DeliveryItemEx.class);
		DataObjectInfo.getInstance().replaceListType(Return.class, "items", ReturnItem.class);

		UpdateDBW.priceHitchingClass = PriceHitchinEx.class;
	}

	@Override
	protected void initChildActivity() {
		super.initChildActivity();
		UpdateDB.activity = UpdateDBEx.class;
		Warehouse.activity = WarehouseEx.class;
		Documents.activity = DocumentsEx.class;
		PriceCount.activity = PriceCountEx.class;
		Setting.WarehouseSettingActivity = WarehouseSettingEx.class;
		Presentation.activity = PresentationFolderEx.class;
	}

	@Override
	protected Class<? extends ReturnImpl> returnsImplType() {
		return ReturnImplEx.class;
	}

	@Override
	protected Class<? extends OrderImplBase<? extends Order>> orderImplType() {
		return OrderImplEx.class;
	}

	@Override
	protected void initChildDocTypes() {
		super.initChildDocTypes();
		DocType.addType(ReturnDoc.instance(returnsImplType()));
	}

	@Override
	protected void initChildFeature() {
		super.initChildFeature();
		Features.WH_QTY = true;

		Features.ENCODE_CONNECTION = true;
		Features.REPORT_REQUEST = true;
		Features.MARK_OVERDUE_DEBTS = true;
		Features.PRESENTATION_IN_DB = true;
	}

	@Override
	public void onCreate() {
		Features.VER_4_1 = true;

		ConfigManager.initConfig(new CfgNplEx());
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
}
