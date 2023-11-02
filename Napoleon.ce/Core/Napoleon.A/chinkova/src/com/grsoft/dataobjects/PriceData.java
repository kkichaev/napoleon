package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.TableInfo;

@TableInfo(name="PriceData", keyFields="idType")
public class PriceData extends DataObject {
	public String idType = "";
	public List<PriceDataItem> items = new ArrayList<PriceDataItem>();
}
