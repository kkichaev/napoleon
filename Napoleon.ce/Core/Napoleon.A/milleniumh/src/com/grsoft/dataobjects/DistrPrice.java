package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;

@TableInfo(name="DistrPrice", keyFields="number")
public class DistrPrice extends DataObject {
	public int number;
	public String id;
	public String name;
}
