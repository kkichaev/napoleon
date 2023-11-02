package com.grsoft.ads.database;

import com.grsoft.ads.dataobjects.Price;
import com.grsoft.database.RcvNewHitching;

public class PriceHitching extends RcvNewHitching {

	public PriceHitching() {
		super(Price.class, "Warehouse");
	}

}
