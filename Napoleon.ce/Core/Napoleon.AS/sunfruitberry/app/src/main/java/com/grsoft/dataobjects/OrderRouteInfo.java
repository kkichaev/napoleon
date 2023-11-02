package com.grsoft.dataobjects;

import java.util.Date;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;
import com.grsoft.util.Util;

@TableInfo(name="OrderRouteInfo", keyFields="number")
@ServerInfo(name="OrderRouteInfo")
public class OrderRouteInfo extends DataObject {
	public String name = "";
	public String phone = "";
	public String number = "";
	
	public String itemid = "";
	public String route = "";
	
	public Date start = new Date();
	public Date finish = new Date();
	
	public String toText() {
		return "В рейсе на " + Util.simpleDateFormat.format(finish);
	}
}
