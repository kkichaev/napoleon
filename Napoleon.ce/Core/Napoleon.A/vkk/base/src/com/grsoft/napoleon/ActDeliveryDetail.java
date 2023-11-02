package com.grsoft.napoleon;

import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.impl.ActDeliveryImpl;
import com.grsoft.dataobjects.impl.DeliveryImplBase;

public class ActDeliveryDetail extends DeliveryDetail {
	@Override
	DeliveryImplBase<? extends Delivery> createDelivery() {
		return new ActDeliveryImpl();
	}
}
