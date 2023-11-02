package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.types.FieldOrder;

public class PartsData extends DataObject {
	@FieldOrder(order = 0)
	public List<WhData> items = new ArrayList<WhData>();
	
	public int totalQty() {
		int qty = 0;
		for(WhData i : items)
			qty += i.weight;
		return qty;
	}

	public void remove(int qty) {
		for(WhData i : items)
			if(i.weight == qty){
				items.remove(i);
				break;
			}
	}
}
