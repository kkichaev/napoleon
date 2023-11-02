/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Интерфейс для объетов, которые работают с базой данных
 *
 * kki   04/11/2010   creating
 */
package com.grsoft.dataobjects.impl;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbReader;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.DocDataObject;
import com.grsoft.util.ExtrasConst;

/**
 * Класс для связи БД с объектами данных - через него происходит вся работа с БД
 * 
 * @author Ert
 *
 * @param <T>
 */
public class DbObject<T extends DataObject>
{
	protected long rowid = ExtrasConst.INVALID_ID;
	protected T data;
	protected String table = null;
	private boolean closeAfterWrite = false;
	
	private String readingFields = null;
	
	@SuppressWarnings("unchecked")
	public DbObject() {
		this.data = (T)createDataInstance(
				DataTypesMap.getReplacedType(getGenericClass()));
		table = DataObjectInfo.getInstance().getTableName(data.getClass());		
	}
	
	public void setReadingFields(String readingFields) {
		this.readingFields = readingFields;
		if( reader != null )
			reader.setReadingFields(readingFields);
	}
	
	public void close() {
		if( writer != null ) {
			writer.close();
			writer = null;
		}
		if( reader != null ) {
			reader.close();
			reader = null;
		}
	}
		
	private DbWriter writer = null;
	protected DbWriter getWriter() {
		if( writer == null )
			writer = new DbWriter();
		
		return writer;
	}
	
	private DbReader reader = null;
	protected DbReader getReader() { 
		if( reader == null ) {
			reader = new DbReader();
			reader.setReadingFields(readingFields);
		}
		return reader;
	}
	
	@Override
	protected void finalize() throws Throwable {
		close();
		super.finalize();
	}
	
	public T getData() { return data; }
	
	public String getTableName() { return table; }
	
