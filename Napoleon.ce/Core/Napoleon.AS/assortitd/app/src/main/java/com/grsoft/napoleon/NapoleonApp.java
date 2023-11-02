/*
 * Copyright (C), 2011, ������� �������������
 * 
 * ���� ��������� (� ����� ������)
 *
 * kki   16/05/2011   creating
 */

package com.grsoft.napoleon;

import android.content.Context;

import com.grsoft.database.Hitching;
import com.grsoft.database.HitchingCtor;
import com.grsoft.database.LoadedOrdersHitching;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.ServerCommand;

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
		DbObject.regNewDataType(Order.class, OrderEx.class);
		UpdateDB.addHitchingCtor(new HitchingCtor(){
			@Override
			public List<Hitching> createList() {
				Hitching[] h = new Hitching[] {
						new LoadedOrdersHitching(),
				};
				return Arrays.asList(h);
			}
		}, UpdateDB.GEN_DATA_HITCHING);
	}

	@Override
	protected void initChildActivity() {
		super.initChildActivity();
		Documents.activity = DocumentsEx.class;
		DocList.activity = DocListEx.class;
	}

	@Override
	protected void initChildFeature() {
		super.initChildFeature();
		Features.SHARED_PICTURES = true;
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
