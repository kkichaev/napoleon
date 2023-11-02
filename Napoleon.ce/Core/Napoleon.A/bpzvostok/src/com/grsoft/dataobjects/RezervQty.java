package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

@TableInfo(name="rezervQty", keyFields="id")
@ServerInfo(name="RezervQty")
public class RezervQty extends DataObject {
	public String id = "";
	
	@Scale(value = Consts.QTY_SCALE)
	public int qty = 0;
}
