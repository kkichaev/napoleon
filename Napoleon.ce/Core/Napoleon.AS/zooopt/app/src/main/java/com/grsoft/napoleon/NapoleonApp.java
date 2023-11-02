/*
 * Copyright (C), 2011, Гильдия Разработчиков
 * 
 * Демо программа (с базой данных)
 *
 * kki   16/05/2011   creating
 */

package com.grsoft.napoleon;

import android.content.Context;
import android.widget.CheckBox;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.napoleon.documents.DebtDocEx;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.ServerCommand;
import com.grsoft.util.ViewInitializer;

public class NapoleonApp extends NapoleonAppBase {
	
	class OrderEditor implements OrderImpl.PropertiesEditor {
		@Override
		public void edit(Context ctx, OrderImpl order, boolean isOldOrder) {
			CreateOrder.open(ctx, order, isOldOrder);
		}
	}
	
	@Override
	public void onCreate() {
		Features.VER_4_1 = true;
		DebtDocEx.initialize();
		ConfigManager.initConfig(new CfgNpl());
		super.onCreate();
		OrderImpl.OrderEditor = new OrderEditor();
		setProgrammVersion();

		UpdateDB.initUI = new ViewInitializer(){
			public void init(android.app.Activity activity) {
				((CheckBox)activity.findViewById(R.id.cbDebt)).setChecked(true);
			};
		};

		//NapoleonChat.init(this);
	}

	private void setProgrammVersion() {
		try{
			ServerCommand.ProgramVersion = getResources().getString(R.string.version);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	protected void initChildDocTypes() {
		super.initChildDocTypes();
		DbObject.regNewDataType(Org.class, OrgEx.class);
		DbObject.regNewDataType(Order.class, OrderEx.class);
	}

	@Override
	protected void initChildFeature() {
		super.initChildFeature();
		Features.ID_COLUMN_IN_PRICE_LIST = true;
		Features.QUESTION = true;
		Features.SCRIPT_DOC = true;
		Features.PACK_INPUT = true;
		Features.HAVE_PRICE_MOVER = true;
		Features.ASSORTMENT_MATRIX = true;
	}

	@Override
	protected void initChildActivity() {
		super.initChildActivity();
		UpdateDB.activity = UpdateDBEx.class;
	}
}
