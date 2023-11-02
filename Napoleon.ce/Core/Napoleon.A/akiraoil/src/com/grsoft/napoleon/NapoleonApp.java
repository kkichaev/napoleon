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

import com.grsoft.database.HitchOnSelect;
import com.grsoft.database.Hitching;
import com.grsoft.database.HitchingCtor;
import com.grsoft.database.LoadedOrdersHitching;
import com.grsoft.database.OrderConfirm;
import com.grsoft.database.PriceHitchingEx;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.Agents;
import com.grsoft.dataobjects.CostItemEx;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.dataobjects.Firm;
import com.grsoft.dataobjects.FolderDiscount;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Payment;
import com.grsoft.dataobjects.PaymentEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceDiscount;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.PaymentImplEx;
import com.grsoft.napoleon.documents.DebtDocList;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.ServerCommand;

import android.content.Context;

public class NapoleonApp extends NapoleonAppBase {
	
	class OrderEditor implements OrderImpl.PropertiesEditor {
		@Override
		public void edit(Context ctx, OrderImpl order, boolean isOldOrder) {
			CreateOrder.open(ctx, order, isOldOrder);
		}
	}
	
	@Override
	protected void defineNewType() {
		DbObject.regNewDataType(Org.class, OrgEx.class);
		DbObject.regNewDataType(Price.class, PriceEx.class);
		DbObject.regNewDataType(Payment.class, PaymentEx.class);
		DbObject.regNewDataType(Order.class, OrderEx.class);
		DbObject.regNewDataType(Delivery.class, DeliveryEx.class);
		
		DataObjectInfo.getInstance().replaceListType(Price.class, "cost", CostItemEx.class);
		DataObjectInfo.getInstance().replacePrimaryKey(PaymentEx.class, "agreeId");
		DataObjectInfo.getInstance().replaceListType(OrderEx.class, "items", OrderItemEx.class);
		
		DebtDocList.PaymentType = PaymentImplEx.class;
		
		UpdateDB.priceHitchingClass = PriceHitchingEx.class;
		UpdateDB.addHitchingCtor(new HitchingCtor(){
			@Override
			public List<Hitching> createList() {
				CostStrategyEx.resetCache();
				Hitching[] h = new Hitching[] {
						new RcvNewHitching(PriceDiscount.class),
						new RcvNewHitching(FolderDiscount.class),
						new RcvNewHitching(Firm.class),
						new LoadedOrdersHitching(),
						new HitchOnSelect(Agents.class, "Agents", "id = '$CURRENT_USERID'", true),
						new OrderConfirm(),
				};
				return Arrays.asList(h);
			}
		}, UpdateDB.GEN_DATA_HITCHING);
		
		//WriteService.requestObjects.add(new OrderConfirm());
		
		UpdateDB.addHitchingCtor(new HitchingCtor(){
			@Override
			public List<Hitching> createList() {
				CfgNpl c = (CfgNpl) ConfigManager.getConfig();
				Hitching[] h = new Hitching[] {
						new LoadedOrdersHitching(c.monthsToRecreate * 31 + c.daysToRecreate, true),
						new OrderConfirm(c.monthsToRecreate * 31 + c.daysToRecreate),
				};
				return Arrays.asList(h);
			}
		}, UpdateDB.RESTORE_DATA_HITCHING);

		CostStrategy.defaultInstance = new CostStrategyEx();
//		FoldersAdapter.TreeNodeComparator = new TreeNodeCmpEx();
	}
	
	@Override protected Class<? extends OrderImplBase<? extends Order>> orderImplType() { return OrderImplEx.class;	}
	
	@Override
	protected void initChildActivity() {
		Warehouse.activity = WarehouseEx.class;
		PriceCount.activity = PriceCountEx.class;
		Documents.activity = DocumentsEx.class;
		OrderDetail.activity = OrderDetailEx.class;
		UpdateDB.activity = UpdateDBEx.class;
		DocList.activity = DocListEx.class;
	}
	
	@Override
	protected void initChildFeature() {
		Features.UNLIMIT_VISIT_ITEMS = true;
		Features.DELIVERY_REPLACE_ORDER_SUM = true;
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
