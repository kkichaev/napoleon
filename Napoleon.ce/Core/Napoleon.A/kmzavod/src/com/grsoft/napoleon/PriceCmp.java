package com.grsoft.napoleon;

import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.PriceImpl;

public class PriceCmp {
	public int compare(String x, String y) {
		PriceImpl px = new PriceImpl();
		px.read("id", x);
		
		PriceImpl py = new PriceImpl();
		py.read("id", y);
		
		int l = ((PriceEx)px.getData()).type;
		int r = ((PriceEx)py.getData()).type;
		
		if(l == r)
			return ((PriceEx)px.getData()).name.compareTo(((PriceEx)py.getData()).name);
		
		return l > r ? 1 : -1;
	}
}
