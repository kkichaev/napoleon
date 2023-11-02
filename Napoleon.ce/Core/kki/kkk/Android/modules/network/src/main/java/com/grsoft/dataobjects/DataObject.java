/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Базовый класс объектов данных
 *
 * kki   12/10/2010   creating
 */

package com.grsoft.dataobjects;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Date;

public class DataObject{
	public String getValue(String field) {
		Class<? extends DataObject> dataType = getClass();
		String value = new String();
		
		try {
			field = field.trim();
			Field f = dataType.getField(field);
			if( f.getType() == Date.class ) {
				Long v = Long.valueOf(((Date)f.get(this)).getTime());
				value = v.toString();					
			} else
				value = f.get(this).toString();
		} catch (Exception e) {
			e.printStackTrace();
			value = "";
		}
		
		return value;
	}
	
	public String[] getValues(String[] fields) {
		String[] values = new String[fields.length];
		
		int index = 0;
		for(String key: fields) {
			values[index] = getValue(key);
			index++;
		}
		
		return values;
	}
	
	/**
	 * Копируем одинаковые поля (то же имя и тот же тип) с одного объекта на другой
	 * @param dest
	 * @param src
	 */
	public static void makeCopy(DataObject dest, DataObject src) {
		try{
			Field[] fields = src.getClass().getFields();
			Class<? extends DataObject> destType = dest.getClass();
			
			for(Field f : fields) {
				try {
					if( (f.getModifiers() & (Modifier.FINAL|Modifier.STATIC)) == 0 ) {
						Field destF = destType.getField(f.getName());
						if( destF != null && destF.getType() == f.getType() )
							f.set(dest, f.get(src));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	public String getTableName() {
		return DataObjectInfo.getInstance().getTableName(getClass());
	}
	
	public Field[] getFields() {
		return DataObjectInfo.getInstance().getFields(getClass());
	}
	
	public Field getField(String name) {
		Field ret = null;
		try {
			ret = getClass().getField(name);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	/***
	 * Клонирует объект
	 * Метод может возвратить null!
	 */
	public DataObject clone(){
		DataObject result = null;
		
		try{
			result = this.getClass().newInstance();
			Field[] fields = this.getClass().getFields();
			
			for(Field f : fields) {
				try {
					if( (f.getModifiers() & (Modifier.FINAL|Modifier.STATIC)) == 0 )
						f.set(result, f.get(this));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return result;
	}
}


