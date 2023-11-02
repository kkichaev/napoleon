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

import com.grsoft.database.DataObjectRestore;
import com.grsoft.database.Hitching;
import com.grsoft.database.HitchingCtor;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.AgentBalance;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DeliveryItemEx;
import com.grsoft.dataobjects.Dover;
import com.grsoft.dataobjects.Incass;
import com.grsoft.dataobjects.IncassEx;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgDistrib;
import com.grsoft.dataobjects.OrgDistribItem;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.Return;
import com.grsoft.dataobjects.ReturnCommit;
import com.grsoft.dataobjects.ReturnEx;
import com.grsoft.dataobjects.ReturnItemEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.ReturnImplEx;
import com.grsoft.napoleon.documents.BankIncassDoc;
import com.grsoft.napoleon.documents.DebtDocEx;
import com.grsoft.napoleon.documents.DistribDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.napoleon.modules.CostHitching;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.ReadService;
import com.grsoft.network.ServerCommand;
import com.grsoft.network.WriteService;
import com.grsoft.network.exception.RuntimeException;
import com.grsoft.util.DocFilterOnClickListener;
import com.grsoft.util.MenuHandler;
import com.grsoft.util.MenuPrepareHitching;
import com.grsoft.util.ViewInitializer;

import android.app.Activity;
import android.content.Context;
import android.view.View;
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
		DebtDocEx.initialize();
		
		DbObject.regNewDataType(Order.class, OrderEx.class);
		DbObject.regNewDataType(Price.class, PriceEx.class);
		DbObject.regNewDataType(Org.class, OrgEx.class);
		DbObject.regNewDataType(Incass.class, IncassEx.class);
		DbObject.regNewDataType(Return.class, ReturnEx.class);
		DataObjectInfo.getInstance().replaceListType(Order.class, "items", OrderItemEx.class);
		DataObjectInfo.getInstance().replaceListType(ReturnEx.class, "items", ReturnItemEx.class);
		DataObjectInfo.getInstance().replaceListType(OrgDistrib.class, "items", OrgDistribItem.class);
		DataObjectInfo.getInstance().replaceListType(Delivery.class, "items", DeliveryItemEx.class);
		
		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override
			public List<Hitching> createList() {
				CostStrategyEx.clearCache();
				Hitching[] h = new Hitching[] {
					new RcvNewHitching(Dover.class, "DoverNumber"),
					new RcvNewHitching(AgentBalance.class, "AgentBalance"),
				};
				return Arrays.asList(h);
			}
		}, UpdateDB.GEN_DATA_HITCHING);
		
		CostStrategy.defaultInstance = new CostStrategyEx();
		
		UpdateDB.initUI = new ViewInitializer() {
			@Override
			public void init(Activity activity) {
				activity.findViewById(R.id.cbRemains).setVisibility(View.GONE);
				((CheckBox)activity.findViewById(R.id.cbCost)).setChecked(true);
			}
		};
		
		ReadService.requestObjects.add(new RcvNewHitching(ReturnCommit.class) {
			@Override protected void postRead(DataObject dobj) { ((ReturnCommit)dobj).processReturn(); }
			@Override
			public void onEnd() {
				super.onEnd();
				try {
					ReturnDoc.instance().refreshDocSum();
				} catch (RuntimeException e) {
					e.printStackTrace();
				}
			}
		});
		WriteService.requestObjects.addAll(ReadService.requestObjects);
		
		Main.docMenuPrepared.add(new MenuPrepareHitching() {
			
			@Override
			public void menuPrepared(List<MenuHandler> menu, final Activity activity) {
				menu.add(new MenuHandler("Инкассация через банкомат", new Runnable() {
					@Override public void run() { BankIncassList.open(activity); }
				}));
			}
		});
		
		CostHitching.COST_OBJECT = "CostNew";
	}
	
	@Override
	protected Class<? extends OrderImplBase<? extends Order>> orderImplType() {
		return OrderImplEx.class;
	}
	
	@Override
	protected void initChildActivity() {
		PriceCount.activity = PriceCountEx.class;
		Warehouse.activity = WarehouseEx.class;
		IncassEdit.activity = IncassEditEx.class;
		ReturnDetail.activity = ReturnDetailEx.class;
		Documents.activity = DocumentsEx.class;
		UpdateDB.activity = UpdateDBEx.class;
		OrderDeliveryDetail.activity = OrderDeliveryDetailEx.class;
		CreateReturn.activity = CreateReturnEx.class;
	}
	
	@Override
	protected void initChildFeature() {
		Features.COST_MANAGER = new CostManagerImplEx();
		Features.CAN_CHANGE_COST = true;
		Features.USE_COST_IN_RETURNS = true;
		Features.LOAD_FULL_PRICE = true;
		Features.HAVE_PRICE_MOVER = true;
		Features.SHOW_WEIGHT_IN_DOC_LIST = true;
	}
	
	@Override
	protected void initChildDocTypes() {
		DocType.addType(ReturnDoc.instance(ReturnImplEx.class));
		DocType.addType(DistribDoc.instance());
		
		DocType.addType(BankIncassDoc.instance());
		DocFilterOnClickListener.HiddenTypes.add(BankIncassDoc.instance()); 
	
		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override public Hitching create() { 
				return new DataObjectRestore(ReturnCommit.class, "ReturnCommit", "created") {
					@Override protected void beforeWrite(DataObject dobj) { ((ReturnCommit)dobj).processReturn(); }
					@Override
					public void onEnd() {
						super.onEnd();
						try {
							ReturnDoc.instance().refreshDocSum();
						} catch (RuntimeException e) {
							e.printStackTrace();
						}
					}
				};  
			}
		}, UpdateDB.RESTORE_DATA_HITCHING);
		
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