	/**
	 * Читает одну запись по ключевому полю
	 * @param key имя ключевого поля
	 * @param value значение ключа
	 * @return true - запись прочитана
	 */
	public boolean read(String key, Object value){
		boolean result = false;
		
		try{
			data.getClass().getField(key).set(data, value);
			result = read();
			close();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return result;
	}
	
	/**
	 * Читает запись по rowid
	 * @param rowid
	 * @return
	 */
	public boolean read(long rowid) { return read(rowid, true); }
	
	/**
	 * Читает по Rowid
	 * @param rowid
	 * @param useCache - true если rowid == this то объект не читается, false - читается всегда
	 */
	public boolean read(long rowid, boolean useCache) {
		if( useCache && this.rowid == rowid )
			return true;
		
		boolean ret = true;
		if( rowid != ExtrasConst.INVALID_ID ) {
			DbReader r = getReader();
			ret = r.read(data, table, rowid);
		}
		this.rowid = (ret) ? rowid : ExtrasConst.INVALID_ID;
		return ret;
	}
	
	/**
	 * Читает запись по ключевым полям (rowid тоже заполняется)
	 * @return
	 */
	public boolean read() {
		DbReader r = getReader();
		rowid = r.read(data, table);
		return (rowid != ExtrasConst.INVALID_ID);
	}
	
	public long getRowid() { return rowid; }
	
	/**
	 * записывает запись. Если новая, происходит вставка, если запись уже была, происходит update
	 * @return
	 */
	public long write() {
		if( rowid != ExtrasConst.INVALID_ID )
			getWriter().updateRecord(data, rowid);
		else
			rowid = getWriter().insertRecord(data);
		
		if (closeAfterWrite)
			close();
		
		return rowid;
	}
	
	public long insert() {
		rowid = getWriter().insertRecord(data);		
		if (closeAfterWrite)
			close();		
		return rowid;
	}
	
	public boolean delete() {
		boolean res = true;
		
		if( rowid != ExtrasConst.INVALID_ID )
			res = getWriter().deleteRecord(data, rowid);
		
		if( res )
			rowid = ExtrasConst.INVALID_ID;
		
		return res;
	}

	public void checkDBTable() {
		DbWriter.checkDBTable(data.getClass());
	}
	
	/**
	 * Удалить все записи из таблицы, вызывает
	 * SqLiteDatabase.delete().
	 */
	public void deleteAll(){
		DataBaseManager.getDataBase().delete(table, null, null);
	}
	
	public void delete(String condition, String[] args){
		DataBaseManager.getDataBase().delete(table, condition, args);
	}
	
	private DataObject createDataInstance(Class<? extends DataObject> dataType){
		try{
			return dataType.newInstance();
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	private Class<? extends DataObject> getGenericClass(){
		Class <?> theClass = this.getClass();
		Type type = null;
		
		while(theClass.getSuperclass() != null){
			type = theClass.getGenericSuperclass();
			
			if (type != null && type instanceof ParameterizedType)
				break;
			
			theClass = theClass.getSuperclass();
		}
		
		if (type != null)
			return (Class<? extends DataObject>) 
				((ParameterizedType)type).getActualTypeArguments()[0];
		else
			return null;
	}
	
	/***
	 * Регистрирует новый тип DataObject, который будет
	 * исопользован как тип данных.
	 * 
	 * @param base базовый тип, которым параметризован класс
	 * @param replaced тип производный от базового типа 
	 */
	public static void regNewDataType(Class<? extends DataObject> base, 
			Class<? extends DataObject> replaced){
		DataTypesMap.put(base, replaced);
	}
	
	/***
	 * Возвращает тип, который хранит данные о DataObject
	 * @param base базовый тип
	 * @return новый тип, который замещен в regNewDataType 
	 * или возвращает базовый тип, если замещение не было установлено
	 */
	public static Class<? extends DataObject> 
		getDataType(Class<? extends DataObject> base){
		return DataTypesMap.getReplacedType(base);
	}
	
	/***
	 * Хранит связу DataType - NewDataType
	 * возвращает актуальный DataType,
	 * если не установлен новый DataType возвращает
	 * тип аргумента.
	 * 
	 * @author kki
	 *
	 */
	private static class DataTypesMap {
		private static Map<Class<? extends DataObject>, 
			Class<? extends DataObject>> map = new HashMap<Class<? 
					extends DataObject>, Class<? extends DataObject>>();
		
		public static Class<? extends DataObject> 
			getReplacedType(Class<? extends DataObject> baseClass )
		{
			if (map.containsKey(baseClass))
				return map.get(baseClass);
			else
				return baseClass;
		}
		
		public static void put(Class<? extends DataObject> base, 
				Class<? extends DataObject> replaced){
			map.put(base, replaced);
		}
	}
	
	public void setCloseAfterWrite(boolean value){
		closeAfterWrite = value;
	}
	
//	Есть задумка сделать такое хранилище, но когда писал, понял, что пока не надо, удалять жалку(22.10.2015 kki) 
//	private static class InstanceMap {
//		private static Map<Class<? extends DbObject<?>>, Class<? extends DbObject<?>>> hash = new HashMap<Class<? extends DbObject<?>>, Class<? extends DbObject<?>>>();
//		
//		public static Class<? extends DbObject<?>> getReplaced(Class<? extends DbObject<?>> base){
//			Class<? extends DbObject<?>> result = base;
//			if(hash.containsKey(base))
//				result = hash.get(base);
//			
//			return result;
//		}
//		
//		public static void put(Class<? extends DbObject<?>> base, Class<? extends DbObject<?>>  replaced){
//			hash.put(base, replaced);
//		}
//	}
//	
//	public static void regNewInstanceMap(Class<? extends DbObject<?>> base, Class<? extends DbObject<?>>  replaced){
//		InstanceMap.put(base, replaced);
//	}
//	
//	public DbObject<?> instance(Class<? extends DbObject<?>> base){
//		DbObject<?> result = null;
//		
//		try{
//			result = InstanceMap.getReplaced(base).newInstance();
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//		
//		return result;
//	}
 }

