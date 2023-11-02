package com.grsoft.dataobjects;

import java.util.List;

import com.grsoft.database.TableInfo;

@TableInfo(name="Plan",keyFields="idplan")
public class Plan extends DataObject {
	public String idplan = "";
	public String name = "";
	public byte[] labels; 
	public int background;
	public List<PlanItem> items;
}
