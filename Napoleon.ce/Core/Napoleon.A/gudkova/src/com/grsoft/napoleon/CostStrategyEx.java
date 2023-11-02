package com.grsoft.napoleon;

import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;

public class CostStrategyEx extends CostStrategy {
	@Override
	public int getItemCost(Price p, Document<?> doc) {
		int result = super.getItemCost(p, doc);
		
		PriceEx pe = (PriceEx)p;
		int iq = pe.itemQty;
		if( iq > Consts.QTY_SCALE) {
			result = (int)((long)result * Consts.QTY_SCALE / iq);
		}
		
		return result;
	}
}
