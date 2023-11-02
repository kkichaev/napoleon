package com.grsoft.napoleon;

import com.grsoft.database.DeliveryHitching;
import com.grsoft.database.DeliveryHitchingEx;

public class UpdateDBEx extends UpdateDB {
	protected DeliveryHitching getDeliveryHitching() {
		return new DeliveryHitchingEx();
	}
}
