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

import com.grsoft.database.DocumentRestore;
import com.grsoft.database.Hitching;
import com.grsoft.database.HitchingCtor;
import com.grsoft.database.PriceHitchingEx;
import com.grsoft.database.PriceQtyHitching;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Dogovor;
import com.grsoft.dataobjects.Firm;
import com.grsoft.dataobjects.FirmEx;
import com.grsoft.dataobjects.Gtin;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceCost;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.PriceType;
import com.grsoft.dataobjects.Sales;
import com.grsoft.dataobjects.SalesEx;
import com.grsoft.dataobjects.SalesItemEx;
import com.grsoft.dataobjects.Sklads;
import com.grsoft.dataobjects.WSOrder;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.SalesImplEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.PkoDoc;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.napoleon.documents.SalesDoc;
import com.grsoft.napoleon.documents.SalesDocEx;
import com.grsoft.napoleon.documents.WSOrderDoc;
import com.grsoft.napoleon.modules.print.NPrinter;
import com.grsoft.napoleon.modules.print.Print;
import com.grsoft.napoleon.modules.print.util.BaseDocNumberStrategy;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.CfgNplEx;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.ServerCommand;
import com.grsoft.network.WriteService;
import com.grsoft.util.DocFilterOnClickListener;
import com.grsoft.util.MenuHandler;
import com.grsoft.util.MenuPrepareHitching;
import com.grsoft.util.NapoleonServiceW;
import com.grsoft.util.ViewInitializer;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.view.View;

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
		Main.ADD_PERMISSIONS.add(Manifest.permission.BLUETOOTH);
		Main.ADD_PERMISSIONS.add(Manifest.permission.BLUETOOTH_ADMIN);
		Main.ADD_PERMISSIONS.add(Manifest.permission.BLUETOOTH_CONNECT);

		SalesDocEx.init();

		super.defineNewType();

		SalesDoc.instance(SalesImplEx.class);
		Print.init();
		NPrinter.forms.put("Накладная", "rn");

		BaseDocNumberStrategy.FormatDocStr = "%s%d";

		DbObject.regNewDataType(Price.class, PriceEx.class);
		DbObject.regNewDataType(Order.class, OrderEx.class);
		DbObject.regNewDataType(Sales.class, SalesEx.class);
		DbObject.regNewDataType(Org.class, OrgEx.class);
		DbObject.regNewDataType(Firm.class, FirmEx.class);

		DataObjectInfo.getInstance().replaceListType(OrderEx.class, "items", OrderItemEx.class);
		DataObjectInfo.getInstance().replaceListType(SalesEx.class, "items", SalesItemEx.class);

		NPrinter.setPrintStrategy(NPrinter.TEXT);
		Setting.addTabs.remove(TextPrinterSetting.class);
		Setting.addTabs.add(WiFiPrinterSettings.class);
		Setting.addTabs.add(ScannerSettings.class);

		CostStrategy.defaultInstance = new CostStrategyEx();

		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override
			public List<Hitching> createList() {
				CostStrategyEx.restCache();
				Hitching[] h = new Hitching[]{
						new RcvNewHitching(Sklads.class, "Sklads"),
						new PriceQtyHitching(),
						new RcvNewHitching(PriceType.class, "PriceType"),
						new RcvNewHitching(Dogovor.class, "Dogovor"),
						new RcvNewHitching(PriceCost.class, "PriceCost"),
						new RcvNewHitching(Gtin.class),
				};
				return Arrays.asList(h);
			}
		}, UpdateDB.GEN_DATA_HITCHING);

		UpdateDB.priceHitchingClass = PriceHitchingEx.class;
		UpdateDB.initUI = new ViewInitializer() {
			@Override public void init(Activity activity) { activity.findViewById(R.id.cbRemains).setVisibility(View.GONE); }
		};

		DocFilterOnClickListener.HiddenTypes.add(WSOrderDoc.instance());

		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override public Hitching create() { return new DocumentRestore(WSOrderDoc.instance()); }
		}, UpdateDB.RESTORE_DATA_HITCHING);

		Main.docMenuPrepared.add( new MenuPrepareHitching() {

			@Override
			public void menuPrepared(List<MenuHandler> menu, final Activity activity) {
				menu.add(new MenuHandler(getString(R.string.wsorder_title), new Runnable() {
					@Override public void run() { WSOrderList.open(activity); }
				}));
			}
		});

//		NapoleonServiceW.priceUpdateHitchings.add(new PriceQtyHitching());
//		NapoleonServiceW.priceUpdateHitchings.add(new PriceHitchingEx());
	}

	@Override
	protected Class<? extends OrderImplBase<? extends Order>> orderImplType() {
		return OrderImplEx.class;
	}

	@Override
	protected void initChildActivity() {
		super.initChildActivity();

		Warehouse.activity = WarehouseEx.class;
		CreateSales.activity = CreateSalesEx.class;
		SalesDetail.activity = SalesDetailEx.class;
		VanRestReport.activity = VanRestReportEx.class;
		VisitEdit.activity = VisitEditEx.class;
	}

	@Override
	protected void initChildFeature() {
		super.initChildFeature();
		Features.LOAD_FULL_PRICE = true;
		Features.USING_CAMERA_X = true;
		Features.USING_SCAN_FRAME = false;
		Features.WH_QTY = true;
	}

	@Override
	protected void initChildDocTypes() {
		super.initChildDocTypes();
		DocType.addType(SalesDoc.instance());
		DocType.addType(PkoDoc.instance());
		DocType.setCurDoc(SalesDoc.instance());

		DocType.removeType(ReturnDoc.instance());
	}

	@Override
	public void setDefDocType() {
		DocType.setCurDoc(SalesDoc.instance());
	}

	@Override
	protected CfgNpl createConfig() {
		return new CfgNplEx();
	}

	@Override
	public void onCreate() {
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
	}}
