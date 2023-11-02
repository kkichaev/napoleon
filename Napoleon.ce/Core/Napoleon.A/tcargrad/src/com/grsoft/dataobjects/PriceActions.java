package com.grsoft.dataobjects;

import java.util.Date;

import com.grsoft.database.TableInfo;

@TableInfo(name="PriceActions", keyFields="id")
public class PriceActions extends DataObject {
	public String id;
	public String action;
	
	public Date start;
	public Date end;
}
