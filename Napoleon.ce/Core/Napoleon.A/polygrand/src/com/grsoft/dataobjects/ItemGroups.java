package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;
import com.grsoft.database.TableInfo;

@TableInfo(name="itemgroups", keyFields="id")
public class ItemGroups extends DataObject {
	public String id = "";
	public String name="";
	public String userid="";
	public List<ItemGroupsItem> items = new ArrayList<ItemGroupsItem>();
}
