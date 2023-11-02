/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Работа с базой данных
 *
 * kki   12/10/2010   creating
 */
package com.grsoft.database;

import static com.grsoft.util.Debug.dbgPrint;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.grsoft.database.FieldDef.FieldType;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.napoleon.util.ConvertConstants;
import com.grsoft.network.exception.RuntimeException;
import com.grsoft.network.exception.UnknownTypeForSqlConversion;
import com.grsoft.network.util.UnicodUtils;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;

/**
 * Содержит методы для редактирования данных и таблицами - все чтение в DataBaseReader
 * @author 1111
 *
 */
public class DbWriter
{
	enum StmtType { none, insert, delete, update };
	
	private int commandCount;
	private int commitInterval = 1;
	
	private StmtType stmtType = StmtType.none;
	private String objectType = "";
	private SQLiteStatement stmt = null;
	
	private HashSet<String> excludedFields = null;
	
	public DbWriter() {
	}
	
	@Override
	protected void finalize() throws Throwable {
		close();
		super.finalize();
	}
	
	/**
	 * передаем список полей которые не надо обновлять
	 * @param fields
	 */
	public void setExcludedFields(String[] fields) {
		excludedFields = new HashSet<String>();
		for(String v : fields)
			excludedFields.add(v);
	}
	
	public void close() {
		if( stmt != null ) {
			stmt.close();
			stmt = null;
			
			stmtType = StmtType.none;
		}
	}
	
	private boolean checkStmt(final StmtType newType, DataObject dataObject, long rid) throws RuntimeException {
		boolean ret = true;
		
		Class<? extends DataObject> dataType = dataObject.getClass();
		String objType = dataType.getName();
		
		if( newType != stmtType || objectType.compareTo(objType) != 0 ) { // create stmt;
			if( stmtType != StmtType.none && stmt != null )
				stmt.close();
			
			switch(newType) {
			case insert:
				stmt = compileInsertStatement(dataType);
				break;
			case update:
				stmt = compileUpdateStatement(dataType);
				break;
			case delete:
				stmt = compileDeleteStatement(dataType);
				break;
			default:
				break;
			}
			
			stmtType = newType;
			objectType = objType;
		}
		
		switch(newType) {
		case insert:
			bindStatement(stmt, dataObject);
			break;
		case update:
			int idx = bindStatement(stmt, dataObject);
			stmt.bindLong(idx, rid);
			break;
		case delete:
			stmt.bindLong(1, rid);
			break;
		default:
			break;
		}		
		return ret;
	}
	
	private SQLiteStatement compileInsertStatement(Class<? extends DataObject> dataType)
	{
		SQLiteStatement retStmt = null;
		try
		{
			checkDBTable(dataType);

			DataObjectInfo doi = DataObjectInfo.getInstance(); 
			Field[] fields = doi.getFields(dataType);
			StringBuilder fldStr = new StringBuilder();
			StringBuilder paramStr = new StringBuilder();
			
			for(Field field : fields)
			{
				String name = field.getName();
				if( name.compareTo(ExtrasConst.ROW_ID_FIELD) == 0 ) continue;
				
				if( excludedFields != null && excludedFields.contains(name)) continue;
				
				fldStr.append("\"" + field.getName() + "\",");
				paramStr.append("?,");
			}
			fldStr.deleteCharAt(fldStr.length() - 1);
			paramStr.deleteCharAt(paramStr.length() - 1);
			
			StringBuilder sqlCmd = new StringBuilder();
			String tableName = "\"" + doi.getTableName(dataType) + "\"";
			
			sqlCmd.append("INSERT OR REPLACE INTO ");
			sqlCmd.append(tableName);
			sqlCmd.append("(");
			sqlCmd.append(fldStr);
			sqlCmd.append(") VALUES (");
			sqlCmd.append(paramStr);
			sqlCmd.append(")");
			
			SQLiteDatabase dataBase = DataBaseManager.getDataBase();
			retStmt = dataBase.compileStatement(sqlCmd.toString());
		}
		catch(Exception e)
		{
			dbgPrint(e.toString());
		}
		
		return retStmt;
	}
	
	private SQLiteStatement compileUpdateStatement(Class<? extends DataObject> dataType) {
		
		checkDBTable(dataType);
		
		StringBuilder SqlCmd = new StringBuilder();
		String tableName = DataObjectInfo.getInstance().getTableName(dataType);
		
		SqlCmd.append("UPDATE \"");
		SqlCmd.append(tableName);
		SqlCmd.append("\" SET ");
		SqlCmd.append(getFieldsValues(dataType, true, tableName));
		SqlCmd.append(" WHERE rowid=?");
		
		SQLiteDatabase dataBase = DataBaseManager.getDataBase();
		return dataBase.compileStatement(SqlCmd.toString());
	}
	
