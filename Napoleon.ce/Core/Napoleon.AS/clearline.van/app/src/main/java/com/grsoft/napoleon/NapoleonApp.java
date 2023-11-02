/*
 * Copyright (C), 2011, Гильдия Разработчиков
 * 
 * Демо программа (с базой данных)
 *
 * kki   16/05/2011   creating
 */

package com.grsoft.napoleon;

import android.app.Activity;
import android.content.Context;

import com.grsoft.database.DocNumberHitching;
import com.grsoft.database.DocumentRestore;
import com.grsoft.database.Hitching;
import com.grsoft.database.HitchingCtor;
import com.grsoft.database.PKO1cHitching;
import com.grsoft.database.PriceHitchingEx;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.database.ReqOrderHitching;
import com.grsoft.database.ServerInfoHitchingCl;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.dataobjects.Firm;
import com.grsoft.dataobjects.FirmEx;
import com.grsoft.dataobjects.Income;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Payment;
import com.grsoft.dataobjects.PaymentEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.Return;
import com.grsoft.dataobjects.ReturnEx;
import com.grsoft.dataobjects.Sales;
import com.grsoft.dataobjects.SalesBan;
import com.grsoft.dataobjects.SalesEx;
import com.grsoft.dataobjects.SalesItemEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.SalesImplEx;
import com.grsoft.dataobjects.impl.ScriptImplEx;
import com.grsoft.napoleon.documents.ArchSalesDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.SalesDoc;
import com.grsoft.napoleon.documents.SalesDocEx;
import com.grsoft.napoleon.documents.TotalSumConvertor;
import com.grsoft.napoleon.documents.WSOrderDoc;
import com.grsoft.napoleon.modules.print.NPrinter;
import com.grsoft.napoleon.modules.print.Print;
import com.grsoft.napoleon.modules.print.util.DocHelper;
import com.grsoft.napoleon.modules.print.util.DocNumberStrategy;
import com.grsoft.napoleon.printsources.SalesPrintEx;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.CfgNplEx;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.ReadService;
import com.grsoft.network.ServerCommand;
import com.grsoft.network.WriteService;
import com.grsoft.script.dataobjects.impl.ScriptImpl;
import com.grsoft.util.MenuHandler;
import com.grsoft.util.MenuPrepareHitching;

import java.util.Arrays;
import java.util.List;


public class NapoleonApp extends NapoleonAppBase  implements ReqOrderHitching.Handler{
	public static final String T12_SCF = "Торг-12/Сч-ф"; 

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
		SalesDocEx.initilize(SalesImplEx.class);

		Print.init();

		DbObject.regNewDataType(Sales.class, SalesEx.class);
		DbObject.regNewDataType(Org.class, OrgEx.class);
		DbObject.regNewDataType(Order.class, OrderEx.class);
		DbObject.regNewDataType(Delivery.class, DeliveryEx.class);
		DbObject.regNewDataType(Payment.class, PaymentEx.class);
		DbObject.regNewDataType(Return.class, ReturnEx.class);
		DataObjectInfo.getInstance().replaceListType(SalesEx.class, "items", SalesItemEx.class);
		DbObject.regNewDataType(Price.class, PriceEx.class);
		DbObject.regNewDataType(Firm.class, FirmEx.class);

		SalesDetail.SalesPrintType = SalesPrintEx.class;

		NPrinter.BARCODE_HEIGHT = 25;
		NPrinter.forms.put("Накладная", "nakl");
		NPrinter.forms.put(T12_SCF, NPrinter.TORG_12_NAME + "," + NPrinter.TORG_12_NAME + "," + NPrinter.SCHET_FACT_NAME);
		NPrinter.forms.put("Счет", "bill");
		NPrinter.forms.put(NPrinter.UPD_CAPTION, NPrinter.UPD_NAME);
		NPrinter.forms.put("Доверенность", "dover");
		NPrinter.forms.put("Удостоверение качества", "otk");
		NPrinter.forms.put("ws_order", "ws_order");
		NPrinter.forms.put("Остатки", "vanrest");
		NPrinter.forms.put("ТТН на остаток", "ttn_rest");
		NPrinter.forms.put("salelistpage", "salelistpage");
		NPrinter.forms.put("price", "price");
		NPrinter.forms.put("ТТН", "ttn");
		NPrinter.forms.put("ttn_back", "ttn_back");
		NPrinter.forms.put("party_nakl", "party_nakl");
		NPrinter.forms.put("wsales", "wsales");

