/*
 * Copyright (C), 2011, Гильдия Разработчиков
 * 
 * Демо программа (с базой данных)
 *
 * kki   16/05/2011   creating
 */

package com.grsoft.napoleon;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.grsoft.database.CheckConfirmHitching;
import com.grsoft.database.CommonChekExporter;
import com.grsoft.database.CommonChekRestore;
import com.grsoft.database.DocumentRestore;
import com.grsoft.database.Hitching;
import com.grsoft.database.HitchingCtor;
import com.grsoft.database.PriceHitchingEx;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.AgentGroupPlan;
import com.grsoft.dataobjects.AgentProcent;
import com.grsoft.dataobjects.Banks;
import com.grsoft.dataobjects.CommonChek;
import com.grsoft.dataobjects.CommonChekItem;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.DogovorRoute;
import com.grsoft.dataobjects.Dover;
import com.grsoft.dataobjects.IncassRights;
import com.grsoft.dataobjects.ItemGroup;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.PriceFolder;
import com.grsoft.dataobjects.Return;
import com.grsoft.dataobjects.ReturnEx;
import com.grsoft.dataobjects.ReturnItemEx;
import com.grsoft.dataobjects.UserPinData;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.ReturnImplEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.IncassDoc;
import com.grsoft.napoleon.documents.RequestCheckDoc;
import com.grsoft.napoleon.documents.ReturnChekBackDoc;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.napoleon.util.CfgNplEx;
import com.grsoft.napoleon.util.CfgNplW;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.RWServiceFactory;
import com.grsoft.network.RWServiceFactoryEx;
import com.grsoft.network.ReadService;
import com.grsoft.network.ServerCommand;
import com.grsoft.network.WriteService;
import com.grsoft.util.MenuHandler;
import com.grsoft.util.MenuPrepareHitching;

