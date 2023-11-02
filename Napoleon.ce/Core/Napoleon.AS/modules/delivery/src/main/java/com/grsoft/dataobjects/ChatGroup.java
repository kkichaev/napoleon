package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="chatgroup", keyFields="id")
@ServerInfo(name="ChatGroupAgent")
public class ChatGroup extends DataObject{
	public String id = "";
	public String title = "";
}
