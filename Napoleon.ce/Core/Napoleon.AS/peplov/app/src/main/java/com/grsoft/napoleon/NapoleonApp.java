/*
 * Copyright (C), 2011, Гильдия Разработчиков
 * 
 * Демо программа (с базой данных)
 *
 * kki   16/05/2011   creating
 */

package com.grsoft.napoleon;

import android.content.Context;
import java.util.List;

import com.grsoft.database.Hitching;
import com.grsoft.database.HitchingCtor;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.MatrixOrder;
import com.grsoft.dataobjects.NETMtx;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.Return;
import com.grsoft.dataobjects.ReturnItem;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.ReturnImplEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.ServerCommand;

import java.util.ArrayList;

public class NapoleonApp extends NapoleonAppBase {
	
	class OrderEditor implements OrderImpl.PropertiesEditor {
		@Override
		public void edit(Context ctx, OrderImpl order, boolean isOldOrder) {
			CreateOrder.open(ctx, order, isOldOrder);
		}
	}
	
	@Override
	public void onCreate() {
		ConfigManager.initConfig(new CfgNpl());
		super.onCreate();

		OrderImpl.OrderEditor = new OrderEditor();
		setProgrammVersion();

		UpdateDBW.addHitchingCtor(new HitchingCtor() {
			@Override
			public List<Hitching> createList() {
				List<Hitching> result = new ArrayList<>();
				result.add(new RcvNewHitching(NETMtx.class));
				result.add(new RcvNewHitching(MatrixOrder.class));

				return result;
			}
		}, UpdateDB.GEN_DATA_HITCHING);

	}

	private void setProgrammVersion() {
		try{
			ServerCommand.ProgramVersion = getResources().getString(R.string.version);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	protected void initChildDocTypes() {
		super.initChildDocTypes();
		DocType.addType(ReturnDoc.instance(ReturnImplEx.class));
	}

	@Override
	protected void defineNewType() {
		super.defineNewType();
		DbObject.regNewDataType(Order.class, OrderEx.class);
		DbObject.regNewDataType(Price.class, PriceEx.class);
		DbObject.regNewDataType(Org.class, OrgEx.class);

		DataObjectInfo doi = DataObjectInfo.getInstance();
		doi.replaceListType(Return.class, "items", ReturnItem.class);
		doi.replaceListType(OrderEx.class, "items", OrderItemEx.class);

		CostStrategy.defaultInstance = new CostStrategyEx();
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
		Features.DELIVERY_ADDRESS = true;
//		Features.BLOCK_IN_STOP_LIST = true;
		Features.ID_COLUMN_IN_PRICE_LIST = true;
		Features.CAN_CHANGE_COST = true;
		Features.EXCLUDE_RETURN_DOC_SUM_FROM_SCRIPT = true;
	}

}
