package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

@TableInfo(name="", keyFields="")
public class Stock extends DocDataObject {
	public String name = "";		
	
	@Scale(value = Consts.QTY_SCALE)
	public int qty;
}
