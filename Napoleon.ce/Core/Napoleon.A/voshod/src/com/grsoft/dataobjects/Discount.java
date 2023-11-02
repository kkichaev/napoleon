package com.grsoft.dataobjects;

import java.util.List;

import com.grsoft.database.TableInfo;

@TableInfo(name="Discount",keyFields="id,dogovor")
public class Discount extends DataObject {
	public String id = "";
	public String dogovor = "";
	
	public List<DiscountItem> items;
}
