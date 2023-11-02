package com.grsoft.napoleon;

import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.OrderReserveImpl;
import com.grsoft.util.ExtrasConst;

import android.content.Context;
import android.content.Intent;
import android.widget.TextView;


public class OrderDeliveryDetailEx extends OrderDeliveryDetail {
	public static void openOrder(Context context, OrderImplEx doc) {
		Intent i = new Intent(context, OrderDeliveryDetailEx.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		context.startActivity(i);		
	}

	@Override
	protected void init() {
		delivery = new OrderReserveImpl();
		Delivery delData = delivery.getData();
		OrderEx ordData = (OrderEx) doc.getData();
		delData.id = ordData.id;
		delData.number = ordData.orderNumber;
		delivery.read();
		TextView tv = (TextView) findViewById(R.id.UnloadTitle);
		tv.setText("Резерв");
	}
}
