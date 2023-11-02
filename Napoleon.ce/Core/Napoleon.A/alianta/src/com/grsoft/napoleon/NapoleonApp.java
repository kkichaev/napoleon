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

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.grsoft.database.HitchOnSelect;
import com.grsoft.database.Hitching;
import com.grsoft.database.HitchingCtor;
import com.grsoft.database.LoadedOrdersRcvr;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.Action;
import com.grsoft.dataobjects.AgentInfo;
import com.grsoft.dataobjects.AgentPlan;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.DeliveryAddress;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.Store;
import com.grsoft.dataobjects.UserAssortMtx;
import com.grsoft.dataobjects.WhData;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.napoleon.documents.AliantaOfferDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.modules.print.NPrinter;
import com.grsoft.napoleon.util.CfgNplW;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.ReadService;
import com.grsoft.network.ServerCommand;
import com.grsoft.network.WriteService;
import com.grsoft.util.MenuHandler;
import com.grsoft.util.MenuPrepareHitching;

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
	protected Class<? extends OrderImplBase<? extends Order>> orderImplType() {
		return OrderImplEx.class;
	}
	
	@Override
	protected void defineNewType() {
		NPrinter.forms.put("offer", "offer");
		
		DbObject.regNewDataType(Org.class, OrgEx.class);
		DbObject.regNewDataType(Price.class, PriceEx.class);
		DbObject.regNewDataType(Order.class, OrderEx.class);
		DataObjectInfo.getInstance().replaceListType(OrderEx.class, "items", OrderItemEx.class);

		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override
			public List<Hitching> createList() {
				ActionHelper.resetCache();
				Hitching[] h = new Hitching[] {
						new RcvNewHitching(AgentPlan.class, "AgentPlan"),
						new RcvNewHitching(WhData.class),
						new RcvNewHitching(Store.class),
						new RcvNewHitching(UserAssortMtx.class),
						new RcvNewHitching(Action.class),
						new RcvNewHitching(DeliveryAddress.class),
						new HitchOnSelect(AgentInfo.class, "UserInfo", "\"userid\" = '$CURRENT_USERID'"),
				};
				return Arrays.asList(h);
			}
		}, UpdateDB.GEN_DATA_HITCHING);
		
		UpdateDB.addHitchingCtor(new HitchingCtor(){
			@Override
			public List<Hitching> createList() {
				Date dateFrom = ((CfgNplW)ConfigManager.getConfig()).getRestoreDate();
				Hitching[] h = new Hitching[] {
					new LoadedOrdersRcvr(dateFrom),
				};
				return Arrays.asList(h);
			}
		}, UpdateDB.RESTORE_DATA_HITCHING);
		
		Main.docMenuPrepared.add(new MenuPrepareHitching() {

			@Override
			public void menuPrepared(List<MenuHandler> menu, final Activity activity) {
				menu.add(new MenuHandler(activity.getString(R.string.plans), new Runnable() {
					@Override public void run() { AgentPlanView.open(activity); }
				}));
			}
		});
		
		WriteService.requestObjects.add(new LoadedOrdersRcvr());
		//WriteService.recievers.add(new OrderResultHitching());
		ReadService.requestObjects.addAll(WriteService.requestObjects);
	}
	
	@Override
	protected void initChildActivity() {
		OrderDetail.activity = OrderDetailEx.class;
		OrderDeliveryDetail.activity = OrderDeliveryDetailEx.class;
		Warehouse.activity = WarehousEx.class;
		Documents.activity = DocumentsEx.class;
		Setting.addTabs.add(OrderDefaultSetting.class);
	}

	@Override
	protected void initChildFeature() {
		Features.DELIVERY_REPLACE_ORDER_SUM = true;
		Features.ID_COLUMN_IN_PRICE_LIST = true;
		Features.LOAD_FULL_PRICE = true;
		Features.REPORT_REQUEST = true;
		Features.ID_IN_PRESENTATION = true;
	}
	
	@Override
	protected void initChildDocTypes() {
		DocType.addType(AliantaOfferDoc.instance());
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
