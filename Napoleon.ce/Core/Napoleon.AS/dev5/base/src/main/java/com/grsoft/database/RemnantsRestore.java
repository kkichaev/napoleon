package com.grsoft.database;

import java.util.Date;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.Remnants;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

public class RemnantsRestore extends DocumentRestore {
	
	Date checkDate = new Date(100, 1, 1);
	
	public RemnantsRestore() {
		super(RemnantsDoc.instance(), RemnantsDoc.instance().getObjectName());
	}
	
	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		DataObject dobj = rawObject.createDataObject(docType.dataType());
		Remnants r = (Remnants)dobj;
		if( r.created == null || r.created.compareTo(checkDate) < 0 )
			r.created = r.date;
		r.params  |= ParamState.ofExported;
		dbProxy.insertRecord(dobj);
	}
}
