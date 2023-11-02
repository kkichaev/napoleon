package com.grsoft.napoleon;

import com.grsoft.database.DeliveryHitching;
import com.grsoft.database.Hitching;
import com.grsoft.database.PaymentHitching;

public class UpdateDBEx extends UpdateDB {
	
	@Override protected DeliveryHitching getDeliveryHitching() { return null; }
	
	@Override
	protected Hitching getPaymentHitching() {
		return new PaymentHitching();
	}
}
