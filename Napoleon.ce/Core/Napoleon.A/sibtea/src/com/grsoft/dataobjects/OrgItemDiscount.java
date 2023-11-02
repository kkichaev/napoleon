package com.grsoft.dataobjects;

import java.util.List;

import com.grsoft.database.TableInfo;

@TableInfo(name="PriceDiscount",keyFields="id")
public class OrgItemDiscount extends OrgDiscountBase {
	public List<OrgPriceDiscountItem> items;

}
