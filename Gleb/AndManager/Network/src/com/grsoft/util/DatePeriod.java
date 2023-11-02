package com.grsoft.util;

import java.util.Calendar;
import java.util.Date;

/***
 * Период дат с - по
 * @author kki
 *
 */
public class DatePeriod {
	
	public static final int CREATED = 1;
	public static final int DATE = 2; 
	
	public Date begin;
	public Date end;
	public int periodType; 

	public DatePeriod(Date begin, Date end){
		this.begin = begin;
		this.end = end;
	}
	
	public DatePeriod(int y1, int m1, int d1, int h1, int min1,
			int y2, int m2, int d2, int h2, int min2){
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(y1, m1, d1, h1, min1);
		
		this.begin = calendar.getTime();
		
		calendar.set(y2, m2, d2, h2, min2);
		this.end = calendar.getTime();
	}
}
