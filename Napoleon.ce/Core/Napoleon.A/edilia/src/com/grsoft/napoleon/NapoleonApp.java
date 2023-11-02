/*
 * Copyright (C), 2011, Гильдия Разработчиков
 * 
 * Демо программа (с базой данных)
 *
 * kki   16/05/2011   creating
 */

package com.grsoft.napoleon;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import com.grsoft.database.DDHitchingEx;
import com.grsoft.database.DlvMoveDayHitching;
import com.grsoft.database.Hitching;
import com.grsoft.database.HitchingCtor;
import com.grsoft.database.OrderResultHitching;
import com.grsoft.database.OrgBalanceHitching;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.database.RemovedDocumentsHitching;
import com.grsoft.dataobjects.ConfigHelper;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DeliveryItemEx;
import com.grsoft.dataobjects.Incass;
import com.grsoft.dataobjects.IncassDebDistrEx;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceCost;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.Return;
import com.grsoft.dataobjects.ReturnEx;
import com.grsoft.dataobjects.ReturnFolders;
import com.grsoft.dataobjects.ReturnItemEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.IncassDebDistrImplEx;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.OrgTaskExecImpl;
import com.grsoft.dataobjects.impl.ReturnImplEx;
import com.grsoft.napoleon.documents.DebtDocEx;
import com.grsoft.napoleon.documents.DeliveryDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.IncassDoc;
import com.grsoft.napoleon.documents.OrderDocEx;
import com.grsoft.napoleon.documents.QuestionDoc;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.napoleon.documents.TaskDoneDoc;
import com.grsoft.napoleon.util.CfgNplEx;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.ReadService;
import com.grsoft.network.ServerCommand;
import com.grsoft.network.WriteService;
import com.grsoft.util.AssortmentMatrixAdapter;
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
	}
	
	@Override
	protected void defineNewType() {
		DbObject.regNewDataType(Order.class, OrderEx.class);
		DbObject.regNewDataType(Price.class, PriceEx.class);
		DbObject.regNewDataType(Org.class, OrgEx.class);
		DbObject.regNewDataType(Incass.class, IncassDebDistrEx.class);
		DbObject.regNewDataType(Return.class, ReturnEx.class);
		
		DataObjectInfo doi = DataObjectInfo.getInstance();
		
		doi.replaceListType(Delivery.class, "items", DeliveryItemEx.class);
		doi.replaceListType(ReturnEx.class, "items", ReturnItemEx.class);
		//doi.replacePrimaryKey(Delivery.class, "id,number,date");

		Hitching ddh = new DDHitchingEx();
		Hitching dbdh = new DlvMoveDayHitching(); 
		Hitching obh = new OrgBalanceHitching();
		Hitching rdh = new RemovedDocumentsHitching();
		
//		NapoleonServiceW.priceUpdateHitchings.add(ddh);
//		NapoleonServiceW.priceUpdateHitchings.add(dbdh);
//		// баланс обновляет поле sumd накладных - обязательно должен приниматься после накладных
//		NapoleonServiceW.priceUpdateHitchings.add(obh);
		

		ReadService.requestObjects.add(ddh);
		ReadService.requestObjects.add(dbdh);
		ReadService.requestObjects.add(obh);
		ReadService.requestObjects.add(rdh);

		Hitching orh = new OrderResultHitching();
		WriteService.requestObjects.addAll(ReadService.requestObjects);
		WriteService.recievers.add(orh);
		
		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override
			public List<Hitching> createList() {
				Hitching[] h = new Hitching[] {
						new RcvNewHitching(ReturnFolders.class, "ReturnFolders"),
//						new RcvNewHitching(OrgLocation.class, "OrgLocation"),
						new RcvNewHitching(PriceCost.class),
//						new RcvNewHitching(PriceTop.class, "PriceTop"),
				};
				return Arrays.asList(h);
			}
		}, UpdateDB.GEN_DATA_HITCHING);

		Messages.DATE_FORMAT = new SimpleDateFormat("dd.MM.yy HH:mm", Locale.getDefault());
		AssortmentMatrixAdapter.MATRIX_DOC = DeliveryDoc.instance();
	}
	
	@Override protected Class<? extends OrderImplBase<? extends Order>> orderImplType() { return OrderImplEx.class; }

	@Override
	protected void initDocTypes() {
		DebtDocEx.initialize();
		OrderDocEx.initialize();
		IncassDoc.instance(IncassDebDistrImplEx.class);
		
		super.initDocTypes();
		
		DocType.removeType(RemnantsDoc.instance());
		DocType.removeType(QuestionDoc.instance());
		DocType.removeType(TaskDoneDoc.instance(OrgTaskExecImpl.class));
		DocType.addType(ReturnDoc.instance(ReturnImplEx.class));
	}
	
	@Override
	protected void initAcivity() {
		super.initAcivity();
		
		Warehouse.activity = WarehouseEx.class;
		UpdateDB.activity = UpdateDBEx.class;
		Documents.activity = DocumentsEx.class;
		PriceCount.activity = PriceCountEx.class;
		OrderDetail.activity = OrderDetailEx.class;
		DocList.activity = DocListEx.class;
		Setting.activity = SettingEx.class;
		OrderDeliveryDetail.activity = OrderDeliveryDetailEx.class;
		IncassDebDistrEdit.editActivity = IncassDebDistrEditEx.class;
		ReturnDetail.activity = ReturnDetailEx.class;
		CreateReturn.activity = CreateReturnEx.class;
		DeliveryDetail.activity = DeliveryDetailEx.class;
	}
	
	@Override
	protected void initFeatures() {
		super.initFeatures();
		
		Features.DDLV = false;
		Features.PACK_INPUT = true;
		Features.INCASS_DEBET_DISTRIB = true;
		Features.RECEIVE_REMNANTS_WHEN_SENDING = true;
		Features.LOAD_FULL_PRICE = true;
		Features.DELIVERY_REPLACE_ORDER_SUM = true;
		Features.SHOW_DAILY_WEIGHT_IN_WAREHOUSE = true;
		Features.COUNT_DOCS_IN_DOCSLIST = true;
		Features.CANT_CHANGE_SEND_FLAG = true;
//		Features.START_STOP = true;
		Features.WEIGHT_SCALE = 100;
		Features.SEND_IN_BACKGROUND = true;
		Features.FOCUSED_ITEMS = true;
		Features.KEEP_NOTES_ON_CLEAR_DB = true;
		
		Features.UNLIMIT_VISIT_ITEMS = true;
		Features.POTENZIAL_ORG = false;
		
		ConfigHelper.DEFAULT_DATE_TYPE = ConfigHelper.DlvDateType.today;
		
		Main.docMenuPrepared.add(new MenuPrepareHitching() {
			
			@Override
			public void menuPrepared(List<MenuHandler> menu, final Activity activity) {
				String dlvMenu = getString(R.string.dlv_doc_list);
				for(MenuHandler mh : menu) {
					if(mh.name.equals(dlvMenu) ) {
						menu.remove(mh);
						break;
					}
				}
				menu.add(new MenuHandler("Кассовка", new Runnable() {
					@Override public void run() { IncassReport.open(activity); }} ));
			}
		});
		
		CostStrategy.defaultInstance = new CostStrategyEx();
	}

	private void setProgrammVersion() {
		try{
			ServerCommand.ProgramVersion = getResources().getString(R.string.version);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
