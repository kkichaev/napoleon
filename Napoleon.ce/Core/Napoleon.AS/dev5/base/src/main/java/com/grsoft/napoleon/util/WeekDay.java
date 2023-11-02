package com.grsoft.napoleon.util;
import com.grsoft.aceteam.R;

import android.annotation.SuppressLint;
import java.util.Calendar;

public class WeekDay {
	private int order;
	private String caption;
	
	public static String MONDAY_STR = "Понедельник";
	public static String TUESDAY_STR = "Вторник";
	public static String WEDNESDAY_STR = "Среда";
	public static String THURSDAY_STR = "Четверг";
	public static String FRIDAY_STR = "Пятница";
	public static String SATURDAY_STR = "Суббота";
	public static String SUNDAY_STR = "Воскресенье";
	
	private WeekDay(int order, String caption)
	{
		this.order = order;
		this.caption = caption;
	}
	
	private static WeekDay weekDay[] = {new WeekDay(0, MONDAY_STR),
		new WeekDay(1, TUESDAY_STR),new WeekDay(2, WEDNESDAY_STR),
		new WeekDay(3, THURSDAY_STR), new WeekDay(4, FRIDAY_STR),
		new WeekDay(5, SATURDAY_STR), new WeekDay(6, SUNDAY_STR)};
	
	@SuppressLint("DefaultLocale")
	public static WeekDay getWeekDay(String name)
	{
		for (int i = 0; i < weekDay.length; i++)
		{
			if (weekDay[i].caption.toLowerCase().equals(name.toLowerCase()))
				return weekDay[i];
		}
		
		return null;
	}
	
	public static int compare(WeekDay wk1, WeekDay wk2)
	{
		if(wk1 == null || wk2 == null)
			return 0;
		else
			return wk1.order - wk2.order;
	}
	
	public static WeekDay today(){
		Calendar calendar = Calendar.getInstance();
		return getDayBySystemId(calendar.get(Calendar.DAY_OF_WEEK));
	}
	
	public static WeekDay getDayBySystemId(int day){
		int systemDays[] = {-1,6,0,1,2,3,4,5};
		return weekDay[systemDays[day]];
	}
	
	public String getCaption() { return caption; }
}
