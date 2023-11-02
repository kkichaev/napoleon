package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="returnCause", keyFields="id,idType")
@ServerInfo(name="ReturnCause")
public class ReturnCause extends DataObject {
	public String id = "";
	public String name = "";
//	public int needPhoto = 0;
	public String idType = "";
	public String firm = "";
	
	
	@Override public String toString() { return name; }
}
