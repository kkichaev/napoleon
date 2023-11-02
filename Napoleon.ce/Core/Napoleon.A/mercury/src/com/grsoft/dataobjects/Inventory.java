package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="inventory", keyFields="id")
@ServerInfo(name="Inventory")
public class Inventory extends DataObject {
	public String id = "";
	public String ido = "";
}
