package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.Price;

public class OrderImplEx extends OrderImpl {
	public int getItem2Value(Price item) {
		int res = super.getItemValue(item);
		
		int whIndex = data.supplyer;
		if(whIndex > item.whQty.size())
			whIndex = item.whQty.size();
		
		for (int i = 0; i < item.whQty.size(); i++)
			if (i + 1 != whIndex)
				res = item.whQty.get(whIndex).qty;
		
		return res;
	}
}
