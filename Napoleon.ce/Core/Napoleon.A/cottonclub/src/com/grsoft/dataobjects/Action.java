package com.grsoft.dataobjects;

import java.util.Date;

import com.grsoft.database.TableInfo;

@TableInfo(name="actions",keyFields="id,org,item")
public class Action extends DataObject {
	public String id;
	public String name;
	public String org;
	public String item;
	public String action;
	public Date begin;
	public Date end;
	public String fio;
	public String phone;
	public String descr;
}
