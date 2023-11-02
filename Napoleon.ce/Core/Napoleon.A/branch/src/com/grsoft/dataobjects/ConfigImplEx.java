package com.grsoft.dataobjects;

import com.grsoft.dataobjects.impl.ConfigImpl;

public class ConfigImplEx extends ConfigImpl {
	public boolean getValue(StringBuilder value, String key){
		if(value != null && key != null){
			Config config = getData();
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
