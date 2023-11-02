/*
 * Copyright (C), 2011, Гильдия Разработчиков
 * 
 * Демо программа (с базой данных)
 *
 * kki   16/05/2011   creating
 */

package com.grsoft.napoleon;

import java.util.ArrayList;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.view.View;
import com.grsoft.database.Hitching;
import com.grsoft.database.HitchingCtor;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.ContractDef;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.ScrAssign;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.ScriptImplEx;
import com.grsoft.napoleon.documents.ContractDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.QuestionDoc;
import com.grsoft.napoleon.documents.ScriptDocEx;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.napoleon.documents.VisitDocEx;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.ServerCommand;
import com.grsoft.script.ScriptEdit;
import com.grsoft.util.DocFilterOnClickListener;
import com.grsoft.util.FirstRunInit;
import com.grsoft.util.MenuHandler;
import com.grsoft.util.MenuPreparedEvent;
import com.grsoft.util.ViewInitializer;

public class NapoleonApp extends Application {
	@SuppressWarnings("unused")
	private static final String TAG = "NapoleonApp";
	
	class OrderEditor implements OrderImpl.PropertiesEditor {
		@Override
		public void edit(Context ctx, OrderImpl order, boolean isOldOrder) {
			CreateOrder.open(ctx, order, isOldOrder);
		}
	}
	
	private void initDocTypes() {
		DocType.addType(VisitDocEx.instance());
		DocType.addType(QuestionDoc.instance());
		DocType.addType(ContractDoc.instance());
		
		ScriptDocEx.instance(ScriptImplEx.class);
		
		DocType.setCurDoc(VisitDoc.instance());
		
		DbObject.regNewDataType(Org.class, OrgEx.class);
		
		Features.SCRIPT_DOC = true;
		Features.POTENZIAL_ORG = false;
		Features.SHOW_ORG_ADDRESS = true;
		
		Warehouse.activity = WarehouseEx.class;
		Presentation.activity = PresentationFolder.class;
		PricePresentation.activity = PricePresentationFolder.class;
		Documents.activity = DocumentsEx.class;
		ScriptEdit.activity = ScriptEditEx.class;
		UpdateDB.activity = UpdateDBEx.class;
		Setting.activity = SettingEx.class;
		
		DocFilterOnClickListener.HiddenTypes.add(QuestionDoc.instance());
		DocFilterOnClickListener.HiddenTypes.add(ContractDoc.instance());
		
		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override
			public Hitching create() {return new RcvNewHitching(ContractDef.class);}
		}, UpdateDB.GEN_DATA_HITCHING);
		
		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override
			public Hitching create() {return new RcvNewHitching(ScrAssign.class);}
		}, UpdateDB.GEN_DATA_HITCHING);
		
		CfgNpl config = (CfgNpl) ConfigManager.getConfig();
		config.priceClmn3Type = WarehouseNew.COLUMN_QTY_ORD;
		ConfigManager.save();
		
		UpdateDB.initUI = new ViewInitializer(){
			@Override
			public void init(Activity activity) {
				activity.findViewById(R.id.cbPresent).setVisibility(View.GONE);
				activity.findViewById(R.id.cbDebt).setVisibility(View.GONE);
				activity.findViewById(R.id.cbRecreateStory).setVisibility(View.GONE);
				activity.findViewById(R.id.spMonthRecreate).setVisibility(View.GONE);
			}
		};
		
		Napoleon.mainMenuPrepared = new MenuPreparedEvent(){
			private static final long serialVersionUID = 1L;

			@Override
			public void menuPrepared(ArrayList<MenuHandler> menu, final  Activity activity) {
				menu.add(2, new MenuHandler(getString(R.string.msg_list), new Runnable() {			
					@Override public void run() { Messages.open(activity); }
				}));
				
				for (MenuHandler h : menu){
					if(h.name.equals(getString(R.string.docs))){
						menu.remove(h);
						break;
					}
				}
					
			}
		};
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		FirstRunInit.init(this);
		initDocTypes();
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
