package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.OrderReserv;
import android.content.Context;

public class OrderReserveImpl extends DeliveryImplBase<OrderReserv> {

	@Override public void open(Context context) {}

	@Override public long sum() { return data.sum(); }
}
