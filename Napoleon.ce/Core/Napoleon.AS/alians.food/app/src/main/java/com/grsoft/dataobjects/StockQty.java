package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

@TableInfo(name="stockQty")
public class StockQty extends DataObject {
	public String id;
	
	@Scale(value=Consts.QTY_SCALE)
	public int qty;
}
