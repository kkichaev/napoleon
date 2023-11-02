package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="salesban", keyFields="id")
@ServerInfo(name="SalesBan")

public class SalesBan extends DataObject {
	public String id = "";
	public int value = 0;
	public String delay = "";
}
