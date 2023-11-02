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
import android.widget.CheckBox;

import java.util.Arrays;
import java.util.List;

import com.grsoft.database.Hitching;
import com.grsoft.database.HitchingCtor;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.Category;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.dataobjects.Goods;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgAction;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Payment;
import com.grsoft.dataobjects.PaymentEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.napoleon.documents.ActiveOrgActionsDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.GoodsAuditDoc;
import com.grsoft.napoleon.modules.CostManagerImpl;
import com.grsoft.network.ServerCommand;
import com.grsoft.util.PriceTextFilter;
import com.grsoft.util.ViewInitializer;

public class NapoleonApp extends NapoleonAppBase {
	@SuppressWarnings("unused")
	private static final String TAG = "NapoleonApp";
	
	class OrderEditor implements OrderImpl.PropertiesEditor {
		@Override
		public void edit(Context ctx, OrderImpl order, boolean isOldOrder) {
			CreateOrder.open(ctx, order, isOldOrder);
		}
	}
	
	@Override
	protected void defineNewType() {
		super.defineNewType();
		DbObject.regNewDataType(Order.class, OrderEx.class);
		DbObject.regNewDataType(Org.class, OrgEx.class);
		DbObject.regNewDataType(Payment.class, PaymentEx.class);
		DbObject.regNewDataType(Delivery.class, DeliveryEx.class);
		DbObject.regNewDataType(Price.class, PriceEx.class);
		
		DataObjectInfo.getInstance().replaceListType(OrderEx.class, "items", OrderItemEx.class);

		UpdateDB.initUI = new ViewInitializer(){
			   public void init(Activity activity) {
			    ((CheckBox) activity.findViewById(R.id.cbRemains)).setChecked(false);
			    ((CheckBox)activity.findViewById(R.id.cbCost)).setChecked(true);
			   };
		};
		
		
		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override
			public List<Hitching> createList() {
				return Arrays.asList(new Hitching[] {
					new RcvNewHitching(OrgAction.class, "OrgActions"),
					new RcvNewHitching(Goods.class, "Goods"),
					new RcvNewHitching(Category.class, "Category"),
				});
			}
		}, UpdateDB.GEN_DATA_HITCHING);
	}
	
	@Override
	protected void initChildDocTypes() {
		super.initChildDocTypes();
		DocType.addType(GoodsAuditDoc.instance());
		DocType.addType(ActiveOrgActionsDoc.instance());
	}
	
	@Override
	protected void initChildActivity() {
		super.initChildActivity();
		Warehouse.activity = WarehouseEx.class;
		PriceCount.activity = PriceCountEx.class;
		Documents.activity = DocumentsEx.class;
		OrderDetail.activity = OrderDetailEx.class;
	}
	
	@Override
	protected void initChildFeature() {
		super.initChildFeature();
		Features.COST_MANAGER = new CostManagerImpl();
		Features.ID_COLUMN_IN_PRICE_LIST = true;
		Features.COST_IN_PRESENTATION = true;

		PriceTextFilter.SRCH_ID_FLD = "article";
	}

	@Override
	public void onCreate() {
		super.onCreate();
		OrderImpl.OrderEditor = new OrderEditor();
		setProgrammVersion();
	}

	private void setProgrammVersion() {
		try{
			ServerCommand.ProgramVersion = getResources().getString(R.string.version);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
