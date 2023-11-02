package com.grsoft.database;

import com.grsoft.dataobjects.Price;
import com.grsoft.napoleon.PriceHelper;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

public class PriceRcvr extends FullPrice {

	public PriceRcvr() {
		super();
		PriceHelper.clear();
	}
	
	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		Price dobj = (Price)rawObject.createDataObject(dataObject);
		dobj.srchName = dobj.name.toUpperCase();
		dbProxy.insertRecord(dobj);
		PriceHelper.put(dobj.id);
	}
	
	@Override
	public void onEnd() {
		PriceHelper.save();
		super.onEnd();
	}
}
