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
import android.content.res.Resources;
import android.util.Log;
import android.view.MenuItem;

import com.grsoft.database.BannerRcv;
import com.grsoft.database.HitchOnSelect;
import com.grsoft.database.Hitching;
import com.grsoft.database.HitchingCtor;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.dataobjects.Firm;
import com.grsoft.dataobjects.FirmEx;
import com.grsoft.dataobjects.IncassDebDistr;
import com.grsoft.dataobjects.IncassDebDistrEx;
import com.grsoft.dataobjects.KupecAction;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Payment;
import com.grsoft.dataobjects.PaymentEx;
import com.grsoft.dataobjects.PrcTypes;
import com.grsoft.dataobjects.Present;
import com.grsoft.dataobjects.PresentEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.PriceHitchingEx;
import com.grsoft.dataobjects.PriceWeight;
import com.grsoft.dataobjects.ServikoAction;
import com.grsoft.dataobjects.ServikoActionItems;
import com.grsoft.dataobjects.ShelfShare;
import com.grsoft.dataobjects.SppAgent;
import com.grsoft.dataobjects.UserAssortMtx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.ScriptImplEx;
import com.grsoft.napoleon.documents.BankDoc;
import com.grsoft.napoleon.documents.ClientCardDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.IncassDocEx;
import com.grsoft.napoleon.documents.KupecDoc;
import com.grsoft.napoleon.documents.ShelfShareDoc;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.CfgNplEx;
import com.grsoft.network.ServerCommand;
import com.grsoft.script.ScriptEdit;
import com.grsoft.script.dataobjects.impl.ScriptImpl;
import com.grsoft.util.DocFilterOnClickListener;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.MenuActionHandler;
import com.grsoft.util.MenuHandler;
import com.grsoft.util.MenuPrepareHitching;
import com.grsoft.util.PriceComparer;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NapoleonApp extends NapoleonAppBase {

    AlertSender alertSender;

    class OrderEditor implements OrderImpl.PropertiesEditor {
        @Override
        public void edit(Context ctx, OrderImpl order, boolean isOldOrder) {
            CreateOrder.open(ctx, order, isOldOrder);
        }
    }

    public void addAlert(Org o, int type) {
        alertSender.add(o, type);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        OrderImpl.OrderEditor = new OrderEditor();
        setProgrammVersion();

        alertSender = new AlertSender(this);
        FoldersAdapter.TreeNodeComparator = new PriceComparer(getApplicationContext());

        //NapoleonChat.init(this);

        Main.docMenuPrepared.add(new MenuPrepareHitching() {
            @Override
            public void menuPrepared(List<MenuHandler> menu, final Activity activity) {
                menu.add(new MenuHandler(getString(R.string.order_report), new Runnable() {
                    @Override
                    public void run() {
                        OrderList.open(activity);
                    }
                }));
            }
        });

        Main.docMenuPrepared.add(new MenuPrepareHitching() {
            @Override
            public void menuPrepared(List<MenuHandler> menu, final Activity activity) {
                menu.add(new MenuHandler(getString(R.string.bank), new Runnable() {
                    @Override
                    public void run() {
                        BankList.open(activity);
                    }
                }));
            }
        });

        Main.mainMenuPrepared.add(new MenuPrepareHitching() {
            @Override
            public void menuPrepared(List<MenuHandler> menu, final Activity activity) {
                menu.add(new MenuActionHandler(getString(R.string.plans), new Runnable() {
                    @Override
                    public void run() {
                        PlanView.open(activity);
                    }
                }, R.drawable.ic_app_registration));
            }
        });

//        String pwFilter = "id in (select id from [WH$CURRENT_USERID]) and  firm in (select id from [FI$CURRENT_USERID])";
        UpdateDB.addHitchingCtor(new HitchingCtor() {
            @Override
            public List<Hitching> createList() {
                List<Hitching> res = new ArrayList<>();
                res.add(new RcvNewHitching(UserAssortMtx.class));
                res.add(new RcvNewHitching(SppAgent.class));
                res.add(new RcvNewHitching(KupecAction.class));
                res.add(new BannerRcv(getApplicationContext()));
//                res.add(new HitchOnSelect(PriceWeight.class, "PriceSortWeight", pwFilter, true));
                res.add(new RcvNewHitching(PriceWeight.class));
                res.add(new RcvNewHitching(ShelfShare.class));
                return res;
            }
        }, UpdateDB.GEN_DATA_HITCHING);
    }

    @Override
    public void onTerminate() {
        alertSender.close();
        super.onTerminate();
    }

    @Override
    protected CfgNpl createConfig() {
        return new CfgNplEx();
    }

    private void setProgrammVersion() {
        try {
            Resources res = getResources();
            ServerCommand.ProgramVersion = res.getString(R.string.version);
            ServerCommand.Project = res.getString(R.string.project);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Class<? extends OrderImplBase<? extends Order>> orderImplType() {
        return OrderImplEx.class;
    }

    @Override
    protected void defineNewType() {
        super.defineNewType();

        IncassDocEx.init();
        DbObject.regNewDataType(Present.class, PresentEx.class);
        DbObject.regNewDataType(Org.class, OrgEx.class);
        DbObject.regNewDataType(Order.class, OrderEx.class);
        DbObject.regNewDataType(Price.class, PriceEx.class);
        DbObject.regNewDataType(Firm.class, FirmEx.class);
        DbObject.regNewDataType(Delivery.class, DeliveryEx.class);
        DbObject.regNewDataType(Payment.class, PaymentEx.class);
        DbObject.regNewDataType(IncassDebDistr.class, IncassDebDistrEx.class);

        DataObjectInfo.getInstance().replaceListType(OrderEx.class, "items", OrderItemEx.class);

        UpdateDB.priceHitchingClass = PriceHitchingEx.class;
        UpdateDB.addHitchingCtor(new HitchingCtor() {
            @Override
            public List<Hitching> createList() {
                CostStrategyEx.clearCache();

                Hitching[] ret = new Hitching[]{
                        new RcvNewHitching(Firm.class, "Firm"),
                        new RcvNewHitching(PrcTypes.class, "PrcTypes"),
                        new RcvNewHitching(ServikoAction.class, "ServikoAction"),
                        new RcvNewHitching(ServikoActionItems.class, "ServikoActionItems")
                };
                return Arrays.asList(ret);
            }
        }, UpdateDB.GEN_DATA_HITCHING);

        CostStrategy.defaultInstance = new CostStrategyEx();
        DocFilterOnClickListener.HiddenTypes.add(KupecDoc.instance());
        DocFilterOnClickListener.HiddenTypes.add(BankDoc.instance());
    }

    @Override protected Class<? extends ScriptImpl> scriptImplType() { return ScriptImplEx.class; }

    @Override
    protected void initChildFeature() {
        super.initChildFeature();

        Features.CHECK_UNCOMPLETE_SCRIPTS = true;
        Features.CANT_CHANGE_SEND_FLAG = true;
        Features.DEL_VISIT_WITHOUT_PHOTO = true;
        Features.START_VISIT_OPEN_CAMERA = true;
        Features.DONT_SHOW_FIRST_SCRIPT_DOC = true;

        Features.LOAD_FULL_PRICE = true;
        Features.PRESENTATION_IN_DB = true;

        // conflict with Action matrix
        Features.OPEN_LAST_MATRIX = false;
        Features.CHECK_UNCOMPLETE_SCRIPTS = true;
    }

    @Override
    protected void initChildActivity() {
        super.initChildActivity();

        ScriptEdit.activity = ScriptEditEx.class;
        DocList.activity = DocListEx.class;
        Documents.activity = DocumentsEx.class;
        PriceCount.activity = PriceCountEx.class;
        Warehouse.activity = WarehouseEx.class;
        UpdateDB.activity = UpdateDBEx.class;
        Presentation.activity = PresentationFolderEx.class;
        IncassDebDistrEdit.editActivity = IncassDebDistrEditEx.class;
        OrderDetail.activity = OrderDetailEx.class;
    }

    @Override
    protected void initChildDocTypes() {
        super.initChildDocTypes();

        DocType.addType(ClientCardDoc.instance());
        DocType.addType(KupecDoc.instance());
        DocType.addType(BankDoc.instance());
        DocType.addType(ShelfShareDoc.instance());
    }

}
