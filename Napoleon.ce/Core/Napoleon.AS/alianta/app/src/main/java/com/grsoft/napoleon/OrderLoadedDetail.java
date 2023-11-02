package com.grsoft.napoleon;

import java.util.Date;

import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DeliveryItem;
import com.grsoft.dataobjects.LoadedOrderItem;
import com.grsoft.dataobjects.LoadedOrders;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.impl.DeliveryImpl;
import com.grsoft.dataobjects.impl.LoadedOrdersImpl;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.util.ExtrasConst;
import android.content.Context;
import android.content.Intent;

public class OrderLoadedDetail extends OrderDeliveryDetailEx {
	public static void openDoc(Context context, OrderImplEx doc) {
		Intent i = new Intent(context, OrderLoadedDetail.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		context.startActivity(i);
	}
	
	@Override
	protected void init() {
		//delivery = new DeliveryImpl();
		//Delivery delData = delivery.getData();
		Order ordData = doc.getData();
		
		LoadedOrdersImpl li = new LoadedOrdersImpl();
		LoadedOrders doc = li.getData();
		doc.created = ordData.created;
		li.read();
		li.close();
		
		//delData.created = ordData.created;
		//delData.payDate = new Date();
		for(LoadedOrderItem i : doc.items) {
			DeliveryItem di = new DeliveryItem();
			di.id = i.id;
			di.qty = i.qty;
			di.sum = i.sum;
//			delData.items.add(di);
			items.add(di);
		}
	}
}
