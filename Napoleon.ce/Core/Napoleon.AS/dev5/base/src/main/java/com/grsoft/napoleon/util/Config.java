package com.grsoft.napoleon.util;

import android.util.Pair;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.grsoft.napoleon.Features;
import com.grsoft.util.Consts;

public class Config implements Serializable {
	private static final long serialVersionUID = 1L;
	public static String HOST_URL;
	public static String SERVER_CODE = "";

	public int cameraWidth;
	public int cameraHeight;
	public int monthsToRecreate = 1;
	public int daysToRecreate = 0;
	//public boolean dataDirShare = true;
	public String address = "10.0.2.2";
	public String address2;
	public int port = 8888;
	public int port2;
	public int duration;
	public String login = new String();
	public String passw = new String();
	
	/**
	 * Изменение дистанции, м
	 */
	public int gpsDistance = 100;
	
	/**
	 * Время опроса, мсек
	 */
	public int gpsFrequience = Consts.ONE_SECOND * 60;
	public String impersonate = "";

	@DefaultValue(value="")
	public String serverCode = SERVER_CODE;

	@DefaultValue(value="")
	public String userid = "";

	@DefaultValue(value="")
	public String uuid = "";

	public void resetToDefault(){}
	
	@DefaultValue(value="true")
	public boolean loggable = true;
	
	public void setProperty(String name, Object value) {
		try {
			Field f = getClass().getField(name);
			f.set(this, value);
		} catch (Exception e) {
			//e.printStackTrace();
		}
	}

	public Object getProperty(String name) {
		try {
			Field f = getClass().getField(name);
			return f.get(this);
		} catch (Exception e) {
			//e.printStackTrace();
			return null;
		}
	}
	
	public String hrefBase() {
		return "http://" + address + ":" + Integer.toString(port) + "/";  
	}

	public void setFrom(List<Pair<String, String>> values) {
		Map<String, Field> data = new HashMap<String, Field>();
		for(Field f : getClass().getFields()){
			int mdf = f.getModifiers();
			if(!Modifier.isFinal(mdf) && !Modifier.isStatic(mdf)){
				data.put(f.getName(), f);
			}
		}
		for(Pair<String, String> kv : values) {
			Field f = data.get(kv.first);
			if( f != null)
				setField(kv.second, f);
		}
	}

	public String getValue(String fieldName) {
		String res = "";
		try {
			Field f = getClass().getField(fieldName);
			Object value = f.get(this);
			if(value != null)
				res = value.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	public boolean setField(String val, Field f) {
		try {
			final Class<?> type = f.getType();
			if(type == int.class)
				f.setInt(this, Integer.parseInt(val));
			else if(type == String.class)
				f.set(this, val);
			else if(type == long.class)
				f.setLong(this, Long.parseLong(val));
			else if(type == boolean.class)
				f.setBoolean(this, Boolean.parseBoolean(val));
			else if(type == float.class)
				f.setFloat(this, Float.parseFloat(val));

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

}
