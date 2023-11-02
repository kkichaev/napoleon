package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

@TableInfo(name="MetelicaPrices", keyFields="price")
@ServerInfo(name="Prices")
public class MetelicaPrices extends DataObject {
	public int price = 0;

	public List<PricesItem> items = new ArrayList<PricesItem>();
}
