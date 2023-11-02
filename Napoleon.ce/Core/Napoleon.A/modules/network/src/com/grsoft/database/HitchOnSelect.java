package com.grsoft.database;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.network.exception.RuntimeException;

public class HitchOnSelect extends Hitching
{
	static final String SELECT_CMD = "SELECT";
	private String condition = "";
	boolean clearTable = false;
	
	public HitchOnSelect(Class<? extends DataObject> dataObject, String objectName)
	{
		super(dataObject, objectName);		
		setCommand(SELECT_CMD);
	}

	public HitchOnSelect(Class<? extends DataObject> dataObject, String objectName, String condition)
	{
		super(dataObject, objectName);		
		setCommand(SELECT_CMD);
		setCondition(condition);
	}

	public HitchOnSelect(Class<? extends DataObject> dataObject, String objectName, String condition, boolean clearTable)
	{
		super(dataObject, objectName);		
		setCommand(SELECT_CMD);
		setCondition(condition);
		this.clearTable = clearTable;
	}
	
	@Override
	public void prepareReading() {
		if(clearTable)
			DbWriter.dropTable(DataObjectInfo.getInstance().getTableName(dataObject));
	}
	
	
	@Override
	public String getParams() throws RuntimeException
	{
		StringBuilder result = new StringBuilder(super.getParams());
		result.append(':');
		result.append(getCondition());
		
		return result.toString();
	}

	public void setCondition(String condition)
	{
		this.condition = condition;
	}

	protected String getCondition()
	{
		return condition;
	}
}
