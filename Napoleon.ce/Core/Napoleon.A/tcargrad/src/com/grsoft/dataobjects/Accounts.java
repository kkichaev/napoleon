package com.grsoft.dataobjects;

import java.util.Date;

import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

@TableInfo(name="accounts", keyFields="ido,type")
public class Accounts extends TypeName {
	public String ido;

	public String taxType;

	@Scale(value=Consts.SUM_SCALE)
	public int discount;

	public String payType;

	@Scale(value = Consts.SUM_SCALE)
	public int limit;

	@Scale(value = Consts.SUM_SCALE)
	public int rest;
	
	public String contrNumber;
	public Date contrDate;
	
	/**
	 * дней отсрочки
	 */
	public int dayDeff;
}
