/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Курсор
 *
 * kki   26/10/2010   creating
 */
package com.grsoft.dataobjects.impl;

import java.util.List;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;

public class Cursor<T extends DataObject>
{
	private List<Long> ids;
	private int pos;
	private DbObject<T> dataObjectImpl;
	private String condition;
	private String orderby;
	
	public Cursor(DbObject<T> dataObject)
	{
		this(dataObject, "");
	}
	
	public Cursor(DbObject<T> dataObject, String condition)
	{
		this(dataObject, condition, "");
	}
	
	public Cursor(DbObject<T> dataObject, String condition, String orderby)
	{
		this.dataObjectImpl = dataObject;
		this.condition = condition;
		this.orderby = orderby;
		
		updateIds();
	}
	
	public void close() {
		if( dataObjectImpl != null )
			dataObjectImpl.close();
	}
	
	public boolean moveNext()
	{
		if(pos + 1 < ids.size())
		{
			pos++;
			return dataObjectImpl.read(ids.get(pos));
		}
		
		return false;
	}
	
	public DbObject<T> get(int cpos){
		return get(cpos, true);
	}
	
	public DbObject<T> get(int cpos, boolean useCache){
		if (cpos < 0 || cpos >= ids.size())
			return null;
		
		boolean res = dataObjectImpl.read(ids.get(cpos), useCache);
		return (res) ? dataObjectImpl : null; 
	}
	
	public long getItemId(int cpos) {
		return (cpos < 0 || cpos >= ids.size()) ? 0 :ids.get(cpos);
	}
	
	public int getCount()
	{
		return ids.size();
	}
	
	public void updateIds()
	{
		String table = DataObjectInfo.getInstance().getTableName(dataObjectImpl.getData().getClass());
		ids = DbReader.readIds(table, condition, orderby);
		pos = -1;
	}
	
	public long curreintId() { return ids.get(pos); }
	
	public DbObject<T> current()
	{
		return dataObjectImpl;
	}
	
	public void applyFilter(String value)
	{
		String table = DataObjectInfo.getInstance().getTableName(dataObjectImpl.getData().getClass());
		ids = DbReader.readIds(table, value, orderby);
		pos = -1;
	}
	
	public void setCondition(String cond){
		this.condition = cond;
	}
}
