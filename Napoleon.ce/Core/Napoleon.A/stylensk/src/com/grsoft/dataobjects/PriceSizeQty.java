package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="PriceSizeQty", keyFields="id,size,color")
@ServerInfo(name="PriceSizeQty")
public class PriceSizeQty extends DataObject {
	public String id = "";
	public String size = "";
	public String color = "";
	
	public List<PriceWhData> qty = new ArrayList<PriceWhData>();
}
