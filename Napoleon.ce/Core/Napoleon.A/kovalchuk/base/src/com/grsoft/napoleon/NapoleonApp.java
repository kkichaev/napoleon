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
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.dataobjects.DlvRpt;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderImplEx;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DebtDocEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.IncassDoc;
import com.grsoft.napoleon.documents.MonitoringDoc;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.QuestionDoc;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.napoleon.documents.ReturnDocEx;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.napoleon.modules.MonitoringInit;
import com.grsoft.network.ServerCommand;
import com.grsoft.util.AssortmentMatrixAdapter;
import com.grsoft.util.FirstRunInit;
import com.grsoft.util.MenuHandler;
import com.grsoft.util.MenuPrepareHitching;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

public class NapoleonApp extends Application {
	@SuppressWarnings("unused")
	private static final String TAG = "NapoleonApp";
	public List<DocType> potenzialOrgDocFilter;
	
	class OrderEditor implements OrderImpl.PropertiesEditor {
		@Override
		public void edit(Context ctx, OrderImpl order, boolean isOldOrder) {
			CreateOrder.open(ctx, order, isOldOrder);
		}
	}
	
	private void initDocTypes() {
		MonitoringInit.init();
		DebtDocEx.initialize();
		ReturnDocEx.initialize();

		DocType.addType(OrderDoc.instance(OrderImplEx.class));
		DocType.addType(DebtDoc.instance());
		DocType.addType(VisitDoc.instance());
		DocType.addType(RemnantsDoc.instance());
		DocType.addType(MonitoringDoc.instance());
		DocType.addType(IncassDoc.instance());
		DocType.addType(QuestionDoc.instance());
		DocType.addType(ReturnDoc.instance());
		
		DocType.setCurDoc(OrderDoc.instance());
		
//		potenzialOrgDocFilter = new ArrayList<DocType>();
//		potenzialOrgDocFilter.add(VisitDoc.instance());
//		potenzialOrgDocFilter.add(MonitoringDoc.instance());
		
		Warehouse.activity = WarehouseNew.class;
		Documents.activity = DocumentsEx.class;
		PriceCount.activity = PriceCountEx.class;
		Presentation.activity = PresentationFolder.class;
		PricePresentation.activity = PricePresentationFolder.class;
		Warehouse.activity = WarehouseEx.class;
		CreateReturn.activity = CreateReturnEx.class;
		
		DbObject.regNewDataType(Order.class, OrderEx.class);
		DbObject.regNewDataType(Org.class, OrgEx.class);
		DbObject.regNewDataType(Delivery.class, DeliveryEx.class);
		DataObjectInfo doi = DataObjectInfo.getInstance();
		doi.replaceListType(OrderEx.class, "items", OrderItemEx.class);
				
		Features.SCRIPT_DOC = true;
		Features.QUESTION = true;
		Features.RECEIVE_REMNANTS_WHEN_SENDING = true;
		Features.INPUT_QTY_IN_PACK = true;
		Features.ASSORTMENT_MATRIX = true;
		Features.ORG_STOP_TABLE = true;
		Features.BLOCK_IN_STOP_LIST = true;
		Features.USE_COST_IN_RETURNS = true;
		Features.SCRIPT_SUM_ONLY_FOR_SALES = true;
		Features.EXCLUDE_RETURN_DOC_SUM_FROM_SCRIPT = true;

		
		AssortmentMatrixAdapter.PERIOD_IN_MONTH = 2;
		
		UpdateDB.addHitchingCtor(new HitchingCtor() { @Override public Hitching create() { return new RcvNewHitching(DlvRpt.class); 
			} }, UpdateDB.GEN_DATA_HITCHING);
		
		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override public Hitching create() { return new DocumentRestore(IncassDoc.instance()); }
		}, UpdateDB.RESTORE_DATA_HITCHING);
		
		Napoleon.docMenuPrepared.add(new MenuPrepareHitching() {
			
			@Override
			public void menuPrepared(List<MenuHandler> menu, final Activity activity) {
				menu.add(new MenuHandler(getString(R.string.dlvpeport), new Runnable() {
					@Override
					public void run() {	ReportWebView.open(activity); }
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
