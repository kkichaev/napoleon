package com.grsoft.database;

import java.util.Collections;

import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PricePrintEx;

public class PriceHitchingEx extends PriceHitching {
	
	@Override
	protected void beforeInsert(Price dobj) {
		super.beforeInsert(dobj);
		PricePrintEx pe = (PricePrintEx)dobj;
		pe.vanQty = pe.partyQty();
		Collections.sort(pe.party);
	}
}
