/*
 * Copyright (C), 2011, Гильдия Разработчиков
 * 
 * Демо программа (с базой данных)
 *
 * kki   16/05/2011   creating
 */

package com.grsoft.napoleon;

import android.content.Context;

import com.grsoft.database.Hitching;
import com.grsoft.database.HitchingCtor;
import com.grsoft.database.PriceHitchingEx;
import com.grsoft.database.PriceQtyHitching;
import com.grsoft.database.ProgamSettingsHitching;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.database.SkladHitching;
import com.grsoft.dataobjects.ConfigHelper;
import com.grsoft.dataobjects.DanaAction;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.DisableOrg;
import com.grsoft.dataobjects.GoodProject;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgCost;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceCost;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.Sklad;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.ServerCommand;
import com.grsoft.network.WriteService;
import com.grsoft.util.NapoleonServiceW;

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
	protected void defineNewType() {
		super.defineNewType();

		CostStrategy.defaultInstance = new CostStrategyEx();

		DbObject.regNewDataType(Price.class, PriceEx.class);
		DbObject.regNewDataType(Org.class, OrgEx.class);
		DbObject.regNewDataType(Order.class, OrderEx.class);

		DataObjectInfo.getInstance().replaceListType(Order.class,"items", OrderItemEx.class );

		UpdateDB.addHitchingCtor(new HitchingCtor(){
			@Override
			public List<Hitching> createList() {
				CostStrategyEx.resetCache();

				Hitching[] h = new Hitching[]{
						new RcvNewHitching(GoodProject.class),
						new SkladHitching(),
						new PriceQtyHitching(),
						new ProgamSettingsHitching(),
						new RcvNewHitching(DanaAction.class),
						new RcvNewHitching(DisableOrg.class),
						new RcvNewHitching(OrgCost.class),
						new RcvNewHitching(PriceCost.class),
				};
				return Arrays.asList(h);
			}
		}, UpdateDB.GEN_DATA_HITCHING);
		UpdateDB.priceHitchingClass = PriceHitchingEx.class;
		ConfigHelper.DEFAULT_DATE_TYPE = ConfigHelper.DlvDateType.nextday;

		WriteService.requestObjects.add(new PriceQtyHitching());
		WriteService.requestObjects.add(new PriceHitchingEx());
		WriteService.requestObjects.add(new RcvNewHitching(DanaAction.class));

		NapoleonServiceW.priceUpdateHitchings.add(new PriceQtyHitching());
		NapoleonServiceW.priceUpdateHitchings.add(new PriceHitchingEx());
	}

	@Override
	protected Class<? extends OrderImplBase<? extends Order>> orderImplType() {
		return OrderImplEx.class;
	}

	@Override
	protected void initChildFeature() {
		super.initChildFeature();
		Features.LOAD_FULL_PRICE = true;
		Features.WH_QTY = true;
	}

	@Override
	protected void initChildActivity() {
		super.initChildActivity();

		//PriceCount.activity = PriceCountEx.class;
		Documents.activity = DocumentsEx.class;
		Warehouse.activity = WarehouseEx.class;
		UpdateDB.activity = UpdateDBEx.class;
		Setting.PhotoSettingActivity = PhotoSettingEx.class;
		Setting.GPSSettingActivity = GpsSettingEx.class;
		PriceCount.activity = PriceCountEx.class;
		OrderDetail.activity = OrderDetailEx.class;
		OrderDeliveryDetail.activity = OrderDeliveryDetailEx.class;
	}

	@Override
	public void onCreate() {
		ConfigManager.initConfig(new CfgNpl());
		super.onCreate();

		OrderImpl.OrderEditor = new OrderEditor();
		setProgramVersion();
		
		//NapoleonChat.init(this);
	}

	private void setProgramVersion() {
		try{
			ServerCommand.ProgramVersion = getResources().getString(R.string.version);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
