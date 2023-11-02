package com.grsoft.dataobjects;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class DeliveryEx extends Delivery {
//	public Date payDate;
	
	@Scale(value=Consts.SUM_SCALE)
	public int sumRet;

	@Scale(value=Consts.SUM_SCALE)
	public int sumPay;
}
