package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="stockorg", keyFields="id")
@ServerInfo(name="StockOrg")
public class StockOrg extends DataObject {
	public String id = ""; 
}
