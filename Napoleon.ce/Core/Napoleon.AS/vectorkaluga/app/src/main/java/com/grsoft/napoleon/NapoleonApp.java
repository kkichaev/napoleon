/*
 * Copyright (C), 2011, Гильдия Разработчиков
 * 
 * Демо программа (с базой данных)
 *
 * kki   16/05/2011   creating
 */

package com.grsoft.napoleon;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.Hitching;
import com.grsoft.database.HitchingCtor;
import com.grsoft.database.PriceHitchingEx;
import com.grsoft.database.PricePhotoHitching;
import com.grsoft.database.PriceQtyHitching;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.database.SkladHitching;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.PriceUnit;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.napoleon.modules.CostManagerImpl;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.napoleon.util.debug.Path;
import com.grsoft.network.ServerCommand;
import com.grsoft.network.WriteService;
import com.grsoft.util.NapoleonServiceW;

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

		DbObject.regNewDataType(Price.class, PriceEx.class);
		DbObject.regNewDataType(Order.class, OrderEx.class);
		DataObjectInfo.getInstance().replaceListType(OrderEx.class, "items", OrderItemEx.class);
		DataObjectInfo.getInstance().replaceListType(Order.class, "items", OrderItemEx.class);

		UpdateDB.addHitchingCtor(new HitchingCtor(){
			@Override
			public List<Hitching> createList() {
				WarehouseEx.clearCache();

				Hitching[] h = new Hitching[]{
						new SkladHitching(),
						new PriceQtyHitching(),
						new RcvNewHitching(PriceUnit.class),
				};
				return Arrays.asList(h);
			}
		}, UpdateDB.GEN_DATA_HITCHING);
		UpdateDB.priceHitchingClass = PriceHitchingEx.class;

		NapoleonServiceW.priceUpdateHitchings.add(new PriceQtyHitching());
		NapoleonServiceW.priceUpdateHitchings.add(new PriceHitchingEx());
	}

	@Override
	protected void initChildActivity() {
		super.initChildActivity();
		PriceCount.activity = PriceCountEx.class;
		Warehouse.activity = WarehouseEx.class;
	}

	@Override
	protected void initChildFeature() {
		super.initChildFeature();
		Features.WH_QTY = true;
		Features.COST_MANAGER = new CostManagerImplEx();
		Features.LOAD_FULL_PRICE = true;
		Features.COST_FILTER_IN_PRICE = true;
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
		try {
			ServerCommand.ProgramVersion = getResources().getString(R.string.version);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
