package com.grsoft.database;

import com.grsoft.dataobjects.OrderProceeded;
import com.grsoft.dataobjects.OrderProceededEx;
import com.grsoft.dataobjects.ParamStateEx;

public class PODHitchingEx extends PODHitching {
	@Override
	protected int makeParams(OrderProceeded op) {
		int result =  super.makeParams(op);
		
		OrderProceededEx ox = (OrderProceededEx)op;
		
		if (ox.status == 1)
			result |= ParamStateEx.pending;
		else if (ox.status == 2)
			result |= ParamStateEx.approved;
		
		return result;
	}
}
