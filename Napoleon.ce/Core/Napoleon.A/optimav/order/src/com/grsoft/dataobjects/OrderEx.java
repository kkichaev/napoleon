package com.grsoft.dataobjects;

import java.util.Date;

public class OrderEx extends Order implements IDelivery{
	public int sumD = 0;

	@Override
	public Date getDate() {
		return date;
	}

//	@Override
//	public Date getPayDate() {
//		// TODO Auto-generated method stub
//		return null;
//	}

	@Override
	public int getSumD() {
		return sumD;
	}

	@Override
	public void setSumD(long sum) {
		sumD = (int)sum;
	}
}