	private String getFieldsValues(Class<? extends DataObject> dataType, boolean excludeRowId, String tableName)
	{
		StringBuilder result = new StringBuilder();
		
		for(Field field: DataObjectInfo.getInstance().getFields(dataType))
		{
			String name = field.getName();
			if( excludeRowId && name.compareTo(ExtrasConst.ROW_ID_FIELD) == 0 ) continue;

			if( excludedFields != null && excludedFields.contains(name)) continue;
			
			result.append("\"" + name + "\"=?");
			result.append(ConvertConstants.COMMA);
		}
		
		result.deleteCharAt(result.length() - 1);
		return result.toString();
	}	

	private SQLiteStatement compileDeleteStatement(Class<? extends DataObject> dataType)
	{
		StringBuilder sqlCmd = new StringBuilder("DELETE FROM ");
		sqlCmd.append("'").append(DataObjectInfo.getInstance().getTableName(dataType)).append("' ");
		sqlCmd.append("WHERE rowid=?");
		
		return DataBaseManager.getDataBase().compileStatement(sqlCmd.toString());
	}
	
	public long insertRecord(DataObject dataObject)
	{
		long res;
		try {
			checkStmt(StmtType.insert, dataObject, ExtrasConst.INVALID_ID);
			commitIfNeeding();
			res = stmt.executeInsert();
		} catch (RuntimeException e) {
			e.printStackTrace();
			res = ExtrasConst.INVALID_ID;
		}
		
		return res;
	}
	
	public boolean updateRecord(DataObject dataObject, long rowid)
	{
		boolean res = true;
		
		try {
			checkStmt(StmtType.update, dataObject, rowid);
			commitIfNeeding();
			stmt.executeInsert();
		} catch (RuntimeException e) {
			e.printStackTrace();
			res = false;
		}
		
		return res;
	}
	
	public boolean deleteRecord(DataObject dataObject, long rowid)
	{
		boolean res = true;
		
		try {
			checkStmt(StmtType.delete, dataObject, rowid);
			commitIfNeeding();
			stmt.executeInsert();
		} catch (RuntimeException e) {
			e.printStackTrace();
			res = false;
		}
		
		return res;
	}
	
	/**
	 * Возвращает следующий индекс
	 * @param statmenet
	 * @param dataObject
	 * @return
	 * @throws RuntimeException
	 */
	private int bindStatement(SQLiteStatement statmenet, DataObject dataObject) throws RuntimeException
	{
		int idField = 1;
		try
		{			
			for(Field field : DataObjectInfo.getInstance().getFields(dataObject.getClass()))
			{
				String fieldName = field.getName(); 
				if( fieldName.compareTo(ExtrasConst.ROW_ID_FIELD) == 0 ) continue;
				if( excludedFields != null && excludedFields.contains(fieldName) ) continue;
				
				if(field.getType() == String.class)
					statmenet.bindString(idField, 
							field.get(dataObject) != null 
								?	field.get(dataObject).toString()
								: "");
				
				else if(field.getType() == int.class)
					statmenet.bindLong(idField, field.getInt(dataObject));
				
				else if(field.getType() == long.class)
					statmenet.bindLong(idField, field.getLong(dataObject));
				
				else if (field.getType() == Date.class)
				{
					Date date = (Date) field.get(dataObject);
					
					if (date != null)
						statmenet.bindLong(idField, date.getTime());
				}
				
				else if(field.getType() == List.class)
				{
					byte[] res = listToBlob((List<?>)field.get(dataObject), field);
					statmenet.bindBlob(idField, res);
				}
				
				else if(field.getType() == byte[].class){
					statmenet.bindBlob(idField, (byte[])field.get(dataObject));
				}
				else 
					throw new RuntimeException(new UnknownTypeForSqlConversion(field.getType()));
				
				idField++;
			}
		}
		catch(Exception exception)
		{
			throw new RuntimeException(exception);
		}
		
		return idField;
	}

