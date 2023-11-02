/*
 * Copyright (C), 2011, Гильдия Разработчиков
 * 
 * Демо программа (с базой данных)
 *
 * kki   16/05/2011   creating
 */

package com.grsoft.napoleon;

import java.util.List;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import com.grsoft.database.Hitching;
import com.grsoft.database.HitchingCtor;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.AgentPrefix;
import com.grsoft.dataobjects.AgentPrefixEx;
import com.grsoft.dataobjects.Agreements;
import com.grsoft.dataobjects.Carrier;
import com.grsoft.dataobjects.Cash;
import com.grsoft.dataobjects.Firm;
import com.grsoft.dataobjects.FirmEx;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.PayTime;
import com.grsoft.dataobjects.Pko;
import com.grsoft.dataobjects.PkoEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.Report;
import com.grsoft.dataobjects.Return;
import com.grsoft.dataobjects.ReturnEx;
import com.grsoft.dataobjects.Sales;
import com.grsoft.dataobjects.SalesEx;
import com.grsoft.dataobjects.Zone;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.ReturnImplEx;
import com.grsoft.dataobjects.impl.SalesImpl;
import com.grsoft.dataobjects.impl.SalesImplEx;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.MovementDoc;
import com.grsoft.napoleon.documents.OfferDoc;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.PkoDoc;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.napoleon.documents.RkoDoc;
import com.grsoft.napoleon.documents.SalesDoc;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.napoleon.modules.print.NPrinter;
import com.grsoft.napoleon.modules.print.Print;
import com.grsoft.napoleon.modules.print.util.BaseDocNumberStrategy;
import com.grsoft.napoleon.modules.print.util.DocHelper;
import com.grsoft.napoleon.modules.print.util.DocNumberStrategy;
import com.grsoft.network.ServerCommand;
import com.grsoft.util.DocFilterOnClickListener;
import com.grsoft.util.FirstRunInit;
import com.grsoft.util.MenuHandler;
import com.grsoft.util.MenuPrepareHitching;

public class NapoleonApp extends Application {
	@SuppressWarnings("unused")
	private static final String TAG = "NapoleonApp";
	
	class OrderEditor implements OrderImpl.PropertiesEditor {
		@Override
		public void edit(Context ctx, OrderImpl order, boolean isOldOrder) {
			CreateOrder.open(ctx, order, isOldOrder);
		}
	}
	
	private void initDocTypes() {
		DocType.addType(SalesDoc.instance(SalesImplEx.class));
		
		Print.init();
		Napoleon.docMenuPrepared.clear(); // Удалим остатки на борту
		
		DocType.addType(OrderDoc.instance(OrderImplEx.class));
		DocType.addType(DebtDoc.instance());
		DocType.addType(VisitDoc.instance());
		DocType.addType(ReturnDoc.instance(ReturnImplEx.class));
		DocType.addType(RemnantsDoc.instance());
		DocType.addType(PkoDoc.instance());
		DocType.addType(OfferDoc.instance());
		DocType.addType(MovementDoc.instance());
		DocType.addType(RkoDoc.instance());
		
		DocType.setCurDoc(OrderDoc.instance());
		
		DocFilterOnClickListener.HiddenTypes.add(RkoDoc.instance());
		DocFilterOnClickListener.HiddenTypes.add(MovementDoc.instance());

		DbObject.regNewDataType(Org.class, OrgEx.class);
		DbObject.regNewDataType(Order.class, OrderEx.class);
		DbObject.regNewDataType(Firm.class, FirmEx.class);
		DbObject.regNewDataType(AgentPrefix.class, AgentPrefixEx.class);
		DbObject.regNewDataType(Return.class, ReturnEx.class);
		DbObject.regNewDataType(Price.class, PriceEx.class);
		DbObject.regNewDataType(Sales.class, SalesEx.class);
		DbObject.regNewDataType(Pko.class, PkoEx.class);
		
		Warehouse.activity = WarehouseEx.class;
		Presentation.activity = PresentationFolder.class;
		PricePresentation.activity = PricePresentationFolder.class;
		Documents.activity = DocumentsEx.class;
		CreateReturn.activity = CreateReturnEx.class;
		PkoInfo.activity = PkoInfoEx.class;
		
		BaseDocNumberStrategy.FormatDocStr = "%s%06d";
		
		NPrinter.forms.put("eurostar_bill", "eurostar_bill");
		NPrinter.forms.put("shelepova_bill", "shelepova_bill");
		NPrinter.forms.put("offer", "offer");
		
		Features.POTENZIAL_ORG = false;
		
		Napoleon.docMenuPrepared.add(new MenuPrepareHitching() {
			
			@Override
			public void menuPrepared(List<MenuHandler> menu, final Activity activity) {
				menu.add(new MenuHandler(getString(R.string.cash_report), new Runnable() {
					@Override public void run() {BalanceReport.open(activity);}
				}));
			}
		});
		
		UpdateDB.addHitchingCtor(new HitchingCtor() { @Override public Hitching create() {return new RcvNewHitching(Carrier.class, "Carrier");}}, UpdateDB.GEN_DATA_HITCHING);
		UpdateDB.addHitchingCtor(new HitchingCtor() { @Override public Hitching create() {return new RcvNewHitching(Cash.class, "Cash");}}, UpdateDB.GEN_DATA_HITCHING);
		UpdateDB.addHitchingCtor(new HitchingCtor() { @Override public Hitching create() {return new RcvNewHitching(Zone.class, "Zone");}}, UpdateDB.GEN_DATA_HITCHING);
		UpdateDB.addHitchingCtor(new HitchingCtor() { @Override public Hitching create() {return new RcvNewHitching(Report.class, "Report");}}, UpdateDB.GEN_DATA_HITCHING);
		UpdateDB.addHitchingCtor(new HitchingCtor() { @Override public Hitching create() {return new RcvNewHitching(PayTime.class, "PayTime");}}, UpdateDB.GEN_DATA_HITCHING);
		UpdateDB.addHitchingCtor(new HitchingCtor() { @Override public Hitching create() {return new RcvNewHitching(Agreements.class, "Agreement");}}, UpdateDB.GEN_DATA_HITCHING);
		
		DocHelper.makeDocNumberStrategy = new DocNumberStrategy();
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		FirstRunInit.init(this);

		initDocTypes();
		OrderImpl.OrderEditor = new OrderEditor();
		SalesImpl.Editor = new SalesPropertiesEditor(){
			@Override
			public void edit(Context ctx, SalesImpl doc, boolean isOldOrder) {
				CreateOrder.open(ctx, doc, isOldOrder);
			}
		};
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