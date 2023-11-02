package com.grsoft.ads.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;
import com.grsoft.dataobjects.CreateDocDataObject;

@TableInfo(name="note", keyFields="created")
@ServerInfo(name="Note")
public class Note extends CreateDocDataObject {
	public String taskid = "";
	public String client = "";
	public String address = "";
}
