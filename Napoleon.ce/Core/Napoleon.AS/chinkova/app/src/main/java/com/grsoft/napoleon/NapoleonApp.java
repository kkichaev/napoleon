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

import com.grsoft.database.CheckConfirmHitching;
import com.grsoft.database.CommonChekRestore;
import com.grsoft.database.CommonIncassRestore;
import com.grsoft.database.DbWriter;
import com.grsoft.database.DocumentRestore;
import com.grsoft.database.Hitching;
import com.grsoft.database.HitchingCtor;
import com.grsoft.database.OrgBalanceHitching;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.database.RequestChekRestore;
import com.grsoft.database.ReturnChekRestore;
import com.grsoft.database.ServerInfoHitching;
import com.grsoft.database.ServerInfoHitchingChinkova;
import com.grsoft.dataobjects.AgentGroupPlan;
import com.grsoft.dataobjects.AgentProcent;
import com.grsoft.dataobjects.Banks;
import com.grsoft.dataobjects.CommonChek;
import com.grsoft.dataobjects.CommonChekItem;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Dover;
import com.grsoft.dataobjects.FolderColor;
import com.grsoft.dataobjects.IncassRights;
import com.grsoft.dataobjects.ItemGroup;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceData;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.PriceTypes;
import com.grsoft.dataobjects.Return;
import com.grsoft.dataobjects.ReturnEx;
import com.grsoft.dataobjects.ReturnItemEx;
import com.grsoft.dataobjects.Sklads;
import com.grsoft.dataobjects.UserPinData;
import com.grsoft.dataobjects.WHQty;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.ReturnImplEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.IncassDoc;
import com.grsoft.napoleon.documents.ProcurationDoc;
import com.grsoft.napoleon.documents.RequestCheckDoc;
import com.grsoft.napoleon.documents.ReturnChekBackDoc;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.CfgNplW;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.RWServiceFactory;
import com.grsoft.network.RWServiceFactoryEx;
import com.grsoft.network.ReadService;
import com.grsoft.network.ServerCommand;
import com.grsoft.network.WriteService;
import com.grsoft.util.Consts;
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
		super.defineNewType();

		RWServiceFactory.instance = new RWServiceFactoryEx();

		DbObject.regNewDataType(Order.class, OrderEx.class);
		DbObject.regNewDataType(Org.class, OrgEx.class);
		DbObject.regNewDataType(Return.class, ReturnEx.class);
		DbObject.regNewDataType(Price.class, PriceEx.class);
		DataObjectInfo.getInstance().replaceListType(OrderEx.class, "items", OrderItemEx.class);
		DataObjectInfo.getInstance().replaceListType(ReturnEx.class, "items", ReturnItemEx.class);
		DataObjectInfo.getInstance().replaceListType(CommonChek.class, "items", CommonChekItem.class);
	
		Main.docMenuPrepared.add(new MenuPrepareHitching() {
			
			@Override
			public void menuPrepared(List<MenuHandler> menu, final Activity activity) {
//				menu.add(new MenuHandler(getString(R.string.incas_doc_title), new Runnable() {
//					@Override public void run() {CommonIncassList.open(activity);}
//				}));
				menu.add(new MenuHandler(getString(R.string.incas_chek), new Runnable() {
					@Override public void run() {CommonChekList.open(activity);}
				}));
			}
		});
		
		Napoleon.docMenuPrepared.add(new MenuPrepareHitching() {
			
			@Override
			public void menuPrepared(List<MenuHandler> menu, final Activity activity) {
				menu.add(new MenuHandler(getString(R.string.report_title), new Runnable() {
					@Override public void run() {BalanceReport.open(activity);}
				}));
			}
		});
		
		ReadService.requestObjects.add(new ServerInfoHitchingChinkova());
		ReadService.requestObjects.add(new CheckConfirmHitching(0, 2));		
		ReadService.requestObjects.add(new OrgBalanceHitching());		
		WriteService.requestObjects.addAll(ReadService.requestObjects);
		
		
		UpdateDB.addHitchingCtor(new HitchingCtor(){
			@Override
			public List<Hitching> createList() {
				CfgNplW cfg = (CfgNplW)ConfigManager.getConfig();

				Hitching[] ret = new Hitching[] {
					new RequestChekRestore(),
					new ReturnChekRestore(),
					new CheckConfirmHitching(cfg.monthsToRecreate, cfg.daysToRecreate),
					new CommonIncassRestore(),
					new CommonChekRestore(),
					new DocumentRestore(ProcurationDoc.instance()),
				};
				return Arrays.asList(ret);
			}
		}, UpdateDB.RESTORE_DATA_HITCHING);

		UpdateDB.addHitchingCtor(new HitchingCtor(){
			@Override
			public List<Hitching> createList() {
				CostStrategyEx.clearCache();
				
				Hitching[] ret = new Hitching[] {
					new RcvNewHitching(Dover.class, "Dover"),
					new RcvNewHitching(Banks.class, "Banks"),
					new RcvNewHitching(IncassRights.class, "IncassRights"),
//					new ReportHitching(),
					new RcvNewHitching(ItemGroup.class, "ItemGroups"),
					new RcvNewHitching(AgentGroupPlan.class, "AgentGroupPlan"),
					new RcvNewHitching(FolderColor.class, "FolderColor"),
					new RcvNewHitching(UserPinData.class, "UserPinData"),
					new RcvNewHitching(PriceTypes.class, "PriceTypes"),
					new RcvNewHitching(Sklads.class, "Sklads"),
					new RcvNewHitching(WHQty.class, "WHQty"),
					new RcvNewHitching(PriceData.class, "PriceData"),
					new RcvNewHitching(AgentProcent.class, "AgentProcent"),
				};
				return Arrays.asList(ret);
			}
		}, UpdateDB.GEN_DATA_HITCHING);
		
		CostStrategy.defaultInstance = new CostStrategyEx();
		
