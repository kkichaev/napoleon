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

import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.Order2Ex;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.Return;
import com.grsoft.dataobjects.ReturnItemEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.ReturnImplEx;
import com.grsoft.dataobjects.impl.ScriptImplEx;
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
import com.grsoft.script.documents.ScriptDoc;
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
		DebtDocEx.initialize();

		DbObject.regNewDataType(Order.class, OrderEx.class);
		DataObjectInfo.getInstance().replaceListType(Return.class, "items", ReturnItemEx.class);
		
		DocType.addType(OrderDoc.instance(OrderImplEx.class));
		DocType.addType(DebtDoc.instance());
		DocType.addType(IncassDoc.instance());
		DocType.addType(VisitDoc.instance());
		DocType.addType(RemnantsDoc.instance());
		DocType.addType(ReturnDoc.instance(ReturnImplEx.class));
		DocType.addType(QuestionDoc.instance());
		DocType.addType(ScriptDoc.instance(ScriptImplEx.class));

		DocType.setCurDoc(OrderDoc.instance());

		Features.SCRIPT_DOC = true;
		Features.CANT_SEND_SCRIPT_PART = true;
		Features.SCRIPT_OFF_IN_DOC_LIST = true;
		Features.DEL_VISIT_WITHOUT_PHOTO = true;
		Features.QUESTION = true;
		Features.WEIGHT_SCALE = Consts.WEIGHT_SCALE;
		Features.LOAD_FULL_PRICE = true;
		Features.SHOW_ORG_ADDRESS = true;

		Warehouse.activity = WarehouseEx.class;
		Presentation.activity = PresentationFolder.class;
		PricePresentation.activity = PricePresentationFolder.class;
		PriceCount.activity = PriceCountEx.class;
		UpdateDB.activity = UpdateDBEx.class;

		DbObject.regNewDataType(Price.class, PriceEx.class);
		DbObject.regNewDataType(Order.class, Order2Ex.class);
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
		try {
			ServerCommand.ProgramVersion = getResources().getString(R.string.version);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
