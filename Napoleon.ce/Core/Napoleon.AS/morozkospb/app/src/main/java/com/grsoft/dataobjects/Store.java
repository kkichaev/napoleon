package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="stores", keyFields="id")
@ServerInfo(name="Store")
public class Store extends DataObject {
	public String id = "";
	public String name = "";
	public int index;
//	public String idFirm = "";

	@Override public String toString() { return name; }
}
