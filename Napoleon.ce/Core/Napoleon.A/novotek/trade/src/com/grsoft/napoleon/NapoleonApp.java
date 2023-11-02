/*
 * Copyright (C), 2011, Гильдия Разработчиков
 * 
 * Демо программа (с базой данных)
 *
 * kki   16/05/2011   creating
 */

package com.grsoft.napoleon;

import com.grsoft.database.Hitching;
import com.grsoft.database.HitchingCtor;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.Discount;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.Org2Ex;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.Price2Ex;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.napoleon.util.CfgNplEx;
import com.grsoft.napoleon.util.ConfigManager;

public class NapoleonApp extends AppBase {
	@Override
	public void onCreate() {
		ConfigManager.initConfig(new CfgNplEx());
		super.onCreate();

		UpdateDB.addHitchingCtor(new HitchingCtor() {@Override
			public Hitching create() {
				return new RcvNewHitching(Discount.class);
			}}, UpdateDB.GEN_DATA_HITCHING);
		
		DbObject.regNewDataType(Org.class, Org2Ex.class);
		DbObject.regNewDataType(Price.class, Price2Ex.class);
		
		Warehouse.activity = Warehouse2Ex.class;
		CostStrategy.defaultInstance = new CostStrategyEx();
		UpdateDB.activity = UpdateDB2Ex.class;
		CreateOrder.activity = CreateOrderEx.class;
	}
}
