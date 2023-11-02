/*
 * Copyright (C), 2011, ??????? ?????????????
 *
 * ???? ????????? (? ????? ??????)
 *
 * kki   16/05/2011   creating
 */

package com.grsoft.napoleon;

import android.content.Context;

import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.PkoDoc;
import com.grsoft.napoleon.documents.SalesDoc;
import com.grsoft.napoleon.modules.print.NPrinter;
import com.grsoft.napoleon.modules.print.Print;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.ServerCommand;

public class NapoleonApp extends NapoleonAppBase {

    class OrderEditor implements OrderImpl.PropertiesEditor {
        @Override
        public void edit(Context ctx, OrderImpl order, boolean isOldOrder) {
            CreateOrder.open(ctx, order, isOldOrder);
        }
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
        try {
            ServerCommand.ProgramVersion = getResources().getString(R.string.version);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initChildActivity() {
        super.initChildActivity();

        SalesDetail.activity = SalesDetailEx.class;

        Print.init();
        NPrinter.forms.put("Заявка", "nakl");
    }

    @Override
    protected void initChildFeature() {
        super.initChildFeature();
        Features.CAN_CHANGE_COST_IN_SALES = true;
    }

    @Override
    protected void initChildDocTypes() {
        super.initChildDocTypes();
        DocType.addType(SalesDoc.instance());
        DocType.addType(PkoDoc.instance());
    }

    @Override
    public void setDefDocType() {
        DocType.setCurDoc(SalesDoc.instance());
    }
}
