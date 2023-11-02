/*
 * Copyright (C), 2011, Гильдия Разработчиков
 * 
 * Демо программа (с базой данных)
 *
 * kki   16/05/2011   creating
 */

package com.grsoft.napoleon;

import java.util.List;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import com.grsoft.database.Hitching;
import com.grsoft.database.HitchingCtor;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.ItemGroups;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.RemnantsImplEx;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.napoleon.documents.TaskDoneDoc;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.network.RWServiceFactory;
import com.grsoft.network.RWServiceFactoryEx;
import com.grsoft.network.ServerCommand;
import com.grsoft.util.FirstRunInit;
import com.grsoft.util.MenuHandler;
import com.grsoft.util.MenuPrepareHitching;

public class NapoleonApp extends Application {
	@SuppressWarnings("unused")
	private static final String TAG = "NapoleonApp";
	static final String GLOBAL_PREFERENCES = "global_preferences";
	static final String ID_ORG_IN_WORK = "id_org_in_work";
	
	class OrderEditor implements OrderImpl.PropertiesEditor {
		@Override
		public void edit(Context ctx, OrderImpl order, boolean isOldOrder) {
			CreateOrder.open(ctx, order, isOldOrder);
		}
	}
	
	private void initDocTypes() {
		DocType.addType(OrderDoc.instance());
		DocType.addType(DebtDoc.instance());
		DocType.addType(VisitDoc.instance());
		DocType.addType(RemnantsDoc.instance(RemnantsImplEx.class));
		DocType.addType(TaskDoneDoc.instance());
		
		DocType.setCurDoc(OrderDoc.instance());		

		Warehouse.activity = WarehouseEx.class;
		Presentation.activity = PresentationFolder.class;
		PricePresentation.activity = PricePresentationFolder.class;
		UpdateDB.activity = UpdateDBEx.class;
		OrderDetail.activity = OrderDetailEx.class;
		Documents.activity = DocumentsEx.class;
		PriceCount.activity = PriceCountEx.class;
		
		Features.ORG_TASK = true; 
		
		Napoleon.docMenuPrepared.add(new MenuPrepareHitching() {

			@Override
			public void menuPrepared(List<MenuHandler> menu,
					final Activity activity) {
				menu.add(new MenuHandler(activity
						.getString(R.string.agent_route_doc), new Runnable() {

					@Override
					public void run() {
						AgentRouteEdit.open(activity);
					}
				}));
			}
		});
		
		RWServiceFactory.instance = new RWServiceFactoryEx();
		
		UpdateDB.addHitchingCtor(new HitchingCtor() { @Override public Hitching create() { return new RcvNewHitching(ItemGroups.class, "ItemGroups"); }	}, UpdateDB.GEN_DATA_HITCHING);
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
	
	public String getInWork(){
		SharedPreferences pref = getSharedPreferences(NapoleonApp.GLOBAL_PREFERENCES, Context.MODE_PRIVATE);
		return pref.getString(NapoleonApp.ID_ORG_IN_WORK, "");
	}
	
	public void putInWork(String inWork) {
		SharedPreferences pref = getSharedPreferences(NapoleonApp.GLOBAL_PREFERENCES, Context.MODE_PRIVATE);
		SharedPreferences.Editor ed = pref.edit();
		ed.putString(NapoleonApp.ID_ORG_IN_WORK, inWork);
		ed.commit();
	}

}
