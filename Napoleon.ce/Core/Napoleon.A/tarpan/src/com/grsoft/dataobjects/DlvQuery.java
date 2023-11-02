package com.grsoft.dataobjects;

import java.util.Date;

import com.grsoft.database.TableInfo;

@TableInfo(name="dlvquery", keyFields="id")
public class DlvQuery extends DataObject {
	public String id = "";
	public Date begin;
	public Date end;
}
