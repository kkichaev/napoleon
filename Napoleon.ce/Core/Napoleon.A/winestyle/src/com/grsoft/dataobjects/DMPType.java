package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@ServerInfo(name="DMPType")
@TableInfo(name="dmptype", keyFields="id")
public class DMPType extends DataObject {
	public String id = "";
	public String text = "";
}
