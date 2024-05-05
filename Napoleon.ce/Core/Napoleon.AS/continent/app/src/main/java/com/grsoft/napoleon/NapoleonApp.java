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

import com.grsoft.database.DayDeliveryHitching;
import com.grsoft.database.DocHandleResultHitching;
import com.grsoft.database.DocumentRestore;
import com.grsoft.database.Hitching;
import com.grsoft.database.HitchingCtor;
import com.grsoft.database.PODHitching;
import com.grsoft.database.SPODHitching;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Inventory;
import com.grsoft.dataobjects.InventoryItem;
import com.grsoft.dataobjects.MovementAnswer;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderAnswer;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.ResponseAttach;
import com.grsoft.dataobjects.Return;
import com.grsoft.dataobjects.ReturnEx;
import com.grsoft.dataobjects.ReturnItemEx;
import com.grsoft.dataobjects.Sales;
import com.grsoft.dataobjects.SalesEx;
import com.grsoft.dataobjects.SalesItem;
import com.grsoft.dataobjects.Visit;
import com.grsoft.dataobjects.VisitItemEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.ReturnImplEx;
import com.grsoft.dataobjects.impl.SalesImplEx;
import com.grsoft.napoleon.documents.ApplyWSOrderDoc;
import com.grsoft.napoleon.documents.DebtDocEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.InventoryDoc;
import com.grsoft.napoleon.documents.NewClientDoc;
import com.grsoft.napoleon.documents.RequestdocDoc;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.napoleon.documents.SalesDoc;
import com.grsoft.napoleon.documents.WSOrderDoc;
import com.grsoft.napoleon.modules.CostManagerImpl;
import com.grsoft.napoleon.modules.print.NPrinter;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.CfgNplEx;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.ReadService;
import com.grsoft.network.ServerCommand;
import com.grsoft.network.WriteService;
import com.grsoft.util.AssortmentMatrixAdapter;
import com.grsoft.util.DocFilterOnClickListener;
import com.grsoft.util.MenuHandler;
import com.grsoft.util.MenuPrepareHitching;
import com.grsoft.util.ViewInitializer;
import com.grsoft.util.ZeroPositionFilter;

import android.app.Activity;
import android.content.Context;
import android.view.View;

public class NapoleonApp extends NapoleonAppBase {
	class OrderEditor implements OrderImpl.PropertiesEditor {
		@Override
		public void edit(Context ctx, OrderImpl order, boolean isOldOrder) {
			CreateOrder.open(ctx, order, isOldOrder);
		}
	}
	
	@Override
	protected void defineNewType() {
		ServerCommand.Category = "vanpda";
		super.defineNewType();

		DbObject.regNewDataType(Org.class, OrgEx.class);
		DbObject.regNewDataType(Order.class, OrderEx.class);
		DbObject.regNewDataType(Sales.class, SalesEx.class);
		DbObject.regNewDataType(Price.class, PriceEx.class);
		DbObject.regNewDataType(Return.class, ReturnEx.class);
		DataObjectInfo.getInstance().replaceListType(ReturnEx.class, "items", ReturnItemEx.class);
		DataObjectInfo.getInstance().replaceListType(OrderEx.class, "items", OrderItemEx.class);
		DataObjectInfo.getInstance().replaceListType(Sales.class, "items", SalesItem.class);
		DataObjectInfo.getInstance().replaceListType(Inventory.class, "items", InventoryItem.class);
		DataObjectInfo.getInstance().replaceListType(Visit.class, "items", VisitItemEx.class);

		NPrinter.forms.put("Перемещение на борт", "ws_order");
	}

	@Override
	protected void initChildDocTypes() {
		super.initChildDocTypes();

		DocType.addType(SalesDoc.instance(SalesImplEx.class));
		DocType.addType(NewClientDoc.instance());
		DocType.addType(InventoryDoc.instance());
		DocType.addType(RequestdocDoc.instance());
		DocType.addType(ReturnDoc.instance(ReturnImplEx.class));
		DocType.addType(WSOrderDoc.instance());
		DocType.addType(ApplyWSOrderDoc.instance());

		DocFilterOnClickListener.HiddenTypes.add(NewClientDoc.instance());
		DocFilterOnClickListener.HiddenTypes.add(InventoryDoc.instance());
		DocFilterOnClickListener.HiddenTypes.add(RequestdocDoc.instance());
		DocFilterOnClickListener.HiddenTypes.add(WSOrderDoc.instance());
		DocFilterOnClickListener.HiddenTypes.add(ApplyWSOrderDoc.instance());
	}

