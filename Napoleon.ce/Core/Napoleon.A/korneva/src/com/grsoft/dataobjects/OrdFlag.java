package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;

@TableInfo(name="ordflag", keyFields="id")
public class OrdFlag extends DataObject {
	public String id = "";
	public String name = ""; 
}
