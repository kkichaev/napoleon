package com.grsoft.database;
import com.grsoft.aceteam.R;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.network.ObjectListener;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

public class ObjectDefImplListener implements ObjectListener
{
	final int COMMIT_INTERVAL = 500;
	DbWriter dbProxy = new DbWriter();
	Class<? extends DataObject> dataObjectClass;
	
	public ObjectDefImplListener(Class<? extends DataObject> dataObjectClass)
	{
		this.dataObjectClass = dataObjectClass;
	}
	
	@Override
	public void onRead(RawObject rawObject) throws RuntimeException
	{
		DataObject dataObject = rawObject.createDataObject(dataObjectClass);
		dbProxy.insertRecord(dataObject);
	}
	
	@Override
	public void onSave() {
	}

	@Override
	public void onEnd()
	{
		dbProxy.endProcess();
	}

	@Override
	public void onStart()
	{
		dbProxy.startProcess(COMMIT_INTERVAL);
	}

	@Override
	public String getObjectName() {	return "";	}
}
