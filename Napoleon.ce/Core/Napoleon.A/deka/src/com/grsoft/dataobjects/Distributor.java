package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

@TableInfo(name="Distributors", keyFields="id")
@ServerInfo(name="Distributors")
public class Distributor extends DataObject {
	public String id = "";
	public String name = "";
	
	@Override public String toString() { return name; }
	@Scale(value=Consts.SUM_SCALE)
	public int disc = 0;
}
