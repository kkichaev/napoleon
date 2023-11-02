package com.grsoft.database;

import java.util.Collections;

import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;

public class PriceHitchingEx extends PriceHitching {
	
	@Override
	protected void beforeInsert(Price dobj) {
		super.beforeInsert(dobj);
		PriceEx pe = (PriceEx)dobj;
		pe.vanQty = pe.partyQty();
		Collections.sort(pe.party);
	}
}
