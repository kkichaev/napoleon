package com.grsoft.dataobjects.impl;

import java.util.HashSet;
import java.util.Set;

import com.grsoft.dataobjects.OrderItem;

public class OrderImplEx extends OrderImpl {
	public int SkuCount() {
		Set<String> set = new HashSet<String>();
		
		for(OrderItem i : data.items)
			set.add(i.id);
		
		return set.size();
	}
}
