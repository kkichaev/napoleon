/*
 * Copyright (C), 2011, Гильдия Разработчиков
 * 
 * Демо программа (с базой данных)
 *
 * kki   16/05/2011   creating
 */

package com.grsoft.napoleon;

import java.util.Calendar;
import java.util.List;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.dataobjects.Folder;
import com.grsoft.dataobjects.FolderEx;
import com.grsoft.dataobjects.Incass;
import com.grsoft.dataobjects.IncassEx;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Payment;
import com.grsoft.dataobjects.PaymentEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.RemnantsImpl;
import com.grsoft.dataobjects.impl.RemnantsImplEx;
import com.grsoft.napoleon.documents.DebtDocEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.ExchangeDoc;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.network.ServerCommand;
import com.grsoft.util.AssortmentMatrixAdapter;
import com.grsoft.util.MenuHandler;
import com.grsoft.util.MenuPrepareHitching;
import android.app.Activity;
import android.content.Context;

public class NapoleonApp extends NapoleonAppBase {
	@SuppressWarnings("unused")
	private static final String TAG = "NapoleonApp";
	
	class OrderEditor implements OrderImpl.PropertiesEditor {
		@Override
		public void edit(Context ctx, OrderImpl order, boolean isOldOrder) {
			if (!isOldOrder){
				Calendar c = Calendar.getInstance();
				Order ord = order.getData(); 
				c.setTime(ord.date);
				c.add(Calendar.DATE, 1);
				ord.date = c.getTime();
				order.write();
				order.close();
			}

			CreateOrder.open(ctx, order, isOldOrder);
		}
	}
	
	@Override
	protected void defineNewType() {
		DebtDocEx.initialize();

		DbObject.regNewDataType(Incass.class, IncassEx.class);
		DbObject.regNewDataType(Order.class, OrderEx.class);
		DbObject.regNewDataType(Delivery.class, DeliveryEx.class);
		DbObject.regNewDataType(Payment.class, PaymentEx.class);
		DbObject.regNewDataType(Org.class, OrgEx.class);
		DbObject.regNewDataType(Price.class, PriceEx.class);
		DbObject.regNewDataType(Folder.class, FolderEx.class);
	
		DataObjectInfo doi = DataObjectInfo.getInstance(); 
		doi.replaceListType(OrderEx.class, "items", OrderItemEx.class);
		doi.replacePrimaryKey(Payment.class, "id,number,payType");
		
		Napoleon.docMenuPrepared.add(new MenuPrepareHitching() {
			
			@Override
			public void menuPrepared(List<MenuHandler> menu, final Activity activity) {
				menu.add(new MenuHandler("Иформация о возвратах",
					new Runnable() {  @Override public void run() { ReturnInfo.open(activity); } }));
			}
		});
	}
	
	@Override protected Class<? extends RemnantsImpl> remantsImplType() { return RemnantsImplEx.class; }
	
	@Override
	protected void initChildDocTypes() {
		DocType.addType(ReturnDoc.instance());
		DocType.addType(ExchangeDoc.instance());
	}
	
	@Override
	protected void initFeatures() {
		super.initFeatures();
	
		Features.SALES_FROM_ORDERS = false;
		Features.PUT_REST_BEFORE_QTY = true;
		Features.ASSORTMENT_MATRIX = true;
		Features.NOT_ZERO_DATA_FOR_COPLEX_HISTORY = true;
		Features.REST_IN_PACK = true;
		Features.SHOW_DAILY_WEIGHT_IN_WAREHOUSE = true;
		
		AssortmentMatrixAdapter.PERIOD_IN_MONTH = 2;
	}
	
	@Override
	protected void initChildActivity() {
		IncassEdit.activity = IncassEditEx.class;
		Documents.activity = DocumentsEx.class;
		UpdateDB.activity = UpdateDBEx.class;
		PriceCount.activity = PriceCountEx.class;
		Warehouse.activity = WarehouseNewEx.class;
		OrderDetail.activity = OrderDetailEx.class;
		VisitEdit.activity = VisitEditEx.class;
		RemnantsDetail.activity = RemnantsDetailEx.class;
		ReturnDetail.activity = ReturnDetailEx.class;
		QuestAnswer.QuestAnswerActivity = QuestAnswerEx.class;
		DocList.activity = DocListEx.class;
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
