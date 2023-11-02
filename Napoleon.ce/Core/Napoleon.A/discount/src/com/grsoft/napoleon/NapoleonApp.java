/*
 * Copyright (C), 2011, Гильдия Разработчиков
 * 
 * Демо программа (с базой данных)
 *
 * kki   16/05/2011   creating
 */

package com.grsoft.napoleon;

import java.util.Arrays;
import java.util.List;
import com.grsoft.database.Hitching;
import com.grsoft.database.HitchingCtor;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.ActionsDiscont;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.dataobjects.Firm;
import com.grsoft.dataobjects.FirmEx;
import com.grsoft.dataobjects.MntrFolders;
import com.grsoft.dataobjects.MntrGoods;
import com.grsoft.dataobjects.MntrMatrix;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Payment;
import com.grsoft.dataobjects.PaymentEx;
import com.grsoft.dataobjects.Plan;
import com.grsoft.dataobjects.Sklad;
import com.grsoft.dataobjects.SkladItem;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.napoleon.documents.DiscountMonitoringDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.ExistDoc;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.ServerCommand;
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
	protected void initChildActivity() {
		Warehouse.activity = WarehouseEx.class;
		DocList.activity = DocListEx.class;
		PriceCount.activity = PriceCountEx.class;
		Documents.activity = DocumentsEx.class;
		OrderDetail.activity = OrderDetailEx.class;
		
		Features.NO_SCRIPT_CONFIG = true;

		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override
			public List<Hitching> createList() {
				CostStrategyEx.clearCache();
				
				return Arrays.asList(new Hitching[] {
						new RcvNewHitching(Plan.class),
						new RcvNewHitching(Firm.class, "Firm"),
						new RcvNewHitching(Sklad.class),
						new RcvNewHitching(SkladItem.class),
						new RcvNewHitching(ActionsDiscont.class),
						new RcvNewHitching(MntrGoods.class),
						new RcvNewHitching(MntrFolders.class),
						new RcvNewHitching(MntrMatrix.class),
					});
			}
		}, UpdateDB.GEN_DATA_HITCHING);
				
		Main.docMenuPrepared.add(new MenuPrepareHitching() {
			
			@Override
			public void menuPrepared(List<MenuHandler> menu, final Activity activity) {
				menu.add(new MenuHandler(getString(R.string.DocStatuses), new Runnable() {
					@Override public void run() { StatusEdit.open(activity); }
				}));
			}
		});
		
		DocStatus.initResources(this);
	}

	@Override
	protected void initChildDocTypes() {
		DocType.addType(ReturnDoc.instance());
		DocType.addType(DiscountMonitoringDoc.instance());
		DocType.addType(ExistDoc.instance());
	}
	
	@Override
	public void onCreate() {
		ConfigManager.initConfig(new CfgNpl());
		super.onCreate();

		OrderImpl.OrderEditor = new OrderEditor();
		setProgrammVersion();
	}
	
	@Override
	protected void defineNewType() {
		DbObject.regNewDataType(Order.class, OrderEx.class);
		DbObject.regNewDataType(Firm.class, FirmEx.class);
		DbObject.regNewDataType(Delivery.class, DeliveryEx.class);
		DbObject.regNewDataType(Payment.class, PaymentEx.class);
		DbObject.regNewDataType(Org.class, OrgEx.class);
		
		DataObjectInfo.getInstance().replaceListType(OrderEx.class, "items", OrderItemEx.class);
	}

	private void setProgrammVersion() {
		try{
			ServerCommand.ProgramVersion = getResources().getString(R.string.version);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Override
	protected Class<? extends OrderImplBase<? extends Order>> orderImplType() {	return OrderImplEx.class; }
	
}
