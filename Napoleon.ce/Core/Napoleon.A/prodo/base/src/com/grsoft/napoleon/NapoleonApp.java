/*
d * Copyright (C), 2011, Гильдия Разработчиков
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
import android.content.SharedPreferences;
import android.widget.CheckBox;
import com.grsoft.database.Hitching;
import com.grsoft.database.HitchingCtor;
import com.grsoft.database.LoadOrdersHitching;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.dataobjects.DistribMatrix;
import com.grsoft.dataobjects.MatrixOrder;
import com.grsoft.dataobjects.OffTakeCoeff;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgCost;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgMargin;
import com.grsoft.dataobjects.OrgPlan;
import com.grsoft.dataobjects.Payment;
import com.grsoft.dataobjects.PaymentEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.Return;
import com.grsoft.dataobjects.ReturnItemEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.RemnantsImplEx;
import com.grsoft.dataobjects.impl.ReturnImplEx;
import com.grsoft.napoleon.documents.BonusDoc;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.IncassDoc;
import com.grsoft.napoleon.documents.MonitoringDoc;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.OrgDistribDoc;
import com.grsoft.napoleon.documents.QuestionDoc;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.napoleon.documents.TaskDoneDoc;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.napoleon.util.ProdoCfg;
import com.grsoft.network.RWServiceFactory;
import com.grsoft.network.RWServiceFactoryEx;
import com.grsoft.network.ServerCommand;
import com.grsoft.script.documents.ScriptDoc;
import com.grsoft.util.FirstRunInit;
import com.grsoft.util.MenuHandler;
import com.grsoft.util.MenuPrepareHitching;
import com.grsoft.util.ViewInitializer;

public class NapoleonApp extends Application {
	@SuppressWarnings("unused")
	private static final String TAG = "NapoleonApp";
	static final String GLOBAL_PREFERENCES = "global_preferences";
	static final String ID_ORG_IN_WORK = "id_org_in_work";

	private final String SET_MAX_PKG_LEN = "SET_MAX_PKG_LEN";
	
	class OrderEditor implements OrderImpl.PropertiesEditor {
		@Override
		public void edit(Context ctx, OrderImpl order, boolean isOldOrder) {
			CreateOrder.open(ctx, order, isOldOrder);
		}
	}
	
	private void initDocTypes() {
		DbObject.regNewDataType(Price.class, PriceEx.class);
		DbObject.regNewDataType(Org.class, OrgEx.class);
		DbObject.regNewDataType(Delivery.class, DeliveryEx.class);
		DbObject.regNewDataType(Payment.class, PaymentEx.class);
		DbObject.regNewDataType(Order.class, OrderEx.class);
		
		DataObjectInfo.getInstance().replaceListType(Return.class, "items", ReturnItemEx.class);
		
		DocType.addType(OrderDoc.instance());
		DocType.addType(IncassDoc.instance());
		DocType.addType(DebtDoc.instance());
		DocType.addType(ReturnDoc.instance(ReturnImplEx.class));
		DocType.addType(OrgDistribDoc.instance());
		DocType.addType(VisitDoc.instance());
		DocType.addType(RemnantsDoc.instance(RemnantsImplEx.class));
		DocType.addType(ScriptDoc.instance());
		DocType.addType(QuestionDoc.instance());
		DocType.addType(TaskDoneDoc.instance());
		DocType.addType(MonitoringDoc.instance());
		DocType.addType(BonusDoc.instance());
		
		DocType.setCurDoc(OrderDoc.instance());		

		Warehouse.activity = WarehouseEx.class;
		Presentation.activity = PresentationFolder.class;
		PricePresentation.activity = PricePresentationFolder.class;
		UpdateDB.activity = UpdateDBEx.class;
		Documents.activity = DocumentsEx.class;
		PriceCount.activity = PriceCountEx.class;
		Setting.PhotoSettingActivity = PhotoSettingEx.class;
		
		CostStrategy.defaultInstance = new CostStrategyEx();
		Features.ORG_STOP_TABLE = true;
		Features.CANT_SEND_SCRIPT_PART = true;
		Features.USE_MATRIX_ORDER = true;
		Features.UPDATE_DB_CHECK_VISITS = true;
		Features.MAX_FOTO_HEIGHT = 1300;
		Features.MAX_FOTO_WIDTH = 1300;
		
		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override public Hitching create() { return new RcvNewHitching(OrgCost.class, "OrgCost"); }
		}, UpdateDB.GEN_DATA_HITCHING);
		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override public Hitching create() { return new RcvNewHitching(MatrixOrder.class, "MatrixOrder"); }
		}, UpdateDB.GEN_DATA_HITCHING);
		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override public Hitching create() { return new RcvNewHitching(OffTakeCoeff.class, "OffTakeCoeff"); }
		}, UpdateDB.GEN_DATA_HITCHING);
		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override public Hitching create() { return new RcvNewHitching(OrgMargin.class, "OrgMargins"); }
		}, UpdateDB.GEN_DATA_HITCHING);
		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override public Hitching create() { return new RcvNewHitching(DistribMatrix.class, "DistributionMatrix"); }
		}, UpdateDB.GEN_DATA_HITCHING);
		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override public Hitching create() { return new Hitching(OrgPlan.class); }
		}, UpdateDB.GEN_DATA_HITCHING);

		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override public Hitching create() { return new LoadOrdersHitching(); }
		}, UpdateDB.DEBET_DATA_HITCHING);

		RWServiceFactory.instance = new RWServiceFactoryEx();
		UpdateDB.initUI = new ViewInitializer(){ @Override public void init(Activity activity) { ((CheckBox)activity.findViewById(R.id.cbVisit)).setChecked(true);	}};
		
		SharedPreferences pref = getSharedPreferences(GLOBAL_PREFERENCES, Context.MODE_PRIVATE);
		
		if (pref.getBoolean(SET_MAX_PKG_LEN, false) == false){
			CfgNpl cfgNpl = (CfgNpl) ConfigManager.getConfig();
			cfgNpl.max_packet_len = 5000000L;
			
			try{
				ConfigManager.save(this);
			}catch(Exception e){}
			
			pref.edit().putBoolean(SET_MAX_PKG_LEN, true).commit();
		}
		
		Napoleon.docMenuPrepared.add(new MenuPrepareHitching() {
			
			@Override
			public void menuPrepared(List<MenuHandler> menu, final Activity activity) {
				menu.add(new MenuHandler("Планы", new Runnable() {
					@Override
					public void run() {
						OrgPlanView.open(activity);
					}
				}));
			}
		});
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		ConfigManager.initConfig(new ProdoCfg());
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

	public String getInWork(){
		SharedPreferences pref = getSharedPreferences(NapoleonApp.GLOBAL_PREFERENCES, Context.MODE_PRIVATE);
		return pref.getString(NapoleonApp.ID_ORG_IN_WORK, "");
	}
	
	public void putInWork(String inWork) {
		SharedPreferences pref = getSharedPreferences(NapoleonApp.GLOBAL_PREFERENCES, Context.MODE_PRIVATE);
		SharedPreferences.Editor ed = pref.edit();
		ed.putString(NapoleonApp.ID_ORG_IN_WORK, inWork);
		ed.commit();
	}
}
