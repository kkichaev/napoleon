package com.ksoft.ardalarm.database;


public class TimeAlarm {
	public static final String TABLE_NAME = "alarm";
	public static final String NAME = "name";
	public static final String PERIOD = "period";
	public static final String HOUR = "hour";
	public static final String MINUTE = "minute";
	public static final String[] PROJECTION = new String[]{ "_id", NAME, PERIOD, HOUR, MINUTE};
}
