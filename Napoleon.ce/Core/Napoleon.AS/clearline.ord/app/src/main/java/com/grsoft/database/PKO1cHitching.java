package com.grsoft.database;

import com.grsoft.dataobjects.PKO1c;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

public class PKO1cHitching extends RcvNewHitching {
	public PKO1cHitching() {
		super(PKO1c.class, "PKO1c");
	}
	
	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		PKO1c dobj = (PKO1c) rawObject.createDataObject(dataObject);
		dobj.created = dobj.date;
		dbProxy.insertRecord(dobj);
	}
}
