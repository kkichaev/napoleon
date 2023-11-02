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
import android.widget.CheckBox;

import com.grsoft.database.DocumentRestore;
import com.grsoft.database.Hitching;
import com.grsoft.database.HitchingCtor;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.BtlPlan;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.GoodsMatrix;
import com.grsoft.dataobjects.GroupGoods;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgFolderItemEx;
import com.grsoft.dataobjects.OrgFolders;
import com.grsoft.dataobjects.OrgMatrix;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.Question;
import com.grsoft.dataobjects.QuestionItemEx;
import com.grsoft.dataobjects.RemnantItemEx;
import com.grsoft.dataobjects.Remnants;
import com.grsoft.dataobjects.Return;
import com.grsoft.dataobjects.ReturnCause;
import com.grsoft.dataobjects.ReturnItemEx;
import com.grsoft.dataobjects.ReturnOnDelivery;
import com.grsoft.dataobjects.ScrAssign;
import com.grsoft.dataobjects.ScriptDefEx;
import com.grsoft.dataobjects.Slsnet;
import com.grsoft.dataobjects.Visit;
import com.grsoft.dataobjects.VisitEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.NbtlScriptResolver;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.RemnantsImplEx;
import com.grsoft.dataobjects.impl.ScriptImplEx;
import com.grsoft.napoleon.documents.CMonitoringDoc;
import com.grsoft.napoleon.documents.ContractDoc;
import com.grsoft.napoleon.documents.DistribDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.PlanogramDoc;
import com.grsoft.napoleon.documents.QuestionDoc;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.napoleon.documents.ScriptDocEx;
import com.grsoft.napoleon.documents.TargetDoc;
import com.grsoft.napoleon.documents.TaskDoneDoc;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.napoleon.documents.VisitDocEx;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.ServerCommand;
import com.grsoft.script.ScriptEdit;
import com.grsoft.script.dataobjects.ScriptDef;
import com.grsoft.script.dataobjects.impl.ScriptDefImpl;
import com.grsoft.util.DocFilterOnClickListener;
import com.grsoft.util.MenuHandler;
import com.grsoft.util.MenuPreparedEvent;
import com.grsoft.util.ViewInitializer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NapoleonAppNbtlBase extends NapoleonAppBase {
    public static Context context;

    class OrderEditor implements OrderImpl.PropertiesEditor {
        @Override
        public void edit(Context ctx, OrderImpl order, boolean isOldOrder) {
            CreateOrder.open(ctx, order, isOldOrder);
        }
    }

    protected void initDocTypes() {
        ServerCommand.Category = "btl";

        DocType.addType(VisitDocEx.initilize());
        DocType.addType(QuestionDoc.instance());
        DocType.addType(ContractDoc.instance());
        DocType.addType(PlanogramDoc.instance());
//		DocType.addType(ReturnDoc.instance(ReturnImplEx.class));
//		DocType.addType(ReturnOnDeliveryDoc.theInstance(ReturnOnDeliveryImpl.class));
        DocType.addType(CMonitoringDoc.instance());
        DocType.addType(RemnantsDoc.instance(RemnantsImplEx.class));
        DocType.addType(DistribDoc.instance());
        DocType.addType(TaskDoneDoc.instance());
        DocType.addType(TargetDoc.instance());

        ScriptDocEx.instance(ScriptImplEx.class);

        DocType.setCurDoc(VisitDoc.instance());

        DbObject.regNewDataType(Org.class, OrgEx.class);
        DbObject.regNewDataType(Price.class, PriceEx.class);
        DbObject.regNewDataType(Visit.class, VisitEx.class);
        DbObject.regNewDataType(ScriptDef.class, ScriptDefEx.class);
        DataObjectInfo.getInstance().replaceListType(Return.class, "items", ReturnItemEx.class);
        DataObjectInfo.getInstance().replaceListType(ReturnOnDelivery.class, "items", ReturnItemEx.class);
        DataObjectInfo.getInstance().replaceListType(OrgFolders.class, "items", OrgFolderItemEx.class);
        DataObjectInfo.getInstance().replaceListType(Remnants.class, "items", RemnantItemEx.class);
        DataObjectInfo.getInstance().replaceListType(Question.class, "items", QuestionItemEx.class);

        Features.SCRIPT_DOC = true;
        Features.POTENZIAL_ORG = false;
        Features.DEL_VISIT_WITHOUT_PHOTO = true;
        Features.LOAD_FULL_PRICE = true;
        Features.CANT_SEND_SCRIPT_PART = true;
        Features.SCRIPT_GO_NEXT = false;

        Warehouse.activity = WarehouseEx.class;
        Presentation.activity = PresentationFolder.class;
        PricePresentation.activity = PricePresentationFolder.class;
        Documents.activity = DocumentsEx.class;
        ScriptEdit.activity = ScriptEditEx.class;
        UpdateDB.activity = UpdateDBEx.class;
        Setting.activity = SettingEx.class;
        VisitEdit.activity = VisitEditEx.class;
        QuestionWebView.activity = QuestEditEx.class;

        DocFilterOnClickListener.HiddenTypes.add(ContractDoc.instance());
        DocFilterOnClickListener.HiddenTypes.add(PlanogramDoc.instance());
        //DocFilterOnClickListener.HiddenTypes.add(CMonitoringDoc.instance());

        UpdateDB.addHitchingCtor(new HitchingCtor() {
            @Override
            public List<Hitching> createList() {
                Hitching[] h = new Hitching[]{
                        new RcvNewHitching(OrgMatrix.class),
                        new RcvNewHitching(ScrAssign.class),
                        new RcvNewHitching(ReturnCause.class),
                        new Hitching(BtlPlan.class),
                        new Hitching(GroupGoods.class),
                        new Hitching(GoodsMatrix.class),
                        new Hitching(Slsnet.class)
                };
                return Arrays.asList(h);
            }
        }, UpdateDB.GEN_DATA_HITCHING);

        UpdateDB.addHitchingCtor(new HitchingCtor() {
            public Hitching create() {
                return new DocumentRestore(TargetDoc.instance());
            }
        }, UpdateDB.RESTORE_DATA_HITCHING);

        CfgNpl config = (CfgNpl) ConfigManager.getConfig();
        config.priceClmn3Type = Warehouse.COLUMN_QTY_ORD;
        ConfigManager.save();

        UpdateDB.initUI = new ViewInitializer() {
            @Override
            public void init(Activity activity) {
                activity.findViewById(R.id.cbPresent).setVisibility(View.GONE);
                activity.findViewById(R.id.cbDebt).setVisibility(View.GONE);
                activity.findViewById(R.id.cbRecreateStory).setVisibility(View.GONE);
                activity.findViewById(R.id.spMonthRecreate).setVisibility(View.GONE);
                ((CheckBox) activity.findViewById(R.id.cbVisit)).setChecked(true);
            }
        };

        Napoleon.mainMenuPrepared = new MenuPreparedEvent() {
            private static final long serialVersionUID = 1L;

            @Override
            public void menuPrepared(ArrayList<MenuHandler> menu, final Activity activity) {
                menu.add(2, new MenuHandler(getString(R.string.msg_list), new Runnable() {
                    @Override
                    public void run() {
                        Messages.open(activity);
                    }
                }));

                for (MenuHandler h : menu) {
                    if (h.name.equals(getString(R.string.docs))) {
                        menu.remove(h);
                        break;
                    }
                }

            }
        };
    }

    @Override
    public void onCreate() {
        ScriptDefImpl.resolver = new NbtlScriptResolver();
        context = getApplicationContext();
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
}
