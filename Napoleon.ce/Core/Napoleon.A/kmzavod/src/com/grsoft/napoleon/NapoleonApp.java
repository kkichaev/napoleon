/*
 * Copyright (C), 2011, Гильдия Разработчиков
 * 
 * Демо программа (с базой данных)
 *
 * kki   16/05/2011   creating
 */

package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.Hitching;
import com.grsoft.database.HitchingCtor;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.database.TaskRemarkExporter;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.DeliverItemEx;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgLocation;
import com.grsoft.dataobjects.Payment;
import com.grsoft.dataobjects.PaymentEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.Return;
import com.grsoft.dataobjects.ReturnEx;
import com.grsoft.dataobjects.ReturnItemEx;
import com.grsoft.dataobjects.SalesChannel;
import com.grsoft.dataobjects.ScriptDefEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.RemnantsImpl;
import com.grsoft.dataobjects.impl.RemnantsImplEx;
import com.grsoft.dataobjects.impl.ReturnImplEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.OrderDocEx;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.napoleon.util.ConfigPhotoInitilizer;
import com.grsoft.network.ServerCommand;
import com.grsoft.script.dataobjects.ScriptDef;
import com.grsoft.util.AssortmentMatrixAdapter;
import com.grsoft.util.CameraHelper;
import com.grsoft.util.Consts;
import com.grsoft.util.MenuHandler;
import com.grsoft.util.MenuPrepareHitching;
import com.grsoft.util.Size;
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
	protected void defineNewType() {
		OrderDocEx.initialize();
		
		CostStrategy.defaultInstance = new CostStrategyEx();
		
		DbObject.regNewDataType(Org.class, OrgEx.class);
		DbObject.regNewDataType(Payment.class, PaymentEx.class);
		DbObject.regNewDataType(Delivery.class, DeliveryEx.class);
		DbObject.regNewDataType(Order.class, OrderEx.class);
		DbObject.regNewDataType(Return.class, ReturnEx.class);
		DbObject.regNewDataType(Price.class, PriceEx.class);
		DbObject.regNewDataType(ScriptDef.class, ScriptDefEx.class);
		
		DataObjectInfo doi = DataObjectInfo.getInstance();
		doi.replacePrimaryKey(PaymentEx.class, "id,dogovor,number");
		doi.replacePrimaryKey(DeliveryEx.class, "id,dogovor,number");
		doi.replaceListType(DeliveryEx.class, "items", DeliverItemEx.class);
		doi.replaceListType(ReturnEx.class, "items", ReturnItemEx.class);
		doi.replaceListType(OrderEx.class, "items", OrderItemEx.class);
		
		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override
			public List<Hitching> createList() {
				CostStrategyEx.resetCache();
				List<Hitching> result = new ArrayList<Hitching>();
				result.add(new RcvNewHitching(OrgLocation.class));
				result.add(new RcvNewHitching(SalesChannel.class));
				return result;
		}}, UpdateDB.GEN_DATA_HITCHING);
		
		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override
			public Hitching create() {
				return new TaskRemarkExporter();
			}
		} ,UpdateDB.EXPORT_DATA_HITCHING );
		
		Main.docMenuPrepared.add(new MenuPrepareHitching() {
			
			@Override
			public void menuPrepared(List<MenuHandler> menu, final Activity activity) {
				menu.add(new MenuHandler(activity.getString(R.string.sales_report), new Runnable() {
					
					@Override
					public void run() {
						SalesParams.open(activity, null);
					}
				}));
				
			}
		});
	}
	
	@Override
	protected void initChildFeature() {
		AssortmentMatrixAdapter.PERIOD_IN_MONTH = 3;
		Features.MAX_FOTO_WIDTH = 2000;
		Features.MAX_FOTO_HEIGHT = 2000;
		Features.LOAD_FULL_PRICE = true;
		Features.SHOW_NUMBER_IN_ORDER = true;
		Features.DDLV = true;
		Features.KEEP_DIALOG_AFTER_SYNC = true;
		Features.EXCLUDE_RETURN_DOC_SUM_FROM_SCRIPT = true;
	}
	
	@Override
	protected void initChildActivity() {
		Documents.activity = DocumentsEx.class;
		Warehouse.activity = WarehouseEx.class;
		CreateReturn.activity = CreateReturnEx.class;
		PriceCount.activity = PriceCountEx.class;
		TaskDocList.activity = TaskDocListEx.class;
		PriceCount.activity = PriceCountEx.class;
		OrderDetail.activity = OrderDetailEx.class;
		RemnantsDetail.activity = RemnantsDetailEx.class;
		
		UpdateDBW.initUI = new ViewInitializer() {
			@Override
			public void init(Activity activity) {
				super.init(activity);
				
				((CheckBox) activity.findViewById(R.id.cbDebt)).setChecked(true);
			}
		};
	}
	
	@Override
	protected void initChildDocTypes() {
		DocType.addType(ReturnDoc.instance(ReturnImplEx.class));
	}
	
	@SuppressWarnings("serial")
	@Override
	public void onCreate() {
		ConfigManager.photoInit = new ConfigPhotoInitilizer() {
			@Override
			protected Size getCamSize() {
				return CameraHelper.getMaxCamSize();
			}
		};
		
		ConfigManager.initConfig(new CfgNpl() {
			@Override
			public void resetToDefault() {
				super.resetToDefault();
				day_to_del_visit = 60;
				monthsToRecreate = 2;
				onlyNewstItems = 1;
				dataSendInBackground = true;
				gpsSendInterval = 10;
				waitGpsCoordOnRequest = 10;
				gps_valid_in_org = 20 * Consts.ONE_SECOND * Consts.SEC_PER_MIN;
				checkPrice = true;
				isComplexSalesHistory = true;
				imagePosInPriceCount = 1;
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
	
	protected Class<? extends RemnantsImpl> remantsImplType() { 
		return RemnantsImplEx.class; 
	}
	
	@Override
	protected Class<? extends OrderImplBase<? extends Order>> orderImplType() {
		return OrderImplEx.class;
	}
}
