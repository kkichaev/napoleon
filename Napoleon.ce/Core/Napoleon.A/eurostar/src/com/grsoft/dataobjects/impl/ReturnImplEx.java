package com.grsoft.dataobjects.impl;

import java.util.List;

import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.PriceQtyItem;
import com.grsoft.dataobjects.ReturnEx;

public class ReturnImplEx extends ReturnImpl {
	int whIndex = -1; 

	public int getWhIndex() {
		if( whIndex == -1 ) 
			whIndex = OrderImplEx.getWhIndex(((ReturnEx)data).whCode);
		return whIndex;
	}
	
	@Override
	public int getItemValue(Price item) {
		if( whIndex == -1 ) 
			getWhIndex();

		List<PriceQtyItem> whQty = ((PriceEx)item).whQty;
		return ( whIndex == 0 || whIndex > whQty.size() ) ?  item.qty : whQty.get(whIndex-1).qty;
	}
}
