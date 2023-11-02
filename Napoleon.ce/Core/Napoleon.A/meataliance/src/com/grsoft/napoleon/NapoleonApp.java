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
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.widget.CheckBox;
import com.grsoft.database.DocumentRestore;
import com.grsoft.database.Hitching;
import com.grsoft.database.HitchingCtor;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.AgentPlan;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Report;
import com.grsoft.dataobjects.Return;
import com.grsoft.dataobjects.ReturnItemEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.ScriptImplEx;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DebtDocEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.DocTypeBase;
import com.grsoft.napoleon.documents.IncassDoc;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.QuestionDoc;
import com.grsoft.napoleon.documents.RealizationDoc;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.napoleon.documents.ReturnDocEx;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.napoleon.documents.VisitDocEx;
import com.grsoft.network.ServerCommand;
import com.grsoft.script.dataobjects.impl.ScriptDefImpl;
import com.grsoft.script.documents.ScriptDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.FirstRunInit;
import com.grsoft.util.ViewInitializer;

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
		DbObject.regNewDataType(Org.class, OrgEx.class);
		
		DebtDocEx.initialize();
		ReturnDocEx.initialize();
		VisitDocEx.initialize();
		
		DocType.addType(OrderDoc.instance(OrderImplEx.class));
		DocType.addType(DebtDoc.instance());
		DocType.addType(VisitDoc.instance());
		DocType.addType(IncassDoc.instance());
		DocType.addType(ReturnDoc.instance());
		DocType.addType(RemnantsDoc.instance());
		DocType.addType(QuestionDoc.instance());
		DocType.addType(RealizationDoc.instance());

		DocType.addType(ScriptDoc.instance(ScriptImplEx.class));
		
		DocType.setCurDoc(OrderDoc.instance());		
		
		ScriptDefImpl.docInScript.add(IncassDoc.instance());
		ScriptDefImpl.docInScript.add(ReturnDoc.instance());
		
		DataObjectInfo doi = DataObjectInfo.getInstance();
		doi.replaceListType(Return.class, "items", ReturnItemEx.class);
		doi.replaceListType(Order.class, "items", OrderItemEx.class);

		Warehouse.activity = WarehouseNew.class;
		DocList.activity = DocListEx.class;		
		Presentation.activity = PresentationFolder.class;
		PricePresentation.activity = PricePresentationFolder.class;
		OrderDetail.activity = OrderDetailEx.class;
		PriceCount.activity = PriceCountEx.class;
		UpdateDB.activity = UpdateDBEx.class;
		ReturnDetail.activity = ReturnDetailEx.class;
		OrderDeliveryDetail.activity = OrderDeliveryDetailEx.class;
		Documents.activity = DocumentsEx.class;
		
		Features.FOCUSED_GROUP = true;
		Features.FOCUSED_ITEMS = true;
		Features.DEL_VISIT_WITHOUT_PHOTO = true;
		Features.LAST_SALED_ITEMS_PERIOD = 2;
		Features.USE_COST_IN_RETURNS = true;
		Features.LOAD_FULL_PRICE = true;
		Features.EXCLUDE_RETURN_DOC_SUM_FROM_SCRIPT = true;
		Features.WEIGHT_SCALE = Consts.QTY_SCALE;
		Features.SHOW_WEIGHT_IN_DOC_LIST = true;
		Features.REMOVE_EMPTY_ORDERS = true;
		Features.USE_COST_IN_RETURNS = true;
		Features.EXCLUDE_RETURN_DOC_SUM_FROM_SCRIPT = true;
		Features.SCRIPT_DOC = true;
		Features.SHOW_PRESENT_IMG = true;
		Features.ORG_STOP_TABLE = true;
		Features.BLOCK_IN_STOP_LIST = true;
		
		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override public Hitching create() { return new RcvNewHitching(AgentPlan.class, "AgentPlan"); }
		}, UpdateDB.GEN_DATA_HITCHING);

		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override public Hitching create() { return new RcvNewHitching(Report.class, "Reports"); }
		}, UpdateDB.GEN_DATA_HITCHING);
		
		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override public Hitching create() { return new DocumentRestore(ReturnDoc.instance()); }
		}, UpdateDB.RESTORE_DATA_HITCHING);
		
		UpdateDB.initUI = new ViewInitializer() {
			@Override public void init(Activity activity) { ((CheckBox)activity.findViewById(R.id.cbVisit)).setChecked(true); }
		};
		
		potenzialOrgDocFilter = new ArrayList<DocTypeBase>();
		potenzialOrgDocFilter.add(VisitDoc.instance());
		potenzialOrgDocFilter.add(ReturnDoc.instance());
		potenzialOrgDocFilter.add(QuestionDoc.instance());
		
		ScriptDefImpl.docInScript.add(ReturnDoc.instance());
		ScriptDefImpl.docInScript.add(IncassDoc.instance());
		ScriptDefImpl.docInScript.add(RealizationDoc.instance());

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
