package com.grsoft.napoleon;

import com.grsoft.database.Hitching;
import com.grsoft.dataobjects.Report;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;


public class ReportHitching extends Hitching {

	public ReportHitching() {
		super(Report.class);
	}
	
	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		// TODO Auto-generated method stub
		super.onRead(rawObject);
	}

}
