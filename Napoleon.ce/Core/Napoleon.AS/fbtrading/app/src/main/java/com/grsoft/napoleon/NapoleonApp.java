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

import com.grsoft.database.FBTransferCommitHitching;
import com.grsoft.database.Hitching;
import com.grsoft.database.HitchingCtor;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.database.ReqTransferHitching;
import com.grsoft.dataobjects.Agent;
import com.grsoft.dataobjects.AgentsEx;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DeliveryItemEx;
import com.grsoft.dataobjects.FBTransferReject;
import com.grsoft.dataobjects.MaxDiscounts;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.OrgDiscount;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.PriceWhData;
import com.grsoft.dataobjects.Sklad;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.FBTransferDoc;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.OrderDocEx;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.ServerCommand;
import com.grsoft.util.AssortmentMatrixAdapter;
import com.grsoft.util.DocFilterOnClickListener;
import com.grsoft.util.MenuHandler;
import com.grsoft.util.MenuPrepareHitching;

import java.util.Arrays;
import java.util.List;

public class NapoleonApp extends NapoleonAppBase {
	
	class OrderEditor implements OrderImpl.PropertiesEditor {
		@Override
		public void edit(Context ctx, OrderImpl order, boolean isOldOrder) {
			CreateOrder.open(ctx, order, isOldOrder);
		}
	}

	@Override protected Class<? extends OrderImplBase<? extends Order>> orderImplType() { return OrderImplEx.class; }

	@Override
	protected void initChildFeature() {
		super.initChildFeature();

		Features.DDLV = true;
		Features.LOAD_FULL_PRICE = true;
		Features.WH_QTY = true;
	}

	@Override
	protected void initChildActivity() {
		super.initChildActivity();

		PriceCount.activity = PriceCountEx.class;
		Warehouse.activity = WarehouseEx.class;
		DocList.activity = DocListEx.class;
	}

	@Override
	protected void defineNewType() {
		super.defineNewType();

		AssortmentMatrixAdapter.PERIOD_IN_MONTH = 0;
		AssortmentMatrixAdapter.PERIOD_IN_DAY = 180;
		DbObject.regNewDataType(Price.class, PriceEx.class);
		DbObject.regNewDataType(Order.class, OrderEx.class);
		DbObject.regNewDataType(Agent.class, AgentsEx.class);

		DataObjectInfo doi = DataObjectInfo.getInstance();
		doi.replaceListType(Delivery.class, "items", DeliveryItemEx.class);
		doi.replaceListType(PriceEx.class, "whQty", PriceWhData.class);
		doi.replaceListType(OrderEx.class, "items", OrderItemEx.class);

		UpdateDB.addHitchingCtor(new HitchingCtor(){
			@Override
			public List<Hitching> createList() {
				Hitching[] h = new Hitching[] {
						new RcvNewHitching(Agent.class),
						new RcvNewHitching(MaxDiscounts.class, "MaxDiscounts"),
						new RcvNewHitching(OrgDiscount.class, "OrgDiscount"),
						new RcvNewHitching(Sklad.class),

						new Hitching(FBTransferReject.class),
						new ReqTransferHitching(),
						new FBTransferCommitHitching("TransferCommit"),
						new FBTransferCommitHitching("TransferDoc1c"),
				};
				return Arrays.asList(h);
			}
		}, UpdateDB.GEN_DATA_HITCHING);

		Main.docMenuPrepared.add(new MenuPrepareHitching() {
			@Override
			public void menuPrepared(List<MenuHandler> menu, final Activity activity) {
				MenuHandler mh = new MenuHandler(getString(R.string.fb_transfer), new Runnable() {
					@Override
					public void run() {
						FBTransferList.open(activity);
					}
				});
				menu.add(mh);
			}
		});

		DocFilterOnClickListener.HiddenTypes.add(FBTransferDoc.instance());
	}

	@Override
	protected void initChildDocTypes() {
		DocType.removeType(ReturnDoc.instance());
	}

	@Override
	public void onCreate() {
		ConfigManager.initConfig(new CfgNpl());
		OrderDocEx.init();

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
}
