package com.grsoft.database;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

public class DDHitchingEx extends DayDeliveryHitching {
	
	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		DataObject dobj = rawObject.createDataObject(dataObject);
		Delivery d = (Delivery)dobj;
		d.number += DeliveryHitchingEx.sd.format(d.date);
		dbProxy.insertRecord(dobj);
		postRead(dobj);
	}
}
