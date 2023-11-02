package com.grsoft.ads.database;

import com.grsoft.ads.dataobjects.WorkType;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

public class WorkTypeHitching extends RcvNewHitching {

	public WorkTypeHitching() {
		super(WorkType.class, "WorkType");
	}
	
	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		// TODO Auto-generated method stub
		super.onRead(rawObject);
	}

}
