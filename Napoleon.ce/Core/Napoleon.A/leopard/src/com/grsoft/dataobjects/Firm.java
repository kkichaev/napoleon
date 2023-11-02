package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;

@TableInfo(name="firm", keyFields="id")
public class Firm extends DataObject {
	public String id = "";
	public String name = "";
	public String address = "";
	public String phone = "";
	public String inn = "";
	public String bank = "";
	public String buh = "";
	public String chief = "";
}
