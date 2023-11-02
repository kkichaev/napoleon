package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="chatuser", keyFields="id")
@ServerInfo(name="ChatUser")
public class ChatUser extends DataObject {
	public String id = "";
	public String name = "";
}
