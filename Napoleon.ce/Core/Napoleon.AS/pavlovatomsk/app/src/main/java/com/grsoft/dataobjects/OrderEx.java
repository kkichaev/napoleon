package com.grsoft.dataobjects;

import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.ReqOrderDoc;

public class OrderEx extends Order{
	public int bonus = 0;

	public boolean needDecision() {
		for(OrderItem oi : items) {
			OrderItemEx oid = (OrderItemEx) oi;
			if(oid.discount > 0 && oid.actionGift == 0)
				return true;
		}
		return false;
	}

	public String objName() {
		return needDecision() ? ReqOrderDoc.instance().getObjectName() :
				OrderDoc.instance().getObjectName();
	}

	public int discountDecision = 0;
}
