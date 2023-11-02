package com.grsoft.dataobjects;

import java.util.List;

import com.grsoft.database.TableInfo;

@TableInfo(name="FocusedGroup",keyFields="id")
public class FocusedGroup extends DataObject {
	
	public String id = "";

	public List<FocusedGroupItem> items;
}
