package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

import java.util.ArrayList;
import java.util.List;

@TableInfo(name="stockorg", keyFields="id")
@ServerInfo(name="StockOrg")
public class StockOrg extends DataObject {
	public String id = "";
	public List<ActionItem> items = new ArrayList<>();
}
