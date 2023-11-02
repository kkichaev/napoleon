package com.grsoft.dataobjects;

import java.util.HashMap;

import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.util.Consts;

public class OrderEx extends Order {
	public String priceType;
	public String whRemark;
	public String otherRemark;

	public int outOrder;
	public String outOrderRemark = "";
	
	public int docPacket = 0;
	
	public HashMap<String, Integer> weightByGroup() {
		HashMap<String, Integer> orderWeight = new HashMap<String, Integer>();

		PriceImpl pi = new PriceImpl();
		PriceEx p = (PriceEx)pi.getData();

		for(OrderItem oi : items) {
			p.id = oi.id;
			pi.read();
			
			Integer val = orderWeight.get(p.itemGroup);
			if(val == null)
				val = 0;
			int cw = (int)((long)oi.qty * p.weight / Consts.QTY_SCALE);
			orderWeight.put(p.itemGroup, val + cw);
		}
		pi.close();
		
		return orderWeight;
	}
}
