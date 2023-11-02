package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="orderremark", keyFields="id")
@ServerInfo(name="OrderRemark")
public class OrderRemark extends DataObject {
	public String id = "";
	public String text = "";
	public int pos = 0;
}
