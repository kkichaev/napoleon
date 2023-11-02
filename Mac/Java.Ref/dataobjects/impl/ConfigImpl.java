/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Объект данных Config для работы с базой
 *
 * kki   26/10/2010   creating
 */
package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.Config;

public class ConfigImpl extends DbObject<Config> {
	public boolean getValue(StringBuilder value, String key){
		if(value != null && key != null){
			Config config = (Config) getData();
			config.key = key;
			boolean result = false;
			
			if (read()){
				value.append(config.value);
				result = true;
			}
			
			close();
			return result;
		}else
			return false;
	}
}
