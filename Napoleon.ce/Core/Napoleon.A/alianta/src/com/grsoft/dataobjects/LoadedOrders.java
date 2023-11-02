package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.util.Consts;

@TableInfo(name="LoadedOrders", keyFields="created")
@ServerInfo(name="LoadedOrders")
public class LoadedOrders extends DataObject {
	public Date created;

	public List<LoadedOrderItem> items = new ArrayList<LoadedOrderItem>();

	public long sum() {
		long sum = 0;
		for(LoadedOrderItem i : items)
			sum += i.sum;
		return sum;
	}
	
	public boolean isEqualToOrder() {
		
		OrderImplEx oi = new OrderImplEx();
		if(!oi.read("created", created))
			return false;

		return isEqualToOrder(oi.getData());
	}
	
	public boolean isEqualToOrder(Order doc) {
		Map<String, ValueData> values = new HashMap<String, ValueData>();
		for(LoadedOrderItem i : items)
			values.put(i.id, new ValueData(i));
		
		for(OrderItem i : doc.items) {
			ValueData vd = values.get(i.id);
			if(vd == null || !vd.isSame(i)) {
				break;
			}
			values.remove(i.id);
		}
			
		return (values.size() == 0);
		
	}
	
	public static Map<Date, LoadedOrders> get(String where) {
		final Map<Date, LoadedOrders> data = new HashMap<Date, LoadedOrders>();
		DataTraveler.travel(LoadedOrders.class, new DataTraveler.Travel<LoadedOrders>(true) {

			@Override
			public boolean travel(DataTraveler<LoadedOrders> item) {
				data.put(item.data.created, item.data);
				return true;
			}
		}, where);
		return data;
	}
}

class ValueData {
	int qty;
	int sum;
	
	public ValueData(LoadedOrderItem i) {
		qty =i.qty;
		sum = i.sum;
	}
	
	public boolean isSame(OrderItem oi) {
		if(qty != oi.qty)
			return false;
		
		int osum = (int)((long)oi.qty * oi.cost / Consts.QTY_SCALE);
		return sum == osum;
	}
}
