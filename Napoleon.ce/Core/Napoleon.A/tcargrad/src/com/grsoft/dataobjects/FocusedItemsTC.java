package com.grsoft.dataobjects;

import java.util.List;

import com.grsoft.database.TableInfo;

@TableInfo(name="FocusedItems", keyFields="type")
public class FocusedItemsTC extends DataObject {
	
	public String type;	
	public List<FocusedItemTC> items;

}
