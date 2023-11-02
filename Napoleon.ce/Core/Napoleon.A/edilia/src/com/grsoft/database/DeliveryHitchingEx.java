package com.grsoft.database;

import java.text.SimpleDateFormat;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

public class DeliveryHitchingEx extends DeliveryHitching {
	public static SimpleDateFormat sd = new SimpleDateFormat("/MM");
	
	public DeliveryHitchingEx() {
		super(true);
	}
	
	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		DataObject dobj = rawObject.createDataObject(dataObject);
		Delivery d = (Delivery)dobj;
		d.number += sd.format(d.date);
		dbProxy.insertRecord(dobj);
		postRead(dobj);
	}
}
