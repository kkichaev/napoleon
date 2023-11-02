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
import com.grsoft.dataobjects.MatrixItemEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.RemnantItemEx;
import com.grsoft.dataobjects.Remnants;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.RemnantsImplEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.network.ServerCommand;
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
		DocType.addType(RemnantsDoc.instance(RemnantsImplEx.class));

		DocType.setCurDoc(RemnantsDoc.instance());
		
		DataObjectInfo doi = DataObjectInfo.getInstance();
		doi.replaceListType(Remnants.class, "items", RemnantItemEx.class);
		doi.replaceListType(OrgEx.class, "matrix", MatrixItemEx.class);
		DbObject.regNewDataType(Org.class, OrgEx.class);

		Warehouse.activity = WarehouseEx.class;
		RemnantsDetail.activity = RemnantsDetailEx.class;
		UpdateDB.activity = UpdateDBEx.class;
		
		Features.POTENZIAL_ORG = false;
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
			ServerCommand.ProgramVersion = getResources().getString(
					R.string.version);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
