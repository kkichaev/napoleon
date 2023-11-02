/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Адаптер для списков базы данных
 *
 * kki   21/01/2011   creating
 */
package com.grsoft.util;
import com.grsoft.aceteam.R;

import android.content.Context;
import android.widget.BaseAdapter;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.impl.Cursor;
import com.grsoft.dataobjects.impl.DbObject;

public abstract class DataBaseAdapter<T extends DataObject> extends BaseAdapter
{
	protected Cursor<T> cursor;
	protected Context context;
	
	public DataBaseAdapter(Context context, DbObject<T> implClass){
		this(context, implClass, "", "");
	}
	
	public DataBaseAdapter(Context context, DbObject<T> implClass, String condition){ 
		this(context, implClass, condition, "");
	}
	
	public DataBaseAdapter(Context context, DbObject<T> implClass, String condition, String order){ 
		cursor = new Cursor<T>(implClass, condition, order);
		this.context = context;
	}
	
	@Override
	public int getCount()
	{
		return cursor.getCount();
	}

	@Override
	public Object getItem(int pos)
	{
		return cursor.get(pos);
	}

	@Override
	public long getItemId(int pos)
	{
		return cursor.getItemId(pos);
	}

	@Override
	public void notifyDataSetChanged()
	{
		cursor.updateIds();
		super.notifyDataSetChanged();
	}
	
	@Override
	public int getViewTypeCount()
	{
		return 1;
	}
	
	public void applyFilter(String value)
	{
		cursor.applyFilter(value);
		super.notifyDataSetChanged();
	}
	
	public void resetFilter()
	{
		cursor.updateIds();
		super.notifyDataSetChanged();
	}
	
	public void close(){
		cursor.close();
	}
}
