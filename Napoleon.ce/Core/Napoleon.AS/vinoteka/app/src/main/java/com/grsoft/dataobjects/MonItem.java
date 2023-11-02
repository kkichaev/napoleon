package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;

@TableInfo(name="MonItems", keyFields="id")
public class MonItem extends DataObject {
	public String id;
}
