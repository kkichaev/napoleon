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
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.dataobjects.DeliveryItemEx;
import com.grsoft.dataobjects.IncassDebDistr;
import com.grsoft.dataobjects.IncassDebDistrEx;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.Return;
import com.grsoft.dataobjects.ReturnCause;
import com.grsoft.dataobjects.ReturnItem;
import com.grsoft.dataobjects.ScriptEx;
import com.grsoft.dataobjects.impl.BalanceDeliveryEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.ReturnImplEx;
import com.grsoft.dataobjects.impl.ScriptImplEx;
import com.grsoft.napoleon.documents.DebtDocEx;
import com.grsoft.napoleon.documents.DebtDocList;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.DocTypeBase;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.ServerCommand;
import com.grsoft.script.ScriptEdit;
import com.grsoft.script.dataobjects.Script;
import com.grsoft.script.dataobjects.impl.ScriptImpl;
import com.grsoft.util.AssortmentMatrixAdapter;
import com.grsoft.util.ViewInitializer;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.CheckBox;

public class NapoleonApp extends NapoleonAppBase {
	public List<DocTypeBase> potenzialOrgDocFilter;

	class OrderEditor implements OrderImpl.PropertiesEditor {
		@Override
		public void edit(Context ctx, OrderImpl order, boolean isOldOrder) {
			CreateOrder.open(ctx, order, isOldOrder);
		}
	}
	
	@Override
	protected void defineNewType() {
		DebtDocList.DeliveryType = BalanceDeliveryEx.class;
		DebtDocEx.initialize();
		DbObject.regNewDataType(Price.class, PriceEx.class);
		DbObject.regNewDataType(Order.class, OrderEx.class);
		DbObject.regNewDataType(Org.class, OrgEx.class);
		DbObject.regNewDataType(Delivery.class, DeliveryEx.class);
		DbObject.regNewDataType(Script.class, ScriptEx.class);
		DbObject.regNewDataType(IncassDebDistr.class, IncassDebDistrEx.class);
		
		DocType.addType(ReturnDoc.instance(ReturnImplEx.class));
		
		DataObjectInfo doi = DataObjectInfo.getInstance(); 
		doi.replaceListType(Delivery.class, "items", DeliveryItemEx.class);
		doi.replaceListType(OrderEx.class, "items", OrderItemEx.class);
		doi.replaceListType(Return.class, "items", ReturnItem.class);

		AssortmentMatrixAdapter.PERIOD_IN_MONTH = 3;
		
		potenzialOrgDocFilter = new ArrayList<DocTypeBase>();
		potenzialOrgDocFilter.add(VisitDoc.instance());
	}
	
	@Override protected Class<? extends OrderImplBase<? extends Order>> orderImplType() { return OrderImplEx.class; }
	
	@Override
	protected void initChildFeature() {
		Features.CHECK_UNCOMPLETE_SCRIPTS = true;
		Features.DEL_VISIT_WITHOUT_PHOTO = true;
		Features.RECEIVE_REMNANTS_WHEN_SENDING = true;
		Features.EXCLUDE_RETURN_DOC_SUM_FROM_SCRIPT = true;
		Features.SCRIPT_SUM_ONLY_FOR_SALES = true;
		Features.INCASS_DEBET_DISTRIB = true;
		Features.CANT_SEND_SCRIPT_PART = true;
		Features.SHOW_ORG_ADDRESS = true;
		Features.REPORT_REQUEST = true;
		Features.OK_BTN_INCASS = true;
	}
	
	@Override
	protected void initChildActivity() {
		Warehouse.activity = WarehouseEx.class;
		UpdateDB.activity = UpdateDBEx.class;
		PriceCount.activity = PriceCountEx.class;
		Documents.activity = DocumentsEx.class;
		IncassDebDistrEdit.editActivity = IncassDebDistrEditEx.class;
		ScriptEdit.activity = ScriptEditEx.class;
		
		UpdateDB.initUI = new ViewInitializer(){
			@Override
			public void init(Activity activity) {
				super.init(activity);
				CheckBox cb = (CheckBox) activity.findViewById(R.id.cbRemains);
				cb.setChecked(false);
				cb.setVisibility(View.GONE);
			}
		};
		
		UpdateDB.addHitchingCtor(new HitchingCtor(){@Override
		public Hitching create() {return new Hitching( ReturnCause.class);}}, UpdateDB.GEN_DATA_HITCHING);
	}
	
	@Override
	public void onCreate() {
		ConfigManager.initConfig(new CfgNpl());
		super.onCreate();
		OrderImpl.OrderEditor = new OrderEditor();
		setProgrammVersion();
		
		//NapoleonChat.init(this);
	}

	@Override
	protected void initFeatures() {
		super.initFeatures();
		
		CostStrategy.defaultInstance = new CostStrategyEx();
	}
	
	private void setProgrammVersion() {
		try{
			ServerCommand.ProgramVersion = getResources().getString(R.string.version);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Override
	protected Class<? extends ScriptImpl> scriptImplType() {
		return ScriptImplEx.class;
	}
}
