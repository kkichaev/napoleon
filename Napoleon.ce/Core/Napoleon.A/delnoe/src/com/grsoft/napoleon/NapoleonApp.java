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
import com.grsoft.dataobjects.Bonus;
import com.grsoft.dataobjects.BonusDef;
import com.grsoft.dataobjects.BonusItem;
import com.grsoft.dataobjects.ConfigHelper;
import com.grsoft.dataobjects.ConfigHelper.DlvDateType;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Firm;
import com.grsoft.dataobjects.FirmEx;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.napoleon.documents.BonusDoc;
import com.grsoft.napoleon.documents.DebtDocEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.ScriptDocEx;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.ServerCommand;
import com.grsoft.util.ViewInitializer;
import android.app.Activity;
import android.content.Context;
import android.widget.CheckBox;

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
		DebtDocEx.initialize();
		ScriptDocEx.initialize();
		DbObject.regNewDataType(Price.class, PriceEx.class);
		DbObject.regNewDataType(Firm.class, FirmEx.class);
		DbObject.regNewDataType(Order.class, OrderEx.class);
		
		DataObjectInfo.getInstance().replaceListType(Bonus.class, "items", BonusItem.class);
		DataObjectInfo.getInstance().replaceListType(OrderEx.class, "items", OrderItemEx.class);
		
		UpdateDB.addHitchingCtor(new HitchingCtor(){
			@Override
			public List<Hitching> createList() {
				Hitching[] ret = new Hitching[] {
					new RcvNewHitching(BonusDef.class, "ActionDef"),
					new RcvNewHitching(FirmEx.class, "Firm"),
				};
				return Arrays.asList(ret);
			}
		}, UpdateDB.GEN_DATA_HITCHING);

//		UpdateDB.addHitchingCtor(new HitchingCtor() {
//			@Override public Hitching create() { return new RcvNewHitching(Report.class, "Reports"); }
//		}, UpdateDB.GEN_DATA_HITCHING);
		

		UpdateDB.initUI = new ViewInitializer(){ 
			@Override
			public void init(Activity activity) {
				((CheckBox)activity.findViewById(R.id.cbRemains)).setChecked(false);
				super.init(activity);
			}
		};
	}
	
	@Override
	protected void initChildDocTypes() {
		DocType.addType(BonusDoc.instance());
	}
	
	@Override
	protected void initChildActivity() {
		Warehouse.activity = WarehouseEx.class;
		OrderDetail.activity = OrderDetailEx.class;
		PriceCount.activity = PriceCountEx.class;
	}
	
	@Override
	protected void initChildFeature() {
		Features.CHECK_UNCOMPLETE_SCRIPTS = true;
		Features.DEL_VISIT_WITHOUT_PHOTO = true;
		ConfigHelper.DEFAULT_DATE_TYPE = DlvDateType.nextday;
		//Features.START_STOP = true;
		Features.INPUT_QTY_IN_PACK = true;
		//Features.SHOW_WEIGHT_IN_DOC_LIST = true;
		Features.SCRIPT_SUM_ONLY_FOR_SALES = true;
		Features.EXCLUDE_RETURN_DOC_SUM_FROM_SCRIPT = true;
		Features.REPORT_REQUEST = true;
		Features.MAX_FOTO_HEIGHT = 1300;
		Features.MAX_FOTO_WIDTH  = 1300;
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
