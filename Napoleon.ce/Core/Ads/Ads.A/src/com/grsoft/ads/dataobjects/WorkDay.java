package com.grsoft.ads.dataobjects;

import java.util.Date;

import com.grsoft.database.TableInfo;
import com.grsoft.dataobjects.DataObject;

@TableInfo(name="workday", keyFields = "date")
public class WorkDay extends DataObject {
	
	/***
	 * Дата без времени
	 */
	public Date date;
	
	/***
	 * Начало
	 */
	public Date begin = null;
	
	/**
	 * Конец
	 */
	public Date end = null;
	
	/***
	 * Путь в метрах
	 */
	public int distance;
	
	public int params;
	
	/**
	 * Сейчас рабочее время
	 * 0 - нет
	 * не 0 - да
	 */
	public int active;
}
