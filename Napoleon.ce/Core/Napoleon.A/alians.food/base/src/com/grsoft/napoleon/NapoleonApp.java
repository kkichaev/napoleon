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
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.dataobjects.Firm;
import com.grsoft.dataobjects.FirmEx;
import com.grsoft.dataobjects.Incass;
import com.grsoft.dataobjects.IncassEx;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Payment;
import com.grsoft.dataobjects.PaymentEx;
import com.grsoft.dataobjects.Return;
import com.grsoft.dataobjects.ReturnEx;
import com.grsoft.dataobjects.Sales;
import com.grsoft.dataobjects.SalesBan;
import com.grsoft.dataobjects.SalesEx;
import com.grsoft.dataobjects.SalesItemEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.IncassImplEx;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.OrgTaskExecImpl;
import com.grsoft.dataobjects.impl.ReturnImplEx;
import com.grsoft.dataobjects.impl.SalesImplEx;
import com.grsoft.napoleon.documents.ArchIncassDoc;
import com.grsoft.napoleon.documents.ArchReturnDoc;
import com.grsoft.napoleon.documents.ArchSalesDoc;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DebtDocEx;
import com.grsoft.napoleon.documents.DivisionHitching;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.IncassDoc;
import com.grsoft.napoleon.documents.IncassDocEx;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.napoleon.documents.SalesDoc;
import com.grsoft.napoleon.documents.TaskDoneDoc;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.napoleon.documents.WSOrderDoc;
import com.grsoft.napoleon.modules.print.NPrinter;
import com.grsoft.napoleon.modules.print.Print;
import com.grsoft.napoleon.modules.print.util.DocHelper;
import com.grsoft.napoleon.printsources.SalesPrintEx;
import com.grsoft.napoleon.util.MakeDocNumber;
import com.grsoft.network.ServerCommand;
import android.content.Context;

public class NapoleonApp extends NapoleonAppBase{
	@SuppressWarnings("unused")
	private static final String TAG = "NapoleonApp";
	
	class OrderEditor implements OrderImpl.PropertiesEditor {
		public void edit(Context ctx, OrderImpl order, boolean isOldOrder) {
			CreateOrder.open(ctx, order, isOldOrder);
		}
	}
	
	protected void initDocTypes() {
		com.grsoft.napoleon.modules.print.DebtDoc.DebtDocType = DebtDocEx.class;		
		DocHelper.makeDocNumberStrategy = new MakeDocNumber(this);
		DocType salesDoc = SalesDoc.instance(SalesImplEx.class);
		Print.init();
		IncassDocEx.init();
		
		DbObject.regNewDataType(Firm.class, FirmEx.class);
		DbObject.regNewDataType(Sales.class, SalesEx.class);
		DbObject.regNewDataType(Delivery.class, DeliveryEx.class);
		DbObject.regNewDataType(Payment.class, PaymentEx.class);
		DbObject.regNewDataType(Org.class, OrgEx.class);
		DbObject.regNewDataType(Incass.class, IncassEx.class);
		DbObject.regNewDataType(Order.class, OrderEx.class);
		DbObject.regNewDataType(Return.class, ReturnEx.class);
		
		DataObjectInfo.getInstance().replacePrimaryKey(PaymentEx.class, "ido,number");
		DataObjectInfo.getInstance().replacePrimaryKey(DeliveryEx.class, "ido,number");
		DataObjectInfo.getInstance().replaceListType(SalesEx.class, "items", SalesItemEx.class);
		DataObjectInfo.getInstance().replaceListType(OrderEx.class, "items", OrderItemEx.class);
		DataObjectInfo.getInstance().replaceListType(ReturnEx.class, "items", OrderItemEx.class);
		
		DocType.addType(OrderDoc.instance(OrderImplEx.class));
		DocType.addType(DebtDoc.instance());
		DocType.addType(VisitDoc.instance());
		DocType.addType(RemnantsDoc.instance());
		DocType.addType(salesDoc);
		DocType.addType(IncassDoc.instance(IncassImplEx.class));
		DocType.addType(ReturnDoc.instance(ReturnImplEx.class));
		DocType.addType(ArchIncassDoc.instance());
		DocType.addType(WSOrderDoc.instance());
		DocType.addType(ArchReturnDoc.instance());
		DocType.addType(ArchSalesDoc.instance());
		DocType.addType(TaskDoneDoc.instance(OrgTaskExecImpl.class));

		DocType.setCurDoc(SalesDoc.instance());
		
		Warehouse.activity = WarehouseEx.class;
		UpdateDB.activity = UpdateDBEx.class;
		Documents.activity = DocumentsEx.class;
		CreateSales.activity = CreateSalesEx.class;
		IncassEdit.activity = IncassEditEx.class;
		CreateReturn.activity = CreateReturnEx.class;
		DocList.activity = DocListEx.class;
		SalesDetail.activity = SalesDetailEx.class;
		PriceCount.activity = PriceCountEx.class;
		
		SalesDetail.SalesPrintType = SalesPrintEx.class;
		
		Features.ORG_STOP_TABLE = true;
		Features.CAN_CHANGE_COST_IN_SALES = true;
		Features.POTENZIAL_ORG = false;
		Features.USE_COST_IN_RETURNS = true;
		Features.OK_BTN_INCASS = true;
		Features.RECIEVE_REMNANTS_IN_MAIN_MENU = false;
		Features.SCRIPT_DOC = true;
		
		NPrinter.forms.put("Накладная", "nakl");
		
		UpdateDB.addHitchingCtor(new HitchingCtor(){
			@Override
			public List<Hitching> createList() {
				Hitching h[] = new Hitching[] {
						new DocumentRestore(WSOrderDoc.instance()),
				};
				return Arrays.asList(h);
			}
		}, UpdateDB.RESTORE_DATA_HITCHING);
		
		
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
}
