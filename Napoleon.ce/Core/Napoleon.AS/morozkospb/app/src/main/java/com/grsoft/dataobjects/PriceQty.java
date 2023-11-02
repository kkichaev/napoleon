package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

@TableInfo(name="qtys", keyFields="idStore, id")
@ServerInfo(name="PriceQty")
public class PriceQty extends DataObject {
	public String id = "";
	public String idStore = "";
	
	@Scale(value=Consts.QTY_SCALE)
	public int qty = 0;
}
