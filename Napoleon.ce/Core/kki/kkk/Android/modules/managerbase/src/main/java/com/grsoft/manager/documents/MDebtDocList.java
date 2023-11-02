package com.grsoft.manager.documents;

import com.grsoft.dataobjects.impl.MBalanceDeliveryImpl;
import com.grsoft.dataobjects.impl.MPaymentImpl;
import com.grsoft.napoleon.documents.BaseDebtDocList;
import com.grsoft.napoleon.documents.Document;


public class MDebtDocList extends BaseDebtDocList {
	public MDebtDocList(String where, String order, boolean loadDelivery) {
		super(where, order, loadDelivery);
	}
	
	@Override protected Class<? extends Document<?>> getDeliveryType() { return MBalanceDeliveryImpl.class; }
	@Override protected Class<? extends Document<?>> getPaymentType() { return MPaymentImpl.class; }
}
