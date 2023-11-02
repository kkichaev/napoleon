package com.grsoft.util;

import java.util.Calendar;
import java.util.Date;

/***
 * Период дат с - по
 * @author kki
 *
 */
public class DatePeriod {
	public static int MIN_PER_DAY = 60 * 24;
	public static final int CREATED = 1;
	public static final int DATE = 2; 
	
	public Date begin;
	public Date end;
	public int periodType; 

	public DatePeriod(Date begin, Date end){
		this.begin = begin;
		this.end = end;
		
		periodType= CREATED;
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
	
	public static long daysDiff(Date from, Date to) {
		return Math.round((to.getTime() - from.getTime()) / 86400000D); // 1000 * 60 * 60 * 24
	}
	
	public static long minDiff(Date from, Date to){
		return Math.round((to.getTime() - from.getTime()) / 60000D); // 1000 * 60
	}
	
	/***
	 * Создает период с началом date и окончанием через min - минут
	 * @return
	 */
	public static DatePeriod createRange(Date date, int min){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MINUTE, min);
		
		DatePeriod result = new DatePeriod(date, cal.getTime());
		return result;
	}
	
	public static long hourDiff(Date from, Date to) {
		return Math.round((to.getTime() - from.getTime()) / 3600000D); // 1000 * 60 * 60
	}
}
