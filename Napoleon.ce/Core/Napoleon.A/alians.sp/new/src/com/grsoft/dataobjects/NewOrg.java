package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

@TableInfo(name="NewOrg", keyFields="created")
public class NewOrg extends OrgData {
	public String name = "";
	public String region = "";
	public String city = "";
	public String punkt = "";
	public String street = "";
	public String dom = "";
	public String kvartira = "";
	public String classTT = "";
	public String visitDay = "";
	public String prevOrg = "";
	public String deliveryRoute = "";
	public int timeOut = 0;
	public int timeIn = 0;	
	
	@Scale(value = Consts.SUM_SCALE)
	public int discount = 0;
	
	public String delay = "";
	public String contactName = "";
	public String contactName2 = "";
	public String contactName3 = "";
	public String phone = "";
	public String dutie = "";
}
