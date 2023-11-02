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
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.OrgProp;
import com.grsoft.dataobjects.RemnantItemEx;
import com.grsoft.dataobjects.Remnants;
import com.grsoft.dataobjects.impl.AMGScriptResolver;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.RemnantsImpl;
import com.grsoft.dataobjects.impl.RemnantsImplEx;
import com.grsoft.napoleon.documents.QuestionDocEx;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.ServerCommand;
import com.grsoft.script.dataobjects.impl.ScriptDefImpl;

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

	@Override
	protected void defineNewType() {
		QuestionDocEx.init();
		ScriptDefImpl.resolver = new AMGScriptResolver();

		UpdateDB.addHitchingCtor(new HitchingCtor(){
			@Override
			public List<Hitching> createList() {
				Hitching[] data = new Hitching[] {
						new RcvNewHitching(OrgProp.class),
				};
				return Arrays.asList(data);
			}
		}, UpdateDB.GEN_DATA_HITCHING);

		DataObjectInfo.getInstance().replaceListType(Remnants.class, "items", RemnantItemEx.class);
	}

	@Override
	protected Class<? extends RemnantsImpl> remnantsImplType() { return RemnantsImplEx.class; }

	@Override
	protected void initChildActivity() {
	}

	@Override
	protected void initChildFeature() {
		Features.MAX_FOTO_HEIGHT = 4000;
		Features.MAX_FOTO_WIDTH = 4000;
		Features.UNLIMIT_VISIT_ITEMS = true;
		Features.NO_SCRIPT_CONFIG = true;
		Features.POTENZIAL_ORG = false;
	}
}
