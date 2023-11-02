package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.TableInfo;

@TableInfo(name="priceqty", keyFields = "id")
public class PriceQty extends DataObject {
	public String id = "";
	public List<PriceQtyItem> items = new ArrayList<PriceQtyItem>();
}
