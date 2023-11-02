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

import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.OrgTaskExecImpl;
import com.grsoft.napoleon.chart.ChartActivity;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.QuestionDoc;
import com.grsoft.napoleon.documents.QuestionDocEx;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.napoleon.documents.TaskDoneDoc;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.napoleon.documents.VisitDocEx;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.network.ServerCommand;
import com.grsoft.script.documents.ScriptDoc;
import com.grsoft.util.ViewInitializer;

public class NapoleonApp extends NapoleonAppBase {

    class OrderEditor implements OrderImpl.PropertiesEditor {
        @Override
        public void edit(Context ctx, OrderImpl order, boolean isOldOrder) {
            CreateOrder.open(ctx, order, isOldOrder);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        OrderImpl.OrderEditor = new OrderEditor();
        setProgrammVersion();

        //NapoleonChat.init(this);
    }

    private void setProgrammVersion() {
        try {
            ServerCommand.ProgramVersion = getResources().getString(R.string.version);
            ServerCommand.Category = "btl";
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initDocTypes() {
        if (inited)
            throw new RuntimeException("Already inited");

        inited = true;
        QuestionDocEx.initialize();
        VisitDocEx.initialize();

        // сложная инкассация включается фичей, фичи надо инициализировать перед документами
        initFeatures();

        DbObject.regNewDataType(Price.class, PriceEx.class);
        CostStrategy.defaultInstance = new CostStrategyEx();

        DocType.addType(OrderDoc.instance(OrderImplEx.class));
        DocType.addType(VisitDoc.instance());
        DocType.addType(RemnantsDoc.instance(remnantsImplType()));
        DocType.addType(QuestionDoc.instance());
        DocType.addType(ScriptDoc.instance(scriptImplType()));
        DocType.addType(TaskDoneDoc.instance(OrgTaskExecImpl.class));

        initChildDocTypes();

        setDefDocType();

        initActivity();
    }

    public void setDefDocType() {
        DocType.setCurDoc(VisitDoc.instance());
    }

    @Override
    protected void initChildActivity() {
        super.initChildActivity();

        ChartActivity.activity = ChartActivityEx.class;
        Documents.activity = DocumentsEx.class;

        UpdateDB.initUI = new ViewInitializer() {
            @Override
            public void init(Activity activity) {
                super.init(activity);
                activity.findViewById(R.id.cbDebt).setVisibility(View.GONE);
                activity.findViewById(R.id.cbRemains).setVisibility(View.GONE);

            }
        };

    }

    @Override
    protected void initChildFeature() {
        super.initChildFeature();

        Features.MAX_FOTO_HEIGHT = 5000;
        Features.MAX_FOTO_WIDTH = 5000;
    }

    @Override
    protected CfgNpl createConfig() {
        return new CfgNpl() {
            @Override
            public void resetToDefault() {
                super.resetToDefault();
                gpsFrequience = 20 * 60;
                dataSendInBackground = true;
            }
        };
    }
}
