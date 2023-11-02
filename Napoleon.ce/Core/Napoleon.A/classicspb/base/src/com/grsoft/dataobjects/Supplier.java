package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="Supplier", keyFields="id")
@ServerInfo(name="Suppliers")
public class Supplier extends DataObject {
	public String id = "";
	public String name = "";
}
