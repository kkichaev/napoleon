/*
 * Copyright (C), 2011, ������� �������������
 * 
 * ���� ��������� (� ����� ������)
 *
 * kki   16/05/2011   creating
 */

package com.grsoft.napoleon;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.CheckBox;

import com.grsoft.database.Hitching;
import com.grsoft.database.HitchingCtor;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.Firm;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrderImplEx;
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
		ConfigManager.initConfig(new CfgNpl());
		super.onCreate();

		OrderImpl.OrderEditor = new OrderEditor();
		setProgrammVersion();
		
		//NapoleonChat.init(this);

		UpdateDBW.addHitchingCtor(new HitchingCtor(){
			@Override
			public Hitching create() {
				return new RcvNewHitching(Firm.class);
			}
		}, UpdateDBW.GEN_DATA_HITCHING);

		UpdateDBW.initUI = new ViewInitializer(){
			@Override
			public void init(Activity activity) {
				super.init(activity);

				CheckBox cb = activity.findViewById(R.id.cbRemains);
				cb.setVisibility(View.GONE);
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
	protected void initChildDocTypes() {
		super.initChildDocTypes();

		DbObject.regNewDataType(Price.class, PriceEx.class);
	}

	@Override
	protected Class<? extends OrderImplBase<? extends Order>> orderImplType() {
		return OrderImplEx.class;
	}

	@Override
	protected void initChildActivity() {
		super.initChildActivity();

		Warehouse.activity = WarehouseEx.class;
		PriceCount.activity = PriceCountEx.class;
	}

	@Override
	protected void initChildFeature() {
		super.initChildFeature();

		Features.LOAD_FULL_PRICE = true;
	}
}
