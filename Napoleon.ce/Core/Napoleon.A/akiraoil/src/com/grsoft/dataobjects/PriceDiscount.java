package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="PriceDiscount", keyFields="agreeId")
@ServerInfo(name="PriceDiscount")
public class PriceDiscount extends DataObject {
	
	public String agreeId = "";
	public List<PriceDiscountItem> items = new ArrayList<PriceDiscountItem>();

}
