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
import com.grsoft.dataobjects.ConfigHelper;
import com.grsoft.dataobjects.ConfigHelper.DlvDateType;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Fridge;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.RejectCause;
import com.grsoft.dataobjects.ScriptItemEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.ScriptImplEx;
import com.grsoft.napoleon.documents.DebtDocEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.InvFrgDoc;
import com.grsoft.napoleon.documents.InvFrgSt1Doc;
import com.grsoft.napoleon.documents.InvFrgSt2Doc;
import com.grsoft.napoleon.documents.InvFrgSt3Doc;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.ServerCommand;
import com.grsoft.script.ScriptEdit;
import com.grsoft.script.dataobjects.Script;
import com.grsoft.script.documents.ScriptDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.DocFilterOnClickListener;
import com.grsoft.util.ViewInitializer;
import android.app.Activity;
import android.content.Context;
import android.view.View;

public class NapoleonApp extends NapoleonAppBase {
	@SuppressWarnings("unused")
	private static final String TAG = "NapoleonApp";
	static final String GLOBAL_PREFERENCES = "global_preferences";
	static final String ID_ORG_IN_WORK = "id_org_in_work";
	
	class OrderEditor implements OrderImpl.PropertiesEditor {
		@Override
		public void edit(Context ctx, OrderImpl order, boolean isOldOrder) {
			CreateOrder.open(ctx, order, isOldOrder);
		}
	}
	
	@Override
	protected void defineNewType() {
		DebtDocEx.initialize();
		ScriptDoc.instance(ScriptImplEx.class);

		DbObject.regNewDataType(Org.class, OrgEx.class);
		DbObject.regNewDataType(Order.class, OrderEx.class);
		DbObject.regNewDataType(Price.class, PriceEx.class);
		DataObjectInfo.getInstance().replaceListType(Order.class, "items", OrderItemEx.class);
		DataObjectInfo.getInstance().replaceListType(Script.class, "items", ScriptItemEx.class);
	
		UpdateDB.initUI = new ViewInitializer() {
			@Override public void init(Activity activity) { activity.findViewById(R.id.cbRemains).setVisibility(View.GONE); }
		};
		
		UpdateDB.addHitchingCtor(new HitchingCtor(){ @Override public Hitching create() { return new RcvNewHitching(Fridge.class); }}, UpdateDB.GEN_DATA_HITCHING);
		UpdateDB.addHitchingCtor(new HitchingCtor(){@Override public Hitching create() {return new RcvNewHitching( RejectCause.class);}}, UpdateDB.GEN_DATA_HITCHING);

	}
	
	@Override
	protected void initChildDocTypes() {
		DocType.addType(ReturnDoc.instance());
		DocType.addType(InvFrgDoc.instance());
		
		DocType.addType(InvFrgSt1Doc.theInstance());
		DocType.addType(InvFrgSt2Doc.theInstance());
		DocType.addType(InvFrgSt3Doc.theInstance());
		
		DocFilterOnClickListener.HiddenTypes.add(InvFrgSt1Doc.theInstance());
		DocFilterOnClickListener.HiddenTypes.add(InvFrgSt2Doc.theInstance());
		DocFilterOnClickListener.HiddenTypes.add(InvFrgSt3Doc.theInstance());
	}
	
	@Override protected Class<? extends OrderImplBase<? extends Order>> orderImplType() { return OrderImplEx.class; }
	
	@Override
	protected void initChildActivity() {
		
		OrderDetail.activity = OrderDetailEx.class;
		PriceCount.activity = PriceCountEx.class;
		Warehouse.activity = WarehouseEx.class;
		Presentation.activity = PresentationFolder.class;
		PricePresentation.activity = PricePresentationFolder.class;
		Documents.activity = DocumentsEx.class;
		ScriptEdit.activity = ScriptEditEx.class;
		
		Warehouse.activity = WarehouseEx.class;
		OrderDetail.activity = OrderDetailEx.class;
		PriceCount.activity = PriceCountEx.class;
		Documents.activity = DocumentsEx.class;
	}
	
	@Override
	protected void initChildFeature() {
		Features.DOC_STATUS_IN_DOC_LIST = true;
		Features.UPDATE_PRICE_BACKGROUND = true;
		Features.CANT_SEND_SCRIPT_PART = true;
		Features.SHOW_DAILY_SALES_IN_WAREHOUSE = true;
		Features.INPUT_QTY_IN_PACK = true;
		Features.SHOW_ORG_ADDRESS = true;
		Features.LOAD_FULL_PRICE = true;
		Features.USE_COST_IN_RETURNS = true;
		Features.WEIGHT_SCALE = 100;
		Features.SCRIPT_SUM_ONLY_FOR_SALES = true;
		Features.EXCLUDE_RETURN_DOC_SUM_FROM_SCRIPT = true;
		
		Features.CHECK_UNCOMPLETE_SCRIPTS = true;
		Features.DEL_VISIT_WITHOUT_PHOTO = true;
		ConfigHelper.DEFAULT_DATE_TYPE = DlvDateType.nextday;
		//Features.START_STOP = true;
		Features.FOCUSED_GROUP = true;
		Features.FOCUSED_ITEMS = true;
		Features.ORG_TASK = true;
		Features.SHOW_DAILY_WEIGHT_IN_WAREHOUSE = true;
		Features.WEIGHT_SCALE = Consts.WEIGHT_SCALE;
	}
	
	@Override
	public void onCreate() {
		ConfigManager.initConfig(new CfgNpl());
		super.onCreate();
		OrderImpl.OrderEditor = new OrderEditor();
		setProgrammVersion();
		
		//NapoleonChat.init(this);
	}

	private void setProgrammVersion() {
		try{
			ServerCommand.ProgramVersion = getResources().getString(R.string.version);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
