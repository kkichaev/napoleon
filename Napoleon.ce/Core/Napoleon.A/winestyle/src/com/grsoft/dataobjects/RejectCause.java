package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@ServerInfo(name="RejectCause")
@TableInfo(name="rejectcause", keyFields="id")
public class RejectCause extends DataObject {
	public String id = "";
	public String text = "";
}
