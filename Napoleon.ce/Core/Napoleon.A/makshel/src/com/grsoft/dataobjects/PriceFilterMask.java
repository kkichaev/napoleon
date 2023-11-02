package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="PriceFilterMask", keyFields="id")
@ServerInfo(name="PriceFilterMask")
public class PriceFilterMask extends DataObject {
	public int id = 0;
	public String name = "";
}
