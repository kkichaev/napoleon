package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="PriceCost", keyFields="\"index\"")
@ServerInfo(name="PriceCostRcv")
public class PriceCost extends DataObject {
	public int index;
	public List<PriceCostItem> items = new ArrayList<PriceCostItem>(); 
}
