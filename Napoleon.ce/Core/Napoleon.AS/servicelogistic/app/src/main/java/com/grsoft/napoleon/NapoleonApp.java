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

import com.grsoft.database.Hitching;
import com.grsoft.database.HitchingCtor;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgAsmMatrix;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.ServerCommand;
import com.grsoft.util.AssortmentMatrixAdapter;
import com.grsoft.util.ViewInitializer;

import java.util.Arrays;
import java.util.List;

public class NapoleonApp extends NapoleonAppBase {
	
	class OrderEditor implements OrderImpl.PropertiesEditor {
		@Override
		public void edit(Context ctx, OrderImpl order, boolean isOldOrder) {
			CreateOrder.open(ctx, order, isOldOrder);
		}
	}

	@Override
	protected void defineNewType() {
		OrderDocEx.init();

		super.defineNewType();

		AssortmentMatrixAdapter.PERIOD_IN_MONTH = 3;

		DbObject.regNewDataType(Order.class, OrderEx.class);
		DbObject.regNewDataType(Org.class, OrgEx.class);

		UpdateDB.addHitchingCtor(new HitchingCtor(){
			@Override
			public List<Hitching> createList() {
				Hitching[] h = new Hitching[] {
						new RcvNewHitching(OrgAsmMatrix.class),
				};
				return Arrays.asList(h);
			}
		}, UpdateDBW.GEN_DATA_HITCHING);

		UpdateDB.initUI = new ViewInitializer(){
			@Override
			public void init(Activity activity) {
				super.init(activity);
				((CheckBox)activity.findViewById(R.id.cbDebt)).setChecked(true);
			}
		};
	}

	@Override
	protected Class<? extends OrderImplBase<? extends Order>> orderImplType() { return OrderImplEx.class; }

	@Override
	protected void initChildActivity() {
		super.initChildActivity();

		Documents.activity = DocumentsEx.class;
		Warehouse.activity = WarehouseEx.class;
		OrderDetail.activity = OrderDetailEx.class;
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
