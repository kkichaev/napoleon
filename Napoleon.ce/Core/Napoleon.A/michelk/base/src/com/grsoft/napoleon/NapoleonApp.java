/*
 * Copyright (C), 2011, Гильдия Разработчиков
 * 
 * Демо программа (с базой данных)
 *
 * kki   16/05/2011   creating
 */

package com.grsoft.napoleon;

import android.content.Context;

import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Present;
import com.grsoft.dataobjects.PresentEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.network.ServerCommand;
import com.grsoft.util.AssortmentMatrixAdapter;
import com.grsoft.util.FirstRunInit;
//import com.grsoft.napoleon.util.PresentInit;

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
		DbObject.regNewDataType(Present.class, PresentEx.class);
		DbObject.regNewDataType(Price.class, PriceEx.class);
		DbObject.regNewDataType(Org.class, OrgEx.class);
	}
	
	@Override
	protected void initChildDocTypes() {
		DocType.addType(ReturnDoc.instance());
	}

	@Override
	protected void initChildFeature() {
		Features.INPUT_QTY_IN_PACK = true;
		Features.FOLDER_PRESENTATION = true;
		Features.HAVE_PRICE_MOVER = true;
		Features.FOCUSED_GROUP = true;
		Features.CAN_CHANGE_PRESENT_FOLDER = true;
		Features.SHOW_PRESENT_IMG = true;
		
		AssortmentMatrixAdapter.PERIOD_IN_MONTH = 2;
	}

	@Override
	protected void initChildActivity() {
		Warehouse.activity = WarehouseEx.class;
		Documents.activity = DocumentsEx.class;
		PriceCount.activity = PriceCountEx.class;
		UpdateDB.activity = UpdateDBEx.class;
	}
	
//	private void initDocTypes() {
//		DocType.addType(OrderDoc.instance());
//		DocType.addType(DebtDoc.instance());
//		DocType.addType(VisitDoc.instance());
//		DocType.addType(RemnantsDoc.instance());
//		DocType.addType(ReturnDoc.instance());
//		DocType.addType(QuestionDoc.instance());
//		
//		DocType.setCurDoc(OrderDoc.instance());
//		
//		DbObject.regNewDataType(Present.class, PresentEx.class);
//		DbObject.regNewDataType(Price.class, PriceEx.class);
//		DbObject.regNewDataType(Org.class, OrgEx.class);
//		
//		Warehouse.activity = WarehouseEx.class;
//		Documents.activity = DocumentsEx.class;
//		PriceCount.activity = PriceCountEx.class;
//		UpdateDB.activity = UpdateDBEx.class;
//		
//		Features.INPUT_QTY_IN_PACK = true;
//		Features.FOLDER_PRESENTATION = true;
//		Features.HAVE_PRICE_MOVER = true;
//		Features.FOCUSED_GROUP = true;
//		Features.SCRIPT_DOC = true;
//		Features.CAN_CHANGE_PRESENT_FOLDER = true;
//		Features.SHOW_PRESENT_IMG = true;
//		
//		AssortmentMatrixAdapter.PERIOD_IN_MONTH = 2;
//		
//	}
	
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
