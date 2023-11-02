/*
 * Copyright (C), 2011, Гильдия Разработчиков
 * 
 * Демо программа (с базой данных)
 *
 * kki   16/05/2011   creating
 */

package com.grsoft.napoleon;

import java.util.List;
import com.grsoft.database.Hitching;
import com.grsoft.database.HitchingCtor;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.AgentPlan;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.QuestionDoc;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.napoleon.documents.TaskDoneDoc;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.network.ServerCommand;
import com.grsoft.script.documents.ScriptDoc;
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
		DbObject.regNewDataType(Delivery.class, DeliveryEx.class);
		
		DocType.addType(OrderDoc.instance());
		DocType.addType(DebtDoc.instance());
		DocType.addType(VisitDoc.instance());
		DocType.addType(RemnantsDoc.instance());
		DocType.addType(QuestionDoc.instance());
		DocType.addType(TaskDoneDoc.instance());
		DocType.addType(ScriptDoc.instance());
		
		DocType.setCurDoc(OrderDoc.instance());		

		Features.SCRIPT_DOC = true;

		Features.FOCUSED_GROUP = true;
		Features.FOCUSED_ITEMS = true;
		Features.CANT_SEND_SCRIPT_PART = true;
		Features.UPDATE_DB_CHECK_VISITS = true;
		Features.DEL_VISIT_WITHOUT_PHOTO = true;

		Warehouse.activity = WarehouseNew.class;
		Presentation.activity = PresentationFolder.class;
		PricePresentation.activity = PricePresentationFolder.class;
	
		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override
			public Hitching create() {
				return new RcvNewHitching(AgentPlan.class, "AgentPlan");
			}
		}, UpdateDB.GEN_DATA_HITCHING);
		
		Napoleon.docMenuPrepared.add(new MenuPrepareHitching() {

			@Override
			public void menuPrepared(List<MenuHandler> menu, final Activity activity) {
				menu.add(new MenuHandler(activity.getString(R.string.plans), new Runnable() {
					@Override public void run() { AgentPlanView.open(activity); }
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
