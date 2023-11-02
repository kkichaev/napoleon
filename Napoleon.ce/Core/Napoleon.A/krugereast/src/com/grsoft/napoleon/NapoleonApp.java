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
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.Sklads;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.ScriptImplEx;
import com.grsoft.napoleon.documents.BonusDoc;
import com.grsoft.napoleon.documents.DebtDocEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.ScriptDocEx;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.ServerCommand;
import com.grsoft.script.dataobjects.impl.ScriptImpl;
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
	
	@Override
	protected Class<? extends OrderImplBase<? extends Order>> orderImplType() {
		return OrderImplEx.class;
	}
	
	@Override
	protected void defineNewType() {
		DebtDocEx.initialize();
		ScriptDocEx.initialize();
		
		DataObjectInfo.getInstance().replaceListType(Bonus.class, "items", BonusItem.class);
		
		UpdateDB.addHitchingCtor(new HitchingCtor(){
			@Override
			public List<Hitching> createList() {
				Hitching[] h = new Hitching[] {
						new RcvNewHitching(BonusDef.class, "ActionDef"),
						new RcvNewHitching(Sklads.class),
						new RcvNewHitching(Firm.class),
				};
				return Arrays.asList(h);
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
		
		DbObject.regNewDataType(Price.class, PriceEx.class);
		DbObject.regNewDataType(Org.class, OrgEx.class);
		DbObject.regNewDataType(Order.class, OrderEx.class);
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
		Documents.activity = DocumentsEx.class;
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
		setProgrammVersion();
		
		OrderImpl.OrderEditor = new OrderEditor();
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
	protected Class<? extends ScriptImpl> scriptImplType() {
		return ScriptImplEx.class;
	}
}
