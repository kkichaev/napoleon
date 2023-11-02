package com.grsoft.database;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.network.ObjectExportListener;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

public class DataObjectSendHitching implements ObjectExportListener {

	DataObject doc;
	String objName;
	public DataObjectSendHitching(DataObject doc, String objName) {
		this.doc = doc;
		this.objName = objName;
	}
	
	@Override public void onStart() {	}
	@Override public void onRead(RawObject rawObject) throws RuntimeException { }
	@Override public void onSave() { }
	@Override public void onEnd() {}

	@Override
	public String getObjectName() { return objName; }

	@Override public int size() { return 1; }

	@Override
	public DataObject get(int i) { 
		if(i != 0)
			return null;
		
		return doc;
	}
}