import android.app.Activity;
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
		DbObject.regNewDataType(Order.class, OrderEx.class);
		DbObject.regNewDataType(Org.class, OrgEx.class);
		DbObject.regNewDataType(Price.class, PriceEx.class);
		DbObject.regNewDataType(Return.class, ReturnEx.class);
		DataObjectInfo.getInstance().replaceListType(OrderEx.class, "items", OrderItemEx.class);
		DataObjectInfo.getInstance().replaceListType(Return.class, "items", ReturnItemEx.class);
		DataObjectInfo.getInstance().replaceListType(CommonChek.class, "items", CommonChekItem.class);
	}
	
	@Override
	protected Class<? extends OrderImplBase<? extends Order>> orderImplType() {
		return OrderImplEx.class;
	}
	
	@Override
	protected void initChildDocTypes() {
		DocType.addType(ReturnDoc.instance(ReturnImplEx.class));
		DocType.addType(RequestCheckDoc.instance());
		DocType.addType(ReturnChekBackDoc.instance());
		
		DocType.removeType(IncassDoc.instance());
	
		UpdateDB.priceHitchingClass = PriceHitchingEx.class;
		
		
		ReadService.recievers.add(new com.grsoft.database.OrgStopHitching());		
		WriteService.recievers.addAll(ReadService.recievers);
		RWServiceFactory.instance = new RWServiceFactoryEx();

		ReadService.requestObjects.add(new CheckConfirmHitching(0, 2));		
		WriteService.requestObjects.addAll(ReadService.requestObjects);
		
		Main.docMenuPrepared.add(new MenuPrepareHitching() {
			
			@Override
			public void menuPrepared(List<MenuHandler> menu, final Activity activity) {
				menu.add(new MenuHandler(getString(R.string.incas_chek), new Runnable() {
					@Override public void run() {CommonChekList.open(activity);}
				}));
			}
		});

		UpdateDB.addHitchingCtor(new HitchingCtor(){
			@Override
			public List<Hitching> createList() {
				CfgNplW cfg = (CfgNplW)ConfigManager.getConfig();

				Hitching[] ret = new Hitching[] {
					new DocumentRestore(RequestCheckDoc.instance(), "RequestChek"),
					new DocumentRestore(ReturnChekBackDoc.instance(), "ReturnChekBack"),
					new CheckConfirmHitching(cfg.monthsToRecreate, cfg.daysToRecreate),
					new CommonChekRestore(),
				};
				return Arrays.asList(ret);
			}
		}, UpdateDB.RESTORE_DATA_HITCHING);

		UpdateDB.addHitchingCtor(new HitchingCtor(){
			@Override
			public List<Hitching> createList() {
				Hitching[] ret = new Hitching[] {
					new RcvNewHitching(Dover.class, "Dover"),
					new RcvNewHitching(Banks.class, "Banks"),
					new RcvNewHitching(IncassRights.class, "IncassRights"),
					new RcvNewHitching(PriceFolder.class),
					new RcvNewHitching(UserPinData.class, "UserPinData"),
					new RcvNewHitching(AgentProcent.class, "AgentProcent"),
					new RcvNewHitching(ItemGroup.class, "ItemGroups"),
					new RcvNewHitching(AgentGroupPlan.class, "AgentGroupPlan"),
					new RcvNewHitching(DogovorRoute.class, "DogovorRoute"),
				};
				return Arrays.asList(ret);
			}
		}, UpdateDB.GEN_DATA_HITCHING);

		UpdateDB.addHitchingCtor(new HitchingCtor(){
			@Override public Hitching create() { return new CommonChekExporter(); }
		}, UpdateDB.EXPORT_DATA_HITCHING);
	}
	
	@Override
	protected void initChildActivity() {
		OrderDetail.activity = OrderDetailEx.class;
		PriceCount.activity = PriceCountEx.class;
		UpdateDB.activity = UpdateDBEx.class;
		Warehouse.activity = WarehouseEx.class;
		Documents.activity = DocumentsEx.class;
		CreateReturn.activity = CreateReturnEx.class;
		Setting.activity = SettingEx.class;
		Setting.GPSSettingActivity = GpsSettingEx.class;
		Setting.BehaviorSettingActivity = BehaviorSettingEx.class;
	}
	
	@Override
	protected void initChildFeature() {
		Features.PUT_SALED_ITEMS_BEFORE = true;
		Features.RECEIVE_REMNANTS_WHEN_SENDING = true;
		Features.USE_COST_IN_RETURNS = true;
	}
	
//	private void initDocTypes() {
//		DocType.addType(OrderDoc.instance(OrderImplEx.class));
//		DocType.addType(DebtDoc.instance());
//		DocType.addType(VisitDoc.instance());
//		DocType.addType(RemnantsDoc.instance());
////		DocType.addType(IncassDoc.instance());
//		DocType.addType(ReturnDoc.instance(ReturnImplEx.class));
//		DocType.addType(RequestCheckDoc.instance());
//		DocType.addType(ReturnChekBackDoc.instance());
//		
//		DocType.setCurDoc(OrderDoc.instance());
//		
//		
//	}
	
	@Override
	public void onCreate() {
		CfgNplEx cfg = new CfgNplEx(); 
		ConfigManager.initConfig(cfg);

		String prevPref = ConfigManager.CFG_SHARED_PREFERENCE;
		
		ConfigManager.CFG_SHARED_PREFERENCE = "main_config_doborogost";
		if(!ConfigManager.isInited(this)) {
			ConfigManager.CFG_SHARED_PREFERENCE = prevPref;
			ConfigManager.load(this);
			cfg.setDefaults();
			
			ConfigManager.CFG_SHARED_PREFERENCE = "main_config_doborogost";
			try {
				ConfigManager.save(this);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		super.onCreate();
		OrderImpl.OrderEditor = new OrderEditor();
		setProgrammVersion();
	}

	private void setProgrammVersion() {
		try{
			ServerCommand.ProgramVersion = getResources().getString(R.string.version);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
