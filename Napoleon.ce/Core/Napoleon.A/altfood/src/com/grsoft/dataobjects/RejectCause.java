package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="rejectcause", keyFields="id")
@ServerInfo(name="RejectCause")
public class RejectCause extends DataObject {
	public String id = "";
	public String text = "";
}
