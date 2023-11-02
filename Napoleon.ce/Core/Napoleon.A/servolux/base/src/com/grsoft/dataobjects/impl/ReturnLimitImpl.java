package com.grsoft.dataobjects.impl;

import java.util.Date;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.ReturnLimit;
import com.grsoft.util.Util;

public class ReturnLimitImpl extends DbObject<ReturnLimit> {
	
	public static ReturnLimit getLimit(Date date, String priceType) {
		RetLimitFinder ret = new RetLimitFinder();
		String sdate = Long.toString(Util.getDayStart(date).getTime());
		DataTraveler.travel(ReturnLimit.class, ret, "start<=" + sdate + " and end >= " + sdate + " and priceType='" + priceType + "'");
		return ret.limit;
	}
}

class RetLimitFinder extends DataTraveler.Travel<ReturnLimit> {
	public ReturnLimit limit = null;
	
	public RetLimitFinder() {}
	
	@Override
	public boolean travel(DataTraveler<ReturnLimit> item) {
		limit = item.data;
		return false;
	}
}
