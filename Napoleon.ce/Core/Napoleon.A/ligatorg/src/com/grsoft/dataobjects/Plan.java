package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;

@TableInfo(name="Plans",keyFields="name")
public class Plan extends DataObject {
	public String name = "";
	public String plan = "";
	public String fact = "";
	public String procent = "";
}
