package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="MonitoringGoods", keyFields="id")
@ServerInfo(name="MonitoringGoods")
public class MntrGoods extends DataObject {
	public String id = "";
	public String name = "";
	public String folder = "";
}
