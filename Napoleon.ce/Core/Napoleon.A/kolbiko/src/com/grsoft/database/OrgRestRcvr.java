package com.grsoft.database;

import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.Remnants;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

public class OrgRestRcvr extends Hitching {
	public OrgRestRcvr() { super(Remnants.class, "OrgRmntsSend"); }
	
	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		Remnants dobj = (Remnants) rawObject.createDataObject(dataObject);
		dobj.params |= (ParamState.ofExported);
		dbProxy.insertRecord(dobj);
	}
}
