package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

public class OrderItemEx extends OrderItem {
	public List<WhData> parts = new ArrayList<WhData>();

	public void updateQty() {
		qty = 0;
		for(WhData i : parts)
			qty += i.weight;
	}
}
