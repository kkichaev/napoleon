package com.grsoft.dataobjects;

import java.util.Date;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

@TableInfo(name="OrgBalance", keyFields="id,idDog")
@ServerInfo(name="OrgBalance")
public class OrgBalance extends DataObject {
	public static final long CHECK_DATE = 365 * 24 * 3600 * 1000;

	public String id = "";
	public String idDog = "";
	
	public int dueDays = 0;
	
	public String name = "";

	@Scale(value=Consts.SUM_SCALE)
	public int balance = 0;

	@Scale(value=Consts.SUM_SCALE)
	public int limit = 0;
	
	public Date unlockDate = new Date();
}
