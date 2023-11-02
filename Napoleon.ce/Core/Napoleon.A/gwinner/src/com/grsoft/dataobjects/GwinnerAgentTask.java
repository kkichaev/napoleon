package com.grsoft.dataobjects;

import java.util.Date;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="GAgentTask", keyFields = "created")
@ServerInfo(name="GwinnerAgentTask")
public class GwinnerAgentTask extends CreateDocDataObject {
	public String task = "";
	public Date done = new Date();
	public int isComplete;
}
