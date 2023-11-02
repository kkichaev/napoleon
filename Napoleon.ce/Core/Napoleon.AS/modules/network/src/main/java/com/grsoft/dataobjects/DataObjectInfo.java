/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Содержит информацию о классах DataObject
 *
 * kki   17/11/2010   creating
 */
package com.grsoft.dataobjects;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;
import com.grsoft.database.BlobSource;
import com.grsoft.database.DataObjectUtils;
import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.network.exception.ListShouldParameterezed;
import com.grsoft.types.Scale;

public class DataObjectInfo
{
	private Map<Class<? extends DataObject>, Map<String, Object>> hash = 
		new HashMap<Class<? extends DataObject>, Map<String, Object>>();
	
	private static DataObjectInfo instance;
	
	private static final String INDEX_KEY = "indexes";
	private static final String TABLE_NAME = "TableName";
	private static final String FIELDS = "fields";
	private static final String PRIMARY_KEY = "primary_key";
	private static final String SRC_FIELD = "source_field";
	private static final String SRV_INFO = "srv_info";
	
	public static DataObjectInfo getInstance()
	{
		if (instance == null)
			instance = new DataObjectInfo();
		
		return instance;
	}
	
	private Map<String, Object> init(Class<? extends DataObject> type)
	{
		if (hash.containsKey(type))
			return hash.get(type);
		
		Map<String, Object> info = new HashMap<String, Object>();
		TableInfo tableInfo = type.getAnnotation(TableInfo.class);
		
		if (tableInfo != null)
		{
			info.put(TABLE_NAME, tableInfo.name());
		}
		
		hash.put(type, info);
		
		return info;
	}
	
	/**
	 * Заменить имя таблицы
	 * @param type - тот тип у которого меняем (OrderEx, а не Order)
	 * @param tableName
	 */
	public void replaceTableName(Class<? extends DataObject> type, String tableName) {
		Map<String, Object> info = init(type);
		info.put(TABLE_NAME, tableName);
	}

	public void replaceIndexes(Class<? extends DataObject> type, String indexes) {
		Map<String, Object> info = init(type);
		info.put(INDEX_KEY, indexes);
	}
	
	// функцию не трогаем или вместе с putTableName
	public String getTableName(Class<? extends DataObject> type)
	{
		Class<? extends DataObject> tpEx = DbObject.getDataType(type);
		init(tpEx);
		String val = (String)getMemberByName(type, TABLE_NAME);
		if( val != null )
			return val;
		
		init(type);
		return (String)getMemberByName(type, TABLE_NAME);
	}
	
	/**
	 * Меняем тип у поля типа коллекция
	 * @param type - тот тип у которого заменяем (OrderEx, а не Order)
	 * @param name
	 * @param listType
	 */
	public void replaceListType(Class<? extends DataObject> type, String name, Class<? extends DataObject>  listType) {
		putMemberByName(type, name, listType);
	}
	
