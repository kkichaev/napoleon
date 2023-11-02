package com.grsoft.database;

import java.util.Date;

import com.grsoft.dataobjects.Gather;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;
import com.grsoft.util.Util;

public class RcvGather extends Hitching {
	
	long created;
	
	public RcvGather() {
		super(Gather.class, "Gather");
		created = Util.getDateTime().getTime();
	}
	
	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		Gather dobj = (Gather)rawObject.createDataObject(dataObject);
		
		dobj.created = new Date(created);
		dobj.date = dobj.created;
		created++;
		
		dbProxy.insertRecord(dobj);
	}
}