	@Override
	protected void initChildActivity() {
		super.initChildActivity();

		ZeroPositionFilter.SalesDoc = SalesDoc.instance();

		Warehouse.activity = WarehouseEx.class;
		Presentation.activity = PresentationFolderEx.class;
		PricePresentation.activity = PricePresentationFolder.class;
		PriceCount.activity = PriceCountEx.class;
		SalesDetail.activity = SalesDetailEx.class;
		CreateSales.activity = CreateSalesEx.class;
		PotenzialOrg.activity = NewClientList.class;
		Documents.activity = DocumentsEx.class;
		OrderDetail.activity = OrderDetailEx.class;
		OrderDeliveryDetail.activity = OrderDeliveryDetailEx.class;
		PricePresentationFolder.activity = PricePresentationFolderEx.class;
		PricePresentation.activity = PricePresentationFolderEx.class;
		CreateReturn.activity = CreateReturnEx.class;
		Setting.BehaviorSettingActivity = BehaviorSettingEx.class;
		UpdateDB.activity = UpdateDBEx.class;
	}

	@Override
	protected Class<? extends OrderImplBase<? extends Order>> orderImplType() {
		return OrderImplEx.class;
	}

	@Override
	protected void initChildFeature() {
		super.initChildFeature();

		Features.DOC_STATUS_IN_DOC_LIST = true;
		Features.UPDATE_PRICE_BACKGROUND = true;
		Features.PRINT_MODULE = true;
		Features.LOAD_FULL_PRICE = true;
		Features.HAVE_PRICE_MOVER = true;
		Features.SYNC_INFO = true;
		Features.OPEN_LAST_MATRIX = true;
		Features.INCASS_DEBET_DISTRIB = true;
		Features.MARK_OVERDUE_DEBTS = true;
		Features.MIN_FOTO_HEIGHT = 1200;
		Features.MIN_FOTO_WIDTH = 1200;
		Features.CONFIG_CHECK_PRICE_QTY = false;

		CostStrategy.defaultInstance = new CostStrategyEx();
		Features.COST_MANAGER = new CostManagerImpl();
	}

	protected void initDocTypes() {
		DebtDocEx.initialize();

		super.initDocTypes();

		UpdateDB.initUI = new ViewInitializer() {
			@Override
			public void init(Activity activity) {
				activity.findViewById(R.id.cbRemains).setVisibility(View.GONE);
			}
		};
		
		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override
			public List<Hitching> createList() {
				Hitching[] h = new Hitching[] {
						new DocumentRestore(WSOrderDoc.instance()),
						new DocumentRestore(SalesDoc.instance()),
						new DocumentRestore(RequestdocDoc.instance()),
				};
				return Arrays.asList(h);
			}
		}, UpdateDB.RESTORE_DATA_HITCHING);

		UpdateDB.addHitchingCtor(new HitchingCtor(){
			@Override
			public List<Hitching> createList() {
				Hitching[] h = new Hitching[] {
						new Hitching(ResponseAttach.class),
						new Hitching(MovementAnswer.class),
				};
				return Arrays.asList(h);
			}
		}, UpdateDB.GEN_DATA_HITCHING);


		Main.docMenuPrepared.add(new MenuPrepareHitching() {
			@Override
			public void menuPrepared(List<MenuHandler> menu, final Activity activity) {
				menu.add(new MenuHandler(getString(R.string.ordrep), new Runnable() {
					@Override
					public void run() {
						RepEdit.open(activity);
					}
				}));
			}
		});
		
		Main.docMenuPrepared.add(new MenuPrepareHitching() {

			@Override
			public void menuPrepared(List<MenuHandler> menu, final Activity activity) {
				menu.add(new MenuHandler(getString(R.string.inventory), new Runnable() {
					@Override
					public void run() {
						InventoryList.open(activity);
					}
				}));
			}
		});

		Main.docMenuPrepared.add(new MenuPrepareHitching() {

			@Override
			public void menuPrepared(List<MenuHandler> menu, final Activity activity) {
				menu.add(new MenuHandler(getString(R.string.wsorder_title), new Runnable() {
					@Override
					public void run() {
						WSOrderList.open(activity);
					}
				}));
			}
		});
		
		Main.docMenuPrepared.add(new MenuPrepareHitching() {

			@Override
			public void menuPrepared(List<MenuHandler> menu, final Activity activity) {
				menu.add(new MenuHandler(getString(R.string.requested_doc), new Runnable() {
					@Override
					public void run() {
						RequestedDocList.open(activity);
					}
				}));
			}
		});

		DayDeliveryHitching ddh = new DayDeliveryHitching();

		Hitching mah = new Hitching(MovementAnswer.class);
		Hitching oah = new Hitching(OrderAnswer.class);
		DocHandleResultHitching drh = new DocHandleResultHitching();

		ReadService.recievers.add(ddh);
		ReadService.recievers.add(mah);
		ReadService.recievers.add(oah);
		ReadService.recievers.add(drh);

		WriteService.recievers.add(ddh);
		WriteService.recievers.add(mah);
		WriteService.recievers.add(oah);
		WriteService.recievers.add(drh);

		PODHitching.type = SPODHitching.class;
	}

	@Override
	public void onCreate() {
		ConfigManager.initConfig(new CfgNplEx());
		super.onCreate();

		AssortmentMatrixAdapter.PERIOD_IN_MONTH = 3;
		OrderImpl.OrderEditor = new OrderEditor();
		setProgrammVersion();
	}

	private void setProgrammVersion() {
		try {
			ServerCommand.ProgramVersion = getResources().getString(R.string.version);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
