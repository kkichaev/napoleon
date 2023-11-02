package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

@TableInfo(name="priceQty", keyFields="type,id")
public class PriceQty extends DataObject {
	public String id;
	
	public int type;
	
	@Scale(value=Consts.QTY_SCALE)
	public int qty;
}
