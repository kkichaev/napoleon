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
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Firm;
import com.grsoft.dataobjects.FirmEx;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.PrcTypes;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.PriceHitchingEx;
import com.grsoft.dataobjects.ServikoAction;
import com.grsoft.dataobjects.ServikoActionItems;
import com.grsoft.dataobjects.UserAssortMtx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.napoleon.documents.ClientCardDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.IncassDocEx;
import com.grsoft.napoleon.util.CfgNplEx;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.ServerCommand;
import com.grsoft.script.ScriptEdit;
import com.grsoft.util.MenuHandler;
import com.grsoft.util.MenuPrepareHitching;
import android.app.Activity;
import android.content.Context;

public class NapoleonApp extends NapoleonAppBase {
	
	class OrderEditor implements OrderImpl.PropertiesEditor {
		@Override
		public void edit(Context ctx, OrderImpl order, boolean isOldOrder) {
			CreateOrder.open(ctx, order, isOldOrder);
		}
	}
	
	@Override
	public void onCreate() {
		ConfigManager.initConfig(new CfgNplEx());
		super.onCreate();
		OrderImpl.OrderEditor = new OrderEditor();
		setProgrammVersion();
		
		//NapoleonChat.init(this);
		
		Main.docMenuPrepared.add(new MenuPrepareHitching() {
			@Override
			public void menuPrepared(List<MenuHandler> menu, final Activity activity) {
				menu.add(new MenuHandler(getString(R.string.order_report), new Runnable() {
					@Override public void run() { OrderList.open(activity); }
				}));
			}
		});
		
		UpdateDB.addHitchingCtor(new HitchingCtor(){ @Override public Hitching create() { return new RcvNewHitching(UserAssortMtx.class);}}, UpdateDB.GEN_DATA_HITCHING);
	}

	private void setProgrammVersion() {
		try{
			ServerCommand.ProgramVersion = getResources().getString(R.string.version);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Override protected Class<? extends OrderImplBase<? extends Order>> orderImplType() { return OrderImplEx.class; }
	
	@Override
	protected void defineNewType() {
		IncassDocEx.init();
		DbObject.regNewDataType(Org.class, OrgEx.class);
		DbObject.regNewDataType(Order.class, OrderEx.class);
		DbObject.regNewDataType(Price.class, PriceEx.class);
		DbObject.regNewDataType(Firm.class, FirmEx.class);
		
		DataObjectInfo.getInstance().replaceListType(OrderEx.class, "items", OrderItemEx.class);
		
		UpdateDB.priceHitchingClass = PriceHitchingEx.class;
		UpdateDB.addHitchingCtor(new HitchingCtor(){
			@Override
			public List<Hitching> createList() {
				CostStrategyEx.clearCache();
				
				Hitching[] ret = new Hitching[] {
						new RcvNewHitching(Firm.class, "Firm"),
						new RcvNewHitching(PrcTypes.class, "PrcTypes"),
						new RcvNewHitching(ServikoAction.class, "ServikoAction"),
						new RcvNewHitching(ServikoActionItems.class, "ServikoActionItems"),
				};
				return Arrays.asList(ret);
			}
		}, UpdateDB.GEN_DATA_HITCHING);
		
		CostStrategy.defaultInstance = new CostStrategyEx();
	}
	
	@Override
	protected void initChildFeature() {
		super.initChildFeature();
		
		Features.CHECK_UNCOMPLETE_SCRIPTS = true;
		Features.CANT_CHANGE_SEND_FLAG = true;
		Features.DEL_VISIT_WITHOUT_PHOTO = true;
		Features.START_VISIT_OPEN_CAMERA = true;
		Features.DONT_SHOW_FIRST_SCRIPT_DOC = true;
		
		Features.LOAD_FULL_PRICE = true;
		
		// conflict with Action matrix
		Features.OPEN_LAST_MATRIX = false;
	}
	
	@Override
	protected void initChildActivity() {
		super.initChildActivity();
		
		ScriptEdit.activity = ScriptEditEx.class;
		DocList.activity = DocListEx.class;
		Documents.activity = DocumentsEx.class;
		PriceCount.activity = PriceCountEx.class;
		Warehouse.activity = WarehouseEx.class;
	}
	
	@Override
	protected void initChildDocTypes() {
		super.initChildDocTypes();
		
		DocType.addType(ClientCardDoc.instance());
	}
}
