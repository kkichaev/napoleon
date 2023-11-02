package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="PriceSklads", keyFields="id")
@ServerInfo(name="PriceSklads")
public class PriceSklads extends DataObject {
	public String id = "";
	public String idwh = "";
}
