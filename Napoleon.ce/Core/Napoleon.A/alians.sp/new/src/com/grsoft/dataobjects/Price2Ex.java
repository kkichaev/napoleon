package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;

@TableInfo(name="Price", keyFields = "id", indexes="catid")
public class Price2Ex extends PriceEx {
	public String type = "";
	public String catid = "";
}
