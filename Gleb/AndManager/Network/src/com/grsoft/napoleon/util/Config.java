package com.grsoft.napoleon.util;

import java.io.Serializable;
import java.lang.reflect.Field;

import com.grsoft.util.Consts;

public class Config implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public int cameraWidth;
	public int cameraHeight;
	public int monthsToRecreate = 1;
	public boolean dataDirShare = false;
	public String address = "10.0.2.2";
	public String address2;
	public int port = 8888;
	public int port2;
	public int duration;
	public String login = new String();
	public String passw = new String();
	public int gpsDistance = 100;
	public int gpsFrequience = Consts.ONE_SECOND * 60;
	
	public void resetToDefault(){}
	
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
}
