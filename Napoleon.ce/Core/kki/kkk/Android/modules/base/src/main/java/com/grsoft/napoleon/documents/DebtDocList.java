package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.DeliveryImpl;
import com.grsoft.dataobjects.impl.PaymentImpl;

/**
 * в ids если id < 0 значит payment иначе delivery
 * @author 1111
 *
 */
public class DebtDocList extends BaseDebtDocList {

	public static Class<? extends DeliveryImpl> DeliveryType = BalanceDelivery.class;

	public DebtDocList(String where, String order, boolean loadDelivery) {
		super(where, order, loadDelivery);
	}
	
	 
	protected DebtDocList() {}

	@Override protected Class<? extends Document<?>> getDeliveryType() { return DeliveryType;	}

	@Override protected Class<? extends Document<?>> getPaymentType() { return PaymentImpl.class;	}
}