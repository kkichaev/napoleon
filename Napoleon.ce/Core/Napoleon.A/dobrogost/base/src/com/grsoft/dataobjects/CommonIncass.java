package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.grsoft.database.TableInfo;

@TableInfo(name="commonincass", keyFields="created")
public class CommonIncass extends DataObject {
	public Date created;
	public String remark;
	public List<CommonIncassItem> items = new ArrayList<CommonIncassItem>();
	public int params;
	public String bank;
}
