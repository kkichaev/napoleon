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
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrgInfoData;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DebtDocEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.GwinnerAgentTaskDoc;
import com.grsoft.napoleon.documents.IncassDoc;
import com.grsoft.napoleon.documents.MonitoringDoc;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.QuestionDoc;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.napoleon.documents.TaskDoneDoc;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.network.ServerCommand;

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
		DebtDocEx.initialize();
		
		DocType.addType(OrderDoc.instance(OrderImplEx.class));
		DocType.addType(DebtDoc.instance());
		DocType.addType(VisitDoc.instance());
		DocType.addType(MonitoringDoc.instance());
		DocType.addType(RemnantsDoc.instance());
		DocType.addType(QuestionDoc.instance());
		DocType.addType(ReturnDoc.instance());
		DocType.addType(IncassDoc.instance());
		
		DocType.removeType(TaskDoneDoc.instance());
		
		DocType.addType(GwinnerAgentTaskDoc.instance());
		
		DocType.setCurDoc(OrderDoc.instance());
		
		Warehouse.activity = WarehouseEx.class;
		Presentation.activity = PresentationFolder.class;
		PricePresentation.activity = PricePresentationFolder.class;
		MonitoringEdit.activity = MonitoringEditEx.class;
		Documents.activity = DocumentsEx.class;
		
		Features.SCRIPT_DOC = true;
		Features.SCRIPT_OFF_IN_DOC_LIST = true;
		Features.DEL_VISIT_WITHOUT_PHOTO = true;
		Features.CANT_SEND_SCRIPT_PART = true;
		
		DbObject.regNewDataType(Order.class, OrderEx.class);
		DbObject.regNewDataType(Price.class, PriceEx.class);
		
		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override public Hitching create() { return new RcvNewHitching(OrgInfoData.class, "OrgInfo"); }
		}, UpdateDB.GEN_DATA_HITCHING);
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
