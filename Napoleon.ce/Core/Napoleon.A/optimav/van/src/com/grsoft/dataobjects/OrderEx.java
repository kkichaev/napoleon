package com.grsoft.dataobjects;

import java.util.Date;

public class OrderEx extends OrderPrint implements IDelivery{
	public int sumD = 0;

	@Override
	public Date getDate() {
		return date;
	}

	@Override
	public int getSumD() {
		return sumD;
	}

	@Override
	public void setSumD(long sum) {
		sumD = (int)sum;
	}
}
