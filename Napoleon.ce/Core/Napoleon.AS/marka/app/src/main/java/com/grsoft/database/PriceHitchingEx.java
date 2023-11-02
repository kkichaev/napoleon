package com.grsoft.database;

import com.grsoft.dataobjects.Price;

public class PriceHitchingEx extends PriceHitching {
	
	@Override
	protected void beforeInsert(Price dobj) {
		super.beforeInsert(dobj);
//		PricePrintEx pe = (PricePrintEx)dobj;
//		pe.vanQty = pe.partyQty();
//		Collections.sort(pe.party);
	}
}
