/*
 * Copyright (C), 2011, Гильдия Разработчиков
 * 
 * Демо программа (с базой данных)
 *
 * kki   16/05/2011   creating
 */

package com.grsoft.napoleon;

import java.util.List;

import com.grsoft.database.Hitching;
import com.grsoft.database.HitchingCtor;
import com.grsoft.database.PriceHitchingEx;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.Action;
import com.grsoft.dataobjects.Categories;
import com.grsoft.dataobjects.ClassTT;
import com.grsoft.dataobjects.ContactEx;
import com.grsoft.dataobjects.Contract;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Delay;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.dataobjects.DeliveryItemEx;
import com.grsoft.dataobjects.DeliveryRoute;
import com.grsoft.dataobjects.Dutie;
import com.grsoft.dataobjects.Folder;
import com.grsoft.dataobjects.FolderEx;
import com.grsoft.dataobjects.Forma;
import com.grsoft.dataobjects.Incass;
import com.grsoft.dataobjects.IncassEx;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgContract;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgFolderItemEx;
import com.grsoft.dataobjects.OrgFolders;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.Price2Ex;
import com.grsoft.dataobjects.Visit;
import com.grsoft.dataobjects.VisitItemEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.VisitImplEx;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DebtDocEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.IncassDoc;
import com.grsoft.napoleon.documents.MonitoringDoc;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.QuestionDoc;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.napoleon.modules.MonitoringInit;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.ServerCommand;
import com.grsoft.util.AssortmentMatrixAdapter;
import com.grsoft.util.FirstRunInit;
import com.grsoft.util.MenuHandler;
import com.grsoft.util.MenuPrepareHitching;
import com.grsoft.util.NapoleonService;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

public class NapoleonApp extends Application {
	private static final String TAG = "NapoleonApp";
	
	class OrderEditor implements OrderImpl.PropertiesEditor {
		@Override
		public void edit(Context ctx, OrderImpl order, boolean isOldOrder) {
			CreateOrder.open(ctx, order, isOldOrder);
		}
	}
	
