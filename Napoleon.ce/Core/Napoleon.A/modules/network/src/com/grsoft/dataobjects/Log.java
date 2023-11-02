package com.grsoft.dataobjects;

import java.util.Date;

import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;

@TableInfo(name="log", keyFields="unixtime")
public class Log extends DataObject {
	public static final int GPS_ON = 1;
	public static final int GPS_OFF = 2;
	public static final int TIME_CHANGED = 3;
	public static final int SYSTEM_LOADED = 4;
	public static final int SYSTEM_SHUTDOWN = 5;
	public static final int PROGRAMM_CRASHED = 6;
	public static final int PROGRAMM_STARTED = 7;
	public static final int PROGRAMM_STOPPED = 8;
	public static final int PDA_STATUS = 9;
	public static final int BKG_SYNC = 10;
	public static final int CLEAR_BASE = 11;
	/***
	 * Фоновое обновление прайса
	 */
	public static final int BKG_PRICE = 12;
	public static final int EXCEPTION = 13;
	
	
	/***
	 * Сообщение для менеджера
	 */
	public static final int MANAGER = 1;
	
	
	/***
	 * Сообщение для отладки
	 */
	public static final int DEBUG = 2;
	
	/***
	 * Дата возникновения события
	 */
	public Date date;
	
	/***
	 * Код события
	 */
	@Scale(value=1)
	public int action;
	
	/***
	 * Коментарий
	 */
	public String comments = "";
	
	/***
	 * Категория сообщения
	 */
	@Scale(value=1)
	public int category;
	
	public long unixtime;
}
