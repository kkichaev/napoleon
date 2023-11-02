package com.grsoft.napoleon.util;

import java.util.Date;

/**
 * Класс хранит время начала и конца процесса 
 * и позволяет получить время процесса
 * @author kki
 *
 */
public class TimePeriod
{
	private Date tmbegin;;
	private Date tmend;;
	
	public void setTimeBegin()
	{
		tmbegin = new Date();
	}
	
	public void setTimeEnd()
	{
		tmend = new Date();
	}
	
	public long diffsk()
	{
		return diffms() / 1000;
	}
	
	public long diffms()
	{
		return tmend.getTime() - tmbegin.getTime();
	}
}
