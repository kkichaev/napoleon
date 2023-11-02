package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="DispatchReturnsInfo", keyFields="created")
@ServerInfo(name="DispatchReturnsInfo")
public class DispatchReturnsInfo extends CreateDocDataObject {
	public String routeItemId = "";
	public Date dispatch = new Date();
	public Date waybillDoc = new Date();
	public List<DispatchReturnsItem> items = new ArrayList<DispatchReturnsItem>();
}
