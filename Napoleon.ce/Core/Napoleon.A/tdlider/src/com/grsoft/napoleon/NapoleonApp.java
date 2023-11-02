/*
 * Copyright (C), 2011, Гильдия Разработчиков
 * 
 * Демо программа (с базой данных)
 *
 * kki   16/05/2011   creating
 */

package com.grsoft.napoleon;

import java.util.List;

import com.grsoft.database.DocumentRestore;
import com.grsoft.database.Hitching;
import com.grsoft.database.HitchingCtor;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.AgentPlan;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.dataobjects.DeliveryItemEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Question;
import com.grsoft.dataobjects.QuestionItemEx;
import com.grsoft.dataobjects.Return;
import com.grsoft.dataobjects.ReturnEx;
import com.grsoft.dataobjects.ReturnItem;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.ReturnImplEx;
import com.grsoft.napoleon.documents.BonusDoc;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DebtDocEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.IncassDoc;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.OrderDocEx;
import com.grsoft.napoleon.documents.QuestionDoc;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.network.ServerCommand;
import com.grsoft.script.dataobjects.impl.ScriptDefImpl;
import com.grsoft.script.documents.ScriptDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.FirstRunInit;
import com.grsoft.util.MenuHandler;
import com.grsoft.util.MenuPrepareHitching;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

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
		DbObject.regNewDataType(Return.class, ReturnEx.class);
		DbObject.regNewDataType(Delivery.class, DeliveryEx.class);
		DbObject.regNewDataType(Org.class, OrgEx.class);
		DataObjectInfo.getInstance().replaceListType(Question.class, "items", QuestionItemEx.class);
		DataObjectInfo.getInstance().replaceListType(Delivery.class, "items", DeliveryItemEx.class);
		DataObjectInfo.getInstance().replaceListType(Return.class, "items", ReturnItem.class);
		
		DebtDocEx.initialize();
		OrderDocEx.initialize();
		
		DocType.addType(OrderDoc.instance());
		DocType.addType(DebtDoc.instance());
		DocType.addType(VisitDoc.instance());
		DocType.addType(IncassDoc.instance());
		DocType.addType(ReturnDoc.instance(ReturnImplEx.class));
		DocType.addType(RemnantsDoc.instance());
		DocType.addType(QuestionDoc.instance());
		DocType.addType(BonusDoc.instance());

		DocType.addType(ScriptDoc.instance());
		
		DocType.setCurDoc(OrderDoc.instance());		
		
		Warehouse.activity = WarehouseEx.class;
		ReturnDetail.activity = ReturnDetailEx.class;
		DocList.activity = DocListEx.class;
		Documents.activity = DocumentsEx.class;
		QuestionWebView.activity = QuestionWebViewEx.class;
		OrderDetail.activity = OrderDetailEx.class;
		
		ScriptDefImpl.docInScript.add(IncassDoc.instance());
		ScriptDefImpl.docInScript.add(ReturnDoc.instance());
		
		Features.SCRIPT_DOC = true;
		Features.QUESTION = true;
		Features.LAST_SALED_ITEMS_PERIOD = 2;
		Features.USE_COST_IN_RETURNS = true;
		Features.LOAD_FULL_PRICE = true;
		Features.EXCLUDE_RETURN_DOC_SUM_FROM_SCRIPT = true;
		Features.SCRIPT_SUM_ONLY_FOR_SALES = true;
		Features.WEIGHT_SCALE = Consts.QTY_SCALE;
		Features.SHOW_WEIGHT_IN_DOC_LIST = true;
		Features.REMOVE_EMPTY_ORDERS = true;
		Features.DEL_VISIT_WITHOUT_PHOTO = true;
		
		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override public Hitching create() { return new RcvNewHitching(AgentPlan.class, "AgentPlan"); }
		}, UpdateDB.GEN_DATA_HITCHING);
		
		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override public Hitching create() { return new DocumentRestore(ReturnDoc.instance()); }
		}, UpdateDB.RESTORE_DATA_HITCHING);
		
		Napoleon.docMenuPrepared.add(new MenuPrepareHitching() {

			@Override
			public void menuPrepared(List<MenuHandler> menu,
					final Activity activity) {
				menu.add(new MenuHandler(activity
						.getString(R.string.plans), new Runnable() {

					@Override
					public void run() {
						AgentPlanView.open(activity);
					}
				}));
			}
		});
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
