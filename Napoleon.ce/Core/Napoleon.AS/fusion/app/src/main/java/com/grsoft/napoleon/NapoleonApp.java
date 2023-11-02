/*
 * Copyright (C), 2011, Гильдия Разработчиков
 *
 * Демо программа (с базой данных)
 *
 * kki   16/05/2011   creating
 */

package com.grsoft.napoleon;

import android.content.Context;

import com.grsoft.dataobjects.Firm;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Sales;
import com.grsoft.dataobjects.SalesEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.SalesImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.SalesDoc;
import com.grsoft.napoleon.modules.print.NPrinter;
import com.grsoft.napoleon.modules.print.Print;
import com.grsoft.napoleon.modules.print.util.DocHelper;
import com.grsoft.napoleon.modules.print.util.SalesDocNumberStrategy;
import com.grsoft.napoleon.printsources.SalesPrintEx;
import com.grsoft.network.ServerCommand;

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
        Print.init();
        super.defineNewType();

        DbObject.regNewDataType(Org.class, OrgEx.class);
        DbObject.regNewDataType(Firm.class, Firm.class);
        DbObject.regNewDataType(Sales.class, SalesEx.class);

        SalesDetail.SalesPrintType = SalesPrintEx.class;
        NPrinter.forms.put("Накладная", "nakl");

        DocHelper.makeDocNumberStrategy = new SalesDocNumberStrategy();
    }

    class SalesEditor extends SalesPropertiesEditor {
        @Override
        public void edit(Context ctx, SalesImpl doc, boolean isOldOrder) {
            SalesProperties.open(ctx, doc.getRowid(), isOldOrder);
        }
    }

    @Override
    protected void initChildFeature() {
        super.initChildFeature();
        Features.CAN_CHANGE_COST = true;
        Features.UPD = true;
    }

    @Override
    protected void initChildActivity() {
        super.initChildActivity();
        SalesDetail.activity = SalesDetailEx.class;
    }

    @Override
    public void setDefDocType() {
        DocType.setCurDoc(SalesDoc.instance());
    }

    @Override
    public void onCreate() {
        super.onCreate();

        OrderImpl.OrderEditor = new OrderEditor();
        SalesImpl.Editor = new SalesEditor();

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
