package com.grsoft.dataobjects;

import java.util.List;

import com.grsoft.database.TableInfo;
import com.grsoft.dataobjects.DataObject;

@TableInfo(name="OrgDiscounts", keyFields="id")
public class OrgDiscount extends DataObject {
	public String id;
	public List<OrgDiscountItem> items;
}
