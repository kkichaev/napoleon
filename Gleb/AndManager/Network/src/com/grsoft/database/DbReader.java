package com.grsoft.database;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.network.exception.RuntimeException;
import com.grsoft.network.exception.TypeNotImplemented;
import com.grsoft.util.ExtrasConst;

public class DbReader {
	private enum CursorType { none, read, select };
	private CursorType ct = CursorType.none;
	boolean distinct = false;
	private int dataHash = 0, whereHash = 0, orderHash = 0;
	private String table = "";
	String currentFields = null, readingFields = null;
	
	private SQLiteCursor cursor = null;	
	
	public DbReader()	{ 
	}
	
	@Override
	protected void finalize() throws Throwable {
		close();
		super.finalize();
	}
	
	public void setReadingFields(String readingFields) {
		this.readingFields = readingFields;
		if( cursor != null ) {
			cursor.close();
			cursor = null;
		}
	}
	
	public void close()	{
		if( cursor != null )
			cursor.close();
		cursor = null;
		currentFields = null;
	}
	
	private boolean isSameCursor(CursorType ct, Class<? extends DataObject> dataType, String table, int whereHash, int orderHash, boolean distinct) {
		if( cursor == null ) return false;
		if( this.ct != ct ) return false;
		if( dataHash != dataType.hashCode() ) return false;		
		if( this.table.compareTo(table) != 0 ) return false;
		
		return (this.whereHash == whereHash && this.orderHash == orderHash && this.distinct == distinct);
	}
	
