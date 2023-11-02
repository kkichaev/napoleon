/*
 * Copyright (C), 2011, Гильдия Разработчиков
 *
 * Демо программа (с базой данных)
 *
 * kki   16/05/2011   creating
 */

package com.grsoft.napoleon;

import android.content.Context;

import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.Sales;
import com.grsoft.dataobjects.SalesEx;
import com.grsoft.dataobjects.SalesItemEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.SalesImplEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.PkoDoc;
import com.grsoft.napoleon.documents.SalesDoc;
import com.grsoft.napoleon.documents.SalesDocEx;
import com.grsoft.napoleon.modules.print.NPrinter;
import com.grsoft.napoleon.modules.print.Print;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.CfgNplEx;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.ServerCommand;
import com.grsoft.types.Feature;

public class NapoleonApp extends NapoleonAppBase {

    class OrderEditor implements OrderImpl.PropertiesEditor {
        @Override
        public void edit(Context ctx, OrderImpl order, boolean isOldOrder) {
            CreateOrder.open(ctx, order, isOldOrder);
        }
    }

    @Override
    protected void defineNewType() {
        SalesDocEx.init();
        Print.init();
        Setting.addTabs.add(ScannerSettings.class);

		DbObject.regNewDataType(Sales.class, SalesEx.class);
		DbObject.regNewDataType(Price.class, PriceEx.class);

		DataObjectInfo.getInstance().replaceListType(SalesEx.class, "items", SalesItemEx.class);

        super.defineNewType();

		NPrinter.forms.put("nakl", "nakl");
    }

    @Override
    protected void initChildFeature() {
        super.initChildFeature();

        Features.DISABLE_EDIT_AFTER_PRINT = true;
        Features.CANT_DEL_PRINTED_DOCS = true;
        Features.ALLOW_MULTY_PKO_ON_SALES = true;
        Features.UPD = true;
        Features.HAVE_PRICE_MOVER = false;
    }

    @Override
    protected void initChildActivity() {
        super.initChildActivity();

        SalesDetail.activity = SalesDetailEx.class;
        Documents.activity = DocumentsEx.class;
        CreateSales.activity = CreateSalesEx.class;
        Warehouse.activity = WarehouseEx.class;
    }

    @Override
    protected void initChildDocTypes() {
        DocType.addType(SalesDoc.instance(SalesImplEx.class));
        DocType.addType(PkoDoc.instance());
    }

    @Override
    public void setDefDocType() {
        DocType.setCurDoc(SalesDoc.instance());
    }

    @Override
    public void onCreate() {
        ConfigManager.initConfig(new CfgNplEx());
        super.onCreate();

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
