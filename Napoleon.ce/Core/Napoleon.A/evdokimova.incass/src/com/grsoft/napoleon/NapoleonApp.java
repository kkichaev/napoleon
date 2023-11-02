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

import java.util.Iterator;
import java.util.List;

import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.dataobjects.IncassDebDistr;
import com.grsoft.dataobjects.IncassItemEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.IncassDoc;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.QuestionDoc;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.napoleon.documents.TaskDoneDoc;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.ServerCommand;
import com.grsoft.script.documents.ScriptDoc;
import com.grsoft.util.DocFilterOnClickListener;
import com.grsoft.util.MenuHandler;
import com.grsoft.util.MenuPrepareHitching;
import com.grsoft.util.ViewInitializer;

public class NapoleonApp extends NapoleonAppBase {
	
	class OrderEditor implements OrderImpl.PropertiesEditor {
		@Override
		public void edit(Context ctx, OrderImpl order, boolean isOldOrder) {
			CreateOrder.open(ctx, order, isOldOrder);
		}
	}
	
	@Override
	protected void defineNewType() {
		super.defineNewType();
		
		DbObject.regNewDataType(Delivery.class, DeliveryEx.class);
		DataObjectInfo.getInstance().replaceListType(IncassDebDistr.class, "items", IncassItemEx.class);
		
		UpdateDB.initUI = new ViewInitializer() {
			@Override
			public void init(Activity activity) {
				CheckBox cb = (CheckBox) activity.findViewById(R.id.cbDebt);
				cb.setChecked(true);
			}
		};
	}
	
	@Override
	protected void initChildFeature() {
		Features.INCASS_DEBET_DISTRIB = true;
		
		DocFilterOnClickListener.HiddenTypes.add(OrderDoc.instance());
		DocFilterOnClickListener.HiddenTypes.add(VisitDoc.instance());
		DocFilterOnClickListener.HiddenTypes.add(RemnantsDoc.instance());
		DocFilterOnClickListener.HiddenTypes.add(QuestionDoc.instance());
		DocFilterOnClickListener.HiddenTypes.add(ScriptDoc.instance());
		DocFilterOnClickListener.HiddenTypes.add(TaskDoneDoc.instance());
		DocFilterOnClickListener.HiddenTypes.add(ReturnDoc.instance());
		
		Main.docMenuPrepared.add(new MenuPrepareHitching() {
			
			@Override
			public void menuPrepared(List<MenuHandler> menu, Activity activity) {
				Iterator<MenuHandler> i = menu.iterator();
				while(i.hasNext()) {
					MenuHandler h = i.next();
					if(h.name.equals(getString(R.string.price_list)))
						i.remove();
				}
			}
		});
		
		Features.POTENZIAL_ORG = false;
	}
	
	@Override
	protected void initChildActivity() {
		super.initChildActivity();
		IncassDebDistrEdit.editActivity = IncassDebDistrEditEx.class;
	}
	
	@Override
	public void setDefDocType() {
		DocType.setCurDoc(IncassDoc.instance());		
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