//		UpdateDB.addHitchingCtor(new HitchingCtor(){
//			@Override public Hitching create() { return new CommonChekExporter(); }
//		}, UpdateDB.EXPORT_DATA_HITCHING);
	}
	
	@Override protected Class<? extends OrderImplBase<? extends Order>> orderImplType() { return OrderImplEx.class; }
	
	@Override
	protected void initChildDocTypes() {
		super.initChildDocTypes();

		DocType.addType(ProcurationDoc.instance());
		DocType.addType(ReturnDoc.instance(ReturnImplEx.class));
		DocType.addType(RequestCheckDoc.instance());
		DocType.addType(ReturnChekBackDoc.instance());
		
		DocType.removeType(IncassDoc.instance());
	}
	
	@Override
	protected void initChildFeature() {
		super.initChildFeature();

		Features.PUT_SALED_ITEMS_BEFORE = true;
		Features.HIDE_LOGIN = true;
		Features.HIDE_PASSWORD = true;
		Features.WEIGHT_SCALE = Consts.QTY_SCALE;
		Features.LOAD_FULL_PRICE = true;
		Features.USE_COST_IN_RETURNS = true;
		Features.HAVE_PRICE_MOVER = false;
	}

	@Override
	protected void initChildActivity() {
		super.initChildActivity();

		OrderDetail.activity = OrderDetailEx.class;
		OrderDeliveryDetail.activity = OrderDeliveryDetailEx.class;
		PriceCount.activity = PriceCountEx.class;
		UpdateDB.activity = UpdateDBEx.class;
		Warehouse.activity = WarehouseEx.class;
		Setting.BehaviorSettingActivity = BehavoirSettingEx.class;
		Documents.activity = DocumentsEx.class;
		CreateReturn.activity = CreateReturnEx.class;
	}
	
	@Override
	public void onCreate() {
		ConfigManager.initConfig(new CfgNpl());
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
