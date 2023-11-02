package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="returncause")
@ServerInfo(name="ReturnCause")
public class ReturnCause extends DataObject {
	public String name = "";
}
