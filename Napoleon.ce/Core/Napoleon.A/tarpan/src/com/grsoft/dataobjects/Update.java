package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.TableInfo;

@TableInfo(name="update", keyFields="name")
public class Update extends DataObject {
	public String name = "";
	public List<UpdateItem> items = new ArrayList<UpdateItem>();
	
	@Override
	public String toString() {
		return name;
	}
}
