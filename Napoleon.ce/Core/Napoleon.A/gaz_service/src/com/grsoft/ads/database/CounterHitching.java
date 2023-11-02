package com.grsoft.ads.database;

import com.grsoft.ads.dataobjects.Counter;
import com.grsoft.database.RcvNewHitching;

public class CounterHitching extends RcvNewHitching{

	public CounterHitching() {
		super(Counter.class, "Counter");
	}

}
