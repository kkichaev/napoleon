package com.grsoft.database;

import com.grsoft.dataobjects.OrderResult;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

public class OrderResultHitching extends Hitching {
	
	public static OrderResult data;
	
	public OrderResultHitching() {
		super(OrderResult.class, "OrderResult");
	}
	
	@Override
	public void onStart() {
		data = new OrderResult();
	}

	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		data = (OrderResult) rawObject.createDataObject(dataObject);
	}
	
	@Override
	public void onEnd() {
	}
}
