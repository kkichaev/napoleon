/*
 * Copyright (C), 2011, Гильдия Разработчиков
 *
 * Демо программа (с базой данных)
 *
 * kki   16/05/2011   creating
 */

package com.grsoft.napoleon;

import android.content.Context;

import com.grsoft.database.DispatchReturnsHitching;
import com.grsoft.database.DocumentRestore;
import com.grsoft.database.Hitching;
import com.grsoft.database.HitchingCtor;
import com.grsoft.database.LoadOrdersHitching;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.dataobjects.MemoStatus;
import com.grsoft.dataobjects.MemoType;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderDriverRouteInfo;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.OrderProceeded;
import com.grsoft.dataobjects.OrderProceededEx;
import com.grsoft.dataobjects.OrderRouteInfo;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgBalance;
import com.grsoft.dataobjects.OrgBalanceData;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Return;
import com.grsoft.dataobjects.ReturnItemEx;
import com.grsoft.dataobjects.RoutePhotos;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.napoleon.documents.AgentMemoDoc;
import com.grsoft.napoleon.documents.DeliveryDoc;
import com.grsoft.napoleon.documents.DispatchReturnsDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.napoleon.documents.ScanLocationDoc;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.CfgNplEx;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.ReadService;
import com.grsoft.network.ServerCommand;
import com.grsoft.network.WriteService;
import com.grsoft.util.AssortmentMatrixAdapter;
import com.grsoft.util.DocFilterOnClickListener;

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
    protected void defineNewType() {
        //DebtDocEx.initialize();

        DbObject.regNewDataType(Order.class, OrderEx.class);
        DbObject.regNewDataType(Org.class, OrgEx.class);
        DbObject.regNewDataType(Delivery.class, DeliveryEx.class);
        DbObject.regNewDataType(OrderProceeded.class, OrderProceededEx.class);
        DataObjectInfo.getInstance().replaceListType(OrderEx.class, "items", OrderItemEx.class);
        DataObjectInfo.getInstance().replaceListType(Return.class, "items", ReturnItemEx.class);

        UpdateDB.addHitchingCtor(new HitchingCtor() {
            @Override public List<Hitching> createList() {
                CostStrategyEx.clear();
                Hitching[] h = new Hitching[] {
                        new LoadOrdersHitching(),
                        new RcvNewHitching(OrderRouteInfo.class),
                        new RcvNewHitching(OrderDriverRouteInfo.class),
                        new RcvNewHitching(RoutePhotos.class),
                        new RcvNewHitching(MemoType.class),
                        new Hitching(MemoStatus.class),
                };
                return Arrays.asList(h);
            }
        }, UpdateDB.GEN_DATA_HITCHING);

        UpdateDB.addHitchingCtor(new HitchingCtor() {
            @Override public List<Hitching> createList() {
                Hitching[] h = new Hitching[] {
                        new RcvNewHitching(OrgBalance.class),
                        new RcvNewHitching(OrgBalanceData.class),
                };
                return Arrays.asList(h);
            }
        }, UpdateDB.DEBET_DATA_HITCHING);

        UpdateDB.addHitchingCtor(new HitchingCtor() {
            @Override public List<Hitching> createList() {
                Hitching[] h = new Hitching[] {
                        new DocumentRestore(AgentMemoDoc.instance()),
                };
                return Arrays.asList(h);
            }
        }, UpdateDB.RESTORE_DATA_HITCHING);

//		PostUpdateDB.POST_UPDATE_RUN = new Runnable() {
//
//			@Override
//			public void run() {
//				BalanceHelper.refreshBalance();
//			}
//		};

        DispatchReturnsHitching drh = new DispatchReturnsHitching(getApplicationContext());

        ReadService.requestObjects.add(drh);
        ReadService.requestObjects.add(new RcvNewHitching(OrderRouteInfo.class));
        ReadService.requestObjects.add(new RcvNewHitching(OrderDriverRouteInfo.class));

        WriteService.requestObjects.addAll(ReadService.requestObjects);

        CostStrategy.defaultInstance = new CostStrategyEx();
        DocFilterOnClickListener.HiddenTypes.add(ScanLocationDoc.instance());
        DocFilterOnClickListener.HiddenTypes.add(ReturnDoc.instance());
    }

    @Override
    protected void initChildFeature() {
        Features.UNLIMIT_VISIT_ITEMS = true;
        Features.SALES_FROM_ORDERS = false;
        AssortmentMatrixAdapter.PERIOD_IN_MONTH = 3;
        AssortmentMatrixAdapter.MATRIX_DOC = DeliveryDoc.instance();
        Features.REPORT_REQUEST = true;
        Features.SHOW_NUMBER_IN_ORDER = true;
        Features.SHOW_ORG_ADDRESS = true;
    }

    @Override
    protected void initChildActivity() {
        Warehouse.activity = WarehouseEx.class;
        PriceCount.activity = PriceCountEx.class;
        Documents.activity = DocumentsEx.class;
        ReportParams.activity = ReportParamsEx.class;
        DocList.activity = DocListEx.class;
        UpdateDB.activity = UpdateDBEx.class;
        Setting.BehaviorSettingActivity = BehaviorSettingEx.class;
        OrderDetail.activity = OrderDetailEx.class;
        DeliveryDetail.activity = DeliveryDetailEx.class;
        DlvDocList.activity = DlvDocListEx.class;
    }

    @Override
    protected void initChildDocTypes() {
        DocType.addType(DispatchReturnsDoc.instance());
        //DocType.addType(ReturnDoc.instance(ReturnImplEx.class));
        DocType.addType(AgentMemoDoc.instance());
    }


    @Override
    public void onCreate() {
        ConfigManager.initConfig(new CfgNplEx());
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
}
