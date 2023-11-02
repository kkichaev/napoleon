package com.grsoft.database;

import com.grsoft.dataobjects.RequestChek;
import com.grsoft.dataobjects.impl.RequestChekImpl;
import com.grsoft.napoleon.documents.RequestCheckDoc;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

public class RequestChekRestore extends DocumentRestore {
	RequestChekImpl doc = new RequestChekImpl();
	
	public RequestChekRestore() {
		super(RequestCheckDoc.instance(), "RequestChek");
	}
	
	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		RequestChek rc = (RequestChek) rawObject.createDataObject(dataObject);
		RequestChek src = doc.getData();
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
