package com.grsoft.dataobjects;

import java.util.Date;

public class DeliveryPrintEx extends DeliveryPrint 
implements IDelivery {
	public int sumDD = 0;

	@Override
	public int getSumD() {
		return sumDD;
	}

	@Override
	public Date getDate() {
		return date;
	}

	@Override
	public void setSumD(long sum) {
		sumDD = (int)sum;
	}
}
