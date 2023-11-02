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
import com.grsoft.database.LoadOrdersHitching;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Payment;
import com.grsoft.dataobjects.PaymentEx;
import com.grsoft.dataobjects.Return;
import com.grsoft.dataobjects.ReturnEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DebtDocEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.IncassDoc;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.napoleon.documents.VisitDocEx;
import com.grsoft.napoleon.util.CfgNplEx;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.ServerCommand;
import com.grsoft.util.FirstRunInit;

public class NapoleonAppBase extends Application {
	@SuppressWarnings("unused")
	private static final String TAG = "NapoleonApp";
	
	public static boolean IS_PRESELLING_PROGRAM = true;
	
	class OrderEditor implements OrderImpl.PropertiesEditor {
		@Override
		public void edit(Context ctx, OrderImpl order, boolean isOldOrder) {
			CreateOrder.open(ctx, order, isOldOrder);
		}
	}
	
	protected void initDocTypes() {
		VisitDocEx.init();
				
		DbObject.regNewDataType(Org.class, OrgEx.class);
		DbObject.regNewDataType(Order.class, OrderEx.class);
		DbObject.regNewDataType(Return.class, ReturnEx.class);
		DbObject.regNewDataType(Delivery.class, DeliveryEx.class);
		DbObject.regNewDataType(Payment.class, PaymentEx.class);
		
//		DataObjectInfo.getInstance().replacePrimaryKey(DeliveryEx.class, "ido,number");
//		DataObjectInfo.getInstance().replacePrimaryKey(PaymentEx.class, "ido,number");

		DocType.addType(OrderDoc.instance(OrderImplEx.class));
		DocType.addType(DebtDoc.instance());
		DocType.addType(VisitDoc.instance());
//		DocType.addType(RemnantsDoc.instance());
//		DocType.addType(ReturnDoc.instance(ReturnImplEx.class));
		DocType.addType(IncassDoc.instance());
		
		DocType.setCurDoc(OrderDoc.instance());		

		Warehouse.activity = WarehouseNew.class;
		Presentation.activity = PresentationFolder.class;
		PricePresentation.activity = PricePresentationFolder.class;
		Documents.activity = DocumentsEx.class;
		ReturnDetail.activity = ReturnDetailEx.class;
		OrderDeliveryDetail.activity = OrderDeliveryDetailEx.class;
		
		CostStrategy.defaultInstance = new CostStrategyEx();
		
		Features.ORG_STOP_TABLE = true;
		Features.BLOCK_IN_STOP_LIST = true;
		Features.INPUT_QTY_IN_PACK = true;
		Features.SHOW_ORG_ADDRESS = true;
		Features.QTY_IN_PACK_IN_DOCS = true;
		Features.CANT_CHANGE_SEND_FLAG = true;
	
		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override public Hitching create() { return new LoadOrdersHitching(); }
		}, UpdateDB.GEN_DATA_HITCHING);
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		ConfigManager.initConfig(new CfgNplEx());
		
		FirstRunInit.init(this);

		com.grsoft.napoleon.modules.print.DebtDoc.DebtDocType = DebtDocEx.class;
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