	private void initDocTypes() {
		DbObject.regNewDataType(Org.class, OrgEx.class);
		DbObject.regNewDataType(Price.class, Price2Ex.class);
		DbObject.regNewDataType(Delivery.class, DeliveryEx.class);
		DbObject.regNewDataType(Order.class, OrderEx.class);
		DbObject.regNewDataType(Folder.class, FolderEx.class);
		DbObject.regNewDataType(Incass.class, IncassEx.class);
		
		MonitoringInit.init();
		DebtDocEx.initialize();
		
		DocType.addType(OrderDoc.instance(OrderImplEx.class));
		DocType.addType(DebtDoc.instance());
		DocType.addType(VisitDoc.instance(VisitImplEx.class));
		//DocType.addType(RemnantsDoc.instance());
		DocType.addType(IncassDoc.instance());
		DocType.addType(MonitoringDoc.instance());
		DocType.addType(QuestionDoc.instance());
		
		DocType.setCurDoc(OrderDoc.instance());
		
		DataObjectInfo doi = DataObjectInfo.getInstance();
		doi.replaceListType(Org.class, "contacts", ContactEx.class);
		doi.replaceListType(Visit.class, "items", VisitItemEx.class);
		doi.replaceListType(OrgFolders.class, "items", OrgFolderItemEx.class);
		doi.replaceListType(Delivery.class, "items", DeliveryItemEx.class);
		
		Features.PACK_INPUT = true;
		Features.SHOW_PRESENT_IMG = true;
		Features.DELIVERY_REPLACE_ORDER_SUM = true;
		Features.POTENZIAL_ORG = false;
		
		PriceCount.activity = PriceCount2Ex.class;
		Documents.activity = DocumentsEx.class;
		Warehouse.activity = WarehouseEx.class;
		UpdateDB.activity = UpdateDB2Ex.class;
		CreateOrder.activity = CreateOrderEx.class;
		Presentation.activity = PresentationFolder.class;
		PricePresentation.activity = PricePresentationFolder.class;
		DocList.activity = DocListEx.class;
		Napoleon.serviceType = NapoleonService.class;
		OrderDeliveryDetail.activity = OrderDeliveryDetailEx.class;
		OrderDetail.activity = OrderDetailEx.class;
		
		Setting.NetworkSettingActivity = ConfigurationEx.class;
		
		Napoleon.docMenuPrepared.add(new MenuPrepareHitching() {
			
			@Override
			public void menuPrepared(List<MenuHandler> menu, final Activity activity) {
				menu.add(new MenuHandler(getString(R.string.incomes),
						new Runnable() { 
							@Override public void run() { IncomeForm.open(activity); }
						}));
			}
		});
		
		Napoleon.docMenuPrepared.add(new MenuPrepareHitching() {
			
			@Override
			public void menuPrepared(List<MenuHandler> menu, final Activity activity) {
				menu.add(new MenuHandler(getString(R.string.order_report),
						new Runnable() { 
							@Override public void run() { OrderReport.open(activity); }
						}));
			}
		});

		Napoleon.mainMenuPrepared.add(new MenuPrepareHitching() {
			
			@Override
			public void menuPrepared(List<MenuHandler> menu, Activity activity) {
				String settings = activity.getString(R.string.setting);
				String about = activity.getString(R.string.about);
		
				int idSet = -1, idAbout = -1;
				for(int i=0; i<menu.size(); i++) {
					String name = menu.get(i).name; 
					if(name.equals(settings))
						idSet = i;
					else if(name.equals(about))
						idAbout = i;
				}
				
				if(idSet >= 0 && idAbout >=0) {
					MenuHandler mh = menu.get(idSet);
					menu.remove(idSet);
					if(idAbout > idSet)
						idAbout--;
					menu.add(idAbout, mh);
				}
			}
		});
		
		Napoleon.mainMenuPrepared.add(new MenuPrepareHitching() {
			
			@Override
			public void menuPrepared(List<MenuHandler> menu, final Activity activity) {
				menu.add(2, new MenuHandler(getString(R.string.add_client),
					new Runnable() { 
						@Override public void run() { OrgsList.open(activity); }
					}));
			}
		});
		
		CostStrategy.defaultInstance = new CostStrategy2Ex();
		AssortmentMatrixAdapter.PERIOD_IN_MONTH = 0;
		
		UpdateDB.priceHitchingClass = PriceHitchingEx.class;
		
		UpdateDB.addHitchingCtor(new HitchingCtor() { @Override	public Hitching create() {
				return new RcvNewHitching(Action.class, "Action"); }}, UpdateDB.GEN_DATA_HITCHING);
		
		UpdateDB.addHitchingCtor(new HitchingCtor() { @Override	public Hitching create() {
			return new RcvNewHitching(Categories.class); }}, UpdateDB.GEN_DATA_HITCHING);
		
		UpdateDB.addHitchingCtor(new HitchingCtor() { @Override	public Hitching create() {
			return new RcvNewHitching(Forma.class); }}, UpdateDB.GEN_DATA_HITCHING);
		
		UpdateDB.addHitchingCtor(new HitchingCtor() { @Override	public Hitching create() {
			return new RcvNewHitching(ClassTT.class); }}, UpdateDB.GEN_DATA_HITCHING);
		
		UpdateDB.addHitchingCtor(new HitchingCtor() { @Override	public Hitching create() {
			return new RcvNewHitching(DeliveryRoute.class); }}, UpdateDB.GEN_DATA_HITCHING);
		
		UpdateDB.addHitchingCtor(new HitchingCtor() { @Override	public Hitching create() {
			return new RcvNewHitching(Delay.class); }}, UpdateDB.GEN_DATA_HITCHING);
		
		UpdateDB.addHitchingCtor(new HitchingCtor() { @Override	public Hitching create() {
			return new RcvNewHitching(Dutie.class); }}, UpdateDB.GEN_DATA_HITCHING);
		
		UpdateDB.addHitchingCtor(new HitchingCtor() { @Override	public Hitching create() {
			return new RcvNewHitching(OrgContract.class); }}, UpdateDB.GEN_DATA_HITCHING);
		
		CfgNpl cfgNpl = (CfgNpl) ConfigManager.getConfig();
		cfgNpl.max_packet_len = 3000000L;
		
		try{
			ConfigManager.save(this);
		}catch(Exception e){}
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		FirstRunInit.init(this);

		initDocTypes();
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
