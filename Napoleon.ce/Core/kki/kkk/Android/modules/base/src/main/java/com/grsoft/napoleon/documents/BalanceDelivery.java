package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.DeliveryImpl;

public class BalanceDelivery extends DeliveryImpl {
	public BalanceDelivery() { super(); }
	
	@Override
	public long sum() { return data.sumD; }		

}
