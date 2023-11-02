/*
 * Copyright (C), 2011, Гильдия Разработчиков
 * 
 * Демо программа (с базой данных)
 *
 * kki   16/05/2011   creating
 */

package com.grsoft.napoleon;

import android.app.Activity;
import android.content.Context;
import android.widget.CheckBox;

import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.napoleon.modules.CostManagerImpl;
import com.grsoft.network.ServerCommand;
import com.grsoft.util.FirstRunInit;
import com.grsoft.util.ViewInitializer;

public class NapoleonApp extends NapoleonAppBase {
	@SuppressWarnings("unused")
	private static final String TAG = "NapoleonApp";
	
	class OrderEditor implements OrderImpl.PropertiesEditor {
		@Override
		public void edit(Context ctx, OrderImpl order, boolean isOldOrder) {
			CreateOrder.open(ctx, order, isOldOrder);
		}
	}
	
	@Override
	protected void defineNewType() {
		UpdateDB.initUI = new ViewInitializer() {
			@Override
			public void init(Activity activity) {
				CheckBox cb = (CheckBox)activity.findViewById(R.id.cbCost);
				if(cb != null)
					cb.setChecked(true);
			}
		};
		
		CostStrategy.defaultInstance = new CostStrategyEx();
	}
	
	@Override
	protected void initChildFeature() {
		Features.INPUT_QTY_IN_PACK = true;
		Features.COST_MANAGER = new CostManagerImpl();
	}
	
//	private void initDocTypes() {
//		DocType.addType(OrderDoc.instance());
//		DocType.addType(DebtDoc.instance());
//		DocType.addType(VisitDoc.instance());
//		DocType.addType(RemnantsDoc.instance());
//		
//		DocType.setCurDoc(OrderDoc.instance());		
//
//		Warehouse.activity = WarehouseNew.class;
//		
//	}
	
	@Override
	public void onCreate() {
		super.onCreate();

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
