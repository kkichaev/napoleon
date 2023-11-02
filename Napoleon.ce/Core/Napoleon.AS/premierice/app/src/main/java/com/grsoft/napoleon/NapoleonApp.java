/*
 * Copyright (C), 2011, Гильдия Разработчиков
 * 
 * Демо программа (с базой данных)
 *
 * kki   16/05/2011   creating
 */

package com.grsoft.napoleon;

import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.Context;

import com.grsoft.database.Hitching;
import com.grsoft.database.HitchingCtor;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.AgentPlan;
import com.grsoft.dataobjects.Bonus;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Incass;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.Return;
import com.grsoft.dataobjects.ReturnEx;
import com.grsoft.dataobjects.Visit;
import com.grsoft.dataobjects.VisitItemEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.VisitImplEx;
import com.grsoft.napoleon.documents.DebetDocEx;
import com.grsoft.napoleon.documents.DebetWorkDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.AuditEquipDoc;
import com.grsoft.napoleon.documents.IncassDoc;
import com.grsoft.napoleon.documents.InvFrgDoc;
import com.grsoft.napoleon.documents.NewClientDoc;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.napoleon.modules.CostManagerImpl;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.ServerCommand;
import com.grsoft.script.dataobjects.ScriptDef;
import com.grsoft.script.dataobjects.impl.ScriptDefImpl;
import com.grsoft.util.DocFilterOnClickListener;
import com.grsoft.util.MenuHandler;
import com.grsoft.util.MenuPrepareHitching;

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

	@Override
	protected Class<? extends OrderImplBase<? extends Order>> orderImplType() {
		return OrderImplEx.class;
	}

	@Override
	protected void defineNewType() {
		DebetDocEx.init();
		VisitDoc.instance(VisitImplEx.class);

		super.defineNewType();

		DataObjectInfo.getInstance().replaceListType(Visit.class, "items", VisitItemEx.class);
		DataObjectInfo.getInstance().replaceListType(OrderEx.class, "items", OrderItemEx.class);
		DbObject.regNewDataType(Org.class, OrgEx.class);
		DbObject.regNewDataType(Order.class, OrderEx.class);
		DbObject.regNewDataType(Return.class, ReturnEx.class);
		DbObject.regNewDataType(Price.class, PriceEx.class);

		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override
			public List<Hitching> createList() {
				Hitching[] h = new Hitching[] {
						new RcvNewHitching(AgentPlan.class, "AgentPlan"),
						new RcvNewHitching(Bonus.class),
				};
				return Arrays.asList(h);
			}
		}, UpdateDB.GEN_DATA_HITCHING);


		Main.docMenuPrepared.add(new MenuPrepareHitching() {

			@Override
			public void menuPrepared(List<MenuHandler> menu, final Activity activity) {
				menu.add(new MenuHandler(activity.getString(R.string.plans), new Runnable() {
					@Override public void run() { AgentPlanView.open(activity); }
				}));
			}
		});
	}

	@Override
	protected void initChildDocTypes() {
		super.initChildDocTypes();
		DocType.addType(ReturnDoc.instance());
		DocType.addType(InvFrgDoc.instance());
		DocType.addType(AuditEquipDoc.instance());

		DocType.addType(DebetWorkDoc.instance());
		DocType.addType(NewClientDoc.instance());
		DocFilterOnClickListener.HiddenTypes.add(NewClientDoc.instance());

		ScriptDefImpl.docInScript.add(DebetWorkDoc.instance());
	}

	@Override
	protected void initChildActivity() {
		super.initChildActivity();
		PotenzialOrg.activity = NewClientList.class;
		Documents.activity = DocumentsEx.class;
		Warehouse.activity = WarehouseEx.class;
		PriceCount.activity = PriceCountEx.class;
		OrderDetail.activity = OrderDetailEx.class;
	}

	@Override
	protected void initChildFeature() {
		super.initChildFeature();
		Features.COST_MANAGER = new CostManagerImpl();
		Features.INCASS_DEBET_DISTRIB = true;
		Features.LOAD_FULL_PRICE = true;
	}

	private void setProgrammVersion() {
		try{
			ServerCommand.ProgramVersion = getResources().getString(R.string.version);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
