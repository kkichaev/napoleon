/*
 * Copyright (C), 2011, Гильдия Разработчиков
 * 
 * Демо программа (с базой данных)
 *
 * kki   16/05/2011   creating
 */

package com.grsoft.napoleon;


import java.util.Arrays;
import java.util.List;

import com.grsoft.database.Hitching;
import com.grsoft.database.HitchingCtor;
import com.grsoft.database.OdometrHitching;
import com.grsoft.database.OrderReservHitching;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.ActCost;
import com.grsoft.dataobjects.OrgCostTypes;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgAssortimentMatrix;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.RemnantItemEx;
import com.grsoft.dataobjects.Remnants;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.RemnantsImplEx;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DistribDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.QuestionDoc;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.napoleon.modules.CostManagerImpl;
import com.grsoft.network.RWServiceFactory;
import com.grsoft.network.RWServiceFactoryEx;
import com.grsoft.network.ReadService;
import com.grsoft.network.ServerCommand;
import com.grsoft.network.WriteService;
import com.grsoft.script.documents.ScriptDoc;
import com.grsoft.util.FirstRunInit;
import com.grsoft.util.MenuHandler;
import com.grsoft.util.MenuPrepareHitching;
import com.grsoft.util.ViewInitializer;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.widget.CheckBox;

public class NapoleonApp extends Application {
	@SuppressWarnings("unused")
	private static final String TAG = "NapoleonApp";
//	static final String GLOBAL_PREFERENCES = "global_preferences";
//	static final String ID_ORG_IN_WORK = "id_org_in_work";
	
	class OrderEditor implements OrderImpl.PropertiesEditor {
		@Override
		public void edit(Context ctx, OrderImpl order, boolean isOldOrder) {
			CreateOrder.open(ctx, order, isOldOrder);
		}
	}
	
	private void initDocTypes() {
		DbObject.regNewDataType(Order.class, OrderEx.class);
		DbObject.regNewDataType(Org.class, OrgEx.class);
		DbObject.regNewDataType(Price.class, PriceEx.class);
		DbObject.regNewDataType(Delivery.class, DeliveryEx.class);
		
		DataObjectInfo doi = DataObjectInfo.getInstance(); 
		doi.replaceListType(Remnants.class, "items", RemnantItemEx.class);
		doi.replaceListType(Order.class, "items", OrderItemEx.class);
		
		DocType.addType(OrderDoc.instance(OrderImplEx.class));
		DocType.addType(DebtDoc.instance());
		DocType.addType(VisitDoc.instance());
		DocType.addType(DistribDoc.instance());
		DocType.addType(QuestionDoc.instance());
		DocType.addType(RemnantsDoc.instance(RemnantsImplEx.class));

		DocType.addType(ScriptDoc.instance());
		
		DocType.setCurDoc(OrderDoc.instance());		

		Warehouse.activity = WarehouseEx.class;
		PriceCount.activity = PriceCountEx.class;
		Presentation.activity = PresentationFolder.class;
		PricePresentation.activity = PricePresentationFolder.class;
		Documents.activity = DocumentsEx.class;
		DocList.activity = DocListEx.class;
		
		Features.COST_MANAGER = new CostManagerImpl();
		Features.HAVE_VISIT_CAUSE = true;
		Features.ASSORTMENT_MATRIX= true;
		Features.SYNC_INFO = true;
		Features.BLOCK_IN_STOP_LIST = true;
		Features.SHOW_ORG_ADDRESS = true;
		Features.DELIVERY_REPLACE_ORDER_SUM = true;
		
		CostStrategy.defaultInstance = new CostStrategyEx();
		
		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override
			public List<Hitching> createList() {
				CostStrategyEx.clearCache();
				Hitching[] ret = new Hitching[] {
					new RcvNewHitching(ActCost.class, "ActionCost"),
					new RcvNewHitching(OrgAssortimentMatrix.class, "OrgAssortimentMatrix"),
					new RcvNewHitching(OrgCostTypes.class, "CostTypes"),
				};
				return Arrays.asList(ret);
			}
		}, UpdateDB.GEN_DATA_HITCHING);

		UpdateDB.addHitchingCtor(new HitchingCtor() {	
			@Override public Hitching create() { return new OdometrHitching();	}
		}, UpdateDB.EXPORT_DATA_HITCHING);
		
		UpdateDB.initUI = new ViewInitializer() {
			@Override public void init(Activity activity) { ((CheckBox)activity.findViewById(R.id.cbCost)).setChecked(true); }
		};
		
		RWServiceFactory.instance = new RWServiceFactoryEx();
		
		OrderReservHitching ddh = new OrderReservHitching();
		ReadService.recievers.add(ddh);
		WriteService.recievers.add(ddh);
		
//		DocType.SumConverter = new TotalSumConvertor() {
//			@Override public String toString(long sum) { return Util.IntToScaleStr(sum/100, 1); }
//		};
		
		Napoleon.docMenuPrepared.add(new MenuPrepareHitching() {
			
			@Override
			public void menuPrepared(List<MenuHandler> menu, final Activity activity) {
				menu.add(new MenuHandler(getString(R.string.DocStatuses), new Runnable() {
					@Override public void run() { StatusEdit.open(activity); }
				}));
			}
		});
		DocStatus.initResources(this);
		
//		DocType.SumConverter = new TotalSumConvertor(){
//			@Override
//			public String toString(long sum) {
//				return Util.IntToScaleStr(sum, Consts.SUM_SCALE, Util.DEC_DELIM, true);
//			}
//		};
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
	
//	public String getInWork(){
//		SharedPreferences pref = getSharedPreferences(NapoleonApp.GLOBAL_PREFERENCES, Context.MODE_PRIVATE);
//		return pref.getString(NapoleonApp.ID_ORG_IN_WORK, "");
//	}
//	
//	public void putInWork(String inWork) {
//		SharedPreferences pref = getSharedPreferences(NapoleonApp.GLOBAL_PREFERENCES, Context.MODE_PRIVATE);
//		SharedPreferences.Editor ed = pref.edit();
//		ed.putString(NapoleonApp.ID_ORG_IN_WORK, inWork);
//		ed.commit();
//	}

	
}
