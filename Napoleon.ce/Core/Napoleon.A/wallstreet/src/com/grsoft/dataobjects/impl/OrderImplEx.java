package com.grsoft.dataobjects.impl;

import android.content.Context;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.napoleon.OrderDeliveryDetail;
import com.grsoft.napoleon.OrderDeliveryDetailEx;
import com.grsoft.napoleon.OrderDetail;

public class OrderImplEx extends OrderImpl {
	@Override
	public void open(Context context) {
		if (data.number.length() == 0) {
			if( ((OrderEx)data).orderNumber.length() == 0 ) 
				OrderDetail.open(context, this);
			else
				OrderDeliveryDetailEx.openOrder(context, this);
		} else
			OrderDeliveryDetail.open(context, this);
	}
}
