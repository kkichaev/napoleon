package com.grsoft.database;

import com.grsoft.dataobjects.ReturnChekBack;
import com.grsoft.dataobjects.impl.ReturnChekBackImpl;
import com.grsoft.napoleon.documents.ReturnChekBackDoc;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

public class ReturnChekRestore extends DocumentRestore {
	ReturnChekBackImpl doc = new ReturnChekBackImpl();
	
	public ReturnChekRestore() {
		super(ReturnChekBackDoc.instance(), "ReturnChekBack");
	}
	
	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		ReturnChekBack rc = (ReturnChekBack)rawObject.createDataObject(dataObject);
		ReturnChekBack src = doc.getData();
		src.created = rc.created;
		if( doc.read() )
			return;
		rc.handleChanged = rc.created;
		dbProxy.insertRecord(rc);
	}
	
	@Override
	public void onEnd() {
		super.onEnd();
		doc.close();
	}
}
