/*
 * Copyright (C), 2011, Гильдия Разработчиков
 * 
 * Демо программа (с базой данных)
 *
 * kki   16/05/2011   creating
 */

package com.grsoft.napoleon;

import java.util.List;

import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Incass;
import com.grsoft.dataobjects.IncassEx;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.napoleon.documents.OrderDocEx;
import com.grsoft.network.ServerCommand;
import com.grsoft.util.FolderNodeCmp;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.MenuHandler;
import com.grsoft.util.MenuPrepareHitching;

import android.app.Activity;
import android.content.Context;

public class NapoleonApp extends NapoleonAppBase {
	@SuppressWarnings("unused")
	private static final String TAG = "NapoleonApp";
	
	class OrderEditor implements OrderImpl.PropertiesEditor {
		@Override
		public void edit(Context ctx, OrderImpl order, boolean isOldOrder) {
			CreateOrder.open(ctx, order, isOldOrder);
		}
	}
	
	@Override
	protected void defineNewType() {
		DbObject.regNewDataType(Org.class, OrgEx.class);
		DbObject.regNewDataType(Order.class, OrderEx.class);
		DbObject.regNewDataType(Incass.class, IncassEx.class);
		DataObjectInfo.getInstance().replaceListType(Order.class, "items", OrderItemEx.class);
		
		OrderDocEx.initialize();
		CostStrategy.defaultInstance = new CostStrategyEx();
		FoldersAdapter.TreeNodeComparator = new FolderNodeCmp();
		
		Main.docMenuPrepared.add(new MenuPrepareHitching() {
			
			@Override
			public void menuPrepared(List<MenuHandler> menu, final Activity activity) {
				menu.add(new MenuHandler(getString(R.string.incass_list), new Runnable() {
					@Override public void run() { IncassList.open(activity); }
				}));
				menu.add(new MenuHandler(getString(R.string.reports), new Runnable() {
					@Override public void run() { TGReportList.open(activity); }
				}));
			}
		});
	}
	
	@Override
	protected void initChildActivity() {
		Documents.activity = DocumentsEx.class;
		Warehouse.activity = WarehouseEx.class;
		UpdateDB.activity = UpdateDBEx.class;
		PriceCount.activity = PriceCountEx.class;
		OrderDetail.activity = OrderDetailEx.class;
		OrderDeliveryDetail.activity = OrderDeliveryDetailEx.class;
		Presentation.activity = PresentationEx.class;
		PricePresentation.activity = PricePresentationEx.class;
		IncassEdit.activity = IncassEditEx.class;
		DocList.activity = DocListEx.class;

		Setting.BehaviorSettingActivity = BehaviorSettingEx.class;
		Setting.WarehouseSettingActivity = WarehouseSettingEx.class;
	}

	@Override
	protected void initChildFeature() {
		Features.PACK_INPUT = true;
		Features.INPUT_QTY_IN_PACK = true;
		Features.ORDER_ONLINE = true;
		Features.RECIEVE_REMNANTS_IN_MAIN_MENU = true;
		Features.DELIVERY_REPLACE_ORDER_SUM = true;
		Features.INTEGER_INPUTS_QTY = true;
		Features.CAN_SEND_EMPTY_DOCS = true;
		
		Features.RECEIVE_REMNANTS_WHEN_SENDING = true;
		Features.SALES_FROM_ORDERS = false;
		
		Features.CONFIG_DONT_CHECK_PRICE_QTY = true;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		
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
