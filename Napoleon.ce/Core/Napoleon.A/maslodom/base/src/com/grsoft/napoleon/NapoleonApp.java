/*
 * Copyright (C), 2011, Гильдия Разработчиков
 * 
 * Демо программа (с базой данных)
 *
 * kki   16/05/2011   creating
 */

package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.Hitching;
import com.grsoft.database.HitchingCtor;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.CategoryProduct;
import com.grsoft.dataobjects.Chance;
import com.grsoft.dataobjects.ClientLevel;
import com.grsoft.dataobjects.Competitor;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.Segment;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.ScriptImplEx;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DebtDocEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.DocTypeBase;
import com.grsoft.napoleon.documents.IncassDoc;
import com.grsoft.napoleon.documents.MonitoringDoc;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.QuestionDoc;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.napoleon.documents.SpancopDoc;
import com.grsoft.napoleon.documents.TaskDoneDoc;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.napoleon.modules.MonitoringInit;
import com.grsoft.network.ServerCommand;
import com.grsoft.script.ScriptEdit;
import com.grsoft.script.dataobjects.impl.ScriptDefImpl;
import com.grsoft.script.documents.ScriptDoc;
import com.grsoft.util.FirstRunInit;

import android.app.Application;
import android.content.Context;

public class NapoleonApp extends Application {
	@SuppressWarnings("unused")
	private static final String TAG = "NapoleonApp";
	
	public List<DocTypeBase> potenzialOrgDocFilter;
	
	class OrderEditor implements OrderImpl.PropertiesEditor {
		@Override
		public void edit(Context ctx, OrderImpl order, boolean isOldOrder) {
			CreateOrder.open(ctx, order, isOldOrder);
		}
	}
	
	private void initDocTypes() {
		DebtDocEx.initialize();
		MonitoringInit.init();
		
		DocType.addType(OrderDoc.instance(OrderImplEx.class));
		DocType.addType(DebtDoc.instance());
		DocType.addType(VisitDoc.instance());
		DocType.addType(RemnantsDoc.instance());
		DocType.addType(QuestionDoc.instance());
		DocType.addType(TaskDoneDoc.instance());
		DocType.addType(IncassDoc.instance());
		DocType.addType(MonitoringDoc.instance());
		DocType.addType(SpancopDoc.instance());
		DocType.addType(ScriptDoc.instance(ScriptImplEx.class));
		
		DocType.setCurDoc(OrderDoc.instance());		
		
		potenzialOrgDocFilter = new ArrayList<DocTypeBase>();
		potenzialOrgDocFilter.add(VisitDoc.instance());
		potenzialOrgDocFilter.add(RemnantsDoc.instance());
		potenzialOrgDocFilter.add(QuestionDoc.instance());
		potenzialOrgDocFilter.add(TaskDoneDoc.instance());
		potenzialOrgDocFilter.add(SpancopDoc.instance());

		DbObject.regNewDataType(Org.class, OrgEx.class);
		DbObject.regNewDataType(Delivery.class, DeliveryEx.class);
		DbObject.regNewDataType(Price.class, PriceEx.class);
		DbObject.regNewDataType(Order.class, OrderEx.class);
		DataObjectInfo.getInstance().replaceListType(OrderEx.class, "items", OrderItemEx.class);
		
		Warehouse.activity = WarehouseEx.class;
		Presentation.activity = PresentationFolder.class;
		PricePresentation.activity = PricePresentationFolder.class;
		Documents.activity = DocumentsEx.class;
		ScriptEdit.activity = ScriptEditEx.class;
		MonitoringEdit.activity = MonitoringEditEx.class;
		UpdateDB.activity = UpdateDBEx.class;
		PriceCount.activity = PriceCountEx.class;
		DocList.activity = DocListEx.class;
		
		Features.ORG_STOP_TABLE = true;
		Features.QUESTION = true;
		Features.ORG_TASK = true;
		Features.SCRIPT_OFF_IN_DOC_LIST = true;
		Features.DEL_VISIT_WITHOUT_PHOTO = true;
		Features.CANT_SEND_SCRIPT_PART = true;
		Features.SCRIPT_DOC = true;
		Features.SCRIPT_SUM_ONLY_FOR_SALES = true;
		Features.LOAD_FULL_PRICE = true;
		Features.RECEIVE_REMNANTS_WHEN_SENDING = true;
		
		ScriptDefImpl.docInScript.add(VisitDoc.instance());

		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override
			public Hitching create() {
				return new RcvNewHitching(CategoryProduct.class, "CategoryProduct");
			}
		}, UpdateDB.GEN_DATA_HITCHING);
		
		
		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override
			public Hitching create() {
				return new RcvNewHitching(Chance.class, "Chance");
			}
		}, UpdateDB.GEN_DATA_HITCHING);
		
		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override
			public Hitching create() {
				return new RcvNewHitching(ClientLevel.class, "ClientLevel");
			}
		}, UpdateDB.GEN_DATA_HITCHING);
		
		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override
			public Hitching create() {
				return new RcvNewHitching(Competitor.class, "Competitor");
			}
		}, UpdateDB.GEN_DATA_HITCHING);
		
		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override
			public Hitching create() {
				return new RcvNewHitching(Segment.class, "Segment");
			}
		}, UpdateDB.GEN_DATA_HITCHING);
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
