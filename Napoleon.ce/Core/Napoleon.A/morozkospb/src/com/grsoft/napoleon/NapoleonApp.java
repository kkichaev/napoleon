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
import com.grsoft.dataobjects.AgentsName;
import com.grsoft.dataobjects.Catalog;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Firm;
import com.grsoft.dataobjects.FirmEx;
import com.grsoft.dataobjects.Motivation;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgCost;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgMatrix;
import com.grsoft.dataobjects.OrgProp;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceCost;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.PriceQty;
import com.grsoft.dataobjects.PriceTypes;
import com.grsoft.dataobjects.RetailCode;
import com.grsoft.dataobjects.RivalFolder;
import com.grsoft.dataobjects.RivalPrice;
import com.grsoft.dataobjects.ScriptDefEx;
import com.grsoft.dataobjects.Store;
import com.grsoft.dataobjects.Visit;
import com.grsoft.dataobjects.VisitItemEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.MorozkoScriptResolver;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.VisitImplEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.MerchDoc;
import com.grsoft.napoleon.documents.MonitoringDoc;
import com.grsoft.napoleon.documents.RivalMntrDoc;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.ServerCommand;
import com.grsoft.script.dataobjects.ScriptDef;
import com.grsoft.script.dataobjects.impl.ScriptDefImpl;
import com.grsoft.util.AssortmentMatrixAdapter;
import com.grsoft.util.PriceTextFilter;
import com.grsoft.util.ViewInitializer;

import android.app.Activity;
import android.content.Context;
import android.view.View;

public class NapoleonApp extends NapoleonAppBase {
	
	class OrderEditor implements OrderImpl.PropertiesEditor {
		@Override
		public void edit(Context ctx, OrderImpl order, boolean isOldOrder) {
			CreateOrder.open(ctx, order, isOldOrder);
		}
	}
	
	@Override protected Class<? extends OrderImplBase<? extends Order>> orderImplType() { return OrderImplEx.class; }
	
	@Override
	protected void defineNewType() {
		ScriptDefImpl.resolver = new MorozkoScriptResolver();
		VisitDoc.instance(VisitImplEx.class);
		
		UpdateDB.addHitchingCtor(new HitchingCtor(){
			@Override
			public List<Hitching> createList() {
				WarehouseEx.resetCache();
				StoreHelper.clearCache();
				CostStrategyEx.resetCache();
				
				return Arrays.asList(new Hitching[] {
						new RcvNewHitching(Store.class, "Store"),
						new RcvNewHitching(PriceTypes.class, "PriceTypes"),
						new RcvNewHitching(PriceCost.class, "PriceCost"),
						new RcvNewHitching(PriceQty.class, "PriceQty"),
						new RcvNewHitching(Firm.class, "Firm"),
						new RcvNewHitching(AgentsName.class, "AgentsName"),
						new RcvNewHitching(OrgMatrix.class),
						new RcvNewHitching(RetailCode.class),
						new RcvNewHitching(RivalFolder.class),
						new RcvNewHitching(RivalPrice.class),
						new RcvNewHitching(Motivation.class),
						new RcvNewHitching(Catalog.class),
						new RcvNewHitching(OrgProp.class),
						new RcvNewHitching(OrgCost.class),
					});
			}
		}, UpdateDB.GEN_DATA_HITCHING);
		
		DbObject.regNewDataType(Order.class, OrderEx.class);
		DbObject.regNewDataType(Firm.class, FirmEx.class);
		DbObject.regNewDataType(Org.class, OrgEx.class);
		DbObject.regNewDataType(Price.class, PriceEx.class);
		DbObject.regNewDataType(ScriptDef.class, ScriptDefEx.class);
		
		DataObjectInfo.getInstance().replaceListType(Visit.class, "items", VisitItemEx.class);
		
		UpdateDB.initUI = new ViewInitializer() {
			@Override
			public void init(Activity activity) {
				activity.findViewById(R.id.cbRemains).setVisibility(View.GONE);
			}
		};
		
		CostStrategy.defaultInstance = new CostStrategyEx();
	}
	
	@Override
	protected void initChildActivity() {
		Warehouse.activity = WarehouseEx.class;
		PriceCount.activity = PriceCountEx.class;
	}
	
	
	@Override
	protected void initChildFeature() {
		Features.LOAD_FULL_PRICE = true;
//		Features.ROUTE_HISTORY = true;
		
		Features.ID_COLUMN_IN_PRICE_LIST = true;
		PriceTextFilter.SRCH_ID_FLD = "article";
		Features.MAX_FOTO_WIDTH = 4000;
		Features.MAX_FOTO_HEIGHT = 4000;
		Features.UNLIMIT_VISIT_ITEMS = true;

		AssortmentMatrixAdapter.PERIOD_IN_MONTH = 6;
	}
	
	@SuppressWarnings("serial")
	@Override
	public void onCreate() {
		ConfigManager.initConfig(new CfgNpl(){
			@Override
			public void resetToDefault() {
				super.resetToDefault();
				
				address = "gate.morozko.ru";
			}
		});
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
	
	@Override
	protected void initChildDocTypes() {
		DocType.addType(MerchDoc.instance());
		DocType.addType(MonitoringDoc.instance());
		DocType.addType(RivalMntrDoc.instance());
	}
}
