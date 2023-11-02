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

import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DebtDocEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.IncassDoc;
import com.grsoft.napoleon.documents.MonitoringDoc;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.QuestionDoc;
import com.grsoft.napoleon.documents.QuestionDocEx;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.napoleon.modules.MonitoringInit;
import com.grsoft.network.ServerCommand;
import com.grsoft.script.dataobjects.impl.ScriptDefImpl;
import com.grsoft.util.Consts;
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
		MonitoringInit.init();
		DebtDocEx.initialize();
		QuestionDocEx.initialize();

		DocType.addType(OrderDoc.instance(OrderImplEx.class));
		DocType.addType(DebtDoc.instance());
		DocType.addType(VisitDoc.instance());
		DocType.addType(RemnantsDoc.instance());
		DocType.addType(IncassDoc.instance());
		DocType.addType(ReturnDoc.instance());
		DocType.addType(QuestionDoc.instance());
		DocType.addType(MonitoringDoc.instance());
		
		DocType.setCurDoc(OrderDoc.instance());		

		DbObject.regNewDataType(Delivery.class, DeliveryEx.class);

		Warehouse.activity = WarehouseEx.class;
		PriceCount.activity = PriceCountEx.class;
		Setting.WarehouseSettingActivity = WarehouseSettingEx.class;
		Documents.activity = DocumentsEx.class;
		DocList.activity = DocListEx.class;
		UpdateDB.activity = UpdateDBEx.class;
		CreateReturn.activity = CreateReturnEx.class;
	
		Features.SCRIPT_DOC = true;
		Features.QUESTION = true;
		Features.RECEIVE_REMNANTS_WHEN_SENDING = true;
		Features.SHOW_ZERO_FILTER = true;
		Features.ASSORTMENT_MATRIX = true;
		Features.CANT_SEND_SCRIPT_PART = true;
		//Features.SHOW_WEIGHT_IN_HISTORY = true;
		Features.ORG_STOP_TABLE = true;
		Features.WEIGHT_SCALE = Consts.WEIGHT_SCALE;
		Features.USE_COST_IN_RETURNS = true;
		Features.SCRIPT_OFF_IN_DOC_LIST = true;
		Features.DEL_VISIT_WITHOUT_PHOTO = true;
		Features.DELIVERY_REPLACE_ORDER_SUM = true;
		Features.LINKS_DISSALLOW = true;
		
		ScriptDefImpl.docInScript.add(IncassDoc.instance());
		ScriptDefImpl.docInScript.add(ReturnDoc.instance());
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
