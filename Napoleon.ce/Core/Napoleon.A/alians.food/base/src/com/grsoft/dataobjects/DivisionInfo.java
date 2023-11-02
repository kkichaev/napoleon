package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;

@TableInfo(name="divisioninfo", keyFields="id,userid")
public class DivisionInfo extends DataObject {
	public int id = -1;
	public int parent = -1;
	public String userid = "";
	public String delay = "";
}
