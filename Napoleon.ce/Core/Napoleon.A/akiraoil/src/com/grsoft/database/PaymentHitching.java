package com.grsoft.database;

import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.PaymentEx;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

public class PaymentHitching extends RcvNewHitching {
	DbReader r = new DbReader();
	OrgEx o = new OrgEx();
	
	public PaymentHitching() {
		super(PaymentEx.class, "Payment");
	}
	
	@Override
	public void onEnd() {
		super.onEnd();
		r.close();
	}
	
	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		PaymentEx dobj = (PaymentEx) rawObject.createDataObject(dataObject);
		
		boolean bdo = r.select(o, o.getTableName(), "agreeId='" + dobj.agreeId + "'");
		while(bdo) {
			dobj.id = o.id;
			dbProxy.insertRecord(dobj);
			bdo = r.selectNext(o);
		}
	}
}
