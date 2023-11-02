package com.grsoft.dataobjects;

import java.util.List;

import com.grsoft.database.TableInfo;
import com.grsoft.dataobjects.DataObject;

@TableInfo(name="Discounts", keyFields="id")
public class Discount extends DataObject {
	public String id;
	public List<DiscountItem> items;
}
