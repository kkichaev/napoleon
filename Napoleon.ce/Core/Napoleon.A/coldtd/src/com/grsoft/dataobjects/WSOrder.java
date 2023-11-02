package com.grsoft.dataobjects;

import java.util.Date;

import com.grsoft.database.TableInfo;

@TableInfo(name="wsorder", keyFields="created")
public class WSOrder extends Order {
	public Date loadDate = new Date();
	public int loadTime;
	public String orgId = "";
}
