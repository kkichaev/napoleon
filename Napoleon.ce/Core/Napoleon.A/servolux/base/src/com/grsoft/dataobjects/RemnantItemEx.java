package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.types.FieldOrder;

public class RemnantItemEx extends RemnantItem {
	@FieldOrder(order = 10)
	public String uid = "";
	
	@FieldOrder(order = 11)
	public List<RmntSalesPlaceQty> items = new ArrayList<RmntSalesPlaceQty>();

	public int countQty() {
		int qty = 0;
		for(RmntSalesPlaceQty rq : items)
			qty += rq.qty;
		return qty;
	}
}
