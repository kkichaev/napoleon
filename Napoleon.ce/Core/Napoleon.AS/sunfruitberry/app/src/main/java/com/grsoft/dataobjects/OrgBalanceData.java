package com.grsoft.dataobjects;

import java.util.Date;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

@TableInfo(name="OrgBalanceData", indexes="id")
@ServerInfo(name="OrgBalanceData")
public class OrgBalanceData extends DataObject {
	public String id = "";
	public String ido = "";
	public String idDog = "";
	public String number = "";
	
	public Date payDate = new Date();
	public Date date = new Date();
	
	@Scale(value=Consts.SUM_SCALE)
	public int sumD;
}
