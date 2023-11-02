package com.grsoft.ads.utils;

import java.util.Calendar;
import java.util.Date;

public class Time implements Comparable<Time> {
	public int h;
	public int m;
	
	public Time(int h, int m){
		this.h = h;
		this.m = m;
	}

	public static Time parse(Date date){
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return new Time(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
	}

	@Override
	public int compareTo(Time t) {
		int result = 0;
		
		int x = getMinutes();
		int y = t.getMinutes();
		
		if (x < y)
			result = -1;
		
		if (x > y)
			result = 1;
			
		return result;
	}

	public int getMinutes() {
		return h * 60 + m;
	}
	
}
