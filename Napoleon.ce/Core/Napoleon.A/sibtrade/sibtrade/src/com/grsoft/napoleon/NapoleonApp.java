/*
 * Copyright (C), 2011, Гильдия Разработчиков
 * 
 * Демо программа (с базой данных)
 *
 * kki   16/05/2011   creating
 */

package com.grsoft.napoleon;

import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.dataobjects.Return;
import com.grsoft.dataobjects.ReturnEx;
import com.grsoft.dataobjects.ReturnItem;
import com.grsoft.dataobjects.Visit;
import com.grsoft.dataobjects.VisitEx;
import com.grsoft.dataobjects.VisitItemEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.ReturnImplEx;
import com.grsoft.dataobjects.impl.ScriptImplEx;
import com.grsoft.dataobjects.impl.VisitImplEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.script.dataobjects.impl.ScriptImpl;
import com.grsoft.script.documents.ScriptDoc;

public class NapoleonApp extends NapoleonAppBaseSBTR {
	@SuppressWarnings("unused")
	private static final String TAG = "NapoleonApp";

	@Override
	protected void defineNewType() {
		VisitDoc.instance(VisitImplEx.class);

		DbObject.regNewDataType(Return.class, ReturnEx.class);
		DbObject.regNewDataType(Delivery.class, DeliveryEx.class);
		DbObject.regNewDataType(Visit.class, VisitEx.class);
		
		DataObjectInfo.getInstance().replaceListType(Return.class, "items", ReturnItem.class);
		DataObjectInfo.getInstance().replaceListType(VisitEx.class, "items", VisitItemEx.class);

		super.defineNewType();
	}
	
	@Override protected Class<? extends ScriptImpl> scriptImplType() { return ScriptImplEx.class; }
	
	@Override
	protected void initChildDocTypes() {
		DocType.addType(ReturnDoc.instance(ReturnImplEx.class));
		super.initChildDocTypes();
	}
	
	@Override
	protected void initChildActivity() {
		ReturnDetail.activity = ReturnDetailEx.class;
		Warehouse.activity = WarehouseEx.class;
		super.initChildActivity();
	}
	
	@Override
	protected void initChildFeature() {
		Features.UNLIMIT_VISIT_ITEMS = true;
		super.initChildFeature();
	}
}