	@SuppressWarnings("unchecked")
	public Class<? extends DataObject> getListType(Class<? extends DataObject> type, String name)
	{
		Class<? extends DataObject>  listType = (Class<? extends DataObject>) getMemberByName(type, name);		
		if (listType != null)
			return listType;
		
		Class<?> stype = type.getSuperclass();
		while( stype != null && stype != DataObject.class && DataObject.class.isAssignableFrom(stype) ) {
			listType = (Class<? extends DataObject>) getMemberByName((Class<? extends DataObject>)stype, name);		
			if (listType != null)
				return listType;
			stype = stype.getSuperclass();
		}
		
		try
		{
			Field field = type.getField(name);
			listType =  (Class<? extends DataObject>) 
				((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
			
			if (listType == null)
				throw new ListShouldParameterezed(type.getClass());
			
			putMemberByName(type, name, listType);
			return listType;
		}
		catch(Exception exception)
		{
			throw new RuntimeException(exception);
		}
	}
	
	public int getScale(Class<? extends DataObject> type, String name)
	{
		Integer result = (Integer) getMemberByName(type, name);
		
		if (result != null)
			return result.intValue();
		
		try
		{
			Scale scale = type.getField(name).getAnnotation(Scale.class);
			result = scale.value();
		}
		catch(Exception exception)
		{
			result = 1;
		}
		
		putMemberByName(type, name, result);		
		return result.intValue();
	}
	
	public Field[] getFields(Class<? extends DataObject> type)
	{
		Field[] result = (Field[]) getMemberByName(type, FIELDS);
		
		if (result != null)
			return result;
		
		try
		{
			result = DataObjectUtils.getUpdatebleFields(type);
			putMemberByName(type, FIELDS, result);
			
			return result;
		}
		catch(Exception exception)
		{
			throw new RuntimeException(exception);
		}
	}
	
	public boolean ifFieldPresent(Class<? extends DataObject> type, String fieldName)
	{
		for (Field field: getFields(type))
		{
			if (field.getName().equals(fieldName))
				return true;
		}
		
		return false;
	}
	
	private Object getMemberByName(Class<? extends DataObject> type, String name)
	{
		Map<String, Object> info = getInfoMap(type);
		
		return info.get(name);
	}
	
	private void putMemberByName(Class<? extends DataObject> type, String name, Object val)
	{
		Map<String, Object> info = getInfoMap(type);
		
		info.put(name, val);
	}

	private Map<String, Object> getInfoMap(Class<? extends DataObject> type)
	{
		Map<String, Object> result = null;
		
		if (hash.containsKey(type))
			result = hash.get(type);
		else
			result = init(type);
		
		return result;
	}
	
	/**
	 * Заменить первичный ключ
	 * @param type тот тип у которого меняем (OrderEx, а не Order)
	 * @param pk
	 */
	public void replacePrimaryKey(Class<? extends DataObject> type, String pk) {
		putMemberByName(type, PRIMARY_KEY, pk);
	}

	// функцию не трогаем, или совместно с replacePrimaryKey
	public String getPrimaryKey(Class<? extends DataObject> type)
	{
		String result = (String) getMemberByName(type, PRIMARY_KEY);
		
		if (result != null)
			return result;
		
		TableInfo tableInfo = type.getAnnotation(TableInfo.class);
		
		if (tableInfo == null)
			new RuntimeException(new Exception("Annotation not found"));
		
		result = tableInfo.keyFields();
		
		putMemberByName(type, PRIMARY_KEY, result);
		
		return result;
	}
	
	public String getIndexes(Class<? extends DataObject> type)
	{
		String result = (String) getMemberByName(type, INDEX_KEY);
		
		if (result != null)
			return result;
		
		TableInfo tableInfo = type.getAnnotation(TableInfo.class);
		
		if (tableInfo == null)
			new RuntimeException(new Exception("Annotation not found"));
		
		result = tableInfo.indexes();
		
		putMemberByName(type, INDEX_KEY, result);
		
		return result;
	}
	
	public boolean isBlobSourceable(Class<? extends DataObject> type, Field field)
	{
		Boolean result = (Boolean) getMemberByName(type, SRC_FIELD);
		
		if (result != null)
			return result;
		
		BlobSource blobSource = field.getAnnotation(BlobSource.class);
		
		result = blobSource != null;
		
		putMemberByName(type, SRC_FIELD, result);
		
		return result;
	}
	
	/***
	 * 
	 * @param type
	 * @return имя объекта данных на сервере
	 */
	public String getSrvName(Class<? extends DataObject> type){
		String result = (String) getMemberByName(type, SRV_INFO);
		
		if(result == null){
			ServerInfo srv = type.getAnnotation(ServerInfo.class);
			
			if(srv == null) {
				result = type.getSimpleName();
			} else {
				result = srv.name();
			}
			putMemberByName(type, SRV_INFO, result);
		}
		
		return result;
	}
}
