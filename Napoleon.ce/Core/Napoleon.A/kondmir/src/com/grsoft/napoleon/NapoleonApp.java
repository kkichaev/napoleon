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
import com.grsoft.dataobjects.Category;
import com.grsoft.dataobjects.Incass;
import com.grsoft.dataobjects.IncassEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.IncassImplEx;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.napoleon.documents.IncassDoc;
import com.grsoft.network.ServerCommand;
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
		DbObject.regNewDataType(Incass.class, IncassEx.class);
		CostStrategy.defaultInstance = new CostStrategyEx();
		
		// init new incass doc
		IncassDoc.instance(IncassImplEx.class);
	}
	
	@Override
	protected void initChildActivity() {
		PriceCount.activity = PriceCountEx.class;
		IncassEdit.activity = IncassEditEx.class;
		Warehouse.activity = WarehouseEx.class;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();

		OrderImpl.OrderEditor = new OrderEditor();
		setProgrammVersion();
		
		UpdateDB.addHitchingCtor(new HitchingCtor() {@Override
		public Hitching create() {
			return new RcvNewHitching(Category.class);
		}}, UpdateDB.GEN_DATA_HITCHING);
	}

	private void setProgrammVersion() {
		try{
			ServerCommand.ProgramVersion = getResources().getString(R.string.version);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
