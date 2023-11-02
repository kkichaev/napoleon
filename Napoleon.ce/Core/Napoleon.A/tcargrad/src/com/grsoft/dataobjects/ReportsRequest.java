package com.grsoft.dataobjects;

import java.util.List;

import com.grsoft.database.TableInfo;

@TableInfo(name="repreq", keyFields="id")
public class ReportsRequest extends DataObject {
	public String id;
	public List<OrderPropData> items;
}
