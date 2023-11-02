package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="returncause", keyFields="id")
@ServerInfo(name="ReturnCause")
public class ReturnCause extends DataObject {
	public String id = "";
	public String text = "";
}
