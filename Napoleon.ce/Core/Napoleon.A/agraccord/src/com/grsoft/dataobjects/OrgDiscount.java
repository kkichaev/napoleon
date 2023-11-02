package com.grsoft.dataobjects;

import java.util.List;

import com.grsoft.database.TableInfo;

@TableInfo(name="OrgDiscount", keyFields="id")
public class OrgDiscount extends DataObject {
	public String id;
	public List<OrgDiscountItem> items;
}