	public long read(DataObject data, String table) {
		Class<? extends DataObject> dataType = data.getClass();
		String pkStr = DataObjectInfo.getInstance().getPrimaryKey(dataType);
		String[] pk = pkStr.split(",");
		String[] keys = data.getValues(pk);

		if( isSameCursor(CursorType.read, dataType, table, pkStr.hashCode(), 0, false) ) {
			try {
				cursor.setSelectionArguments(keys);
				cursor.requery();
			} catch(Exception e) {
				cursor = null;
			}
		} else {
			close();
			try {
				cursor = makeCursor(dataType, table, pkStr, keys, true);
			} catch(Exception e) {
				e.printStackTrace();
				cursor = null;
			}
		}
		
		long rid = ExtrasConst.INVALID_ID;
		try {
			if( cursor != null && cursor.moveToNext() ) {
				rid = cursor.getLong(0);
				update(cursor, data);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rid;
	}
	
	
	
	private SQLiteCursor makeCursor(Class<? extends DataObject> dataType , String table, String pkStr, String[] keys, boolean addRID) {
		StringBuilder sb = new StringBuilder("SELECT ");
		if( addRID ) sb.append("rowid,");
		if( currentFields == null )
			initCurrentFields(dataType, table);
		
		sb.append(currentFields).append(" FROM '").append(table).append("' WHERE ");
		
		boolean first = true;
		String[] pk = pkStr.split(",");
		for(String key : pk) {
			if( first ) first = false;
			else sb.append(" AND ");
			
			sb.append('[').append(key).append("]=?");
		}
		
		ct = CursorType.read;
		dataHash = dataType.hashCode();
		this.table = table;
		whereHash = pkStr.hashCode();
		orderHash = 0;
		distinct = false;

		return (SQLiteCursor) DataBaseManager.getDataBase().rawQuery(sb.toString(), keys);
	}

	private SQLiteCursor makeCursor(Class<? extends DataObject> dataType, String table, String where, String order, String[] keys, boolean distinct) {
		StringBuilder sb = new StringBuilder("SELECT ");
		if( distinct ) sb.append("DISTINCT ");
		if( currentFields == null )
			initCurrentFields(dataType, table);

		sb.append(currentFields).append(" FROM '").append(table).append("'");
		
		if( where != null && where.length() > 0 ) sb.append(" WHERE ").append(where);
		if( order != null && order.length() > 0 ) sb.append(" ORDER BY ").append(order);
				
		ct = CursorType.select;
		dataHash = dataType.hashCode();
		this.table = table;
		whereHash = (where == null) ? 0 : where.hashCode();
		orderHash = (order == null) ? 0 : order.hashCode();
		this.distinct = distinct;

		return (SQLiteCursor) DataBaseManager.getDataBase().rawQuery(sb.toString(), keys);
	}

	private void initCurrentFields(Class<? extends DataObject> dataType, String table) {
		if( readingFields != null ) {
			String[] fields = readingFields.split(",");
			ArrayList<FieldDef> tblFields = DbWriter.readDBFields(table);
			
			readingFields = "";
			for(String f : fields) {
				f = f.trim();
				for(FieldDef fd : tblFields) {
					if( fd.name.equals(f) ) {
						if( readingFields.length() > 0 )
							readingFields += ",";
						readingFields += f;
					}
				}
			}
			
			if(readingFields.length() == 0)
				readingFields = DataObjectUtils.getFields(dataType);
			currentFields = readingFields;
		} else 
			currentFields = DataObjectUtils.getFields(dataType);
	}

	public boolean read(DataObject data, String table, long rowid) {
		Class<? extends DataObject> dataType = data.getClass();
		String pk = "rowid";
		String[] keys = {((Long)rowid).toString()};

		if( isSameCursor(CursorType.read, dataType, table, pk.hashCode(), 0, false) ) {
			cursor.setSelectionArguments(keys);
			cursor.requery();
		} else {
			close();
			try {
				cursor = makeCursor(dataType, table, pk, keys, false);
			} catch(Exception e) {
				e.printStackTrace();
				cursor = null;
			}
		}

		boolean ret = false;
		try {
			if( cursor != null &&
					!cursor.isClosed() &&
					(ret = cursor.moveToNext()) )
				update(cursor, data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	public boolean select(DataObject data, String table, String where) {
		return select(data, table, where, null, null, false);
	}
	
	public boolean select(DataObject data, String table, String where, String order) {
		return select(data, table, where, order, null, false);
	}

	public boolean select(DataObject data, String table, String where, String order, String[] keys, boolean distinct) {
		Class<? extends DataObject> dataType = data.getClass();
		
		if( isSameCursor(CursorType.select, dataType, table, (where == null) ? 0 : where.hashCode(), 
				(order == null) ? 0 : order.hashCode(), distinct) ) {
			cursor.setSelectionArguments(keys);
			cursor.requery();
		} else {
			close();
			try {
				cursor = makeCursor(dataType, table, where, order, keys, distinct);
			} catch(Exception e) {
				e.printStackTrace();
				cursor = null;
			}
		}
		
		return selectNext(data);
	}
	
	public boolean selectNext(DataObject data) {
		boolean ret = false;
	
		try {
			if( cursor != null && cursor.moveToNext() ) {
				ret = true;
				update(cursor, data);
			}
		} catch (RuntimeException e) {
			e.printStackTrace();
			ret = false;
		}
		return ret;
	}
	
	/**
	 * „итает список id из таблицы - методы объ€влены static т.к. курсор не измен€етс€
	 * @param table
	 * @param condition
	 * @param orderby
	 * @return
	 */
	static public List<Long> readIds(String table, String condition, String orderby) {
		return readIds(table, condition, orderby, null);
	}
	
	static public List<Long> readIds(String table, String condition, String orderby, String[] args) {
		List<Long> result = new Vector<Long>();
		
		String whereClause = (condition != null && condition.length() != 0)
			? new StringBuilder(" WHERE ").append(condition).toString()
			: "";
			
		String orderbyClause = (orderby != null && orderby.length() != 0)
			? new StringBuilder(" ORDER BY ").append(orderby).toString()
			: "";
			
		final StringBuilder stmt = new StringBuilder("SELECT rowid FROM '").
			append(table).append("' ").append(whereClause).append(orderbyClause); 
		
		Cursor cursor = null;
		try
		{
			cursor = DataBaseManager.getDataBase().rawQuery(stmt.toString(), args);
			while(cursor.moveToNext())
				result.add(cursor.getLong(0));
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		finally
		{
			if( cursor != null )
				cursor.close();
		}
		return result;
	}

	private void update(Cursor cursor, DataObject dataObject, Field[] fields) throws RuntimeException
	{
		Class<? extends DataObject> dataObjectClass = dataObject.getClass();
		try
		{
			for(Field field: fields)
			{
				Object value = readFromCursor(cursor, field, dataObjectClass);
				if( value != null )
					field.set(dataObject, value);
			}
		}
		catch(Exception exception)
		{
			exception.printStackTrace();
			throw new RuntimeException(exception);
		}
	}
	
	private void update(Cursor cursor, DataObject dataObject) throws RuntimeException
	{
		Field[] fields = DataObjectInfo.getInstance().getFields(dataObject.getClass());
		update(cursor, dataObject, fields);
	}
	
	private Object readFromCursor(Cursor cursor, Field field, Class<? extends DataObject> dataObjectClass, int index) 
		throws TypeNotImplemented, RuntimeException
	{
		Class<?> type = field.getType();
		
		if (type == int.class)
			return cursor.getInt(index);
		
		else if (type == long.class)
			return cursor.getLong(index);
		
		else if (type == String.class)
			return cursor.getString(index);
		
		else if (type == Date.class)
			return readDateType(cursor, index);
	
		else if (type == List.class)
			return readListType(cursor, field, index, dataObjectClass);
		
		else if(type == byte[].class)
			return cursor.getBlob(index);
		
		throw new TypeNotImplemented(type);
	}
	
	private Object readFromCursor(Cursor cursor, Field field, Class<? extends DataObject> dataObjectClass) 
		throws TypeNotImplemented, RuntimeException
	{
		int index = cursor.getColumnIndex(field.getName());
		if( index < 0 )
			return null;

		return readFromCursor(cursor, field, dataObjectClass, index);
	}
	
	private List<? extends DataObject> readListType(Cursor cursor, Field field, int index, Class<? extends DataObject> dataObjectClass)
		throws RuntimeException
	{
		byte[] stream = cursor.getBlob(index);
		Class<? extends DataObject> paramType = DataObjectInfo.getInstance().getListType(dataObjectClass, field.getName()); 
//		List<? extends DataObject> list = DataObjectUtils.readList(paramType, ByteBuffer.wrap(stream));
		List<? extends DataObject> list = DataObjectUtils.readList(paramType, stream);
		return list;
	}
	
	private Date readDateType(Cursor cursor, int index)
	{
		long millisec = cursor.getLong(index);
		Date date = new Date(millisec);
		return date;
	}
}