		UpdateDB.priceHitchingClass = PriceHitchingEx.class;
		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override public Hitching create() { return new DocumentRestore(WSOrderDoc.instance()); }
		}, UpdateDB.RESTORE_DATA_HITCHING);

		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override
			public List<Hitching> createList() {
				Hitching[] h = new Hitching[] {
						new RcvNewHitching(Income.class, "Incomes"),
						new PKO1cHitching(),
				};
				return Arrays.asList(h);
			}
		}, UpdateDB.DEBET_DATA_HITCHING);

		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override
			public List<Hitching> createList() {
				Hitching[] h = new Hitching[] {
						new Hitching(SalesBan.class),
						new DocNumberHitching(false),
				};
				return Arrays.asList(h);
			}
		}, UpdateDB.GEN_DATA_HITCHING);

		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override public Hitching create() { return new DocNumberHitching(true); }
		}, UpdateDB.EXPORT_DATA_HITCHING);

		DocHelper.makeDocNumberStrategy = new DocNumberStrategy();

		DocType.SumConverter = new TotalSumConvertor(){
			@Override
			public String toString(long sum) {
				String result = super.toString(sum);

				if(((CfgNplEx) ConfigManager.getConfig()).simpleMode)
					result = getString(R.string.sum_str, result);

				return result;
			}
		};


		ReqOrderHitching roh = new ReqOrderHitching();
		roh.setHandler(this);

		ReadService.requestObjects.add(new ServerInfoHitchingCl());
		ReadService.requestObjects.add(roh);
		WriteService.requestObjects.add(roh);

		Main.docMenuPrepared.add( new MenuPrepareHitching() {
			@Override
			public void menuPrepared(List<MenuHandler> menu, final Activity activity) {
				menu.add(new MenuHandler(getString(R.string.wsorder_title), new Runnable() {
					@Override public void run() { WSOrderList.open(activity); }
				}));
				menu.add(new MenuHandler("Приходы", new Runnable() {
					@Override public void run() { IncomeList.open(activity); }
				}));
			}
		});

		CostStrategy.defaultInstance = new CostStrategyEx();
	}

	@Override
	protected void initChildActivity() {
		super.initChildActivity();

		CreateSales.activity = CreateSalesEx.class;
		SalesDetail.activity = SalesDetailEx.class;
		VanRestReport.activity = VanRestReportEx.class;
		Warehouse.activity = WarehouseEx.class;
//		PriceCount.activity = PriceCountEx.class;
		Documents.activity = DocumentsEx.class;
		Setting.BehaviorSettingActivity = BehaviorSettingEx.class;
		UpdateDB.activity = UpdateDBEx.class;
		DocList.activity = DocListEx.class;
	}

	@Override
	protected void initChildFeature() {
		super.initChildFeature();

		Features.USE_PACK_QTY_IN_FORMS = true;
		Features.MULTI_WORD_SEARCH = true;
		Features.UPD = true;
		DocumentsEx.NOT_CHECK_ORG_BLOCKED = true;
		Features.FILE_LOG_DEBUG = true;
		Features.INPUT_QTY_IN_PACK = true;
		Features.SHOW_ORG_ADDRESS = true;
	}

	@Override
	protected void initChildDocTypes() {
		super.initChildDocTypes();

		DocType.addType(SalesDoc.instance());
		DocType.addType(ArchSalesDoc.instance());
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
	}

	protected Class<? extends OrderImplBase<? extends Order>> orderImplType() {
		return OrderImplEx.class;
	}

	@Override
	public void onNewOrders() {
//		Intent a = new Intent(this, DocList.activity);
//		a.putExtra(DocListEx.SHOW_NEW_ORDERS, true);
//        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, a, 0);
//
//        Notification noti = new NotificationCompat.Builder(this)
//         .setContentTitle(getString(R.string.message))
//         .setContentText("Вам пришли новые заказы")
//         .setSmallIcon(R.drawable.message)
//         .setAutoCancel(true)
//         .setContentIntent(contentIntent)
//         .build();
//
//        noti.defaults |= Notification.DEFAULT_SOUND;
//
//        NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
//		nm.notify(1, noti);
	}

	@Override
	protected Class<? extends ScriptImpl> scriptImplType() {
		return ScriptImplEx.class;
	}
}
