package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="MerchRouteForAgent")
@ServerInfo(name="MerchRouteForAgent")
public class MerchRouteForAgent extends DataObject {
	public String day = "";
	public String id = "";
}
