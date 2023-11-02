package com.grsoft.napoleon;

import java.lang.reflect.Field;

import com.grsoft.dataobjects.DataObject;

public class VDataObject {
	private String str = "";
	private DataObject source; 
	
	public VDataObject(DataObject source, String name) {
		this.source = source; 
		str = inflateVal(source, name);
	}

	private String inflateVal(DataObject source, String name) {
		String result = "";
		try {
			Field f = source.getClass().getField(name);
			result = f.get(source).toString();
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	@Override
	public String toString() {
		return str;
	}
	
	public DataObject getSource() {
		return source;
	}
}
