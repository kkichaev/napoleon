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
import com.grsoft.database.RetNtfyHitching;
import com.grsoft.dataobjects.AgentPlan;
import com.grsoft.dataobjects.CommonMatrix;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgMtx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.RetMtx;
import com.grsoft.dataobjects.Return;
import com.grsoft.dataobjects.ReturnEx;
import com.grsoft.dataobjects.ReturnItemEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DebtDocEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.DocTypeBase;
import com.grsoft.napoleon.documents.IncassDoc;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.QuestionDoc;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.napoleon.documents.ReturnDocEx;
import com.grsoft.napoleon.documents.TaskDoneDoc;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.network.ReadService;
import com.grsoft.network.ServerCommand;
import com.grsoft.script.dataobjects.impl.ScriptDefImpl;
import com.grsoft.util.FirstRunInit;
import com.grsoft.util.MenuHandler;
import com.grsoft.util.MenuPrepareHitching;
import android.app.Activity;
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
		DbObject.regNewDataType(Order.class, OrderEx.class);
		DbObject.regNewDataType(Delivery.class, DeliveryEx.class);
		DbObject.regNewDataType(Org.class, OrgEx.class);

		DebtDocEx.initialize();
		ReturnDocEx.initialize();

		DocType.addType(OrderDoc.instance(OrderImplEx.class));
		DocType.addType(DebtDoc.instance());
		DocType.addType(IncassDoc.instance());
		DocType.addType(VisitDoc.instance());
		DocType.addType(RemnantsDoc.instance());
		DocType.addType(QuestionDoc.instance());
		DocType.addType(TaskDoneDoc.instance());
		DocType.addType(ReturnDoc.instance());

		DocType.setCurDoc(OrderDoc.instance());

		DbObject.regNewDataType(Return.class, ReturnEx.class);
		DataObjectInfo.getInstance().replaceListType(ReturnEx.class, "items", ReturnItemEx.class);
		
		Features.SCRIPT_DOC = true;
		Features.QUESTION = true;
		Features.ASSORTMENT_MATRIX = true;
		Features.CANT_SEND_SCRIPT_PART = true;
		Features.SCRIPT_OFF_IN_DOC_LIST = true;
		Features.ORG_STOP_TABLE = true;
		Features.DEL_VISIT_WITHOUT_PHOTO = true;
		Features.SCRIPT_SUM_ONLY_FOR_SALES = true;
		Features.WEIGHT_SCALE = 1000;
		Features.FOCUSED_GROUP = true;
		Features.FOCUSED_ITEMS = true;
		Features.USE_COST_IN_RETURNS = true;
		Features.EXCLUDE_RETURN_DOC_SUM_FROM_SCRIPT = true;
		Features.LOAD_FULL_PRICE = true;
		Features.CHECK_UNCOMPLETE_SCRIPTS = true;
		
		Warehouse.activity = WarehouseEx.class;
		Setting.WarehouseSettingActivity = WarehouseSettingEx.class;
		Presentation.activity = PresentationFolder.class;
		PricePresentation.activity = PricePresentationFolder.class;
		DocList.activity = DocListEx.class;
		Documents.activity = DocumentsEx.class;
		OrgTaskList.activity = OrgTaskListEx.class;
		UpdateDB.activity = UpdateDBEx.class;
		PriceCount.activity = PriceCountEx.class;
		CreateReturn.activity = CreateReturnEx.class;
		OrderDetail.activity = OrderDetailEx.class;

		ScriptDefImpl.docInScript.add(IncassDoc.instance());

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
				return new RcvNewHitching(RetMtx.class, "RetMtx");
			}
		}, UpdateDB.GEN_DATA_HITCHING);

		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override
			public Hitching create() {
				return new RcvNewHitching(AgentPlan.class, "AgentPlan");
			}
		}, UpdateDB.GEN_DATA_HITCHING);

		DbObject.regNewDataType(Price.class, PriceEx.class);
		DbObject.regNewDataType(Order.class, OrderEx.class);

		Napoleon.docMenuPrepared.add(new MenuPrepareHitching() {

			@Override
			public void menuPrepared(List<MenuHandler> menu,
					final Activity activity) {
				menu.add(new MenuHandler(activity
						.getString(R.string.agent_route_doc), new Runnable() {

					@Override
					public void run() {
						AgentRouteEdit.open(activity);
					}
				}));
			}
		});

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
		
		Napoleon.docMenuPrepared.add(new MenuPrepareHitching() {

			@Override
			public void menuPrepared(List<MenuHandler> menu,
					final Activity activity) {
				menu.add(new MenuHandler(activity
						.getString(R.string.forsakereturns), new Runnable() {

					@Override
					public void run() {
						ForsakeReturn.open(activity);
					}
				}));
			}
		});
		
		ReadService.recievers.add(new RetNtfyHitching());
		
		potenzialOrgDocFilter = new ArrayList<DocTypeBase>();
		potenzialOrgDocFilter.add(VisitDoc.instance());
		potenzialOrgDocFilter.add(ReturnDoc.instance());
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
