package com.grsoft.database;

import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.ReturnRequest;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

public class ReturnAcceptHitching extends Hitching {
	public ReturnAcceptHitching() {
		super(ReturnRequest.class, "ReturnAccepted");
	}
	
	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		ReturnRequest dobj = (ReturnRequest) rawObject.createDataObject(dataObject);
		dobj.params |= (ParamState.ofExported | ParamState.ofProceeded);
		dbProxy.insertRecord(dobj);
		postRead(dobj);
	}
}
