/*
 * Copyright (C), 2011, Гильдия Разработчиков
 * 
 * Демо программа (с базой данных)
 *
 * kki   16/05/2011   creating
 */

package com.grsoft.napoleon;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.widget.CheckBox;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DebtDocEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.network.ServerCommand;
import com.grsoft.util.FirstRunInit;
import com.grsoft.util.ViewInitializer;

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
		DbObject.regNewDataType(Org.class, OrgEx.class);
		
		DocType.addType(OrderDoc.instance());
		DocType.addType(DebtDoc.instance());
//		DocType.addType(VisitDoc.instance());
//		DocType.addType(RemnantsDoc.instance());
//		DocType.addType(IncassDoc.instance());
		
		DocType.setCurDoc(OrderDoc.instance());
	
		Features.RECEIVE_REMNANTS_WHEN_SENDING = true;
		Features.SHOW_NUMBER_IN_ORDER = true;
		Features.INPUT_QTY_IN_PACK = true;
		Features.SCRIPT_DOC = true;
		Features.DEL_VISIT_WITHOUT_PHOTO = true;
		Features.SCRIPT_SUM_ONLY_FOR_SALES = true;
		Features.CANT_SEND_SCRIPT_PART = true;
		
		Warehouse.activity = WarehouseNew.class;
		DocList.activity = DocListEx.class;
		VisitEdit.activity = VisitEditEx.class;
		Documents.activity = DocumentsEx.class;
		DocList.activity = DocListEx.class;
		
		UpdateDB.initUI = new ViewInitializer() {
			@Override
			public void init(Activity activity) {
				((CheckBox) activity.findViewById(R.id.cbVisit))
						.setChecked(true);
			}
		};
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
