/*
 * Copyright (C), 2011, Гильдия Разработчиков
 *
 * Демо программа (с базой данных)
 *
 * kki   16/05/2011   creating
 */

package com.grsoft.napoleon;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;

import com.grsoft.database.Hitching;
import com.grsoft.database.HitchingCtor;
import com.grsoft.database.LoadedOrdersHitching;
import com.grsoft.database.PODHitching;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgMatrix;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.TargetDoc;
import com.grsoft.napoleon.modules.CostManagerImpl;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.ServerCommand;
import com.grsoft.util.AssortmentMatrixAdapter;
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

    @Override
    public void onCreate() {
		PODHitching.type = PODHitchingEx.class;
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
	protected void defineNewType() {
		DbObject.regNewDataType(Price.class, PriceEx.class);
		DbObject.regNewDataType(Org.class, OrgEx.class);
		DbObject.regNewDataType(Order.class, OrderEx.class);
		DataObjectInfo.getInstance().replaceListType(OrderEx.class, "items", OrderItemEx.class);

		CostStrategy.defaultInstance = new CostStrategyEx();

		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override
			public List<Hitching> createList() {
				Hitching[] h = new Hitching[] {
						new RcvNewHitching(OrgMatrix.class),
						new LoadedOrdersHitching(),
				};
				return Arrays.asList(h);
			}
		}, UpdateDB.GEN_DATA_HITCHING);

		UpdateDB.addHitchingCtor(new HitchingCtor(){
			@Override
			public List<Hitching> createList() {
				CfgNpl c = (CfgNpl) ConfigManager.getConfig();
				Hitching[] h = new Hitching[] {
						new LoadedOrdersHitching(c.monthsToRecreate * 31 + c.daysToRecreate, true),
				};
				return Arrays.asList(h);
			}
		}, UpdateDB.RESTORE_DATA_HITCHING);

		UpdateDB.initUI = new ViewInitializer() {
			public void init(android.app.Activity activity) {
				((CheckBox)activity.findViewById(R.id.cbCost)).setChecked(true);
				activity.findViewById(R.id.cbRemains).setVisibility(View.GONE);
			}
		};
	}

	@Override
	protected void initChildActivity() {
		Warehouse.activity = WarehouseEx.class;
		PriceCount.activity = PriceCountEx.class;
		OrderDetail.activity = OrderDetailEx.class;
		UpdateDB.activity = UpdateDBEx.class;
		Documents.activity = DocumentsEx.class;
	}

	@Override
	protected void initChildFeature() {
		Features.CAN_CHANGE_COST = true;
		Features.PRESENTATION_ON_SDCARD = true;
		Features.COST_MANAGER = new CostManagerImpl();
		Features.LOAD_FULL_PRICE = true;
		AssortmentMatrixAdapter.PERIOD_IN_MONTH = 0;
		AssortmentMatrixAdapter.PERIOD_IN_DAY = 90;
	}

	@Override protected Class<? extends OrderImplBase<? extends Order>> orderImplType() { return OrderImplEx.class; }

	@Override
	protected void initChildDocTypes() {
		super.initChildDocTypes();

		DocType.addType(TargetDoc.instance());
	}
}
