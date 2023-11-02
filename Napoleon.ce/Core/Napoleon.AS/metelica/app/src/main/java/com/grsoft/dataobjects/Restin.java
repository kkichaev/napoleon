package com.grsoft.dataobjects;

import java.util.List;

import com.grsoft.database.TableInfo;

@TableInfo(name="RestIn", keyFields="id")
public class Restin extends DataObject {
	public String id;
	
	public List<RestInItem> items;
}
