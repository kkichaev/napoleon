package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

@TableInfo(name="plan", keyFields="id,pid")
@ServerInfo(name="Plan")
public class Plan extends DataObject {
	public String id = "";
	public String pid = "";
	
	@Scale(value=Consts.QTY_SCALE)
	public int qty = 0;
	
}
