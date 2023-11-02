package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;


@TableInfo(name="drivers", keyFields="")
public class Drivers extends DataObject {
	public String name;
	public String phone;
}
