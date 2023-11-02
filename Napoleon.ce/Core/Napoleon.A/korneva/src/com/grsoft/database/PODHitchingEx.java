package com.grsoft.database;

import com.grsoft.dataobjects.OrderProceeded;
import com.grsoft.dataobjects.OrderProceededEx;


public class PODHitchingEx extends PODHitching {
	
	@Override
	protected ProceededDocHandler createHandler() {
		return new Handler();
	}
}

class Handler extends ProceededDocHandler {
	@Override
	protected String getParamStmt() {
		return "' SET params = ?, podRemark = ? WHERE created = ?";
	}
	
	@Override
	public void handle(OrderProceeded proceeded, int param) {
		
		if(((OrderProceededEx)proceeded).approved == 1)
			param |= OrderProceededEx.APPROVED;
		super.handle(proceeded, param);
	}
}
