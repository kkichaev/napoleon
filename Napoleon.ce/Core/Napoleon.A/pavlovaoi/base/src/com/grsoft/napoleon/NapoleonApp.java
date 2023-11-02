/*
 * Copyright (C), 2011, Гильдия Разработчиков
 * 
 * Демо программа (с базой данных)
 *
 * kki   16/05/2011   creating
 */

package com.grsoft.napoleon;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.grsoft.dataobjects.Config;
import com.grsoft.dataobjects.Incass;
import com.grsoft.dataobjects.IncassEx;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.IncassDoc;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.napoleon.modules.CostManagerImpl;
import com.grsoft.napoleon.modules.print.Print;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.NetworkBroadcasts;
import com.grsoft.network.ServerCommand;
import com.grsoft.util.FirstRunInit;
import com.grsoft.util.NapoleonServiceW;

public class NapoleonApp extends Application {
	@SuppressWarnings("unused")
	private static final String TAG = "NapoleonApp";
	
	class OrderEditor implements OrderImpl.PropertiesEditor {
		@Override
		public void edit(Context ctx, OrderImpl order, boolean isOldOrder) {
			CreateOrder.open(ctx, order, isOldOrder);
		}
	}
	
	protected void initDocTypes() {
		Print.init();
		
		ServerCommand.Category = "pda";
		
		DbObject.regNewDataType(Order.class, OrderEx.class);
		DbObject.regNewDataType(Incass.class, IncassEx.class);
		DbObject.regNewDataType(Price.class, PriceEx.class);
		DbObject.regNewDataType(Org.class, OrgEx.class);
		
		DocType.addType(OrderDoc.instance(OrderImplEx.class));
		DocType.addType(DebtDoc.instance());
		DocType.addType(VisitDoc.instance());
		DocType.addType(RemnantsDoc.instance());
		DocType.addType(IncassDoc.instance());
		
		DocType.setCurDoc(OrderDoc.instance());

		IncassEdit.activity = IncassEditEx.class;
		
		Features.COST_MANAGER = new CostManagerImpl();
		Features.DOC_STATUS_IN_DOC_LIST = true;
		Features.UPDATE_PRICE_BACKGROUND = true;
		Features.SYNC_INFO = true;
		
		Warehouse.activity = WarehouseEx.class;
		Napoleon.serviceType = NapoleonServiceW.class;
		Setting.WarehouseSettingActivity = WarehouseSettingEx.class;
		
		configUpdater = new BroadcastReceiver() {
			
			@Override
			public void onReceive(Context ctx, Intent i) {
				if(NetworkBroadcasts.getSyncResult(i) == false)
					return;
				
				CfgNpl cfg = (CfgNpl) ConfigManager.getConfig();
				
				ConfigImpl ci = new ConfigImpl();
				Config c = ci.getData();
				c.key = "ПродажаВМинус";
				if( ci.read() )
					cfg.checkPrice = Integer.parseInt(c.value) == 0; 
				
				c.key = "ФоноваяСинхронизация";
				if(ci.read()) {
					int value = Integer.parseInt(c.value);
					cfg.useUpdatePrice = value != 0;
					if(value > 0) {
						if (value < 10)
							value = 10;
						cfg.updatePriceTime = value; 
					}
				}
				try {
					ConfigManager.save(getApplicationContext());
					updateService();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		
		NetworkBroadcasts.registerSyncResultReceiver(this, configUpdater);
	}
	
	protected void updateService() {
		Intent intent = new Intent(this, Napoleon.serviceType);
		bindService(intent, new ServiceConnection() {
			
			@Override public void onServiceDisconnected(ComponentName arg0) {}
			
			@Override
			public void onServiceConnected(ComponentName arg0, IBinder service) {
				((NapoleonServiceW.LocalBinder) service).getService().update();
				unbindService(this);
			}
		}, Context.BIND_AUTO_CREATE);	
	}

	BroadcastReceiver configUpdater;
	
	@Override
	public void onTerminate() {
		unregisterReceiver(configUpdater);
		super.onTerminate();		
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