	@SuppressWarnings("unchecked")
	private byte[] listToBlob(List<?> data, Field field) throws Exception {
		
		final int ARRAY_SIZE = 128;
		ByteArrayOutputStream array = new ByteArrayOutputStream(ARRAY_SIZE);
		
		if (data != null)
		{
			int count = data.size();
			array.write(DataObjectUtils.counterMarker);
			array.write(Util.intToBytes(count)); //Количество в списке
			array.write(Util.intToBytes(0));     //Версия объекта данных
			
			for (Object item: data)
			{
				for(Field ifield :  DataObjectInfo.getInstance().getFields((Class<? extends DataObject>) item.getClass()))
				{
					Class<?> fldType = ifield.getType();
					if(fldType == int.class)
						array.write(Util.intToBytes(ifield.getInt(item)));
					
					else if (fldType == String.class)
					{
						Object val =  ifield.get(item);
						array.write(UnicodUtils.toBytes(((val != null) ? val.toString() : "")));
						array.write(UnicodUtils.toBytes("\0"));
					}
					
					else if (fldType == byte[].class)
					{
						byte[] buf = (byte[]) ifield.get(item);
						
						if(buf != null)
						{
							array.write(Util.intToBytes(buf.length));
							array.write(buf);
						}
					}
					
					else if (fldType == Date.class)
					{
						Date date = (Date) ifield.get(item);
						long val = (date == null) ? 0 : date.getTime();
						array.write(Util.longToBytes(val));
					} else if(fldType == List.class) {
						byte[] fld = listToBlob((List<?>)ifield.get(item), ifield);
						array.write(fld);
					}
					
					else
						throw new RuntimeException(new Exception("Type not implemented " + ifield.getType()));
				}
			}
		}

		byte[] res = array.toByteArray();
		return res;
	}
	
	public static boolean dropTable(String tableName) 
	{
		StringBuilder dropTable = new StringBuilder("DROP TABLE IF EXISTS '");
		dropTable.append(tableName);
		//dropTable.append(DataObjectInfo.getInstance().getTableName(dataClass));
		dropTable.append("'");
		
		boolean res = true;
		try {
			DataBaseManager.getDataBase().execSQL(dropTable.toString());
		} catch(Exception e) {
			res = false;
		}
		
		return res;
	}
	
//	public void makeTables(List<? extends Hitching> hitchings) 
//		throws RuntimeException
//	{
//		try
//		{
//			for(Hitching hitching : hitchings)
//			{
//				Class<? extends DataObject> dc = hitching.getDataObjectClass();
//				
//				dropTable(dc);
//				createTable(dc);
//			}
//		}
//		catch(Exception exception)
//		{
//			dbgPrint(exception.toString());
//			throw new RuntimeException(exception);
//		}
//	}
	
	public static boolean createTable(Class<? extends DataObject> dataType)
	{
		boolean res = true;
		String tableName = DataObjectInfo.getInstance().getTableName(dataType);
		
		if( isTableExists(tableName) )
			return res;
		try
		{
			final String CREATE_TABLE_STR = "CREATE TABLE IF NOT EXISTS ";
			StringBuilder createTable = new StringBuilder();
			StringBuilder tableFormat = new StringBuilder();
		
			makeTableFormat(dataType,tableFormat);
			createTable.setLength(0);
			createTable.append(CREATE_TABLE_STR);
			createTable.append("'").append(tableName).append("'");
			createTable.append(" ");
			createTable.append(tableFormat);
		
			DataBaseManager.getDataBase().execSQL(createTable.toString());
			
			String indexes = DataObjectInfo.getInstance().getIndexes(dataType);
			if( indexes.length() > 0 )
				for( String index : indexes.split(":")) {
					String stmt = "CREATE INDEX " + index.replace(',', '_') + " ON '" + tableName + "' (" + index + ")";
					DataBaseManager.getDataBase().execSQL(stmt);				
				}
		}
		catch(Exception exception)
		{
			dbgPrint(exception.toString());
			res = false;
		}
		
		return res;
	}
	
	private static String convertToSQLType(Class<?> type) throws UnknownTypeForSqlConversion
	{
		if (type == String.class)
			return "TEXT";
		
		else if (type == int.class ||
				type == Date.class ||
				type == long.class)
			return "INTEGER";
		
		/*else if (type == double.class)
			return "REAL";*/
		
		else if (type == List.class ||
				type == byte[].class)
			return "BLOB";
		
		throw new UnknownTypeForSqlConversion(type);
	}
	
