/*
 * Copyright (C), 2011, Гильдия Разработчиков
 * 
 * Демо программа (с базой данных)
 *
 * kki   16/05/2011   creating
 */

package com.grsoft.napoleon;

import java.util.List;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import com.grsoft.database.Hitching;
import com.grsoft.database.HitchingCtor;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.AgentPlan;
import com.grsoft.dataobjects.CommonMatrix;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgMtx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DebtDocEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.IncassDoc;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.QuestionDoc;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.napoleon.documents.TaskDoneDoc;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.napoleon.modules.CostManagerImpl;
import com.grsoft.network.ServerCommand;
import com.grsoft.util.FirstRunInit;
import com.grsoft.util.MenuHandler;
import com.grsoft.util.MenuPrepareHitching;

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
		
		DocType.addType(OrderDoc.instance(OrderImplEx.class));
		DocType.addType(DebtDoc.instance());
		DocType.addType(VisitDoc.instance());
		DocType.addType(RemnantsDoc.instance());
		DocType.addType(IncassDoc.instance());
		DocType.addType(QuestionDoc.instance());
		DocType.addType(TaskDoneDoc.instance());
		
		DocType.setCurDoc(OrderDoc.instance());		

		Features.FOCUSED_GROUP = true;
		Features.FOCUSED_ITEMS = true;
		Features.SCRIPT_DOC = true;
		Features.CANT_SEND_SCRIPT_PART = true;
		Features.SCRIPT_OFF_IN_DOC_LIST = true;
		Features.SCRIPT_SUM_ONLY_FOR_SALES = true;
		Features.DEL_VISIT_WITHOUT_PHOTO = true;
		Features.COST_MANAGER = new CostManagerImpl();
		
		Warehouse.activity = WarehouseEx.class;
		Presentation.activity = PresentationFolder.class;
		PricePresentation.activity = PricePresentationFolder.class;
		PriceCount.activity = PriceCountEx.class;
		
		DbObject.regNewDataType(Org.class,  OrgEx.class);
		DataObjectInfo doi = DataObjectInfo.getInstance();
		doi.replaceListType(Order.class, "items", OrderItemEx.class);
		
		DbObject.regNewDataType(Order.class, OrderEx.class);
		DbObject.regNewDataType(Price.class, PriceEx.class);
		
		UpdateDB.activity = UpdateDBEx.class;
		
		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override
			public Hitching create() {
				return new RcvNewHitching(CommonMatrix.class, "CommonMatrix");
			}
		}, UpdateDB.GEN_DATA_HITCHING);

		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override
			public Hitching create() {
				return new RcvNewHitching(OrgMtx.class, "OrgMtx");
			}
		}, UpdateDB.GEN_DATA_HITCHING);
		
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
		
		CostStrategy.defaultInstance = new CostStrategyEx();
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
