package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="PriceTypes", keyFields="idwh")
@ServerInfo(name="PriceTypes")
public class WHQty extends DataObject {
	public String idwh="";
	public List<WHQtyItem> items = new ArrayList<WHQtyItem>();
}
