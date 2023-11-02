package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

@TableInfo(name="WhQty", keyFields="whCode,id")
public class WhData extends DataObject {
	public String whCode = "";
	
	public String id = "";
	
	@Scale(value=Consts.QTY_SCALE)
	public int qty;
}
