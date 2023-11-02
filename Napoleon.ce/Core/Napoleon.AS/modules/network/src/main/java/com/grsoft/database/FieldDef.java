package com.grsoft.database;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;

import android.database.sqlite.SQLiteCursor;

public class FieldDef {

	enum FieldType { Integer, Text, Blob, Real };
	
	FieldType type;
	String name;
	
	FieldDef(SQLiteCursor cursor) {
		int ni = cursor.getColumnIndex("name");
		int ti = cursor.getColumnIndex("type");
		
		name = cursor.getString(ni);
		type = stringToFieldType(cursor.getString(ti));
	}
	
	FieldDef(Field src) {
		name = src.getName();
		type = typeToFieldType(src.getType());
	}
	
	@Override
	public boolean equals(Object o) {
		if( this == o )
			return true;
		if( ! (o instanceof FieldDef) )
			return false;
		
		FieldDef fd = (FieldDef)o;
		if( name.compareTo(fd.name) != 0 )
			return false;
		return (type == fd.type);
	}
	
	@Override
	public int hashCode() {
		int hash = (31 + name.hashCode()) * 31 + type.hashCode();
		return hash;
	}
	
	private FieldType typeToFieldType(Class<?> c) {
		if( c == int.class || c == long.class || c == Date.class ) return FieldType.Integer;
		if( c == String.class ) return FieldType.Text;
		if( c == List.class || c ==  byte[].class) return FieldType.Blob;
		return FieldType.Real;
	}
	
	public static FieldType stringToFieldType(String name) {
		if(name.compareToIgnoreCase("INTEGER") == 0) return FieldType.Integer;
		if(name.compareTo("TEXT") == 0) return FieldType.Text;
		if(name.compareToIgnoreCase("BLOB") == 0) return FieldType.Blob;
		return FieldType.Real;
	}
}
