package com.grsoft.dataobjects;

import java.util.Date;

import com.grsoft.database.TableInfo;

@TableInfo(name="ord_del", keyFields = "created")
public class OrderToDel extends DataObject {
	public Date created;
	public Date date;
}
