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
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Incass;
import com.grsoft.dataobjects.IncassEx;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.Terminal;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.napoleon.documents.DistrDocType;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.network.ServerCommand;
import com.grsoft.util.AssortmentMatrixAdapter;
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
		DbObject.regNewDataType(Price.class, PriceEx.class);
		DataObjectInfo.getInstance().replaceListType(Order.class, "items", OrderItemEx.class);
		DbObject.regNewDataType(Org.class, OrgEx.class);
		DbObject.regNewDataType(Incass.class, IncassEx.class);
	
		Napoleon.docMenuPrepared.add(new MenuPrepareHitching() {
			@Override
			public void menuPrepared(List<MenuHandler> menu, final Activity activity) {
				menu.add(new MenuHandler(getString(R.string.incassList), new Runnable() {
					@Override public void run() { IncassDocList.open(activity);	}
				}));
			}
		});

		Main.docMenuPrepared.add(new MenuPrepareHitching() {
			@Override
			public void menuPrepared(List<MenuHandler> menu, final Activity activity) {
				menu.add(new MenuHandler(getString(R.string.incassList), new Runnable() {
					@Override public void run() { IncassDocList.open(activity);	}
				}));
			}
		});
		
		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override
			public Hitching create() {
				CostStrategyEx.clearCache();
				return new RcvNewHitching(Terminal.class);
			}
		}, UpdateDB.GEN_DATA_HITCHING);
		
		CostStrategy.defaultInstance = new CostStrategyEx();
	}
	
	@Override
	protected void initChildDocTypes() {
		DocType.addType(DistrDocType.instance());
	}
	
	@Override
	protected void initChildActivity() {
		Warehouse.activity = WarehouseEx.class;
		PriceCount.activity = PriceCountEx.class;
		OrderDetail.activity = OrderDetailEx.class;
		UpdateDB.activity = UpdateDBEx.class;
		Presentation.activity = PresentationFolder.class;
		PricePresentation.activity = PricePresentationFolder.class;
		Documents.activity = DocumentsEx.class;
		IncassEdit.activity = IncassEditEx.class;
	}
	
	@Override
	protected void initChildFeature() {
		Features.INPUT_QTY_IN_PACK = true;
		Features.SHOW_WEIGHT_IN_DOC_LIST = true;
		Features.LAST_SALED_ITEMS_PERIOD = 2;
		Features.MAX_FOTO_HEIGHT = 2200;
		Features.MAX_FOTO_WIDTH = 2200;
		Features.OK_BTN_INCASS = true;
		
		AssortmentMatrixAdapter.PERIOD_IN_MONTH = 2;
	}
	
//	private void initDocTypes() {
//
//		DocType.addType(OrderDoc.instance());
//		DocType.addType(DebtDoc.instance());
//		DocType.addType(VisitDoc.instance());
//		DocType.addType(IncassDoc.instance());
//		DocType.addType(RemnantsDoc.instance());
//		DocType.addType(DistrDocType.instance());
//		DocType.addType(ScriptDoc.instance());
//		
//		DocType.setCurDoc(OrderDoc.instance());		
//
//		Warehouse.activity = WarehouseEx.class;
//		PriceCount.activity = PriceCountEx.class;
//		OrderDetail.activity = OrderDetailEx.class;
//		UpdateDB.activity = UpdateDBEx.class;
//		Presentation.activity = PresentationFolder.class;
//		PricePresentation.activity = PricePresentationFolder.class;
//		Documents.activity = DocumentsEx.class;
//		IncassEdit.activity = IncassEditEx.class;
//
//		Features.INPUT_QTY_IN_PACK = true;
//		Features.SHOW_WEIGHT_IN_DOC_LIST = true;
//		Features.LAST_SALED_ITEMS_PERIOD = 2;
//		Features.MAX_FOTO_HEIGHT = 2200;
//		Features.MAX_FOTO_WIDTH = 2200;
//		Features.OK_BTN_INCASS = true;
//		
//		AssortmentMatrixAdapter.PERIOD_IN_MONTH = 2;
//		
//		Napoleon.docMenuPrepared.add(new MenuPrepareHitching() {
//			@Override
//			public void menuPrepared(List<MenuHandler> menu, final Activity activity) {
//				menu.add(new MenuHandler(getString(R.string.incassList), new Runnable() {
//					@Override public void run() { IncassDocList.open(activity);	}
//				}));
//			}
//		});
//		
//		UpdateDB.addHitchingCtor(new HitchingCtor() {
//			@Override
//			public Hitching create() {
//				return new RcvNewHitching(Terminal.class);
//			}
//		}, UpdateDB.GEN_DATA_HITCHING);
//	}
	
	@Override
	public void onCreate() {
		super.onCreate();
//		FirstRunInit.init(this);
//
//		initDocTypes();
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