	private static String makeTableFormat(Class<? extends DataObject> dataObjectClass, 
			StringBuilder result) 
		throws UnknownTypeForSqlConversion
	{
		result.setLength(0);
		result.append('(');
		
		for(Field field : DataObjectInfo.getInstance().getFields(dataObjectClass))
		{
			String fieldName = field.getName();
			if( fieldName.compareTo(ExtrasConst.ROW_ID_FIELD) == 0 ) continue;
			
			String sqlType = convertToSQLType(field.getType());
			result.append("'" + fieldName + "' " + sqlType +",");
		}
		
		String pk = DataObjectInfo.getInstance().getPrimaryKey(dataObjectClass);
		if( pk.length() > 0 ) {
			String primaryKey = "PRIMARY KEY (" + pk + ")";		
			result.append(primaryKey);
		} else
			result.deleteCharAt(result.length()-1);
		
		result.append(')');
		
		return result.toString();
	}
	
	private void commitIfNeeding()
	{
		if (commitInterval > 1 && (++commandCount % commitInterval) == 0)
			commitAntStartNewTransaction();
	}
	
	public static boolean isTableExists(String name) 
	{		
		SQLiteDatabase dataBase = DataBaseManager.getDataBase();
		String[] args = { name };
		SQLiteCursor cs = (SQLiteCursor) dataBase.rawQuery("SELECT name FROM SQLITE_MASTER WHERE type='table' AND name=?", args);
		
		boolean ret = cs.moveToNext();
		cs.close();
		
		return ret;
	}
	
	static public ArrayList<FieldDef> readDBFields(String tableName) {
		ArrayList<FieldDef> dbFields = new ArrayList<FieldDef>();
		SQLiteDatabase dataBase = DataBaseManager.getDataBase();
		String[] args = {};
		SQLiteCursor cs = (SQLiteCursor) dataBase.rawQuery("PRAGMA TABLE_INFO('" + tableName + "')", args );
		while( cs.moveToNext() ) {
			dbFields.add(new FieldDef(cs));
		}
		cs.close();
		return dbFields;
	}
	
	static private HashSet<FieldDef> getFieldsDef(Class<? extends DataObject> dbc) {
		HashSet<FieldDef> retFields = new HashSet<FieldDef>();
		Field[] fields = DataObjectInfo.getInstance().getFields(dbc);
		for( Field f : fields ) {
			if( f.getName().compareTo(ExtrasConst.ROW_ID_FIELD) == 0 ) continue;
			
			retFields.add(new FieldDef(f));
		}
		return retFields;
	}
	
	static private String fieldTypeToString(final FieldType type) {
		switch(type) {
		case Integer:
			return "INTEGER";
		case Text:
			return "TEXT";
		case Blob:
			return "BLOB";
		default:
			break;
		}		
		return "REAL";
	}
	
	static private boolean addDBFields(String tableName, HashSet<FieldDef> objFields) {
		boolean res = true;
		
		SQLiteDatabase dataBase = DataBaseManager.getDataBase();
		for(FieldDef fd : objFields) {
			String sql = "ALTER TABLE \"" + tableName + "\" ADD COLUMN [" + fd.name + "] "  + fieldTypeToString(fd.type);
			try {
				dataBase.execSQL(sql);
			} catch (Exception e) {
				res = false;
				break;
			}
		}
		
		return res;
	}

	static private boolean checkDBFormat(String tableName, Class<? extends DataObject> dbc) {
		ArrayList<FieldDef> dbFields = readDBFields(tableName);
		HashSet<FieldDef> objFields = getFieldsDef(dbc);
		
		for(FieldDef fd : dbFields) {
			objFields.remove(fd);
		}
		
		return addDBFields(tableName, objFields);
	}

	static public boolean checkDBTable(Class<? extends DataObject> dataType) {
		boolean res = true;

		String tableName = DataObjectInfo.getInstance().getTableName(dataType);
		if( tableName != null && tableName.length() > 0 ) {
			if( isTableExists(tableName) )
				res = checkDBFormat(tableName, dataType);
			else
				res = createTable(dataType);
		}
		return res;
	}
	
	private void commitAntStartNewTransaction()
	{
		SQLiteDatabase dataBase = DataBaseManager.getDataBase();
		dataBase.setTransactionSuccessful();
		dataBase.endTransaction();
		dataBase.beginTransaction();
	}
	
	public void startProcess(int commitInterval)
	{
		commandCount = 0;
		this.commitInterval = commitInterval;
		
		SQLiteDatabase dataBase = DataBaseManager.getDataBase();
		if( commitInterval > 1 )
			dataBase.beginTransaction();
	}
	
	public void endProcess()
	{
		SQLiteDatabase dataBase = DataBaseManager.getDataBase();
		
		if(dataBase.inTransaction())
		{
			dataBase.setTransactionSuccessful();
			dataBase.endTransaction();
		}
		
		if(stmt != null)
			stmt.close();
		
		commitInterval = 1;
	}
}
