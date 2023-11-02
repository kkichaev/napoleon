package com.grsoft.dataobjects;

import java.util.Date;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="OrderDriverRouteInfo", keyFields="created,userid")
@ServerInfo(name="OrderDriverRouteInfo")
public class OrderDriverRouteInfo extends DataObject {
	
	public static final int STATUS_ACTIVE = 0;
	public static final int STATUS_FINISHED = 1;
	public static final int STAUS_CANCEL = 2;
	public static final int STATUS_REJECT = 3;

	public static final int STAUS_DRIVER_FREE = 4;
	public static final int STAUS_DRIVER_WORKING = 5;
	public static final int STAUS_DRIVER_DRIVING = 6;
	public static final int STAUS_DRIVER_BROKE = 7;

	public static final int STATUS_IN_ROUTE = 8;
	public static final int STATUS_DONE_WIITH_RETURNS = 9;
	
	public Date created = new Date();
	
	public String routeItemId = "";
	
	public int status = 0;
	
	public String docNumber = "";
	
	public String route = "";
	public String userid = "";
}
