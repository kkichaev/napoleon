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

import java.util.List;

import com.grsoft.database.Hitching;
import com.grsoft.database.HitchingCtor;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.AgentPlan;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.Incass;
import com.grsoft.dataobjects.IncassEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.ServerCommand;
import com.grsoft.util.MenuHandler;
import com.grsoft.util.MenuPrepareHitching;

public class NapoleonApp extends NapoleonAppBase {
	
	class OrderEditor implements OrderImpl.PropertiesEditor {
		@Override
		public void edit(Context ctx, OrderImpl order, boolean isOldOrder) {
			CreateOrder.open(ctx, order, isOldOrder);
		}
	}
	
	@Override
	protected void defineNewType() {
		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override public Hitching create() { return new RcvNewHitching(AgentPlan.class, "AgentPlan"); }
		}, UpdateDB.GEN_DATA_HITCHING);
		
		Main.docMenuPrepared.add(new MenuPrepareHitching() {
			@Override public void menuPrepared(List<MenuHandler> menu, final Activity activity) {
				menu.add(new MenuHandler("Планы", new Runnable() {
					@Override public void run() { AgentPlanView.open(activity); }
				})); 
			}
		});
		
		DbObject.regNewDataType(Incass.class, IncassEx.class);
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
	protected void initChildActivity() {
		super.initChildActivity();
		
		IncassEdit.activity = IncassEditEx.class;
	}
}
