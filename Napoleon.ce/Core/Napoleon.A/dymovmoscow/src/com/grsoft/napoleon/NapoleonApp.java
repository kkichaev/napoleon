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
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.DistribDef;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Return;
import com.grsoft.dataobjects.ReturnItemEx;
import com.grsoft.dataobjects.Visit;
import com.grsoft.dataobjects.VisitItemEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.ReturnImplEx;
import com.grsoft.dataobjects.impl.VisitImplEx;
import com.grsoft.napoleon.documents.DistribDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.DymovTaskDoc;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.napoleon.documents.TaskDoneDoc;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.CfgNplEx;
import com.grsoft.network.ServerCommand;
import com.grsoft.script.dataobjects.impl.ScriptDefImpl;
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
		VisitDoc.instance(VisitImplEx.class);

		DbObject.regNewDataType(Org.class, OrgEx.class);
		DataObjectInfo.getInstance().replaceListType(Return.class, "items", ReturnItemEx.class);
		DataObjectInfo.getInstance().replaceListType(Visit.class, "items", VisitItemEx.class);
	}
	
	@Override
	protected void initChildDocTypes() {
		DocType.addType(DymovTaskDoc.instance());
		DocType.removeType(TaskDoneDoc.instance());
		ScriptDefImpl.docInScript.remove(TaskDoneDoc.instance());
		ScriptDefImpl.docInScript.add(DymovTaskDoc.instance());
		DocType.addType(DistribDoc.instance());
		DocType.addType(ReturnDoc.instance(ReturnImplEx.class));
	}
	
	@Override
	protected void initAcivity() {
		super.initAcivity();

		Warehouse.activity = WarehouseEx.class;
		UpdateDB.activity = UpdateDBEx.class;
		Documents.activity = DocumentsEx.class;
		ReturnDetail.activity = ReturnDetailEx.class;
	}
	
	@Override
	protected void initFeatures() {
		super.initFeatures();

		Features.ID_COLUMN_IN_PRICE_LIST = true;
		Features.WEIGHT_SCALE = 10;
		Features.REPORT_REQUEST = true;
		Features.UNLIMIT_VISIT_ITEMS = true;
	}
	
	@Override protected CfgNpl createConfig() { return new CfgNplEx(); }
	
	@Override
	public void onCreate() {
		super.onCreate();
		UpdateDB.addHitchingCtor(new HitchingCtor() { 
			@Override public Hitching create() { return new Hitching(DistribDef.class); 
		}}, UpdateDB.GEN_DATA_HITCHING);
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
