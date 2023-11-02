package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.grsoft.database.TableInfo;

@TableInfo(name="dispatch", keyFields="created", indexes="itemid")
public class Dispatch extends CreateDocDataObject {
	public static int NOT_READY_TO_SEND = 0x40;
	public static int REJECTED = 0x80;
	public static int USER_STATUS = 0x10000;
	
	public List<DispatchTime> times = new ArrayList<DispatchTime>();
	public List<DispatchItem> items = new ArrayList<DispatchItem>();
	public List<DispatchPhoto> photos = new ArrayList<DispatchPhoto>();
	public Date visit;
	
	/**
	 * RouteItem.itemid;
	 */
	public String itemid = "";
}
