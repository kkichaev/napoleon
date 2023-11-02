package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;

@TableInfo(name="pricegroup", keyFields="id")
public class PriceGroup extends DataObject {
	public String id = "";
	public String name = "";
}
