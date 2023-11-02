package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="PriceCost", keyFields="idPrice")
@ServerInfo(name="PriceCost")
public class PriceCost extends DataObject {
	public String idPrice = "";
	
	public List<PriceCostItem> items = new ArrayList<PriceCostItem>();
}
