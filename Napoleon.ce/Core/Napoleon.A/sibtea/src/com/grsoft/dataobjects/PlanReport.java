package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;

@TableInfo(name="plnrpt")
public class PlanReport extends DataObject {
	/***
	 * Отчет
	 */
	public byte[] report;
}
