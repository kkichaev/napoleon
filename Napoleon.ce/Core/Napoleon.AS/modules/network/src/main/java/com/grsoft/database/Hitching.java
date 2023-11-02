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
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.network.ObjectListener;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

public class Hitching implements CommandArgs, ObjectListener
{
	final int COMMIT_INTERVAL = 500;
	protected DbWriter dbProxy = new DbWriter();

	protected Class<? extends DataObject> dataObject;
	protected String selectCMD = "GET";
	protected String objectName;
	private String userid = null;
	
	public Hitching(Class<? extends DataObject> type){
		this(type, DataObjectInfo.getInstance().getSrvName(type));
	}
	
	public Hitching(Class<? extends DataObject> dataObject, String objectName) {
		this(dataObject, objectName, false);
	}

	public Hitching(Class<? extends DataObject> dataObject, String objectName, boolean rcvOnlyNew) {
		this.dataObject = DbObject.getDataType(dataObject);
		this.objectName = objectName;
		if(rcvOnlyNew)
			dbProxy.setUpsert(false);
	}
	
	public String getCommand() { return userid == null ? selectCMD : selectCMD + " AS '" + userid + "'"; }
	
	protected void setCommand(String val) { selectCMD = val; }
	public void impersonate(String userid){
		this.userid = userid;
	}
	
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
		postRead(dobj);
	}
	
	protected void postRead(DataObject dobj) {}
	
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
