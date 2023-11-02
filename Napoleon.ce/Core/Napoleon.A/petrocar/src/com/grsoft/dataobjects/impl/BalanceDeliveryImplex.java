package com.grsoft.dataobjects.impl;

import java.util.Date;

import com.grsoft.napoleon.documents.BalanceDelivery;

public class BalanceDeliveryImplex extends BalanceDelivery {
	@Override
	public long sum() {
		Date curDate = new Date();
		return data.payDate.compareTo(curDate) < 0 ? data.sumD : 0;
	}
}
