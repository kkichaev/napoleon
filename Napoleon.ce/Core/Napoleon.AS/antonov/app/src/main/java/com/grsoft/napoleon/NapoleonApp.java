/*
 * Copyright (C), 2011, Гильдия Разработчиков
 *
 * Демо программа (с базой данных)
 *
 * kki   16/05/2011   creating
 */

package com.grsoft.napoleon;

import android.content.Context;

import com.grsoft.database.Hitching;
import com.grsoft.database.HitchingCtor;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.Return;
import com.grsoft.dataobjects.ReturnCause;
import com.grsoft.dataobjects.ReturnItem;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.ReturnImpl;
import com.grsoft.dataobjects.impl.ReturnImplEx;
import com.grsoft.dataobjects.impl.ScriptImplEx;
import com.grsoft.napoleon.documents.DebtDocEx;
import com.grsoft.network.ServerCommand;
import com.grsoft.script.dataobjects.impl.ScriptImpl;
import com.grsoft.util.Consts;

public class NapoleonApp extends NapoleonAppBase {
    class OrderEditor implements OrderImpl.PropertiesEditor {
        @Override
        public void edit(Context ctx, OrderImpl order, boolean isOldOrder) {
            CreateOrder.open(ctx, order, isOldOrder);
        }
    }


    @Override
    protected Class<? extends OrderImplBase<? extends Order>> orderImplType() {
        return OrderImplEx.class;
    }

    @Override
    protected Class<? extends ReturnImpl> returnsImplType() {
        return ReturnImplEx.class;
    }

    @Override
    protected Class<? extends ScriptImpl> scriptImplType() {
        return ScriptImplEx.class;
    }

    @Override
    public void onCreate() {
        DebtDocEx.initialize();
        super.onCreate();

        OrderImpl.OrderEditor = new OrderEditor();
        setProgrammVersion();

        UpdateDB.addHitchingCtor(new HitchingCtor() {
            @Override
            public Hitching create() {
                return new RcvNewHitching(ReturnCause.class);
            }
        }, UpdateDB.GEN_DATA_HITCHING);
    }

    private void setProgrammVersion() {
        try{
            ServerCommand.ProgramVersion = getResources().getString(R.string.version);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void initChildFeature() {
        super.initChildFeature();
        Features.SCRIPT_DOC = true;
        Features.CANT_SEND_SCRIPT_PART = true;
        Features.SCRIPT_OFF_IN_DOC_LIST = true;
        Features.DEL_VISIT_WITHOUT_PHOTO = true;
        Features.QUESTION = true;
        Features.WEIGHT_SCALE = Consts.WEIGHT_SCALE;
        Features.LOAD_FULL_PRICE = true;
        Features.SHOW_ORG_ADDRESS = true;
        Features.HAVE_RETURN_DOC = true;
    }

    @Override
    protected void initChildActivity() {
        super.initChildActivity();

        Warehouse.activity = WarehouseEx.class;
        Presentation.activity = PresentationFolder.class;
        PricePresentation.activity = PricePresentationFolder.class;
        PriceCount.activity = PriceCountEx.class;
        UpdateDB.activity = UpdateDBEx.class;
    }

    @Override
    protected void initChildDocTypes() {
        super.initChildDocTypes();
        DbObject.regNewDataType(Price.class, PriceEx.class);
        DbObject.regNewDataType(Order.class, OrderEx.class);
        DataObjectInfo.getInstance().replaceListType(Return.class, "items", ReturnItem.class);
    }
}
