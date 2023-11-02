/*
 * Copyright (C), 2011, Гильдия Разработчиков
 *
 * Демо программа (с базой данных)
 *
 * kki   16/05/2011   creating
 */

package com.grsoft.napoleon;

import android.content.Context;

import com.grsoft.database.GetReportsHitching;
import com.grsoft.database.Hitching;
import com.grsoft.database.HitchingCtor;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.Concurent;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.dataobjects.Incass;
import com.grsoft.dataobjects.IncassEx;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Payment;
import com.grsoft.dataobjects.PaymentEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.Remnants;
import com.grsoft.dataobjects.RemnantsEx;
import com.grsoft.dataobjects.Return;
import com.grsoft.dataobjects.ReturnEx;
import com.grsoft.dataobjects.Sklad;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.RemnantsImpl;
import com.grsoft.dataobjects.impl.RemnantsImplEx;
import com.grsoft.dataobjects.impl.ReturnImpl;
import com.grsoft.dataobjects.impl.ReturnImplEx;
import com.grsoft.napoleon.modules.CostManagerImpl;
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
    }

    private void setProgrammVersion() {
        try {
            ServerCommand.ProgramVersion = getResources().getString(R.string.version);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initChildDocTypes() {
        super.initChildDocTypes();

        DbObject.regNewDataType(Org.class, OrgEx.class);
        DbObject.regNewDataType(Order.class, OrderEx.class);
        DbObject.regNewDataType(Price.class, PriceEx.class);
        DbObject.regNewDataType(Incass.class, IncassEx.class);
        DbObject.regNewDataType(Return.class, ReturnEx.class);
        DbObject.regNewDataType(Remnants.class, RemnantsEx.class);
        DbObject.regNewDataType(Delivery.class, DeliveryEx.class);
        DbObject.regNewDataType(Payment.class, PaymentEx.class);

        DataObjectInfo.getInstance().replaceListType(OrderEx.class, "items", OrderItemEx.class);
        DataObjectInfo.getInstance().replaceListType(ReturnEx.class, "items", OrderItemEx.class);

        CostStrategy.register(OrderImpl.class, new CostStrategyOrder());

        UpdateDB.addHitchingCtor(new HitchingCtor() {
            @Override
            public Hitching create() {
                return new GetReportsHitching();
            }
        }, UpdateDB.GEN_DATA_HITCHING);

        UpdateDB.addHitchingCtor(new HitchingCtor() {
            @Override
            public Hitching create() {
                return new RcvNewHitching(Concurent.class);
            }
        }, UpdateDB.GEN_DATA_HITCHING);

        UpdateDB.addHitchingCtor(new HitchingCtor() {
            @Override
            public Hitching create() {
                return new RcvNewHitching(Sklad.class);
            }
        }, UpdateDB.GEN_DATA_HITCHING);
    }

    @Override
    protected Class<? extends RemnantsImpl> remnantsImplType() {
        return RemnantsImplEx.class;
    }

    @Override
    protected Class<? extends ReturnImpl> returnsImplType() {
        return ReturnImplEx.class;
    }

    @Override
    protected void initChildFeature() {
        super.initChildFeature();

        Features.USE_COST_IN_RETURNS = true;
        Features.COST_MANAGER = new CostManagerImpl();
        Features.BLOCK_IN_STOP_LIST = true;
        Features.INCASS_DEBET_DISTRIB = false;
    }

    @Override
    protected void initChildActivity() {
        super.initChildActivity();

        PriceCount.activity = PriceCountEx.class;
        UpdateDB.activity = UpdateDBEx.class;
        Warehouse.activity = WarehouseEx.class;
        IncassEdit.activity = IncassEditEx.class;
        CreateReturn.activity = CreateReturnEx.class;
        RemnantsDetail.activity = RemnantsDetailEx.class;
        Documents.activity = DocumentsEx.class;
        ReportList.activity = ReportListEx.class;
    }
}
