package com.grsoft.dataobjects;

import java.util.Date;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class OrgDiscount extends DataObject {
	
	static final long MIN_DATE = 3 * (24 * 3600 * 1000); // 03 Jan 1970
	
	@FieldOrder(order = 0)
	public Date start = null;
	
	@FieldOrder(order = 1)
	public Date finish = null;
	
	@FieldOrder(order = 2)
	public String id = "";
	
	@Scale(value=Consts.SUM_SCALE)
	@FieldOrder(order = 3)
	public int discount = 0;
	
	boolean cmpDate(Date bound, long docDate, boolean cmpStart) { 
		if(bound == null)
			return true;
		long bt = bound.getTime();
		return (bt < MIN_DATE || (cmpStart && bt <= docDate) || (!cmpStart && bt >= docDate)); 
	}
	
	public boolean match(Date docDate) {
		long chkDate = Util.getDayStart(docDate).getTime();
		return cmpDate(start, chkDate, true) && cmpDate(finish, chkDate, false);
	}
}
