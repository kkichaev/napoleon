package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;

@TableInfo(name="sklads", keyFields="id")
public class Sklads extends DataObject {
	public int id;
	public String name = "";
}
