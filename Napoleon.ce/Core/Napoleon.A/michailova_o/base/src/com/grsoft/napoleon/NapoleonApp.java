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
import com.grsoft.dataobjects.Matrix;
import com.grsoft.dataobjects.MatrixEx;
import com.grsoft.dataobjects.OffTakeCoeff;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgDistrict;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgTypeMatrix;
import com.grsoft.dataobjects.Payment;
import com.grsoft.dataobjects.Return;
import com.grsoft.dataobjects.ReturnItem;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.ReturnImplEx;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DebtDocEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.IncassDoc;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.QuestionDoc;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.network.ServerCommand;
import com.grsoft.script.ScriptEdit;
import com.grsoft.util.NapoleonService;
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
	
	protected void initDocTypes() {
		DbObject.regNewDataType(Delivery.class, DeliveryEx.class);
		DbObject.regNewDataType(Org.class, OrgEx.class);
		DbObject.regNewDataType(Matrix.class, MatrixEx.class);
		
		DataObjectInfo.getInstance().replaceListType(Return.class, "items", ReturnItem.class);
		DataObjectInfo.getInstance().replaceListType(Order.class, "items", OrderItemEx.class);
		
		DataObjectInfo.getInstance().replacePrimaryKey(DeliveryEx.class, "");
		DataObjectInfo.getInstance().replacePrimaryKey(Payment.class, "");
		
		DebtDocEx.initialize();
		
		DocType.addType(OrderDoc.instance());
		DocType.addType(DebtDoc.instance());
		DocType.addType(ReturnDoc.instance(ReturnImplEx.class));
		DocType.addType(VisitDoc.instance());
		DocType.addType(RemnantsDoc.instance());
		DocType.addType(IncassDoc.instance());
		DocType.addType(QuestionDoc.instance());
		
		DocType.setCurDoc(OrderDoc.instance());
		
		UpdateDB.activity = UpdateDBEx.class;
		Warehouse.activity = WarehouseEx.class;
		ReturnDetail.activity = ReturnDetailEx.class;
		Napoleon.serviceType = NapoleonService.class;
		PriceCount.activity = PriceCountEx.class;
		Setting.WarehouseSettingActivity = WarehouseSettingEx.class;
		Setting.GPSSettingActivity = GpsSettingEx.class;
		Documents.activity = DocumentsEx.class;
		ScriptEdit.activity = ScriptEditEx.class; 
		
		Features.USE_COST_IN_RETURNS = true;
		Features.ASSORTMENT_MATRIX = false;
		Features.SALES_FROM_ORDERS = false;
		Features.SCRIPT_DOC = true;
		
		UpdateDB.addHitchingCtor(new HitchingCtor(){@Override public Hitching create() { return new Hitching(OrgTypeMatrix.class); }}, UpdateDB.GEN_DATA_HITCHING);
		UpdateDB.addHitchingCtor(new HitchingCtor() { @Override public Hitching create() { return new RcvNewHitching(OffTakeCoeff.class, "OffTakeCoeff"); } }, UpdateDB.GEN_DATA_HITCHING);
		UpdateDB.addHitchingCtor(new HitchingCtor() { @Override public Hitching create() { return new RcvNewHitching(OrgDistrict.class, "OrgDistrict"); } }, UpdateDB.GEN_DATA_HITCHING);
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
