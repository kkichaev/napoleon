package com.grsoft.dataobjects;

import java.util.Date;

import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

@TableInfo(name="Plans",keyFields="name,date")
public class Plan extends DataObject {
	public String name = "";

	/**
	 * дата создания
	 */
	public Date date;

	/**
	 * процент выполнения плана
	 */
	@Scale(value=Consts.SUM_SCALE)
	public int plan; 

	@Scale(value=Consts.SUM_SCALE)
	public int fact;
}
