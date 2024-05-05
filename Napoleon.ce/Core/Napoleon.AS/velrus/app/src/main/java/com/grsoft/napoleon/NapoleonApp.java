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

import com.grsoft.database.Hitching;
import com.grsoft.database.HitchingCtor;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.DistribMatrix;
import com.grsoft.dataobjects.Firm;
import com.grsoft.dataobjects.Folder;
import com.grsoft.dataobjects.FolderEx;
import com.grsoft.dataobjects.Incass;
import com.grsoft.dataobjects.IncassEx;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderAction;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.VelrusPlan;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.napoleon.documents.DebtDocEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.MonitoringDoc;
import com.grsoft.napoleon.documents.OrderDocEx;
import com.grsoft.napoleon.documents.OrgDistribDoc;
import com.grsoft.napoleon.documents.PkoDoc;
import com.grsoft.napoleon.documents.SalesDoc;
import com.grsoft.napoleon.modules.CostManagerImpl;
import com.grsoft.napoleon.modules.MonitoringInit;
import com.grsoft.napoleon.modules.print.Print;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.ServerCommand;
import com.grsoft.util.MenuHandler;
import com.grsoft.util.MenuPrepareHitching;
import com.grsoft.util.MenuPreparedEvent;
import com.grsoft.util.ViewInitializer;

import java.util.Arrays;
import java.util.List;

public class NapoleonApp extends NapoleonAppBase {

    class OrderEditor implements OrderImpl.PropertiesEditor {
        @Override
        public void edit(Context ctx, OrderImpl order, boolean isOldOrder) {
            CreateOrder.open(ctx, order, isOldOrder);
        }
    }

    protected void initTypes() {}

    @Override
    protected void defineNewType() {
		DebtDocEx.initialize();
		OrderDocEx.initialize();
		MonitoringInit.init();

		DbObject.regNewDataType(Incass.class, IncassEx.class);
		DbObject.regNewDataType(Org.class, OrgEx.class);
		DbObject.regNewDataType(Order.class, OrderEx.class);

		DbObject.regNewDataType(Folder.class, FolderEx.class);
        DbObject.regNewDataType(Price.class, PriceEx.class);

		DataObjectInfo.getInstance().replaceListType(Order.class, "items", OrderItemEx.class);

        Main.docMenuPrepared.add(new MenuPrepareHitching() {
            @Override
            public void menuPrepared(List<MenuHandler> menu, Activity activity) {
                menu.add(new MenuHandler("Планы", () -> VelrusPlanView.open(activity)));
            }
        });

        UpdateDB.addHitchingCtor(new HitchingCtor() {
            @Override
            public List<Hitching> createList() {
                Hitching[] h = new Hitching[] {
                        new RcvNewHitching(DistribMatrix.class, "DistributionMatrix"),
                        new RcvNewHitching(OrderAction.class),
                        new RcvNewHitching(Firm.class),
                        new RcvNewHitching(VelrusPlan.class),
                };
                return Arrays.asList(h);
            }
        }, UpdateDB.GEN_DATA_HITCHING);

        UpdateDB.initUI = new ViewInitializer() {
            @Override public void init(Activity activity) { activity.findViewById(R.id.cbRemains).setVisibility(View.GONE); }
        };

		initTypes();
    }

    @Override
    protected void initChildFeature() {
        super.initChildFeature();

        Features.HAVE_PRICE_MOVER = false;

		Features.DOC_SUM_BY_PERIOD = true;
		Features.COST_MANAGER = new CostManagerImpl();
		Features.FOCUSED_ITEMS = true;
		Features.LOAD_FULL_PRICE = true;
	}

    @Override
    public void onCreate() {
        ConfigManager.initConfig(new CfgNpl());
        super.onCreate();

        OrderImpl.OrderEditor = new OrderEditor();
        setProgrammVersion();
    }

    @Override
    protected void initChildActivity() {
        super.initChildActivity();

        Warehouse.activity = WarehouseEx.class;
		IncassEdit.activity = IncassEditEx.class;
		Documents.activity = DocumentsEx.class;
		PriceCount.activity = PriceCountEx.class;
		DocList.activity = DocListEx.class;
		OrderDetail.activity = OrderDetailEx.class;

	}

    @Override
    protected void initChildDocTypes() {
        DocType.addType(PkoDoc.instance());
		DocType.addType(OrgDistribDoc.instance());
		DocType.addType(MonitoringDoc.instance());
    }

    private void setProgrammVersion() {
        try {
            ServerCommand.ProgramVersion = getResources().getString(R.string.version);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
