package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;

@TableInfo(name="Dealer", keyFields="id")
public class Dealer extends DataObject {
	public String id = "";
	public String name = "";
}
