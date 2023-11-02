package com.grsoft.dataobjects;

import java.util.Date;

import com.grsoft.database.TableInfo;

@TableInfo(name="report", keyFields = "id")
public class Report extends DataObject {
	/***
	 * Имя отчета
	 */
	public String id = "";
	
	/**
	 * Кодировка отчета
	 */
	public String encoding = "";
	
	/***
	 * Дата 
	 */
	public Date date;
		
	/***
	 * Отчет
	 */
	public byte[] report;
}
