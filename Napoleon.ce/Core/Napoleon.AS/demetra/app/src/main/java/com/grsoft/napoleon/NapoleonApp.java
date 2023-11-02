/*
 * Copyright (C), 2011, ������� �������������
 * 
 * ���� ��������� (� ����� ������)
 *
 * kki   16/05/2011   creating
 */

package com.grsoft.napoleon;

import android.app.Activity;
import android.content.Context;
import android.view.MenuItem;
import android.widget.CheckBox;

import com.grsoft.database.Hitching;
import com.grsoft.database.HitchingCtor;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.DistribGroup;
import com.grsoft.dataobjects.Incass;
import com.grsoft.dataobjects.IncassEx;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgGroup;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.Terminal;
import com.grsoft.dataobjects.TypeOrgMatrix;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.napoleon.documents.DistrDocType;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.ServerCommand;
import com.grsoft.util.AssortmentMatrixAdapter;
import com.grsoft.util.MenuActionHandler;
import com.grsoft.util.MenuHandler;
import com.grsoft.util.MenuPrepareHitching;
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
		ConfigManager.initConfig(new CfgNpl());
		super.onCreate();
		OrderImpl.OrderEditor = new OrderEditor();
		setProgrammVersion();
		
		//NapoleonChat.init(this);
	}

	@Override
	protected void defineNewType() {
		super.defineNewType();

		DbObject.regNewDataType(Org.class, OrgEx.class);
		DbObject.regNewDataType(Price.class, PriceEx.class);
		DbObject.regNewDataType(Incass.class, IncassEx.class);
		DataObjectInfo.getInstance().replaceListType(Order.class, "items", OrderItemEx.class);

		UpdateDB.addHitchingCtor(new HitchingCtor(){
			@Override
			public List<Hitching> createList() {
				Hitching[] h = new Hitching[] {
						new RcvNewHitching(OrgGroup.class, "OrgGroup"),
						new RcvNewHitching(TypeOrgMatrix.class, "TypeOrgMatrix"),
						new RcvNewHitching(DistribGroup.class, "DistribGroup")
				};
				return Arrays.asList(h);
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
	protected void initChildDocTypes() {
		super.initChildDocTypes();

		DocType.addType(DistrDocType.instance());

		NapoleonEx.mainMenuPrepared.add(new MenuPrepareHitching() {

			@Override
			public void menuPrepared(List<MenuHandler> menu, final Activity activity) {
				menu.add(new MenuActionHandler(activity.getString(R.string.show_route_menu_hint),
						new Runnable() {
							@Override
							public void run() {
								((NapoleonEx) activity).openReports();
							}
						},
						R.drawable.ic_reports)
				);

				menu.add(new MenuActionHandler(activity.getString(R.string.reports_menu_hint),
						new Runnable() {
							@Override
							public void run() {
								((NapoleonEx) activity).showRouteMap();
							}
						},
						R.drawable.globus) {
					@Override
					public void initMenu(Context context, MenuItem item) {
						super.initMenu(context, item);
						item.setVisible(((NapoleonEx)activity).isGlobusAvail());
					}
				});
			}
		});

		UpdateDB.initUI = new ViewInitializer(){
			@Override
			public void init(Activity activity) {
				super.init(activity);

				((CheckBox) activity.findViewById(R.id.cbVisit)).setChecked(true);
			}
		};

		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override
			public Hitching create() {
				CostStrategyEx.clearCache();
				return new RcvNewHitching(Terminal.class);
			}
		}, UpdateDB.GEN_DATA_HITCHING);

		CostStrategy.defaultInstance = new CostStrategyEx();
	}

	@Override
	protected void initChildActivity() {
		super.initChildActivity();

		Warehouse.activity = WarehouseEx.class;
		IncassEdit.activity = IncassEditEx.class;
		PriceCount.activity = PriceCountEx.class;
		OrderDetail.activity = OrderDetailEx.class;
		Documents.activity = DocumentsEx.class;
	}

	@Override
	protected void initChildFeature() {
		super.initChildFeature();
		Features.INCASS_DEBET_DISTRIB = false;
		Features.INPUT_QTY_IN_PACK = true;
		Features.SHOW_WEIGHT_IN_DOC_LIST = true;
		Features.LAST_SALED_ITEMS_PERIOD = 2;
		Features.MAX_FOTO_HEIGHT = 2200;
		Features.MAX_FOTO_WIDTH = 2200;
		Features.OK_BTN_INCASS = true;

		AssortmentMatrixAdapter.PERIOD_IN_MONTH = 2;
	}
}
