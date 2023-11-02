package com.grsoft.dataobjects;

import java.util.Date;

public class SalesEx extends Sales implements IDelivery {
	public int printCount;
	public int sumD = 0;
	@Override
	
	public Date getDate() {
		return created;
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
