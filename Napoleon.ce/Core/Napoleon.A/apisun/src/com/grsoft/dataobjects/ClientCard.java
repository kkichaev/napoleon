package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;

@TableInfo(name="clientcard", keyFields="id")
public class ClientCard extends DataObject {
	public String id = "";
	public String html = "";
}
