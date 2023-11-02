/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Связывает объекты DataObject с событиями.
 *
 * kki   12/10/2010   creating
 */

package com.grsoft.database;

import com.grsoft.dataobjects.CommandArgs;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.network.ObjectListener;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

public class Hitching implements CommandArgs, ObjectListener
{
	final int COMMIT_INTERVAL = 500;
	protected DbWriter dbProxy = new DbWriter();

	protected Class<? extends DataObject> dataObject;
	private String selectCMD = "GET";
	private String objectName;
	
	public Hitching(Class<? extends DataObject> dataObject, String objectName)
	{
		this.dataObject = DbObject.getDataType(dataObject);
		this.objectName = objectName;
	}
	
	public String getCommand() { return selectCMD; }
	
	protected void setCommand(String val) { selectCMD = val; }
	
	public String getParams() throws RuntimeException { return getObjectName();	}
	
	public DataObject createDataObject() throws RuntimeException
	{
		try
		{
			return dataObject.newInstance();
		}
		catch( Exception exception)
		{
			throw new RuntimeException(exception);
		}
	}
	
	public String getObjectName() { return objectName; }

	@Override
	public void onRead(RawObject rawObject) throws RuntimeException
	{
		DataObject dobj = rawObject.createDataObject(dataObject);
		dbProxy.insertRecord(dobj);
	}
	
	@Override
	public void onSave() { }
	
	public Class<? extends DataObject> getDataObjectClass() { return dataObject; }
	
	@Override
	public void onEnd() { 
		dbProxy.endProcess();
		dbProxy.close(); 
	}
	
	@Override
	public void onStart()
	{
		DbWriter.checkDBTable(dataObject);
		dbProxy.startProcess(COMMIT_INTERVAL);
	}

	public void prepareReading() {
	}
}
