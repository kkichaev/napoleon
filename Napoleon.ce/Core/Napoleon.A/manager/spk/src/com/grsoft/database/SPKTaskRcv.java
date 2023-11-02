package com.grsoft.database;

import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.SPKTask;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

public class SPKTaskRcv extends Hitching {

	public SPKTaskRcv() {
		super(SPKTask.class, "SPKTaskRCV");
	}
	
	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		SPKTask dobj = (SPKTask) rawObject.createDataObject(dataObject);
		dobj.params = ParamState.ofExported;
		dbProxy.insertRecord(dobj);
	}

}
