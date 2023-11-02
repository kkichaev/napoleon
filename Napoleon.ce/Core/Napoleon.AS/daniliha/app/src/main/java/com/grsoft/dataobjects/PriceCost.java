package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="PriceCost",keyFields="id")
@ServerInfo(name="PriceCost")
public class PriceCost extends DataObject {
	public String id = "";
	public String name = "";
	public String shortName = "";
	
	public List<PriceCostItem> items = new ArrayList<PriceCostItem>();
			
}
