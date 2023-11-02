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
import com.grsoft.dataobjects.AgentPlan;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.dataobjects.Inventory;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.InvAuditDoc;
import com.grsoft.napoleon.documents.OrderDocEx;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.ServerCommand;
import com.grsoft.script.ScriptEdit;
import com.grsoft.util.MenuHandler;
import com.grsoft.util.MenuPrepareHitching;
import com.grsoft.util.ViewInitializer;
import android.app.Activity;
import android.content.Context;
import android.widget.CheckBox;

public class NapoleonApp extends NapoleonAppBase {
	
	class OrderEditor implements OrderImpl.PropertiesEditor {
		@Override
		public void edit(Context ctx, OrderImpl order, boolean isOldOrder) {
			CreateOrder.open(ctx, order, isOldOrder);
		}
	}
	
	@Override
	public void onCreate() {
		ConfigManager.initConfig(new CfgNpl());
		super.onCreate();
		OrderImpl.OrderEditor = new OrderEditor();
		setProgrammVersion();
		
		UpdateDB.addHitchingCtor(new HitchingCtor() { @Override public Hitching create() { return new Hitching(AgentPlan.class, "AgentPlan"); }}, UpdateDB.GEN_DATA_HITCHING);
		
		UpdateDB.addHitchingCtor(new HitchingCtor() { @Override public Hitching create() { return new Hitching(Inventory.class); }}, UpdateDB.GEN_DATA_HITCHING);
		
		Main.docMenuPrepared.add(new MenuPrepareHitching() {
			@Override public void menuPrepared(List<MenuHandler> menu, final Activity activity) { menu.add(new MenuHandler(activity .getString(R.string.plans), new Runnable() {
					@Override public void run() { AgentPlanView.open(activity);	}
				}));
			}
		});
		
		UpdateDB.initUI = new ViewInitializer(){
			@Override
			public void init(Activity activity) {
				((CheckBox) activity.findViewById(R.id.cbDebt)).setChecked(true);
			}
		};

	}

	private void setProgrammVersion() {
		try{
			ServerCommand.ProgramVersion = getResources().getString(R.string.version);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Override
	protected void initFeatures() {
		super.initFeatures();
		Features.HAVE_PRICE_MOVER  = true;
		Features.LOAD_FULL_PRICE = true;
		Features.INPUT_QTY_IN_PACK = true;
		Features.BTN_NEW_DOC_INVISIBLE = true;
		Features.OK_BTN_INCASS = true;
		Features.OPEN_LAST_MATRIX = true;
		Features.REPORT_REQUEST = true;
		Features.DEL_VISIT_WITHOUT_PHOTO = true;
		Features.CHECK_UNCOMPLETE_SCRIPTS = true;
	}
	
	@Override
	protected void initAcivity() {
		super.initAcivity();
		Warehouse.activity = WarehouseEx.class;
		PriceCount.activity = PriceCountEx.class;
		IncassEdit.activity = IncassEditEx.class;
		Documents.activity = DocumentsEx.class;
		ScriptEdit.activity = ScriptEditEx.class;
	}
	
	@Override
	protected void initChildDocTypes() {
		DocType.addType(InvAuditDoc.instance());
	}
	
	@Override
	protected void defineNewType() {
		OrderDocEx.init();
		
		DbObject.regNewDataType(Order.class, OrderEx.class);
		DbObject.regNewDataType(Delivery.class, DeliveryEx.class);
//		DbObject.regNewDataType(Incass.class, IncassEx.class);
		DbObject.regNewDataType(Org.class, OrgEx.class);
	}
	
}
