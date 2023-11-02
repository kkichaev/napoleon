package com.grsoft.dataobjects.impl;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderReceived;

public class OrderReceivedImpl extends DbObject<OrderReceived> {
	public static boolean haveData(Order o) {
		
		final List<OrderReceived> res = new ArrayList<OrderReceived>();
		
		DataTraveler.travel(OrderReceived.class, new DataTraveler.Travel<OrderReceived>() {

			@Override
			public boolean travel(DataTraveler<OrderReceived> item) {
				res.add(item.data);
				return false;
			}
		}, "created=" + Long.toString(o.created.getTime()));
		
		return res.size() > 0;
	}
}
