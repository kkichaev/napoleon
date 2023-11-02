package com.grsoft.network;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateStampFormat extends DateTimeFormat {

	private static TimeZone utc = Calendar.getInstance().getTimeZone();// = TimeZone.getDefault();
	static TimeZone gmt = TimeZone.getTimeZone("GMT");	
//	static Boolean isDaylight = null;
	
	protected String dateFormat; 

	public DateStampFormat(String name, String dateFormat, String formatString) {
		super(name, Date.class, formatString);
		
		this.dateFormat = dateFormat;
	}

	@Override
	public String valueToFormatString(Object value)
	{
		SimpleDateFormat simpleDateFormat =  new SimpleDateFormat(dateFormat, Locale.US);
		simpleDateFormat.setTimeZone(utc);
		
		return simpleDateFormat.format((Date)value);
	}
		
	@Override
	public Date parse(String str)
	{
		String checkStr = new String(str);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.US);
		simpleDateFormat.setTimeZone(gmt);
		Date d = new Date();
		try {
			d = simpleDateFormat.parse(checkStr);
			d = new Date(d.getTime() - utc.getOffset(d.getTime()));
			
		} catch (ParseException e) {
			e.printStackTrace();
		}		
		return d;
		
//		if( isDaylight == null )
//			isDaylight = setdaylight(str);
//		
//		str += utc.getDisplayName(isDaylight, TimeZone.SHORT, Locale.US);
//		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat+"Z", Locale.US);
//		simpleDateFormat.setTimeZone(utc);
//		
//		Date d = new Date();
//		try {
//			d = simpleDateFormat.parse(str);
//			
//			StringBuilder sb = new StringBuilder();
//			sb.append("Convert ").append(str).append(" time zone ").append(utc.getDisplayName()).
//				append(" offset ").append(utc.getRawOffset()).append( "date ").append(d.getTime()).append('\n');
//			Debug.putLog(sb.toString());
//			
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
//		
//		return d;		
	}

//	String stripLine(String str) {
//		StringBuilder sb = new StringBuilder();
//		
//		for(char sym : str.toCharArray()) {
//			if( Character.isDigit(sym))
//				sb.append(sym);
//		}
//		return sb.toString();
//	}
	
//	private Boolean setdaylight(String str) {
//		String checkStr = new String(str);
//		checkStr += utc.getDisplayName(false, TimeZone.SHORT, Locale.US);
//		
//		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat+"Z", Locale.US);
//		simpleDateFormat.setTimeZone(utc);
//
//		boolean isdl = false;
//		try {
//			Date d = new Date();
//			d = simpleDateFormat.parse(checkStr);
//			String check = valueToFormatString(d);
//			isdl = (stripLine(str).compareTo(stripLine(check)) != 0);
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
//		
//		return isdl;
//	}
}
