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
	public String[] getValues(String[] fields) {
		Class<? extends DataObject> dataType = getClass();
		String[] values = new String[fields.length];
		
		int index = 0;
		for(String key: fields) {
			try {
				Field f = dataType.getField(key);
				if( f.getType() == Date.class ) {
					Long v = new Long(((Date)f.get(this)).getTime());
					values[index] = v.toString();					
				} else
					values[index] = f.get(this).toString();
			} catch (Exception e) {
				e.printStackTrace();
				values[index] = "";
			}
			index++;
		}
		
		return values;
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


