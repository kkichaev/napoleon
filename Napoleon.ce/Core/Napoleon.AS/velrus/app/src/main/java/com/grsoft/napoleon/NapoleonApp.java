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
import android.view.View;

import com.grsoft.database.DbWriter;
import com.grsoft.database.PODHitching;
import com.grsoft.database.PODHitchingEx;
import com.grsoft.database.SalesResultHitching;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Folder;
import com.grsoft.dataobjects.FolderEx;
import com.grsoft.dataobjects.Incass;
import com.grsoft.dataobjects.IncassEx;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.OrderProceeded;
import com.grsoft.dataobjects.OrderProceededEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.Sales;
import com.grsoft.dataobjects.SalesBonus;
import com.grsoft.dataobjects.SalesEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.SalesImplEx;
import com.grsoft.napoleon.documents.DebtDocEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.OrderDocEx;
import com.grsoft.napoleon.documents.PkoDoc;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.napoleon.documents.SalesBonusDoc;
import com.grsoft.napoleon.documents.SalesDoc;
import com.grsoft.napoleon.modules.CostManagerImpl;
import com.grsoft.napoleon.modules.MonitoringInit;
import com.grsoft.napoleon.modules.print.NPrinter;
import com.grsoft.napoleon.modules.print.Print;
import com.grsoft.napoleon.printsources.SalesPrintEx;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.ServerCommand;
import com.grsoft.network.WriteService;
import com.grsoft.util.ViewInitializer;

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
		OrderDocEx.initialize();
		MonitoringInit.init();
        Print.init();

        SalesDetail.SalesPrintType = SalesPrintEx.class;

		DbObject.regNewDataType(Incass.class, IncassEx.class);
		DbObject.regNewDataType(Org.class, OrgEx.class);
        DbObject.regNewDataType(Sales.class, SalesEx.class);
		DbObject.regNewDataType(Order.class, OrderEx.class);
		DbObject.regNewDataType(Price.class, PriceEx.class);
		DbObject.regNewDataType(Folder.class, FolderEx.class);
        DbObject.regNewDataType(Price.class, PriceEx.class);
        DbObject.regNewDataType(OrderProceeded.class, OrderProceededEx.class);

        DbWriter.checkDBTable(SalesBonus.class);

		DataObjectInfo.getInstance().replaceListType(Order.class, "items", OrderItemEx.class);

		super.defineNewType();

        UpdateDB.initUI = new ViewInitializer() {
            @Override public void init(Activity activity) { activity.findViewById(R.id.cbRemains).setVisibility(View.GONE); }
        };

        NPrinter.BARCODE_HEIGHT = 25;
        NPrinter.forms.put("Удостоверение качества", "otk");
        NPrinter.forms.put("Накладная", "nakl");

        PODHitching.type = PODHitchingEx.class;
//        ReadService.requestObjects.add(new PODHitchingEx());
//        WriteService.requestObjects.addAll(ReadService.requestObjects);
        WriteService.recievers.add(new SalesResultHitching());
    }

    @Override
    protected void initChildFeature() {
        super.initChildFeature();

        Features.DISABLE_EDIT_AFTER_PRINT = true;
        Features.CANT_DEL_PRINTED_DOCS = true;
        Features.ALLOW_MULTY_PKO_ON_SALES = true;
        Features.UPD = true;
        Features.HAVE_PRICE_MOVER = false;

		Features.DOC_SUM_BY_PERIOD = true;
		Features.COST_MANAGER = new CostManagerImpl();
		Features.FOCUSED_ITEMS = true;
		Features.LOAD_FULL_PRICE = true;

	}

    @Override
    public void onCreate() {
        ConfigManager.initConfig(new CfgNpl());
        super.onCreate();

        OrderImpl.OrderEditor = new OrderEditor();
        setProgrammVersion();
    }

    @Override
    protected void initChildActivity() {
        super.initChildActivity();

        Documents.activity = DocumentsEx.class;
        Warehouse.activity = WarehouseEx.class;
		IncassEdit.activity = IncassEditEx.class;
		Documents.activity = DocumentsEx.class;
		PriceCount.activity = PriceCountEx.class;
		DocList.activity = DocListEx.class;
		OrderDetail.activity = OrderDetailEx.class;
        SalesDetail.activity = SalesDetailEx.class;
        CreateSales.activity = CreateSalesEx.class;
	}

    @Override
    protected void initChildDocTypes() {
        DocType.addType(SalesDoc.instance(SalesImplEx.class));
        DocType.addType(PkoDoc.instance());
        DocType.addType(SalesBonusDoc.instance());
        DocType.removeType(ReturnDoc.instance());
    }

    @Override
    public void setDefDocType() {
        DocType.setCurDoc(SalesDoc.instance());
    }

    private void setProgrammVersion() {
        try {
            ServerCommand.ProgramVersion = getResources().getString(R.string.version);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
