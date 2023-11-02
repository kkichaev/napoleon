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
import com.grsoft.database.PODHitching;
import com.grsoft.database.PODHitchingEx;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.dataobjects.Dogovors;
import com.grsoft.dataobjects.IncassDebDistr;
import com.grsoft.dataobjects.IncassDebDistrItemEx;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderProceeded;
import com.grsoft.dataobjects.OrderProceededEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.PriceCost;
import com.grsoft.dataobjects.Return;
import com.grsoft.dataobjects.ReturnEx;
import com.grsoft.dataobjects.SenegInputDoc;
import com.grsoft.dataobjects.SenegOutputDoc;
import com.grsoft.dataobjects.Visit;
import com.grsoft.dataobjects.VisitEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.napoleon.documents.DocSendListner;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.InventDoc;
import com.grsoft.napoleon.documents.MerchDoc;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.OrderDocEx;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.napoleon.documents.SenegDoc;
import com.grsoft.napoleon.documents.TrainingDoc;
import com.grsoft.napoleon.documents.VisitDocEx;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.CfgNplEx;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.ServerCommand;
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
		OrderDocEx.init();
		VisitDocEx.init();

		PODHitching.type = PODHitchingEx.class;

		DbObject.regNewDataType(Org.class, OrgEx.class);
		DbObject.regNewDataType(Order.class, OrderEx.class);
		DbObject.regNewDataType(Delivery.class, DeliveryEx.class);
		DbObject.regNewDataType(Visit.class, VisitEx.class);
		DbObject.regNewDataType(OrderProceeded.class, OrderProceededEx.class);
		DbObject.regNewDataType(Return.class, ReturnEx.class);

		DataObjectInfo.getInstance().replaceListType(IncassDebDistr.class, "items", IncassDebDistrItemEx.class);

		CostStrategy.defaultInstance = new CostStrategyEx();

		UpdateDB.addHitchingCtor(new HitchingCtor(){
			@Override
			public List<Hitching> createList() {
				CostStrategyEx.resetCache();
				Hitching[] h = new Hitching[] {
						new RcvNewHitching(PriceCost.class),
						new RcvNewHitching(Dogovors.class),
						new Hitching(SenegInputDoc.class),
				};
				return Arrays.asList(h);
			}
		}, UpdateDB.GEN_DATA_HITCHING);
	}

	@Override
	protected void initChildFeature() {
		super.initChildFeature();
	}

	@Override
	protected void initChildActivity() {
		super.initChildActivity();

		Documents.activity = DocumentsEx.class;
		OrderDetail.activity = OrderDetailEx.class;
		DocList.activity = DocListEx.class;
		ReturnDetail.activity = ReturnDetailEx.class;
		IncassDebDistrEdit.editActivity = IncassDebDistrEditEx.class;
		VisitEdit.activity = VisitEditEx.class;
	}

	@Override
	protected void initChildDocTypes() {
		DocType.addType(ReturnDoc.instance());
		DocType.addType(InventDoc.instance());
		DocType.addType(MerchDoc.instance());
		DocType.addType(TrainingDoc.instance());
		DocType.addType(SenegDoc.instance());

		DocType.removeType(RemnantsDoc.instance());

		DocFilterOnClickListener.HiddenTypes.add(SenegDoc.instance());
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
