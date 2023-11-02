package com.grsoft.dataobjects;

import java.util.Date;

import com.grsoft.database.TableInfo;

@TableInfo(name="rptanswer", keyFields = "name")
public class ReportAnswerSPK extends DataObject {
	/***
	 * Имя отчета
	 */
	public String name = "";
	
	/***
	 * Дата создания
	 */
	public Date created;
	
	/***
	 * Отчет
	 */
	public byte[] report;
	
	public String encoding = "";
}
