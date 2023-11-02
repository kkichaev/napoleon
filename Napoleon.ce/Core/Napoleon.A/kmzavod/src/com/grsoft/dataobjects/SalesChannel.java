package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="saleschannel", keyFields="id")
@ServerInfo(name="SalesChannel")
public class SalesChannel extends DataObject {
	public String id;
	public String name;
}
