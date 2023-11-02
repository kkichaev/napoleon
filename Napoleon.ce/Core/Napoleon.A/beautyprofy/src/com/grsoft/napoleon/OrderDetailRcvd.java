package com.grsoft.napoleon;

import android.content.Context;
import android.content.Intent;

import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DeliveryItem;
import com.grsoft.dataobjects.OrderReceived;
import com.grsoft.dataobjects.impl.DeliveryImpl;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;

public class OrderDetailRcvd extends OrderDeliveryDetail {

	static public void open(Context context, OrderImplEx order) {
		Intent i = new Intent(context, OrderDetailRcvd.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, order.getRowid());
		context.startActivity(i);		
	}
	
	@Override
	protected void init() {
		delivery = new DeliveryImpl();
		final Delivery d = delivery.getData();
	
		DataTraveler.travel(OrderReceived.class, new DataTraveler.Travel<OrderReceived>() {

			@Override
			public boolean travel(DataTraveler<OrderReceived> item) {
				DeliveryItem di = new DeliveryItem();
				di.id = item.data.id;
				di.qty = item.data.qty;
				di.sum = (int)((long)item.data.cost * item.data.qty / Consts.QTY_SCALE);
				d.items.add(di);
				
				return true;
			}
		}, "created=" + Long.toString(doc.getData().created.getTime()));
	}
}
