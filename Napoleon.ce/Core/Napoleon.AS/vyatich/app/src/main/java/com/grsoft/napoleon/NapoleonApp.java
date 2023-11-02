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
import com.grsoft.database.OrgNotesHitching;
import com.grsoft.database.PlanogramHitching;
import com.grsoft.database.PriceMovieHitching;
import com.grsoft.database.RestoreDocProceeded;
import com.grsoft.database.RestoreDocProceededEx;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.DocDataObject;
import com.grsoft.dataobjects.Fridge;
import com.grsoft.dataobjects.Message;
import com.grsoft.dataobjects.MessageEx;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderProceeded;
import com.grsoft.dataobjects.OrderProceededEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgNotes;
import com.grsoft.dataobjects.OrgNotesEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.Return;
import com.grsoft.dataobjects.ReturnEx;
import com.grsoft.dataobjects.ReturnItem;
import com.grsoft.dataobjects.Visit;
import com.grsoft.dataobjects.VisitEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.ReturnImplEx;
import com.grsoft.dataobjects.impl.VisitImplEx;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.InvFrgDoc;
import com.grsoft.napoleon.documents.OrderDocEx;
import com.grsoft.napoleon.documents.PlanogramDoc;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.napoleon.modules.CostManagerImpl;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.CfgNplEx;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.NetworkAsyncTask;
import com.grsoft.network.ServerCommand;
import com.grsoft.util.Consts;
import com.grsoft.util.gps.GPSUtilNew;

import java.util.Arrays;
import java.util.List;

public class NapoleonApp extends NapoleonAppBase {

    class OrderEditor implements OrderImpl.PropertiesEditor {
        @Override
        public void edit(Context ctx, OrderImpl order, boolean isOldOrder) {
            CreateOrder.open(ctx, order, isOldOrder);
        }
    }

    @Override
    public void onCreate() {
        CfgNplEx cfg = new CfgNplEx();
        ConfigManager.initConfig(cfg);

        String prevPref = ConfigManager.CFG_SHARED_PREFERENCE;

        ConfigManager.CFG_SHARED_PREFERENCE = "main_config_upd1";
        if(!ConfigManager.isInited(this)) {
            ConfigManager.CFG_SHARED_PREFERENCE = prevPref;
            ConfigManager.load(this);
            cfg.setDetaults();

            ConfigManager.CFG_SHARED_PREFERENCE = "main_config_upd1";
            try {
                ConfigManager.save(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            ConfigManager.load(this);
        }

        NetworkAsyncTask.rcvMsgDlg = new VyatichRecievedMessageDlg();

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
    protected void defineNewType() {
        DbObject.regNewDataType(Org.class, OrgEx.class);
        DbObject.regNewDataType(Price.class, PriceEx.class);
        DbObject.regNewDataType(OrderProceeded.class, OrderProceededEx.class);
        DbObject.regNewDataType(Order.class, OrderEx.class);
        DbObject.regNewDataType(Visit.class, VisitEx.class);
        DbObject.regNewDataType(Return.class, ReturnEx.class);
        DbObject.regNewDataType(Message.class, MessageEx.class);
        DbObject.regNewDataType(OrgNotes.class, OrgNotesEx.class);

        DataObjectInfo.getInstance().replaceListType(ReturnEx.class, "items", ReturnItem.class);

        DocType.addType(OrderDocEx.instance(OrderImplEx.class));
        DocType.addType(DebtDoc.instance());
        DocType.addType(VisitDoc.instance(VisitImplEx.class));
        DocType.addType(RemnantsDoc.instance());

        CostStrategy.defaultInstance = new CostStrategyEx();

        GPSUtilNew.TIME_FOR_GPS_VALID = Consts.ONE_SECOND * Consts.SEC_PER_MIN * 15; //15 мин

        RWServiceFactoryNapoleon.instance = new RWServiceFactoryNapoleonEx();

        UpdateDB.addHitchingCtor(new HitchingCtor(){
            @Override
            public List<Hitching> createList() {
                Hitching h[] = new Hitching[] {
                        new PriceMovieHitching(),
                        new RestoreDocProceededEx(1),
                        new Hitching(Fridge.class),
                        new PlanogramHitching(getApplicationContext()),
                };
                return Arrays.asList(h);
            }
        }, UpdateDB.GEN_DATA_HITCHING);

        UpdateDB.addHitchingCtor(new HitchingCtor(){
            @Override
            public Hitching create() {
                return new OrgNotesHitching();
            }
        }, UpdateDB.EXPORT_DATA_HITCHING);
    }

    @Override
    protected void initChildDocTypes() {
        super.initChildDocTypes();

        DocType.addType(ReturnDoc.instance(ReturnImplEx.class));
        DocType.addType(InvFrgDoc.instance());
        DocType.addType(PlanogramDoc.instance());
    }

    @Override
    protected void initChildActivity() {
        super.initChildActivity();
        Warehouse.activity = WarehouseEx.class;
        Presentation.activity = PresentationFolder.class;
        PricePresentationFolder.activity = PricePresentationFolderEx.class;
        Documents.activity = DocumentsEx.class;
        UpdateDB.activity = UpdateDBEx.class;
        DocList.activity = DocListEx.class;
        PriceCount.activity = PriceCountEx.class;
        OrderDetail.activity = OrderDetailEx.class;
        VisitEdit.activity = VisitEditEx.class;

        ReturnDetail.activity = ReturnDetailEx.class;
    }

    @Override
    protected void initChildFeature() {
        super.initChildFeature();

        Features.COST_MANAGER = new CostManagerImpl();
        Features.INPUT_QTY_IN_PACK = true;
        Features.UNLIMIT_VISIT_ITEMS = true;
        Features.COST_FILTER_IN_PRICE = true;
    }

}
