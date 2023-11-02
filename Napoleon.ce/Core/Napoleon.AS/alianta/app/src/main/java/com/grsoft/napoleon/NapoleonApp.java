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
import android.widget.TextView;

import com.grsoft.database.HitchOnSelect;
import com.grsoft.database.Hitching;
import com.grsoft.database.HitchingCtor;
import com.grsoft.database.LoadedOrdersRcvr;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.Action;
import com.grsoft.dataobjects.AgentInfo;
import com.grsoft.dataobjects.AgentPlan;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.DeliveryAddress;
import com.grsoft.dataobjects.GoodsAnalogs;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.ReportList;
import com.grsoft.dataobjects.ReportRequest;
import com.grsoft.dataobjects.Store;
import com.grsoft.dataobjects.UserAssortMtx;
import com.grsoft.dataobjects.WhData;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.napoleon.documents.AliantaOfferDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.modules.print.NPrinter;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.CfgNplW;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.ReadService;
import com.grsoft.network.ServerCommand;
import com.grsoft.network.WriteService;
import com.grsoft.util.MenuHandler;
import com.grsoft.util.MenuPrepareHitching;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
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

	private void setProgrammVersion() {
		try{
			ServerCommand.ProgramVersion = getResources().getString(R.string.version);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	protected Class<? extends OrderImplBase<? extends Order>> orderImplType() {
		return OrderImplEx.class;
	}

	@Override
	protected void defineNewType() {
		NPrinter.forms.put("offer", "offer");

		DbObject.regNewDataType(Org.class, OrgEx.class);
		DbObject.regNewDataType(Price.class, PriceEx.class);
		DbObject.regNewDataType(Order.class, OrderEx.class);
		DataObjectInfo.getInstance().replaceListType(OrderEx.class, "items", OrderItemEx.class);

		ReportParams.init = new ReportParams.Init() {
			boolean isWorkDay(Calendar c) {
				int day = c.get(Calendar.DAY_OF_WEEK);
				return day != Calendar.SUNDAY && day != Calendar.SATURDAY;
			}

			@Override
			public void init(ReportRequest request, ReportList repDef, ReportParams owner) {
				if(request.id.equals("MoneyIncome")) {
					owner.findViewById(R.id.trStartDate).setVisibility(View.GONE);
					owner.findViewById(R.id.trOrg).setVisibility(View.GONE);
					((TextView)owner.findViewById(R.id.tvEndDateLabel)).setText("Дата отчета");

					Calendar c = Calendar.getInstance();
					c.add(Calendar.DAY_OF_YEAR, -1);
					while(!isWorkDay(c))
						c.add(Calendar.DAY_OF_YEAR, -1);

					request.end = c.getTime();
				}
			}
		};

		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override
			public List<Hitching> createList() {
				ActionHelper.resetCache();
				Hitching[] h = new Hitching[] {
						new RcvNewHitching(AgentPlan.class, "AgentPlan"),
						new RcvNewHitching(WhData.class),
						new RcvNewHitching(Store.class),
						new RcvNewHitching(UserAssortMtx.class),
						new RcvNewHitching(Action.class),
						new RcvNewHitching(DeliveryAddress.class),
						new HitchOnSelect(AgentInfo.class, "UserInfo", "\"userid\" = '$CURRENT_USERID'"),
						new RcvNewHitching(GoodsAnalogs.class),
				};
				return Arrays.asList(h);
			}
		}, UpdateDB.GEN_DATA_HITCHING);

		UpdateDB.addHitchingCtor(new HitchingCtor(){
			@Override
			public List<Hitching> createList() {
				Date dateFrom = ((CfgNplW)ConfigManager.getConfig()).getRestoreDate();
				Hitching[] h = new Hitching[] {
						new LoadedOrdersRcvr(dateFrom),
				};
				return Arrays.asList(h);
			}
		}, UpdateDB.RESTORE_DATA_HITCHING);

		Main.docMenuPrepared.add(new MenuPrepareHitching() {

			@Override
			public void menuPrepared(List<MenuHandler> menu, final Activity activity) {
				menu.add(new MenuHandler(activity.getString(R.string.plans), new Runnable() {
					@Override public void run() { AgentPlanView.open(activity); }
				}));
			}
		});

		WriteService.requestObjects.add(new LoadedOrdersRcvr());
		//WriteService.recievers.add(new OrderResultHitching());
		ReadService.requestObjects.addAll(WriteService.requestObjects);
	}

	@Override
	protected void initChildActivity() {
		OrderDetail.activity = OrderDetailEx.class;
		OrderDeliveryDetail.activity = OrderDeliveryDetailEx.class;
		Warehouse.activity = WarehousEx.class;
		Documents.activity = DocumentsEx.class;
		Setting.addTabs.add(OrderDefaultSetting.class);
	}

	@Override
	protected void initChildFeature() {
		Features.DELIVERY_REPLACE_ORDER_SUM = true;
		Features.ID_COLUMN_IN_PRICE_LIST = true;
		Features.LOAD_FULL_PRICE = true;
		Features.REPORT_REQUEST = true;
		Features.ID_IN_PRESENTATION = true;
		Features.SEARCh_PRICE_ID_EXACT = true;
	}

	@Override
	protected void initChildDocTypes() {
		DocType.addType(AliantaOfferDoc.instance());
	}

}
