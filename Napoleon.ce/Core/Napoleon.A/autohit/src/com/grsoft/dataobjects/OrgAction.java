package com.grsoft.dataobjects;

import java.util.Date;

import com.grsoft.database.TableInfo;

@TableInfo(name="actions", keyFields="id")
public class OrgAction extends DataObject {
	public String id;
	public Date start;
	public Date end;
	public String name;
	
	@Override
	public String toString() {
		return name;
	}
}
