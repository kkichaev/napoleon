package com.grsoft.dataobjects;

import java.util.Date;

import com.grsoft.database.TableInfo;

@TableInfo(name="rptanswer", keyFields = "id")
public class Report extends DataObject {
	/***
	 * Имя отчета
	 */
	public String id = "";
	
	/**
	 * Кодировка отчета
	 */
	public String encoding = "";
	
	public Date rcvdDate = new Date();
		
	/***
	 * Отчет
	 */
	public byte[] report;
}
